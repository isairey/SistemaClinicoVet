package co.edu.upb.veterinaria.services.ServicioVenta;

import co.edu.upb.veterinaria.config.DatabaseConfig;
import co.edu.upb.veterinaria.models.ModeloLineaVenta.LineaVenta;
import co.edu.upb.veterinaria.models.ModeloVenta.Venta;
import co.edu.upb.veterinaria.repositories.RepositorioLineaVenta.LineaVentaRepository;
import co.edu.upb.veterinaria.repositories.RepositorioVenta.VentaRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Servicio para la gestión de ventas.
 * Maneja la lógica de negocio y coordina repositorios.
 */
public class VentaService {

    private final VentaRepository ventaRepository;
    private final LineaVentaRepository lineaVentaRepository;
    private final DataSource dataSource;

    public VentaService() {
        this.dataSource = DatabaseConfig.getDataSource();
        this.ventaRepository = new VentaRepository(dataSource);
        this.lineaVentaRepository = new LineaVentaRepository(dataSource);
    }

    /**
     * Crea una venta completa con sus líneas de venta.
     * Reduce el stock de los productos vendidos.
     * Todo en una transacción para garantizar consistencia.
     */
    public int crearVenta(Venta venta) throws SQLException {
        Connection conn = null;
        boolean autoCommitOriginal = true;

        try {
            conn = dataSource.getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);

            // 1. Crear la venta usando la misma conexión
            int idVenta = ventaRepository.createWithConnection(conn, venta);

            if (idVenta <= 0) {
                conn.rollback();
                throw new SQLException("No se pudo crear la venta");
            }

            // 2. Crear las líneas de venta y actualizar stock
            if (venta.getLineasVenta() != null && !venta.getLineasVenta().isEmpty()) {
                for (LineaVenta linea : venta.getLineasVenta()) {
                    // Crear línea de venta usando la misma conexión
                    lineaVentaRepository.createWithConnection(conn, linea, idVenta);

                    // Si es un producto, reducir stock
                    if (linea.getProducto() != null) {
                        reducirStock(conn, linea.getProducto().getIdProducto(), linea.getCantidad());
                    }
                }
            }

            conn.commit();
            return idVenta;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    // Ignorar error de rollback
                }
            }
            throw new SQLException("Error al crear la venta: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(autoCommitOriginal);
                    conn.close();
                } catch (SQLException e) {
                    // Ignorar error al cerrar
                }
            }
        }
    }

    /**
     * Reduce el stock de un producto.
     * Método interno para usar dentro de transacciones.
     */
    private void reducirStock(Connection conn, int idProducto, int cantidad) throws SQLException {
        String sql = "UPDATE veterinaria.producto " +
                     "SET stock = stock - ? " +
                     "WHERE idproducto = ? AND stock >= ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, idProducto);
            ps.setInt(3, cantidad);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Stock insuficiente para el producto ID: " + idProducto);
            }
        }
    }

    /**
     * Busca una venta por ID.
     */
    public Venta buscarPorId(int id) throws SQLException {
        Venta venta = ventaRepository.findById(id);
        if (venta != null) {
            List<LineaVenta> lineas = lineaVentaRepository.findByVenta(id);
            venta.setLineasVenta(lineas);
        }
        return venta;
    }

    /**
     * Lista todas las ventas.
     */
    public List<Venta> listarTodas() throws SQLException {
        return ventaRepository.findAll();
    }

    /**
     * Lista ventas por cliente.
     */
    public List<Venta> listarPorCliente(int idCliente) throws SQLException {
        return ventaRepository.findByCliente(idCliente);
    }
}
