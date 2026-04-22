package co.edu.upb.veterinaria.repositories.RepositorioAccesorio;

import co.edu.upb.veterinaria.config.DatabaseConfig;
import co.edu.upb.veterinaria.models.ModeloAccesorio.Accesorio;
import co.edu.upb.veterinaria.models.ModeloMarca.Marca;
import co.edu.upb.veterinaria.models.ModeloProveedor.Proveedor;
import co.edu.upb.veterinaria.models.ModeloUsuario.Usuario;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * AccesorioRepository
 * ===================
 * Repositorio para productos tipo ACCESORIO/JUGUETE.
 *
 * CARACTERÍSTICAS:
 * - Caja negra: TODAS las consultas usan PreparedStatement
 * - Seguridad SQL: Protección contra inyección SQL
 * - CRUD completo: Create, Read, Update, Delete
 * - Búsquedas avanzadas: Por nombre, referencia, código, marca, stock
 * - Filtros múltiples: Estado, stock mínimo/máximo, búsqueda de texto
 * - Paginación: limit y offset
 * - Ordenamiento seguro: Whitelist de columnas
 */
public class AccesorioRepository {

    private final DataSource dataSource;
    private static final int TIPO_ACCESORIO = 4; // ID fijo del tipo Accesorio/Juguete

    public AccesorioRepository() {
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

    private Accesorio mapRow(ResultSet rs) throws SQLException {
        Marca marca = new Marca(
            rs.getInt("m_id"),
            rs.getString("m_nombre")
        );

        Usuario usuario = null;
        int uId = rs.getInt("u_id");
        if (!rs.wasNull()) {
            usuario = new Usuario();
            usuario.setIdUsuario(uId);
        }

        Proveedor prov = null;
        int provId = rs.getInt("prov_id");
        if (!rs.wasNull()) {
            prov = new Proveedor();
            prov.setIdProveedor(provId);
            prov.setNombre(rs.getString("prov_nombre"));
        }

        Accesorio acc = new Accesorio();
        acc.setIdProducto(rs.getInt("idproducto"));
        acc.setNombre(rs.getString("nombre"));
        acc.setReferencia(rs.getString("referencia"));
        acc.setCodigoBarras(rs.getString("codigobarras"));
        acc.setPrecio(rs.getBigDecimal("precio").doubleValue());
        acc.setCosto(rs.getBigDecimal("costo").doubleValue());
        acc.setMarca(marca);
        acc.setDescripcion(rs.getString("descripcion"));
        acc.setStock(rs.getBigDecimal("stock").intValue());
        acc.setImagenProducto(rs.getBytes("imagenproducto"));
        acc.setEstado(rs.getString("estado"));
        acc.setProveedor(prov);
        acc.setUsuario(usuario);

        return acc;
    }

    private String buildSelectBase() {
        return """
            SELECT 
                p.idproducto, p.nombre, p.referencia, p.codigobarras, 
                p.precio, p.costo, p.descripcion, p.stock, 
                p.imagenproducto, p.estado,
                m.idmarca AS m_id, m.nombremarca AS m_nombre,
                u.idusuario AS u_id,
                prov.idproveedor AS prov_id, prov.nombre AS prov_nombre
            FROM veterinaria.producto p
            INNER JOIN veterinaria.marca m ON m.idmarca = p.marca_idmarca
            LEFT JOIN veterinaria.usuario u ON u.idusuario = p.usuario_idusuario
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

    public int create(Accesorio acc) throws SQLException {
        final String sql = """
            INSERT INTO veterinaria.producto (
                nombre, precio, costo, referencia, codigobarras, stock, descripcion,
                unidadesingresadas, tipoproducto_idtipoproducto, usuario_idusuario,
                marca_idmarca, imagenproducto, estado
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING idproducto
            """;

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            int i = 1;
            ps.setString(i++, acc.getNombre());
            ps.setBigDecimal(i++, bd(acc.getPrecio()));
            ps.setBigDecimal(i++, bd(acc.getCosto()));
            ps.setString(i++, acc.getReferencia());
            ps.setString(i++, acc.getCodigoBarras());
            ps.setBigDecimal(i++, BigDecimal.valueOf(acc.getStock()));
            ps.setString(i++, acc.getDescripcion());
            ps.setInt(i++, acc.getStock());
            ps.setInt(i++, TIPO_ACCESORIO);

            if (acc.getUsuario() != null) {
                ps.setInt(i++, acc.getUsuario().getIdUsuario());
            } else {
                ps.setNull(i++, Types.INTEGER);
            }

            if (acc.getMarca() != null) {
                ps.setInt(i++, acc.getMarca().getIdMarca());
            } else {
                ps.setNull(i++, Types.INTEGER);
            }

            ps.setBytes(i++, acc.getImagenProducto());
            ps.setString(i, acc.getEstado());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    if (acc.getProveedor() != null) {
                        linkProveedor(newId, acc.getProveedor().getIdProveedor(), cn);
                    }
                    return newId;
                }
            }
            throw new SQLException("No se devolvió idproducto en INSERT accesorio");
        }
    }

    // ============================================================
    //                    CRUD - UPDATE
    // ============================================================

    public boolean update(Accesorio acc, Integer idProveedor) throws SQLException {
        final String sql = """
            UPDATE veterinaria.producto SET
                nombre = ?, precio = ?, costo = ?, referencia = ?, codigobarras = ?,
                stock = ?, descripcion = ?, marca_idmarca = ?,
                imagenproducto = ?, estado = ?
            WHERE idproducto = ? AND tipoproducto_idtipoproducto = ?
            """;

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            int i = 1;
            ps.setString(i++, acc.getNombre());
            ps.setBigDecimal(i++, bd(acc.getPrecio()));
            ps.setBigDecimal(i++, bd(acc.getCosto()));
            ps.setString(i++, acc.getReferencia());
            ps.setString(i++, acc.getCodigoBarras());
            ps.setBigDecimal(i++, BigDecimal.valueOf(acc.getStock()));
            ps.setString(i++, acc.getDescripcion());

            if (acc.getMarca() != null) {
                ps.setInt(i++, acc.getMarca().getIdMarca());
            } else {
                ps.setNull(i++, Types.INTEGER);
            }

            ps.setBytes(i++, acc.getImagenProducto());
            ps.setString(i++, acc.getEstado());
            ps.setInt(i++, acc.getIdProducto());
            ps.setInt(i, TIPO_ACCESORIO);

            boolean ok = ps.executeUpdate() > 0;

            if (ok && idProveedor != null) {
                unlinkProveedores(acc.getIdProducto(), cn);
                if (idProveedor >= 0) {
                    linkProveedor(acc.getIdProducto(), idProveedor, cn);
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
                ps.setInt(2, TIPO_ACCESORIO);
                return ps.executeUpdate() > 0;
            }
        }
    }

    // ============================================================
    //                    CRUD - READ (BÚSQUEDAS)
    // ============================================================

    public Optional<Accesorio> findById(int idProducto) throws SQLException {
        final String sql = buildSelectBase() +
            " WHERE p.idproducto = ? AND p.tipoproducto_idtipoproducto = ?";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ps.setInt(2, TIPO_ACCESORIO);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public Optional<Accesorio> findByCodigoBarras(String codigoBarras) throws SQLException {
        final String sql = buildSelectBase() +
            " WHERE p.codigobarras = ? AND p.tipoproducto_idtipoproducto = ?";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, codigoBarras);
            ps.setInt(2, TIPO_ACCESORIO);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public Optional<Accesorio> findByReferencia(String referencia) throws SQLException {
        final String sql = buildSelectBase() +
            " WHERE p.referencia = ? AND p.tipoproducto_idtipoproducto = ?";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, referencia);
            ps.setInt(2, TIPO_ACCESORIO);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public List<Accesorio> findByNombre(String nombre) throws SQLException {
        final String sql = buildSelectBase() +
            " WHERE p.nombre ILIKE ? AND p.tipoproducto_idtipoproducto = ?";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, likeParam(nombre));
            ps.setInt(2, TIPO_ACCESORIO);

            List<Accesorio> result = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
            return result;
        }
    }

    public List<Accesorio> findByMarca(int idMarca) throws SQLException {
        final String sql = buildSelectBase() +
            " WHERE p.marca_idmarca = ? AND p.tipoproducto_idtipoproducto = ?";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idMarca);
            ps.setInt(2, TIPO_ACCESORIO);

            List<Accesorio> result = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
            return result;
        }
    }

    public List<Accesorio> findByEstado(String estado) throws SQLException {
        final String sql = buildSelectBase() +
            " WHERE p.estado = ? AND p.tipoproducto_idtipoproducto = ?";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, TIPO_ACCESORIO);

            List<Accesorio> result = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
            return result;
        }
    }

    public List<Accesorio> findStockBajo(int umbral) throws SQLException {
        final String sql = buildSelectBase() +
            " WHERE p.stock <= ? AND p.tipoproducto_idtipoproducto = ? ORDER BY p.stock ASC";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, umbral);
            ps.setInt(2, TIPO_ACCESORIO);

            List<Accesorio> result = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
            return result;
        }
    }

    // ============================================================
    //                BÚSQUEDA AVANZADA CON FILTROS
    // ============================================================

    public List<Accesorio> search(
            String q,
            String estadoFilter,
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
            sql.append("      OR p.descripcion ILIKE ? OR m.nombremarca ILIKE ?) ");
        }

        if (estadoFilter != null && !estadoFilter.isBlank()) {
            sql.append(" AND p.estado = ? ");
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
            ps.setInt(i++, TIPO_ACCESORIO);

            if (q != null && !q.isBlank()) {
                String like = likeParam(q);
                ps.setString(i++, like);
                ps.setString(i++, like);
                ps.setString(i++, like);
                ps.setString(i++, like);
                ps.setString(i++, like);
            }

            if (estadoFilter != null && !estadoFilter.isBlank()) {
                ps.setString(i++, estadoFilter);
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

            List<Accesorio> out = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRow(rs));
            }
            return out;
        }
    }

    public long count(
            String q,
            String estadoFilter,
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
            sql.append("      OR p.descripcion ILIKE ? OR m.nombremarca ILIKE ?) ");
        }
        if (estadoFilter != null && !estadoFilter.isBlank()) {
            sql.append(" AND p.estado = ? ");
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
            ps.setInt(i++, TIPO_ACCESORIO);

            if (q != null && !q.isBlank()) {
                String like = likeParam(q);
                ps.setString(i++, like);
                ps.setString(i++, like);
                ps.setString(i++, like);
                ps.setString(i++, like);
                ps.setString(i++, like);
            }

            if (estadoFilter != null && !estadoFilter.isBlank()) {
                ps.setString(i++, estadoFilter);
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
            ps.setInt(2, TIPO_ACCESORIO);
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
            ps.setInt(2, TIPO_ACCESORIO);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt("c") > 0;
            }
        }
    }
}

