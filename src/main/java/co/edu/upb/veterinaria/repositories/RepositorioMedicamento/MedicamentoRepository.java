package co.edu.upb.veterinaria.repositories.RepositorioMedicamento;

import co.edu.upb.veterinaria.config.DatabaseConfig;
import co.edu.upb.veterinaria.models.ModeloMedicamento.Medicamento;
import co.edu.upb.veterinaria.models.ModeloMarca.Marca;
import co.edu.upb.veterinaria.models.ModeloProveedor.Proveedor;
import co.edu.upb.veterinaria.models.ModeloUnidadMedida.UnidadMedida;
import co.edu.upb.veterinaria.models.ModeloUsuario.Usuario;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * MedicamentoRepository
 * =====================
 * Repositorio especializado para productos tipo MEDICAMENTO.
 *
 * CARACTERÍSTICAS:
 * - Caja negra: TODAS las consultas con PreparedStatement
 * - Campos específicos: lote, fechaVencimiento, dosisPorUnidad, contenido, unidadMedida
 * - Búsquedas por: nombre, lote, fecha vencimiento, dosis
 * - Filtros avanzados: próximos a vencer, stock bajo
 */
public class MedicamentoRepository {

    private final DataSource dataSource;
    private static final int TIPO_MEDICAMENTO = 2; // ID fijo del tipo Medicamento

    public MedicamentoRepository() {
        this.dataSource = DatabaseConfig.getDataSource();
    }

    // ============================================================
    //                    HELPERS DE SEGURIDAD
    // ============================================================

    private BigDecimal bd(double v) {
        return BigDecimal.valueOf(v);
    }

    private String likeParam(String q) {
        return (q == null || q.isBlank()) ? "%" : "%" + q.trim() + "%";
    }

    private Date toUtilDate(java.sql.Date d) {
        return d == null ? null : new Date(d.getTime());
    }

    private java.sql.Date toSqlDate(Date d) {
        return d == null ? null : new java.sql.Date(d.getTime());
    }

    private String validateSortColumn(String sort) {
        if (sort == null) return "p.nombre";
        return switch (sort.toLowerCase()) {
            case "id", "idproducto" -> "p.idproducto";
            case "nombre" -> "p.nombre";
            case "referencia" -> "p.referencia";
            case "codigobarras" -> "p.codigobarras";
            case "precio" -> "p.precio";
            case "costo" -> "p.costo";
            case "stock" -> "p.stock";
            case "estado" -> "p.estado";
            case "lote" -> "p.lote";
            case "fechavencimiento" -> "p.\"fechaVencimiento\"";
            case "contenido" -> "p.contenido";
            case "dosisunidad" -> "p.\"dosisUnidad\"";
            case "marca" -> "m.nombremarca";
            default -> "p.nombre";
        };
    }

    private String sortDir(boolean asc) {
        return asc ? "ASC" : "DESC";
    }

    // ============================================================
    //                    MAPEO RESULTSET → MODELO
    // ============================================================

    private Medicamento mapRow(ResultSet rs) throws SQLException {
        Marca marca = new Marca(rs.getInt("m_id"), rs.getString("m_nombre"));

        Usuario usuario = null;
        int uId = rs.getInt("u_id");
        if (!rs.wasNull()) {
            usuario = new Usuario();
            usuario.setIdUsuario(uId);
        }

        UnidadMedida um = null;
        int umId = rs.getInt("um_id");
        if (!rs.wasNull()) {
            um = new UnidadMedida(umId, rs.getString("um_nombre"));
        }

        Proveedor prov = null;
        int provId = rs.getInt("prov_id");
        if (!rs.wasNull()) {
            prov = new Proveedor();
            prov.setIdProveedor(provId);
            prov.setNombre(rs.getString("prov_nombre"));
        }

        Medicamento m = new Medicamento();
        m.setIdProducto(rs.getInt("idproducto"));
        m.setNombre(rs.getString("nombre"));
        m.setReferencia(rs.getString("referencia"));
        m.setCodigoBarras(rs.getString("codigobarras"));
        m.setPrecio(rs.getBigDecimal("precio").doubleValue());
        m.setCosto(rs.getBigDecimal("costo").doubleValue());
        m.setMarca(marca);
        m.setDescripcion(rs.getString("descripcion"));
        m.setStock(rs.getBigDecimal("stock").intValue());
        m.setImagenProducto(rs.getBytes("imagenproducto"));
        m.setEstado(rs.getString("estado"));
        m.setProveedor(prov);
        m.setUsuario(usuario);

        m.setLote(rs.getString("lote"));
        m.setFechaVencimiento(toUtilDate(rs.getDate("fechaVencimiento")));
        m.setSemanasParaAlerta(rs.getInt("semanaalerta"));
        m.setFraccionable(rs.getBoolean("fraccionable"));
        m.setFraccionado(rs.getBoolean("fraccionado"));
        BigDecimal contenido = rs.getBigDecimal("contenido");
        m.setContenido(contenido != null ? contenido.doubleValue() : 0.0);
        m.setUnidadmedida(um);

        String dosisUnidad = rs.getString("dosisUnidad");
        if (dosisUnidad != null && !dosisUnidad.isBlank()) {
            try {
                m.setDosisPorUnidad(Double.parseDouble(dosisUnidad));
            } catch (NumberFormatException e) {
                m.setDosisPorUnidad(0.0);
            }
        }

        return m;
    }

    private String buildSelectBase() {
        return """
            SELECT 
                p.idproducto, p.nombre, p.referencia, p.codigobarras, 
                p.precio, p.costo, p.descripcion, p.stock, 
                p.imagenproducto, p.estado,
                p.lote, p."fechaVencimiento", p.semanaalerta,
                p.fraccionable, p.fraccionado, p.contenido, p."dosisUnidad",
                m.idmarca AS m_id, m.nombremarca AS m_nombre,
                u.idusuario AS u_id,
                um.idunidadmedida AS um_id, um.nombre AS um_nombre,
                prov.idproveedor AS prov_id, prov.nombre AS prov_nombre
            FROM veterinaria.producto p
            INNER JOIN veterinaria.marca m ON m.idmarca = p.marca_idmarca
            LEFT JOIN veterinaria.usuario u ON u.idusuario = p.usuario_idusuario
            LEFT JOIN veterinaria.unidadmedida um ON um.idunidadmedida = p.unidadmedida_idunidadmedida
            LEFT JOIN LATERAL (
                SELECT pr.idproveedor, pr.nombre
                FROM veterinaria.proveedor_has_producto php
                JOIN veterinaria.proveedor pr ON pr.idproveedor = php.proveedor_idproveedor
                WHERE php.producto_idproducto = p.idproducto
                LIMIT 1
            ) prov ON true
            """;
    }

    // ============================================================
    //                    CRUD - CREATE
    // ============================================================

    public int create(Medicamento m) throws SQLException {
        final String sql = """
            INSERT INTO veterinaria.producto (
                nombre, precio, costo, referencia, codigobarras, stock, descripcion,
                unidadesingresadas, tipoproducto_idtipoproducto, usuario_idusuario,
                marca_idmarca, unidadmedida_idunidadmedida, imagenproducto, estado,
                lote, "fechaVencimiento", semanaalerta, fraccionable, fraccionado, contenido, "dosisUnidad"
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING idproducto
            """;

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            int i = 1;
            ps.setString(i++, m.getNombre());
            ps.setBigDecimal(i++, bd(m.getPrecio()));
            ps.setBigDecimal(i++, bd(m.getCosto()));
            ps.setString(i++, m.getReferencia());
            ps.setString(i++, m.getCodigoBarras());
            ps.setBigDecimal(i++, BigDecimal.valueOf(m.getStock()));
            ps.setString(i++, m.getDescripcion());
            ps.setInt(i++, m.getStock());
            ps.setInt(i++, TIPO_MEDICAMENTO);

            if (m.getUsuario() != null) {
                ps.setInt(i++, m.getUsuario().getIdUsuario());
            } else {
                ps.setNull(i++, Types.INTEGER);
            }

            if (m.getMarca() != null) {
                ps.setInt(i++, m.getMarca().getIdMarca());
            } else {
                ps.setNull(i++, Types.INTEGER);
            }

            if (m.getUnidadMedida() != null) {
                ps.setInt(i++, m.getUnidadMedida().getIdUnidadMedida());
            } else {
                ps.setNull(i++, Types.INTEGER);
            }

            ps.setBytes(i++, m.getImagenProducto());
            ps.setString(i++, m.getEstado());

            ps.setString(i++, m.getLote());
            if (m.getFechaVencimiento() != null) {
                ps.setDate(i++, toSqlDate(m.getFechaVencimiento()));
            } else {
                ps.setNull(i++, Types.DATE);
            }
            ps.setInt(i++, m.getSemanasParaAlerta());
            ps.setBoolean(i++, m.isFraccionable());
            ps.setBoolean(i++, m.isFraccionado());
            ps.setBigDecimal(i++, bd(m.getContenido()));
            ps.setString(i, String.valueOf(m.getDosisPorUnidad()));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    if (m.getProveedor() != null) {
                        linkProveedor(newId, m.getProveedor().getIdProveedor(), cn);
                    }
                    return newId;
                }
            }
            throw new SQLException("No se devolvió idproducto en INSERT medicamento");
        }
    }

    // ============================================================
    //                    CRUD - UPDATE
    // ============================================================

    public boolean update(Medicamento m, Integer idProveedor) throws SQLException {
        final String sql = """
            UPDATE veterinaria.producto SET
                nombre = ?, precio = ?, costo = ?, referencia = ?, codigobarras = ?,
                stock = ?, descripcion = ?, marca_idmarca = ?, unidadmedida_idunidadmedida = ?,
                imagenproducto = ?, estado = ?,
                lote = ?, "fechaVencimiento" = ?, semanaalerta = ?,
                fraccionable = ?, fraccionado = ?, contenido = ?, "dosisUnidad" = ?
            WHERE idproducto = ? AND tipoproducto_idtipoproducto = ?
            """;

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            int i = 1;
            ps.setString(i++, m.getNombre());
            ps.setBigDecimal(i++, bd(m.getPrecio()));
            ps.setBigDecimal(i++, bd(m.getCosto()));
            ps.setString(i++, m.getReferencia());
            ps.setString(i++, m.getCodigoBarras());
            ps.setBigDecimal(i++, BigDecimal.valueOf(m.getStock()));
            ps.setString(i++, m.getDescripcion());

            if (m.getMarca() != null) {
                ps.setInt(i++, m.getMarca().getIdMarca());
            } else {
                ps.setNull(i++, Types.INTEGER);
            }

            if (m.getUnidadMedida() != null) {
                ps.setInt(i++, m.getUnidadMedida().getIdUnidadMedida());
            } else {
                ps.setNull(i++, Types.INTEGER);
            }

            ps.setBytes(i++, m.getImagenProducto());
            ps.setString(i++, m.getEstado());

            ps.setString(i++, m.getLote());
            if (m.getFechaVencimiento() != null) {
                ps.setDate(i++, toSqlDate(m.getFechaVencimiento()));
            } else {
                ps.setNull(i++, Types.DATE);
            }
            ps.setInt(i++, m.getSemanasParaAlerta());
            ps.setBoolean(i++, m.isFraccionable());
            ps.setBoolean(i++, m.isFraccionado());
            ps.setBigDecimal(i++, bd(m.getContenido()));
            ps.setString(i++, String.valueOf(m.getDosisPorUnidad()));

            ps.setInt(i++, m.getIdProducto());
            ps.setInt(i, TIPO_MEDICAMENTO);

            boolean ok = ps.executeUpdate() > 0;

            if (ok && idProveedor != null) {
                unlinkProveedores(m.getIdProducto(), cn);
                if (idProveedor >= 0) {
                    linkProveedor(m.getIdProducto(), idProveedor, cn);
                }
            }
            return ok;
        }
    }

    // ============================================================
    //                    CRUD - DELETE
    // ============================================================

    public boolean delete(int idProducto) throws SQLException {
        try (Connection cn = dataSource.getConnection()) {
            unlinkProveedores(idProducto, cn);

            final String sql = """
                DELETE FROM veterinaria.producto 
                WHERE idproducto = ? AND tipoproducto_idtipoproducto = ?
                """;
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setInt(1, idProducto);
                ps.setInt(2, TIPO_MEDICAMENTO);
                return ps.executeUpdate() > 0;
            }
        }
    }

    // ============================================================
    //                    CRUD - READ (BÚSQUEDAS)
    // ============================================================

    public Optional<Medicamento> findById(int idProducto) throws SQLException {
        final String sql = buildSelectBase() +
            " WHERE p.idproducto = ? AND p.tipoproducto_idtipoproducto = ?";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ps.setInt(2, TIPO_MEDICAMENTO);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public Optional<Medicamento> findByCodigoBarras(String codigoBarras) throws SQLException {
        final String sql = buildSelectBase() +
            " WHERE p.codigobarras = ? AND p.tipoproducto_idtipoproducto = ?";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, codigoBarras);
            ps.setInt(2, TIPO_MEDICAMENTO);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public Optional<Medicamento> findByReferencia(String referencia) throws SQLException {
        final String sql = buildSelectBase() +
            " WHERE p.referencia = ? AND p.tipoproducto_idtipoproducto = ?";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, referencia);
            ps.setInt(2, TIPO_MEDICAMENTO);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public List<Medicamento> findByLote(String lote) throws SQLException {
        final String sql = buildSelectBase() +
            " WHERE p.lote = ? AND p.tipoproducto_idtipoproducto = ?";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, lote);
            ps.setInt(2, TIPO_MEDICAMENTO);

            List<Medicamento> result = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
            return result;
        }
    }

    public List<Medicamento> findByMarca(int idMarca) throws SQLException {
        final String sql = buildSelectBase() +
            " WHERE p.marca_idmarca = ? AND p.tipoproducto_idtipoproducto = ?";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idMarca);
            ps.setInt(2, TIPO_MEDICAMENTO);

            List<Medicamento> result = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
            return result;
        }
    }

    public List<Medicamento> findByEstado(String estado) throws SQLException {
        final String sql = buildSelectBase() +
            " WHERE p.estado = ? AND p.tipoproducto_idtipoproducto = ?";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, TIPO_MEDICAMENTO);

            List<Medicamento> result = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
            return result;
        }
    }

    public List<Medicamento> findStockBajo(int umbral) throws SQLException {
        final String sql = buildSelectBase() +
            " WHERE p.stock <= ? AND p.tipoproducto_idtipoproducto = ? ORDER BY p.stock ASC";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, umbral);
            ps.setInt(2, TIPO_MEDICAMENTO);

            List<Medicamento> result = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
            return result;
        }
    }

    public List<Medicamento> findProximosAVencer(int diasUmbral) throws SQLException {
        final String sql = buildSelectBase() + """
             WHERE p.tipoproducto_idtipoproducto = ? 
             AND p."fechaVencimiento" IS NOT NULL
             AND p."fechaVencimiento" <= CURRENT_DATE + INTERVAL '? days'
             AND p."fechaVencimiento" >= CURRENT_DATE
             ORDER BY p."fechaVencimiento" ASC
            """;

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, TIPO_MEDICAMENTO);
            ps.setInt(2, diasUmbral);

            List<Medicamento> result = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
            return result;
        }
    }

    // ============================================================
    //                BÚSQUEDA AVANZADA CON FILTROS
    // ============================================================

    public List<Medicamento> search(
            String q,
            String estadoFilter,
            String lote,
            Date fechaVencimientoDesde,
            Date fechaVencimientoHasta,
            Boolean fraccionable,
            Integer stockMinimo,
            Integer stockMaximo,
            String sort,
            boolean asc,
            int limit,
            int offset) throws SQLException {

        StringBuilder sql = new StringBuilder(buildSelectBase());
        sql.append(" WHERE p.tipoproducto_idtipoproducto = ? ");

        if (q != null && !q.isBlank()) {
            sql.append(" AND (p.nombre ILIKE ? OR p.referencia ILIKE ? OR p.codigobarras ILIKE ? ");
            sql.append("      OR p.descripcion ILIKE ? OR p.lote ILIKE ? OR m.nombremarca ILIKE ?) ");
        }

        if (estadoFilter != null && !estadoFilter.isBlank()) {
            sql.append(" AND p.estado = ? ");
        }

        if (lote != null && !lote.isBlank()) {
            sql.append(" AND p.lote = ? ");
        }

        if (fechaVencimientoDesde != null) {
            sql.append(" AND p.\"fechaVencimiento\" >= ? ");
        }

        if (fechaVencimientoHasta != null) {
            sql.append(" AND p.\"fechaVencimiento\" <= ? ");
        }

        if (fraccionable != null) {
            sql.append(" AND p.fraccionable = ? ");
        }

        if (stockMinimo != null) {
            sql.append(" AND p.stock >= ? ");
        }
        if (stockMaximo != null) {
            sql.append(" AND p.stock <= ? ");
        }

        String sortCol = validateSortColumn(sort);
        sql.append(" ORDER BY ").append(sortCol).append(" ").append(sortDir(asc));

        if (limit > 0) {
            sql.append(" LIMIT ? OFFSET ? ");
        }

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            int i = 1;
            ps.setInt(i++, TIPO_MEDICAMENTO);

            if (q != null && !q.isBlank()) {
                String like = likeParam(q);
                ps.setString(i++, like);
                ps.setString(i++, like);
                ps.setString(i++, like);
                ps.setString(i++, like);
                ps.setString(i++, like);
                ps.setString(i++, like);
            }

            if (estadoFilter != null && !estadoFilter.isBlank()) {
                ps.setString(i++, estadoFilter);
            }

            if (lote != null && !lote.isBlank()) {
                ps.setString(i++, lote);
            }

            if (fechaVencimientoDesde != null) {
                ps.setDate(i++, toSqlDate(fechaVencimientoDesde));
            }

            if (fechaVencimientoHasta != null) {
                ps.setDate(i++, toSqlDate(fechaVencimientoHasta));
            }

            if (fraccionable != null) {
                ps.setBoolean(i++, fraccionable);
            }

            if (stockMinimo != null) {
                ps.setInt(i++, stockMinimo);
            }
            if (stockMaximo != null) {
                ps.setInt(i++, stockMaximo);
            }

            if (limit > 0) {
                ps.setInt(i++, limit);
                ps.setInt(i, Math.max(0, offset));
            }

            List<Medicamento> out = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRow(rs));
            }
            return out;
        }
    }

    public long count(
            String q,
            String estadoFilter,
            String lote,
            Date fechaVencimientoDesde,
            Date fechaVencimientoHasta,
            Boolean fraccionable,
            Integer stockMinimo,
            Integer stockMaximo) throws SQLException {

        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*) AS c
            FROM veterinaria.producto p
            JOIN veterinaria.marca m ON m.idmarca = p.marca_idmarca
            WHERE p.tipoproducto_idtipoproducto = ?
            """);

        if (q != null && !q.isBlank()) {
            sql.append(" AND (p.nombre ILIKE ? OR p.referencia ILIKE ? OR p.codigobarras ILIKE ? ");
            sql.append("      OR p.descripcion ILIKE ? OR p.lote ILIKE ? OR m.nombremarca ILIKE ?) ");
        }
        if (estadoFilter != null && !estadoFilter.isBlank()) {
            sql.append(" AND p.estado = ? ");
        }
        if (lote != null && !lote.isBlank()) {
            sql.append(" AND p.lote = ? ");
        }
        if (fechaVencimientoDesde != null) {
            sql.append(" AND p.\"fechaVencimiento\" >= ? ");
        }
        if (fechaVencimientoHasta != null) {
            sql.append(" AND p.\"fechaVencimiento\" <= ? ");
        }
        if (fraccionable != null) {
            sql.append(" AND p.fraccionable = ? ");
        }
        if (stockMinimo != null) {
            sql.append(" AND p.stock >= ? ");
        }
        if (stockMaximo != null) {
            sql.append(" AND p.stock <= ? ");
        }

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            int i = 1;
            ps.setInt(i++, TIPO_MEDICAMENTO);

            if (q != null && !q.isBlank()) {
                String like = likeParam(q);
                ps.setString(i++, like);
                ps.setString(i++, like);
                ps.setString(i++, like);
                ps.setString(i++, like);
                ps.setString(i++, like);
                ps.setString(i++, like);
            }

            if (estadoFilter != null && !estadoFilter.isBlank()) {
                ps.setString(i++, estadoFilter);
            }

            if (lote != null && !lote.isBlank()) {
                ps.setString(i++, lote);
            }

            if (fechaVencimientoDesde != null) {
                ps.setDate(i++, toSqlDate(fechaVencimientoDesde));
            }

            if (fechaVencimientoHasta != null) {
                ps.setDate(i++, toSqlDate(fechaVencimientoHasta));
            }

            if (fraccionable != null) {
                ps.setBoolean(i++, fraccionable);
            }

            if (stockMinimo != null) {
                ps.setInt(i++, stockMinimo);
            }
            if (stockMaximo != null) {
                ps.setInt(i, stockMaximo);
            }

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong("c") : 0L;
            }
        }
    }

    // ============================================================
    //            MÉTODOS AUXILIARES (PROVEEDORES)
    // ============================================================

    private void linkProveedor(int idProducto, int idProveedor, Connection cn) throws SQLException {
        final String sql = """
            INSERT INTO veterinaria.proveedor_has_producto (producto_idproducto, proveedor_idproveedor)
            VALUES (?, ?)
            """;
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ps.setInt(2, idProveedor);
            ps.executeUpdate();
        }
    }

    private void unlinkProveedores(int idProducto, Connection cn) throws SQLException {
        final String sql = "DELETE FROM veterinaria.proveedor_has_producto WHERE producto_idproducto = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ps.executeUpdate();
        }
    }

    // ============================================================
    //                VALIDACIONES DE EXISTENCIA
    // ============================================================

    public boolean existsByCodigoBarras(String codigoBarras) throws SQLException {
        final String sql = """
            SELECT COUNT(*) AS c FROM veterinaria.producto 
            WHERE codigobarras = ? AND tipoproducto_idtipoproducto = ?
            """;
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, codigoBarras);
            ps.setInt(2, TIPO_MEDICAMENTO);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt("c") > 0;
            }
        }
    }

    public boolean existsByReferencia(String referencia) throws SQLException {
        final String sql = """
            SELECT COUNT(*) AS c FROM veterinaria.producto 
            WHERE referencia = ? AND tipoproducto_idtipoproducto = ?
            """;
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, referencia);
            ps.setInt(2, TIPO_MEDICAMENTO);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt("c") > 0;
            }
        }
    }
}

