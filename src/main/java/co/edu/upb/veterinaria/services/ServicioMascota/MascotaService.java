package co.edu.upb.veterinaria.services.ServicioMascota;

import co.edu.upb.veterinaria.models.ModeloMascota.Mascota;
import co.edu.upb.veterinaria.repositories.RepositorioMascota.MascotaRepository;
import co.edu.upb.veterinaria.repositories.RepositorioMascota.MascotaRepositoryImpl;

import java.util.List;
import java.util.Optional;

public class MascotaService {

    private final MascotaRepository mascotaRepository;

    public MascotaService() {
        this.mascotaRepository = new MascotaRepositoryImpl();
    }

    /**
     * Registra una nueva mascota en la base de datos (sin cliente asignado).
     * @param mascota La mascota a registrar
     * @return El ID de la mascota registrada
     * @throws IllegalArgumentException Si los datos no son válidos
     */
    public int registrarMascota(Mascota mascota) {
        validarMascota(mascota);
        return mascotaRepository.create(mascota);
    }

    /**
     * Busca una mascota por su ID.
     */
    public Optional<Mascota> buscarPorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        return mascotaRepository.findById(id);
    }

    /**
     * Busca todas las mascotas de un cliente específico.
     */
    public List<Mascota> buscarPorCliente(int clienteId) {
        if (clienteId <= 0) {
            throw new IllegalArgumentException("ID de cliente inválido");
        }
        return mascotaRepository.findByClienteId(clienteId);
    }

    /**
     * Obtiene todas las mascotas que no tienen cliente asignado (disponibles).
     */
    public List<Mascota> obtenerMascotasDisponibles() {
        return mascotaRepository.findSinCliente();
    }

    /**
     * Lista todas las mascotas con paginación.
     */
    public List<Mascota> listarTodas(int limit, int offset) {
        return mascotaRepository.findAll(limit, offset);
    }

    /**
     * Actualiza los datos de una mascota existente.
     */
    public boolean actualizarMascota(Mascota mascota) {
        validarMascota(mascota);
        if (mascota.getIdMascota() <= 0) {
            throw new IllegalArgumentException("La mascota debe tener un ID válido");
        }
        return mascotaRepository.update(mascota);
    }

    /**
     * Asigna una mascota a un cliente específico.
     */
    public boolean asignarMascotaACliente(int idMascota, int idCliente) {
        if (idMascota <= 0) {
            throw new IllegalArgumentException("ID de mascota inválido");
        }
        if (idCliente <= 0) {
            throw new IllegalArgumentException("ID de cliente inválido");
        }
        return mascotaRepository.asignarCliente(idMascota, idCliente);
    }

    /**
     * Elimina una mascota por su ID.
     */
    public boolean eliminarMascota(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        return mascotaRepository.delete(id);
    }

    /**
     * Cuenta el total de mascotas registradas.
     */
    public int contarMascotas() {
        return mascotaRepository.count();
    }

    /**
     * Valida que los datos de la mascota sean correctos.
     */
    private void validarMascota(Mascota mascota) {
        if (mascota == null) {
            throw new IllegalArgumentException("La mascota no puede ser nula");
        }

        if (mascota.getNombre() == null || mascota.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la mascota es obligatorio");
        }

        if (mascota.getEspecie() == null || mascota.getEspecie().trim().isEmpty()) {
            throw new IllegalArgumentException("La especie es obligatoria");
        }

        if (mascota.getRaza() == null || mascota.getRaza().trim().isEmpty()) {
            throw new IllegalArgumentException("La raza es obligatoria");
        }

        if (mascota.getEdad() < 0) {
            throw new IllegalArgumentException("La edad no puede ser negativa");
        }

        if (mascota.getSexo() != 'M' && mascota.getSexo() != 'F' && mascota.getSexo() != 'N') {
            throw new IllegalArgumentException("El sexo debe ser M (Macho), F (Hembra) o N (No especificado)");
        }
    }
}
