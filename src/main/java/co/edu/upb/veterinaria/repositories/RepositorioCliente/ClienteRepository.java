package co.edu.upb.veterinaria.repositories.RepositorioCliente;

import co.edu.upb.veterinaria.models.ModeloCliente.Cliente;
import java.util.*;

public interface ClienteRepository {

    // Crea un cliente (y opcionalmente sus mascotas) en transacción
    int create(Cliente c, boolean incluirMascotas);

    // Lecturas
    Optional<Cliente> findById(int id, boolean incluirMascotas);
    Optional<Cliente> findByCc(String cc, boolean incluirMascotas);
    List<Cliente> findAll(int limit, int offset);

    // Actualiza datos (no cambia activo)
    boolean update(Cliente c);

    // Utilidades
    boolean existsByCc(String cc);
    int count();
}
