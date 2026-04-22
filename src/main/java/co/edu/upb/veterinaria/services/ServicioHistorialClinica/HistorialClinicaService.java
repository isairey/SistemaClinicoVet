package co.edu.upb.veterinaria.services.ServicioHistorialClinica;

import co.edu.upb.veterinaria.models.ModeloHistorialClinica.HistorialClinica;
import co.edu.upb.veterinaria.models.ModeloServicio.Servicio;
import co.edu.upb.veterinaria.repositories.RepositorioHistoriaClinica.HistoriaClinicaRepository;

import java.sql.SQLException;
import java.util.List;

/**
 * Servicio para gestionar la lógica de negocio de HistorialClinica.
 */
public class HistorialClinicaService {

    private final HistoriaClinicaRepository repository;

    public HistorialClinicaService() {
        this.repository = new HistoriaClinicaRepository();
    }

    /**
     * Obtiene el historial clínico completo de una mascota.
     *
     * @param idMascota ID de la mascota
     * @return HistorialClinica con sus servicios, o null si no existe
     */
    public HistorialClinica obtenerHistorialPorMascota(int idMascota) throws SQLException {
        return repository.obtenerPorMascota(idMascota);
    }

    /**
     * Obtiene todos los servicios asociados a una mascota.
     *
     * @param idMascota ID de la mascota
     * @return Lista de servicios
     */
    public List<Servicio> obtenerServiciosDeMascota(int idMascota) throws SQLException {
        return repository.obtenerServiciosPorMascota(idMascota);
    }

    /**
     * Verifica si una mascota tiene historial clínico registrado.
     *
     * @param idMascota ID de la mascota
     * @return true si tiene historial, false si no
     */
    public boolean tieneHistorial(int idMascota) throws SQLException {
        return repository.existeHistorialParaMascota(idMascota);
    }
}

