package co.edu.upb.veterinaria.repositories.RepositorioLineaVenta;

import co.edu.upb.veterinaria.models.ModeloLineaVenta.LineaVenta;
import co.edu.upb.veterinaria.models.ModeloProducto.Producto;
import co.edu.upb.veterinaria.models.ModeloServicio.Servicio;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio para la gestión de líneas de venta.
 * Implementa el patrón de caja negra con sentencias preparadas (PreparedStatement)
 * para protección contra SQL Injection.
 */
public class LineaVentaRepository {

    private final DataSource dataSource;

    public LineaVentaRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Crea una nueva línea de venta y retorna el ID generado.
     */
    public int create(LineaVenta linea, int idVenta) throws SQLException {
        String sql = "INSERT INTO veterinaria.lineaventa " +
                     "(venta_idventa, producto_idproducto, servicio_idservicio, cantidad, subtotal, valor) " +
                     "VALUES (?, ?, ?, ?, ?, ?) RETURNING idlineaventa";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idVenta);

            if (linea.getProducto() != null) {
                ps.setInt(2, linea.getProducto().getIdProducto());
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            if (linea.getServicio() != null) {
                ps.setInt(3, linea.getServicio().getIdServicio());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            ps.setInt(4, linea.getCantidad());
            ps.setDouble(5, linea.getsubTotal());
            ps.setDouble(6, linea.getValor());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idlineaventa");
                }
            }
        }
        return 0;
    }

    /**
     * Crea una nueva línea de venta usando una conexión existente (para transacciones).
     * Retorna el ID generado.
     */
    public int createWithConnection(Connection conn, LineaVenta linea, int idVenta) throws SQLException {
        String sql = "INSERT INTO veterinaria.lineaventa " +
                     "(venta_idventa, producto_idproducto, servicio_idservicio, cantidad, subtotal, valor) " +
                     "VALUES (?, ?, ?, ?, ?, ?) RETURNING idlineaventa";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idVenta);

            if (linea.getProducto() != null) {
                ps.setInt(2, linea.getProducto().getIdProducto());
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            if (linea.getServicio() != null) {
                ps.setInt(3, linea.getServicio().getIdServicio());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            ps.setInt(4, linea.getCantidad());
            ps.setDouble(5, linea.getsubTotal());
            ps.setDouble(6, linea.getValor());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idlineaventa");
                }
            }
        }
        return 0;
    }

    /**
     * Lista todas las líneas de venta de una venta específica.
     */
    public List<LineaVenta> findByVenta(int idVenta) throws SQLException {
        String sql = "SELECT lv.*, " +
                     "p.nombre AS p_nombre, p.precio AS p_precio, p.referencia AS p_referencia, p.codigobarras AS p_codigobarras, " +
                     "s.nombreservicio AS s_nombre, s.precio AS s_precio " +
                     "FROM veterinaria.lineaventa lv " +
                     "LEFT JOIN veterinaria.producto p ON lv.producto_idproducto = p.idproducto " +
                     "LEFT JOIN veterinaria.servicio s ON lv.servicio_idservicio = s.idservicio " +
                     "WHERE lv.venta_idventa = ?";

        List<LineaVenta> lista = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idVenta);
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapResultSetToLineaVenta(rs));
            }
        } finally {
            // Cerrar explícitamente en orden inverso
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { /* ignorar */ }
            }
            if (ps != null) {
                try { ps.close(); } catch (SQLException e) { /* ignorar */ }
            }
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { /* ignorar */ }
            }
        }

        return lista;
    }

    /**
     * Mapea ResultSet a LineaVenta.
     */
    private LineaVenta mapResultSetToLineaVenta(ResultSet rs) throws SQLException {
        LineaVenta linea = new LineaVenta();
        linea.setIdLineaVenta(rs.getInt("idlineaventa"));
        linea.setCantidad(rs.getInt("cantidad"));
        linea.setsubTotal(rs.getDouble("subtotal"));
        linea.setValor(rs.getDouble("valor"));

        // Producto (si existe)
        int idProducto = rs.getInt("producto_idproducto");
        if (!rs.wasNull()) {
            Producto producto = new Producto();
            producto.setIdProducto(idProducto);
            producto.setNombre(rs.getString("p_nombre"));
            producto.setPrecio(rs.getDouble("p_precio"));
            producto.setReferencia(rs.getString("p_referencia"));
            producto.setCodigoBarras(rs.getString("p_codigobarras"));
            linea.setProducto(producto);
        }

        // Servicio (si existe)
        int idServicio = rs.getInt("servicio_idservicio");
        if (!rs.wasNull()) {
            Servicio servicio = new Servicio();
            servicio.setIdServicio(idServicio);
            servicio.setNombreServicio(rs.getString("s_nombre"));
            servicio.setPrecio(rs.getDouble("s_precio"));
            linea.setServicio(servicio);
        }

        return linea;
    }
}