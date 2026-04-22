// src/main/java/co/edu/upb/veterinaria/repositories/RepositorioProveedor/ProveedorRepository.java
package co.edu.upb.veterinaria.repositories.RepositorioProveedor;

import co.edu.upb.veterinaria.models.ModeloProveedor.Proveedor;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio para la gestión de proveedores.
 * Implementa el patrón de caja negra con sentencias preparadas (PreparedStatement)
 * para protección contra SQL Injection.
 */
public class ProveedorRepository {

    private final DataSource dataSource;

    // Constructor único: recibe DataSource del pool de conexiones
    public ProveedorRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Inserta un nuevo proveedor y retorna el ID generado.
     * Usa RETURNING de PostgreSQL para obtener el ID sin hacer otra consulta.
     */
    public int create(Proveedor proveedor) throws SQLException {
        String sql = "INSERT INTO veterinaria.proveedor " +
                     "(tipopersona, tipodocumento, nit_rut, nombre, apellido, telefono, direccion, email, ciudad) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING idproveedor";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, proveedor.getTipoPersona());
            ps.setString(2, proveedor.getTipoDocumento());
            ps.setString(3, proveedor.getNit_rut());
            ps.setString(4, proveedor.getNombre());
            ps.setString(5, proveedor.getApellido());
            ps.setString(6, proveedor.getTelefono());
            ps.setString(7, proveedor.getDireccion());
            ps.setString(8, proveedor.getEmail());
            ps.setString(9, proveedor.getCiudad());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idproveedor");
                }
            }
        }
        return 0;
    }

    /**
     * Verifica si ya existe un proveedor con el NIT/RUT dado.
     * Usa sentencia parametrizada para evitar SQL Injection.
     */
    public boolean existsByNit(String nit) throws SQLException {
        String sql = "SELECT 1 FROM veterinaria.proveedor WHERE nit_rut = ? LIMIT 1";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nit);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Lista todos los proveedores en la base de datos.
     * Retorna una lista completa ordenada por ID.
     */
    public List<Proveedor> findAll() throws SQLException {
        String sql = "SELECT * FROM veterinaria.proveedor ORDER BY idproveedor";
        List<Proveedor> lista = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapResultSetToProveedor(rs));
            }
        }
        return lista;
    }

    /**
     * Busca un proveedor por su ID.
     * Retorna null si no existe.
     */
    public Proveedor findById(int id) throws SQLException {
        String sql = "SELECT * FROM veterinaria.proveedor WHERE idproveedor = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProveedor(rs);
                }
            }
        }
        return null;
    }

    /**
     * Actualiza los datos de un proveedor existente.
     * Retorna true si se actualizó correctamente.
     */
    public boolean update(Proveedor proveedor) throws SQLException {
        String sql = "UPDATE veterinaria.proveedor SET " +
                     "tipopersona = ?, tipodocumento = ?, nit_rut = ?, nombre = ?, apellido = ?, " +
                     "telefono = ?, direccion = ?, email = ?, ciudad = ? " +
                     "WHERE idproveedor = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, proveedor.getTipoPersona());
            ps.setString(2, proveedor.getTipoDocumento());
            ps.setString(3, proveedor.getNit_rut());
            ps.setString(4, proveedor.getNombre());
            ps.setString(5, proveedor.getApellido());
            ps.setString(6, proveedor.getTelefono());
            ps.setString(7, proveedor.getDireccion());
            ps.setString(8, proveedor.getEmail());
            ps.setString(9, proveedor.getCiudad());
            ps.setInt(10, proveedor.getIdProveedor());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Busca proveedores por nombre (búsqueda parcial con LIKE).
     * Usa ILIKE para búsqueda insensible a mayúsculas/minúsculas (PostgreSQL).
     */
    public List<Proveedor> findByNombre(String nombre) throws SQLException {
        String sql = "SELECT * FROM veterinaria.proveedor WHERE nombre ILIKE ? ORDER BY nombre";
        List<Proveedor> lista = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSetToProveedor(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Elimina un proveedor de la base de datos por su ID.
     * NOTA: Esto elimina físicamente el registro - usar con precaución.
     * Retorna true si se eliminó correctamente.
     */
    public boolean delete(int idProveedor) throws SQLException {
        String sql = "DELETE FROM veterinaria.proveedor WHERE idproveedor = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idProveedor);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Método auxiliar privado para mapear un ResultSet a un objeto Proveedor.
     * Evita duplicación de código.
     */
    private Proveedor mapResultSetToProveedor(ResultSet rs) throws SQLException {
        Proveedor p = new Proveedor();
        p.setIdProveedor(rs.getInt("idproveedor"));
        p.setTipoPersona(rs.getString("tipopersona"));
        p.setTipoDocumento(rs.getString("tipodocumento")); // Leer de la BD
        p.setNit_rut(rs.getString("nit_rut"));
        p.setNombre(rs.getString("nombre"));
        p.setApellido(rs.getString("apellido"));
        p.setTelefono(rs.getString("telefono"));
        p.setDireccion(rs.getString("direccion"));
        p.setEmail(rs.getString("email"));
        p.setCiudad(rs.getString("ciudad"));
        return p;
    }

    // ===== Métodos de compatibilidad con código existente =====

    public int insertarProveedor(Proveedor proveedor) throws SQLException {
        return create(proveedor);
    }

    public List<Proveedor> listarProveedores() throws SQLException {
        return findAll();
    }
}
