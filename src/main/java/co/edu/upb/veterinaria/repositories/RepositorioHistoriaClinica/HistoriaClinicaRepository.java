package co.edu.upb.veterinaria.repositories.RepositorioHistoriaClinica;

import co.edu.upb.veterinaria.config.DatabaseConfig;
import co.edu.upb.veterinaria.models.ModeloHistorialClinica.HistorialClinica;
import co.edu.upb.veterinaria.models.ModeloServicio.Servicio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio para gestionar el acceso a datos de HistorialClinica.
 */
public class HistoriaClinicaRepository {

    /**
     * Obtiene el historial clínico de una mascota específica.
     *
     * @param idMascota ID de la mascota
     * @return HistorialClinica con sus servicios asociados, o null si no existe
     */
    public HistorialClinica obtenerPorMascota(int idMascota) throws SQLException {
        String sql = """
            SELECT idhistorialclinica, observaciones, mascota_idmascota
            FROM veterinaria.historialclinica
            WHERE mascota_idmascota = ?
            LIMIT 1
        """;

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idMascota);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                HistorialClinica historial = new HistorialClinica();
                historial.setIdHistoriaClinica(rs.getInt("idhistorialclinica"));
                historial.setObservaciones(rs.getString("observaciones"));

                // Cargar los servicios asociados
                List<Servicio> servicios = obtenerServiciosPorHistorial(
                    rs.getInt("idhistorialclinica")
                );
                historial.setServicios(servicios);

                return historial;
            }

            return null;
        }
    }

    /**
     * Obtiene todos los servicios asociados a un historial clínico específico.
     *
     * @param idHistorialClinica ID del historial clínico
     * @return Lista de servicios
     */
    public List<Servicio> obtenerServiciosPorHistorial(int idHistorialClinica) throws SQLException {
        List<Servicio> servicios = new ArrayList<>();

        String sql = """
            SELECT s.idservicio, s.nombreservicio, s.precio, s.descripcion
            FROM veterinaria.servicio s
            INNER JOIN veterinaria.historialclinica_has_servicio hcs 
                ON s.idservicio = hcs.servicio_idservicio
            WHERE hcs.historialclinica_idhistorialclinica = ?
            ORDER BY s.nombreservicio
        """;

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idHistorialClinica);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Servicio servicio = new Servicio();
                servicio.setIdServicio(rs.getInt("idservicio"));
                servicio.setNombreServicio(rs.getString("nombreservicio"));
                servicio.setPrecio(rs.getDouble("precio"));
                servicio.setDescripcion(rs.getString("descripcion"));

                servicios.add(servicio);
            }
        }

        return servicios;
    }

    /**
     * Obtiene todos los servicios de una mascota directamente.
     * Útil cuando no existe un historial clínico aún.
     *
     * @param idMascota ID de la mascota
     * @return Lista de servicios asociados
     */
    public List<Servicio> obtenerServiciosPorMascota(int idMascota) throws SQLException {
        List<Servicio> servicios = new ArrayList<>();

        String sql = """
            SELECT s.idservicio, s.nombreservicio, s.precio, s.descripcion
            FROM veterinaria.servicio s
            INNER JOIN veterinaria.historialclinica_has_servicio hcs 
                ON s.idservicio = hcs.servicio_idservicio
            INNER JOIN veterinaria.historialclinica hc 
                ON hcs.historialclinica_idhistorialclinica = hc.idhistorialclinica
            WHERE hc.mascota_idmascota = ?
            ORDER BY s.nombreservicio
        """;

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idMascota);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Servicio servicio = new Servicio();
                servicio.setIdServicio(rs.getInt("idservicio"));
                servicio.setNombreServicio(rs.getString("nombreservicio"));
                servicio.setPrecio(rs.getDouble("precio"));
                servicio.setDescripcion(rs.getString("descripcion"));

                servicios.add(servicio);
            }
        }

        return servicios;
    }

    /**
     * Verifica si una mascota tiene historial clínico.
     *
     * @param idMascota ID de la mascota
     * @return true si existe historial, false si no
     */
    public boolean existeHistorialParaMascota(int idMascota) throws SQLException {
        String sql = "SELECT COUNT(*) FROM veterinaria.historialclinica WHERE mascota_idmascota = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idMascota);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }
}

