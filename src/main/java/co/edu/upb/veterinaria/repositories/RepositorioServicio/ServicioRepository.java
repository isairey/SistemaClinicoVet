package co.edu.upb.veterinaria.repositories.RepositorioServicio;

import co.edu.upb.veterinaria.models.ModeloServicio.Servicio;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio para la gestión de servicios.
 * Implementa el patrón de caja negra con sentencias preparadas (PreparedStatement)
 * para protección contra SQL Injection.
 */
public class ServicioRepository {

    private final DataSource dataSource;

    // Constructor único: recibe DataSource del pool de conexiones
    public ServicioRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Inserta un nuevo servicio y retorna el ID generado.
     * Usa RETURNING de PostgreSQL para obtener el ID sin hacer otra consulta.
     */
    public int create(Servicio servicio) throws SQLException {
        String sql = "INSERT INTO veterinaria.servicio " +
                     "(nombreservicio, precio, descripcion) " +
                     "VALUES (?, ?, ?) RETURNING idservicio";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, servicio.getNombreServicio());
            ps.setDouble(2, servicio.getPrecio());
            ps.setString(3, servicio.getDescripcion());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idservicio");
                }
            }
        }
        return 0;
    }

    /**
     * Verifica si ya existe un servicio con el nombre dado.
     * Usa sentencia parametrizada para evitar SQL Injection.
     */
    public boolean existsByNombre(String nombre) throws SQLException {
        String sql = "SELECT 1 FROM veterinaria.servicio WHERE nombreservicio ILIKE ? LIMIT 1";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Lista todos los servicios en la base de datos.
     * Retorna una lista completa ordenada por ID.
     */
    public List<Servicio> findAll() throws SQLException {
        String sql = "SELECT * FROM veterinaria.servicio ORDER BY idservicio";
        List<Servicio> lista = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapResultSetToServicio(rs));
            }
        }
        return lista;
    }

    /**
     * Busca un servicio por su ID.
     * Retorna null si no existe.
     */
    public Servicio findById(int id) throws SQLException {
        String sql = "SELECT * FROM veterinaria.servicio WHERE idservicio = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToServicio(rs);
                }
            }
        }
        return null;
    }

    /**
     * Actualiza los datos de un servicio existente.
     * Retorna true si se actualizó correctamente.
     */
    public boolean update(Servicio servicio) throws SQLException {
        String sql = "UPDATE veterinaria.servicio SET " +
                     "nombreservicio = ?, precio = ?, descripcion = ? " +
                     "WHERE idservicio = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, servicio.getNombreServicio());
            ps.setDouble(2, servicio.getPrecio());
            ps.setString(3, servicio.getDescripcion());
            ps.setInt(4, servicio.getIdServicio());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Busca servicios por nombre (búsqueda parcial con LIKE).
     * Usa ILIKE para búsqueda insensible a mayúsculas/minúsculas (PostgreSQL).
     */
    public List<Servicio> findByNombre(String nombre) throws SQLException {
        String sql = "SELECT * FROM veterinaria.servicio WHERE nombreservicio ILIKE ? ORDER BY nombreservicio";
        List<Servicio> lista = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSetToServicio(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Elimina un servicio de la base de datos por su ID.
     * NOTA: Esto elimina físicamente el registro - usar con precaución.
     * Retorna true si se eliminó correctamente.
     */
    public boolean delete(int idServicio) throws SQLException {
        String sql = "DELETE FROM veterinaria.servicio WHERE idservicio = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idServicio);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Método auxiliar privado para mapear un ResultSet a un objeto Servicio.
     * Evita duplicación de código.
     */
    private Servicio mapResultSetToServicio(ResultSet rs) throws SQLException {
        Servicio s = new Servicio();
        s.setIdServicio(rs.getInt("idservicio"));
        s.setNombreServicio(rs.getString("nombreservicio"));
        s.setPrecio(rs.getDouble("precio"));
        s.setDescripcion(rs.getString("descripcion"));
        return s;
    }

    // ===== Métodos de compatibilidad con código existente =====

    public int insertarServicio(Servicio servicio) throws SQLException {
        return create(servicio);
    }

    public List<Servicio> listarServicios() throws SQLException {
        return findAll();
    }


}
