package co.edu.upb.veterinaria.services.ServicioServicio;

import co.edu.upb.veterinaria.models.ModeloServicio.Servicio;
import co.edu.upb.veterinaria.repositories.RepositorioServicio.ServicioRepository;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * Capa de servicio para la gestión de servicios.
 * Contiene la lógica de negocio y validaciones.
 */
public class ServicioService {

    private final ServicioRepository repository;

    public ServicioService(DataSource dataSource) {
        this.repository = new ServicioRepository(dataSource);
    }

    /**
     * Crea un nuevo servicio con validaciones.
     * @throws IllegalArgumentException si los datos son inválidos
     * @throws SQLException si hay error en la base de datos
     */
    public int crearServicio(Servicio servicio) throws SQLException {
        // Validaciones
        if (servicio.getNombreServicio() == null || servicio.getNombreServicio().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del servicio es obligatorio");
        }

        if (servicio.getPrecio() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }

        // Verificar si ya existe un servicio con ese nombre
        if (repository.existsByNombre(servicio.getNombreServicio())) {
            throw new IllegalArgumentException("Ya existe un servicio con ese nombre");
        }

        return repository.create(servicio);
    }

    /**
     * Lista todos los servicios disponibles.
     */
    public List<Servicio> listarTodosLosServicios() throws SQLException {
        return repository.findAll();
    }

    /**
     * Busca un servicio por su ID.
     */
    public Servicio buscarPorId(int id) throws SQLException {
        return repository.findById(id);
    }

    /**
     * Busca servicios por nombre (búsqueda parcial).
     */
    public List<Servicio> buscarPorNombre(String nombre) throws SQLException {
        return repository.findByNombre(nombre);
    }

    /**
     * Actualiza un servicio existente.
     */
    public boolean actualizarServicio(Servicio servicio) throws SQLException {
        if (servicio.getNombreServicio() == null || servicio.getNombreServicio().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del servicio es obligatorio");
        }

        if (servicio.getPrecio() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }

        return repository.update(servicio);
    }



    /**
     * Elimina un servicio por su ID.
     */
    public boolean eliminarServicio(int idServicio) throws SQLException {
        return repository.delete(idServicio);
    }
}
