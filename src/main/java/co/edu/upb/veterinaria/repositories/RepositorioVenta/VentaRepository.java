package co.edu.upb.veterinaria.repositories.RepositorioVenta;

import co.edu.upb.veterinaria.models.ModeloCliente.Cliente;
import co.edu.upb.veterinaria.models.ModeloVenta.Venta;
import co.edu.upb.veterinaria.models.ModeloUsuario.Usuario;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio para la gestión de ventas.
 * Implementa el patrón de caja negra con sentencias preparadas (PreparedStatement)
 * para protección contra SQL Injection.
 */
public class VentaRepository {

    private final DataSource dataSource;

    public VentaRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Crea una nueva venta y retorna el ID generado.
     * Usa RETURNING de PostgreSQL para obtener el ID.
     */
    public int create(Venta venta) throws SQLException {
        String sql = "INSERT INTO veterinaria.venta " +
                     "(cliente_idcliente, fecha, totalventa, usuario_idusuario) " +
                     "VALUES (?, ?, ?, ?) RETURNING idventa";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, venta.getComprador().getIdCliente());
            ps.setTimestamp(2, new Timestamp(venta.getFecha().getTime()));
            ps.setDouble(3, venta.getTotalVenta());

            if (venta.getUsuario() != null && venta.getUsuario().getIdUsuario() > 0) {
                ps.setInt(4, venta.getUsuario().getIdUsuario());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idventa");
                }
            }
        }
        return 0;
    }

    /**
     * Crea una nueva venta usando una conexión existente (para transacciones).
     * Retorna el ID generado.
     */
    public int createWithConnection(Connection conn, Venta venta) throws SQLException {
        String sql = "INSERT INTO veterinaria.venta " +
                     "(cliente_idcliente, fecha, totalventa, usuario_idusuario) " +
                     "VALUES (?, ?, ?, ?) RETURNING idventa";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, venta.getComprador().getIdCliente());
            ps.setTimestamp(2, new Timestamp(venta.getFecha().getTime()));
            ps.setDouble(3, venta.getTotalVenta());

            if (venta.getUsuario() != null && venta.getUsuario().getIdUsuario() > 0) {
                ps.setInt(4, venta.getUsuario().getIdUsuario());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idventa");
                }
            }
        }
        return 0;
    }

    /**
     * Busca una venta por su ID.
     */
    public Venta findById(int id) throws SQLException {
        String sql = "SELECT v.*, c.nombre AS c_nombre, c.apellidos AS c_apellidos, c.cc AS c_cc " +
                     "FROM veterinaria.venta v " +
                     "LEFT JOIN veterinaria.cliente c ON v.cliente_idcliente = c.idcliente " +
                     "WHERE v.idventa = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVenta(rs);
                }
            }
        }
        return null;
    }

    /**
     * Lista todas las ventas.
     */
    public List<Venta> findAll() throws SQLException {
        String sql = "SELECT v.*, c.nombre AS c_nombre, c.apellidos AS c_apellidos, c.cc AS c_cc " +
                     "FROM veterinaria.venta v " +
                     "LEFT JOIN veterinaria.cliente c ON v.cliente_idcliente = c.idcliente " +
                     "ORDER BY v.fecha DESC";

        List<Venta> lista = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapResultSetToVenta(rs));
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
     * Lista ventas por cliente.
     */
    public List<Venta> findByCliente(int idCliente) throws SQLException {
        String sql = "SELECT v.*, c.nombre AS c_nombre, c.apellidos AS c_apellidos, c.cc AS c_cc " +
                     "FROM veterinaria.venta v " +
                     "LEFT JOIN veterinaria.cliente c ON v.cliente_idcliente = c.idcliente " +
                     "WHERE v.cliente_idcliente = ? " +
                     "ORDER BY v.fecha DESC";

        List<Venta> lista = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSetToVenta(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Mapea ResultSet a Venta.
     */
    private Venta mapResultSetToVenta(ResultSet rs) throws SQLException {
        Venta venta = new Venta();
        venta.setIdVenta(rs.getInt("idventa"));
        venta.setFecha(rs.getTimestamp("fecha"));
        venta.setTotalVenta(rs.getDouble("totalventa"));

        // Cliente
        Cliente cliente = new Cliente();
        cliente.setIdCliente(rs.getInt("cliente_idcliente"));
        cliente.setNombre(rs.getString("c_nombre"));
        cliente.setApellidos(rs.getString("c_apellidos"));
        cliente.setCc(rs.getString("c_cc"));
        venta.setComprador(cliente);

        // Usuario
        int idUsuario = rs.getInt("usuario_idusuario");
        if (!rs.wasNull()) {
            Usuario usuario = new Usuario();
            usuario.setIdUsuario(idUsuario);
            venta.setUsuario(usuario);
        }

        return venta;
    }
}
