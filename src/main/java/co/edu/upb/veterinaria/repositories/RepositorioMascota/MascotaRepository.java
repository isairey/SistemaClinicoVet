package co.edu.upb.veterinaria.repositories.RepositorioMascota;

import co.edu.upb.veterinaria.models.ModeloMascota.Mascota;
import java.util.List;
import java.util.Optional;

public interface MascotaRepository {

    // Crear una mascota (puede ser sin cliente asignado)
    int create(Mascota mascota);

    // Buscar mascota por ID
    Optional<Mascota> findById(int id);

    // Buscar mascotas por cliente
    List<Mascota> findByClienteId(int clienteId);

    // Buscar mascotas sin cliente asignado (disponibles)
    List<Mascota> findSinCliente();

    // Listar todas las mascotas
    List<Mascota> findAll(int limit, int offset);

    // Actualizar mascota
    boolean update(Mascota mascota);

    // Asignar mascota a un cliente
    boolean asignarCliente(int idMascota, int idCliente);

    // Eliminar mascota
    boolean delete(int id);

    // Contar mascotas
    int count();

    // Verificar si existe por número de chip
    boolean existsByNumeroChip(String numeroChip);
}
