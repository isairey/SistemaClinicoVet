package co.edu.upb.veterinaria.repositories.RepositorioProducto;

import co.edu.upb.veterinaria.config.DatabaseConfig;
import co.edu.upb.veterinaria.models.ModeloMarca.Marca;
import co.edu.upb.veterinaria.models.ModeloProducto.Producto;
import co.edu.upb.veterinaria.models.ModeloProveedor.Proveedor;
import co.edu.upb.veterinaria.models.ModeloUsuario.Usuario;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ProductoRepository
 * ------------------
 * - Acceso a BD encapsulado (caja negra).
 * - TODAS las consultas con PreparedStatement ('?') -> evita SQL injection.
 * - CRUD completo SIN delete físico (soft: 'estado' INACTIVO).
 * - Búsquedas y listados con paginación, filtro y ORDER BY seguro (whitelist).
 * - Actualización de stock ATÓMICA en BD.
 * - Manejo de relación N:N Producto-Proveedor (tabla veterinaria.proveedor_has_producto):
 *      * linkProveedorProducto(idProducto, idProveedor)
 *      * unlinkProveedoresDeProducto(idProducto)  // para re-vincular
 *
 * NOTAS de mapeo (según script de BD):
 *  - Tabla veterinaria.producto (muuuchos campos). Aquí usamos los base:
 *      idproducto, nombre, referencia, codigobarras, precio, costo, descripcion,
 *      stock (numeric), imagenproducto (bytea), estado (varchar),
 *      marca_idmarca (FK), usuario_idusuario (FK), unidadmedida_idunidadmedida (FK, NULL),
 *      tipoproducto_idtipoproducto (FK, NOT NULL), unidadesingresadas (int NOT NULL).
 *  - El modelo Producto tiene (Marca, Proveedor, Usuario) como objetos "ligeros".
 *    Para Proveedor, como la relación es N:N, cargamos UNO (si existe) para mostrar
 *    y dejamos helpers para administrar la tabla puente.
 */
public class ProductoRepository {

    // ---------- Infra ----------
    private final DataSource dataSource;

    public ProductoRepository() {
        this.dataSource = DatabaseConfig.getDataSource();
    }

    // ---------- Helpers de seguridad / util ----------

    /** Convierte double->BigDecimal evitando problemas de escala. */
    private BigDecimal bd(double v) { return BigDecimal.valueOf(v); }

    /** '%q%' para ILIKE. Si q es null/blank -> '%' */
    private String likeParam(String q) {
        return (q == null || q.isBlank()) ? "%" : "%" + q.trim() + "%";
    }

    /** Whitelist de columnas para ORDER BY. */
    private String validateSortColumn(String sort) {
        if (sort == null) return "p.idproducto";
        switch (sort.toLowerCase()) {
            case "id":
            case "idproducto":      return "p.idproducto";
            case "nombre":          return "p.nombre";
            case "referencia":      return "p.referencia";
            case "codigobarras":    return "p.codigobarras";
            case "precio":          return "p.precio";
            case "costo":           return "p.costo";
            case "stock":           return "p.stock";
            case "estado":          return "p.estado";
            case "marca":
            case "nombremarca":     return "m.nombremarca";
            case "proveedor":
            case "nombreproveedor": return "prov.nombre";
            default:                return "p.idproducto";
        }
    }

    /** Dirección segura del sort. */
    private String sortDir(boolean asc) { return asc ? "ASC" : "DESC"; }

    // ---------- Mapeos ----------

    /**
     * Mapea la fila (con posibles columnas de joins) a Producto + objetos anidados "ligeros".
     * Espera alias:
     *  p.*  (producto)
     *  m.idmarca AS m_id, m.nombremarca AS m_nombre
     *  u.idusuario AS u_id
     *  prov.idproveedor AS prov_id, prov.nombre AS prov_nombre, prov.apellido AS prov_apellido (opcionales)
     */
    private Producto mapRow(ResultSet rs) throws SQLException {
        Marca marca = new Marca(
                rs.getInt("m_id"),
                rs.getString("m_nombre")
        );

        // Usuario "ligero" (cargamos solo id)
        Usuario usuario = null;
        int uId = rs.getInt("u_id");
        if (!rs.wasNull()) {
            usuario = new Usuario();
            usuario.setIdUsuario(uId);
        }

        // Proveedor "ligero" si viene del LEFT JOIN (puede ser null)
        Proveedor proveedor = null;
        int provId = rs.getInt("prov_id");
        if (!rs.wasNull()) {
            proveedor = new Proveedor();
            proveedor.setIdProveedor(provId);
            proveedor.setNombre(rs.getString("prov_nombre"));
            // El modelo tiene más campos; no son obligatorios para mostrar en inventario
        }

        Producto p = new Producto(
                rs.getInt("idproducto"),
                rs.getString("nombre"),
                rs.getString("referencia"),
                rs.getString("codigobarras"),
                rs.getBigDecimal("precio").doubleValue(),
                rs.getBigDecimal("costo").doubleValue(),
                marca,
                rs.getString("descripcion"),
                rs.getBigDecimal("stock").intValue(), // en BD es numeric; en modelo int
                rs.getBytes("imagenproducto"),
                rs.getString("estado"),
                proveedor,
                usuario
        );
        return p;
    }

    // ============================================================
    //                           CRUD
    // ============================================================

    /**
     * INSERT de producto.
     * Importante: en BD la PK es identity -> usamos RETURNING para obtener id.
     * NOT NULL en BD que resolvemos aquí:
     *  - tipoproducto_idtipoproducto (pásalo como parámetro)
     *  - unidadesingresadas (inicialmente = stock)
     *  - usuario_idusuario (✅ NULLABLE - puede ser NULL)
     *  - marca_idmarca (viene en p.getMarca())
     *  - imagenproducto (viene en p.getImagenProducto(), NOT NULL)
     *  - estado (viene en p.getEstado())
     *
     * Además, si p.getProveedor() != null, vinculamos en la tabla puente.
     *
     * @param p               modelo base
     * @param idTipoProducto  FK obligatoria (según tu catálogo)
     * @param idUnidadMedida  FK opcional (null si no aplica)
     * @return id generado
     */
    public int create(Producto p, int idTipoProducto, Integer idUnidadMedida) throws SQLException {
        final String sql =
                "INSERT INTO veterinaria.producto (" +
                        "  nombre, precio, costo, referencia, codigobarras, stock, descripcion," +
                        "  unidadesingresadas, tipoproducto_idtipoproducto, usuario_idusuario," +
                        "  marca_idmarca, unidadmedida_idunidadmedida, imagenproducto, estado" +
                        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING idproducto";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            int i = 1;
            ps.setString(i++, p.getNombre());
            ps.setBigDecimal(i++, bd(p.getPrecio()));
            ps.setBigDecimal(i++, bd(p.getCosto()));
            ps.setString(i++, p.getReferencia());
            ps.setString(i++, p.getCodigoBarras());
            ps.setBigDecimal(i++, BigDecimal.valueOf(p.getStock())); // numeric
            ps.setString(i++, p.getDescripcion());
            ps.setInt(i++, p.getStock()); // unidadesingresadas inicial = stock
            ps.setInt(i++, idTipoProducto);

            // ✅ CORRECCIÓN: Permitir NULL para usuario_idusuario
            if (p.getUsuario() != null && p.getUsuario().getIdUsuario() > 0) {
                ps.setInt(i++, p.getUsuario().getIdUsuario());
            } else {
                ps.setNull(i++, Types.INTEGER);
            }

            if (p.getMarca() != null) {
                ps.setInt(i++, p.getMarca().getIdMarca());
            } else {
                ps.setNull(i++, Types.INTEGER);
            }
            if (idUnidadMedida == null) {
                ps.setNull(i++, Types.INTEGER);
            } else {
                ps.setInt(i++, idUnidadMedida);
            }
            ps.setBytes(i++, p.getImagenProducto());   // NOT NULL en BD
            ps.setString(i, p.getEstado());

            int newId = -1;
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) newId = rs.getInt(1);
            }

            // Vincular proveedor (si viene)
            if (newId > 0 && p.getProveedor() != null) {
                linkProveedorProducto(newId, p.getProveedor().getIdProveedor(), cn);
            }
            return newId;
        }
    }

    /**
     * UPDATE de campos base.
     * Si se desea re-vincular proveedor, pasar idProveedor (nullable):
     *   - null => no toca vínculos
     *   - >=0  => reemplaza vínculos (borra y crea uno nuevo si no es null)
     */
    public boolean update(Producto p, Integer idTipoProducto, Integer idUnidadMedida, Integer idProveedor) throws SQLException {
        final String sql =
                "UPDATE veterinaria.producto SET " +
                        "  nombre = ?, precio = ?, costo = ?, referencia = ?, codigobarras = ?, " +
                        "  stock = ?, descripcion = ?, " +
                        "  tipoproducto_idtipoproducto = COALESCE(?, tipoproducto_idtipoproducto), " +
                        "  usuario_idusuario = ?, marca_idmarca = ?, unidadmedida_idunidadmedida = ?, " +
                        "  imagenproducto = ?, estado = ? " +
                        "WHERE idproducto = ?";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            int i = 1;
            ps.setString(i++, p.getNombre());
            ps.setBigDecimal(i++, bd(p.getPrecio()));
            ps.setBigDecimal(i++, bd(p.getCosto()));
            ps.setString(i++, p.getReferencia());
            ps.setString(i++, p.getCodigoBarras());
            ps.setBigDecimal(i++, BigDecimal.valueOf(p.getStock()));
            ps.setString(i++, p.getDescripcion());
            if (idTipoProducto == null) {
                ps.setNull(i++, Types.INTEGER);
            } else {
                ps.setInt(i++, idTipoProducto);
            }

            // ✅ CORRECCIÓN: Permitir NULL para usuario_idusuario
            if (p.getUsuario() != null && p.getUsuario().getIdUsuario() > 0) {
                ps.setInt(i++, p.getUsuario().getIdUsuario());
            } else {
                ps.setNull(i++, Types.INTEGER);
            }

            if (p.getMarca() != null) {
                ps.setInt(i++, p.getMarca().getIdMarca());
            } else {
                ps.setNull(i++, Types.INTEGER);
            }
            if (idUnidadMedida == null) {
                ps.setNull(i++, Types.INTEGER);
            } else {
                ps.setInt(i++, idUnidadMedida);
            }
            ps.setBytes(i++, p.getImagenProducto());
            ps.setString(i++, p.getEstado());
            ps.setInt(i, p.getIdProducto());

            boolean ok = ps.executeUpdate() > 0;

            // Re-vinculación de proveedor si se solicitó
            if (ok && idProveedor != null) {
                unlinkProveedoresDeProducto(p.getIdProducto(), cn);
                if (idProveedor >= 0) {
                    linkProveedorProducto(p.getIdProducto(), idProveedor, cn);
                }
            }
            return ok;
        }
    }

    /**
     * DELETE físico: elimina el producto permanentemente de la BD.
     * ⚠️ CUIDADO: Esta operación NO se puede deshacer.
     *
     * IMPORTANTE: Si hay relaciones FK con otras tablas (ej. proveedor_has_producto),
     * debes eliminarlas primero o configurar ON DELETE CASCADE en la BD.
     *
     * @param idProducto ID del producto a eliminar
     * @return true si se eliminó, false si no existía
     * @throws SQLException si hay error de BD (ej. violación de FK)
     */
    public boolean delete(int idProducto) throws SQLException {
        // Primero eliminar relaciones en tabla puente (si existen)
        try (Connection cn = dataSource.getConnection()) {
            // 1. Eliminar vínculos con proveedores
            unlinkProveedoresDeProducto(idProducto, cn);

            // 2. Eliminar el producto
            final String sql = "DELETE FROM veterinaria.producto WHERE idproducto = ?";
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setInt(1, idProducto);
                return ps.executeUpdate() > 0;
            }
        }
    }

    // ============================================================
    //                     BÚSQUEDAS / LECTURAS
    // ============================================================

    /**
     * SELECT por PK (incluye Marca, Usuario id y un Proveedor si existe).
     * ✅ CORREGIDO: LEFT JOIN con usuario ya que puede ser NULL
     */
    public Optional<Producto> findById(int idProducto) throws SQLException {
        final String sql =
                "SELECT " +
                        "p.*, " +
                        "m.idmarca AS m_id, m.nombremarca AS m_nombre, m.descripcion AS m_descripcion, " +
                        "u.idusuario AS u_id, " +
                        "prov.idproveedor AS prov_id, prov.nombre AS prov_nombre, " +
                        "prov.apellido AS prov_apellido " +
                        "FROM veterinaria.producto p " +
                        "JOIN veterinaria.marca m ON m.idmarca = p.marca_idmarca " +
                        "LEFT JOIN veterinaria.usuario u ON u.idusuario = p.usuario_idusuario " +
                        "LEFT JOIN LATERAL ( " +
                        "   SELECT pr.* FROM veterinaria.proveedor_has_producto php " +
                        "   JOIN veterinaria.proveedor pr ON pr.idproveedor = php.proveedor_idproveedor " +
                        "   WHERE php.producto_idproducto = p.idproducto " +
                        "   LIMIT 1 " +
                        ") prov ON true " +
                        "WHERE p.idproducto = ?";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    /**
     * SELECT por código de barras (case-sensitive según BD; se puede ajustar).
     * ✅ CORREGIDO: LEFT JOIN con usuario ya que puede ser NULL
     */
    public Optional<Producto> findByCodigoBarras(String codigo) throws SQLException {
        final String sql =
                "SELECT p.idproducto, p.nombre, p.referencia, p.codigobarras, p.precio, p.costo, " +
                        "       p.descripcion, p.stock, p.imagenproducto, p.estado, " +
                        "       m.idmarca AS m_id, m.nombremarca AS m_nombre, " +
                        "       u.idusuario AS u_id, " +
                        "       prov.idproveedor AS prov_id, prov.nombre AS prov_nombre " +
                        "FROM veterinaria.producto p " +
                        "JOIN veterinaria.marca m ON m.idmarca = p.marca_idmarca " +
                        "LEFT JOIN veterinaria.usuario u ON u.idusuario = p.usuario_idusuario " +
                        "LEFT JOIN LATERAL ( " +
                        "   SELECT pr.* FROM veterinaria.proveedor_has_producto php " +
                        "   JOIN veterinaria.proveedor pr ON pr.idproveedor = php.proveedor_idproveedor " +
                        "   WHERE php.producto_idproducto = p.idproducto " +
                        "   LIMIT 1 " +
                        ") prov ON true " +
                        "WHERE p.codigobarras = ?";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    /**
     * Búsqueda full (filtro + orden + paginación).
     * Filtro aplica a: nombre, referencia, codigobarras, descripcion, marca, proveedor.
     * Muestra productos ACTIVO/INACTIVO (si quieres solo activos, pásame estadoFilter="ACTIVO").
     * ✅ CORREGIDO: LEFT JOIN con usuario ya que puede ser NULL
     */
    public List<Producto> search(String q, String estadoFilter,
                                 String sort, boolean asc,
                                 int limit, int offset) throws SQLException {

        final String sortCol = validateSortColumn(sort);
        final String dir = sortDir(asc);

        StringBuilder sql = new StringBuilder(
                "SELECT " +
                        "p.*, " +
                        "m.idmarca AS m_id, m.nombremarca AS m_nombre, m.descripcion AS m_descripcion, " +
                        "u.idusuario AS u_id, " +
                        "prov.idproveedor AS prov_id, prov.nombre AS prov_nombre, " +
                        "prov.apellido AS prov_apellido " +
                        "FROM veterinaria.producto p " +
                        "JOIN veterinaria.marca m ON m.idmarca = p.marca_idmarca " +
                        "LEFT JOIN veterinaria.usuario u ON u.idusuario = p.usuario_idusuario " +
                        "LEFT JOIN LATERAL ( " +
                        "   SELECT pr.* FROM veterinaria.proveedor_has_producto php " +
                        "   JOIN veterinaria.proveedor pr ON pr.idproveedor = php.proveedor_idproveedor " +
                        "   WHERE php.producto_idproducto = p.idproducto " +
                        "   LIMIT 1 " +
                        ") prov ON true " +
                        "WHERE (p.nombre ILIKE ? OR p.referencia ILIKE ? OR p.codigobarras ILIKE ? " +
                        "       OR p.descripcion ILIKE ? OR m.nombremarca ILIKE ? " +
                        "       OR (prov.nombre IS NOT NULL AND prov.nombre ILIKE ?)) "
        );

        if (estadoFilter != null && !estadoFilter.isBlank()) {
            sql.append("AND p.estado = ? ");
        }

        sql.append("ORDER BY ").append(sortCol).append(" ").append(dir);
        if (limit > 0) sql.append(" LIMIT ? OFFSET ?");

        List<Producto> out = new ArrayList<>();
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            int i = 1;
            String like = likeParam(q);
            ps.setString(i++, like); // nombre
            ps.setString(i++, like); // referencia
            ps.setString(i++, like); // codigobarras
            ps.setString(i++, like); // descripcion
            ps.setString(i++, like); // marca
            ps.setString(i++, like); // proveedor

            if (estadoFilter != null && !estadoFilter.isBlank()) {
                ps.setString(i++, estadoFilter);
            }
            if (limit > 0) {
                ps.setInt(i++, limit);
                ps.setInt(i, Math.max(0, offset));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRow(rs));
            }
        }
        return out;
    }

    /** Conteo para paginación; mismo filtro que search (sin estado si no se proporciona). */
    public long count(String q, String estadoFilter) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) AS c " +
                        "FROM veterinaria.producto p " +
                        "JOIN veterinaria.marca m ON m.idmarca = p.marca_idmarca " +
                        "LEFT JOIN LATERAL ( " +
                        "   SELECT pr.* FROM veterinaria.proveedor_has_producto php " +
                        "   JOIN veterinaria.proveedor pr ON pr.idproveedor = php.proveedor_idproveedor " +
                        "   WHERE php.producto_idproducto = p.idproducto " +
                        "   LIMIT 1 " +
                        ") prov ON true " +
                        "WHERE (p.nombre ILIKE ? OR p.referencia ILIKE ? OR p.codigobarras ILIKE ? " +
                        "       OR p.descripcion ILIKE ? OR m.nombremarca ILIKE ? " +
                        "       OR (prov.nombre IS NOT NULL AND prov.nombre ILIKE ?)) "
        );
        if (estadoFilter != null && !estadoFilter.isBlank()) {
            sql.append("AND p.estado = ? ");
        }

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            int i = 1;
            String like = likeParam(q);
            ps.setString(i++, like);
            ps.setString(i++, like);
            ps.setString(i++, like);
            ps.setString(i++, like);
            ps.setString(i++, like);
            ps.setString(i++, like);
            if (estadoFilter != null && !estadoFilter.isBlank()) {
                ps.setString(i++, estadoFilter);
            }

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong("c") : 0L;
            }
        }
    }

    /** ¿Existe por id? */
    public boolean existsById(int idProducto) throws SQLException {
        final String sql = "SELECT 1 FROM veterinaria.producto WHERE idproducto = ? LIMIT 1";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Verifica si existe un producto con el código de barras dado.
     * Útil para evitar duplicados en el registro.
     */
    public boolean existsByCodigoBarras(String codigoBarras) throws SQLException {
        final String sql = "SELECT 1 FROM veterinaria.producto WHERE codigobarras = ? LIMIT 1";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, codigoBarras);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Verifica si existe un producto con la referencia dada.
     * Útil para evitar duplicados en el registro.
     */
    public boolean existsByReferencia(String referencia) throws SQLException {
        final String sql = "SELECT 1 FROM veterinaria.producto WHERE referencia = ? LIMIT 1";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, referencia);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ============================================================
    //                  STOCK / OPERACIONES ATÓMICAS
    // ============================================================

    /**
     * Ajuste atómico de stock: stock = stock + delta.
     * Útil para ventas/ingresos. Devuelve el nuevo stock.
     * Lanza excepción si no existe el producto.
     */
    public int updateStockAtomic(int idProducto, int delta) throws SQLException {
        final String sql =
                "UPDATE veterinaria.producto " +
                        "SET stock = stock + ? " +
                        "WHERE idproducto = ? " +
                        "RETURNING stock";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setBigDecimal(1, BigDecimal.valueOf(delta)); // stock es numeric
            ps.setInt(2, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("Producto no encontrado: " + idProducto);
                return rs.getBigDecimal(1).intValue();
            }
        }
    }

    // ============================================================
    //            RELACIÓN N:N Producto <-> Proveedor
    // ============================================================

    /**
     * Inserta vínculo en la tabla puente veterinaria.proveedor_has_producto.
     *
     * ✅ AJUSTADO: Tu esquema actual SÍ tiene idproveedor_has_producto (IDENTITY).
     * Generamos un ID sintético para evitar conflictos.
     */
    public void linkProveedorProducto(int idProducto, int idProveedor) throws SQLException {
        try (Connection cn = dataSource.getConnection()) {
            linkProveedorProducto(idProducto, idProveedor, cn);
        }
    }

    /** Borra TODOS los vínculos del producto (tabla puente). */
    public void unlinkProveedoresDeProducto(int idProducto) throws SQLException {
        try (Connection cn = dataSource.getConnection()) {
            unlinkProveedoresDeProducto(idProducto, cn);
        }
    }

    // --- versiones internas que reutilizan la misma conexión (para transacciones compuestas) ---

    /**
     * Versión interna de linkProveedorProducto que usa una conexión existente.
     *
     * ✅ AJUSTADO: El esquema actual tiene idproveedor_has_producto (IDENTITY).
     * PostgreSQL no permite especificar valores para columnas IDENTITY,
     * por lo que insertamos solo las FKs y dejamos que la BD genere el ID.
     */
    private void linkProveedorProducto(int idProducto, int idProveedor, Connection cn) throws SQLException {
        final String sql =
                "INSERT INTO veterinaria.proveedor_has_producto " +
                "  (producto_idproducto, proveedor_idproveedor) " +
                "VALUES (?, ?) " +
                "ON CONFLICT DO NOTHING";

        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ps.setInt(2, idProveedor);
            ps.executeUpdate();
        }
    }

    private void unlinkProveedoresDeProducto(int idProducto, Connection cn) throws SQLException {
        final String sql = "DELETE FROM veterinaria.proveedor_has_producto WHERE producto_idproducto = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ps.executeUpdate();
        }
    }
}

