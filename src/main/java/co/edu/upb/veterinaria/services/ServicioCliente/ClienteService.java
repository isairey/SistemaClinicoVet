package co.edu.upb.veterinaria.services.ServicioCliente;

import co.edu.upb.veterinaria.models.ModeloCliente.Cliente;
import co.edu.upb.veterinaria.repositories.RepositorioCliente.ClienteRepository;
import co.edu.upb.veterinaria.repositories.RepositorioCliente.ClienteRepositoryImpl;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de clientes.
 * Capa de lógica de negocio entre el controlador y el repositorio.
 */
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService() {
        this.clienteRepository = new ClienteRepositoryImpl();
    }

    /**
     * Registra un nuevo cliente con sus mascotas en la base de datos.
     *
     * @param cliente Cliente a registrar (con lista de mascotas)
     * @return ID del cliente creado
     * @throws IllegalArgumentException si los datos son inválidos
     * @throws RuntimeException si hay error en la BD
     */
    public int registrarCliente(Cliente cliente) {
        validarCliente(cliente);

        // Verificar si ya existe un cliente con ese documento
        if (clienteRepository.existsByCc(cliente.getCc())) {
            throw new IllegalArgumentException(
                "Ya existe un cliente registrado con el documento: " + cliente.getCc()
            );
        }

        // Crear cliente SIN las mascotas (las mascotas ya están en la BD)
        // Se asignarán después usando MascotaService.asignarMascotaACliente()
        return clienteRepository.create(cliente, false);
    }

    /**
     * Registra un nuevo cliente CON sus mascotas en la base de datos.
     * Las mascotas se guardan en la misma transacción.
     *
     * @param cliente Cliente a registrar (con lista de mascotas)
     * @return ID del cliente creado
     * @throws IllegalArgumentException si los datos son inválidos
     * @throws RuntimeException si hay error en la BD
     */
    public int registrarClienteConMascotas(Cliente cliente) {
        validarCliente(cliente);

        // Verificar si ya existe un cliente con ese documento
        if (clienteRepository.existsByCc(cliente.getCc())) {
            throw new IllegalArgumentException(
                "Ya existe un cliente registrado con el documento: " + cliente.getCc()
            );
        }

        // Crear cliente CON las mascotas en la misma transacción
        return clienteRepository.create(cliente, true);
    }

    /**
     * Busca un cliente por su número de documento.
     *
     * @param cc Número de documento
     * @param incluirMascotas Si se deben cargar las mascotas asociadas
     * @return Optional con el cliente si existe
     */
    public Optional<Cliente> buscarPorDocumento(String cc, boolean incluirMascotas) {
        if (cc == null || cc.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de documento no puede estar vacío");
        }
        return clienteRepository.findByCc(cc.trim(), incluirMascotas);
    }

    /**
     * Busca un cliente por su ID.
     *
     * @param id ID del cliente
     * @param incluirMascotas Si se deben cargar las mascotas asociadas
     * @return Optional con el cliente si existe
     */
    public Optional<Cliente> buscarPorId(int id, boolean incluirMascotas) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        return clienteRepository.findById(id, incluirMascotas);
    }

    /**
     * Obtiene todos los clientes registrados.
     *
     * @param limit Cantidad máxima de registros
     * @param offset Desplazamiento (paginación)
     * @return Lista de clientes
     */
    public List<Cliente> listarClientes(int limit, int offset) {
        return clienteRepository.findAll(limit, offset);
    }

    /**
     * Actualiza los datos de un cliente existente.
     *
     * @param cliente Cliente con datos actualizados
     * @return true si se actualizó correctamente
     */
    public boolean actualizarCliente(Cliente cliente) {
        validarCliente(cliente);

        if (cliente.getIdCliente() <= 0) {
            throw new IllegalArgumentException("El cliente debe tener un ID válido para actualizar");
        }

        return clienteRepository.update(cliente);
    }

    /**
     * Verifica si existe un cliente con el documento dado.
     *
     * @param cc Número de documento
     * @return true si existe
     */
    public boolean existeCliente(String cc) {
        if (cc == null || cc.trim().isEmpty()) {
            return false;
        }
        return clienteRepository.existsByCc(cc.trim());
    }

    /**
     * Obtiene el total de clientes registrados.
     *
     * @return Cantidad total de clientes
     */
    public int contarClientes() {
        return clienteRepository.count();
    }

    /**
     * Valida que los datos del cliente sean correctos.
     *
     * @param cliente Cliente a validar
     * @throws IllegalArgumentException si algún dato es inválido
     */
    private void validarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo");
        }

        if (esVacio(cliente.getNombre())) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        if (esVacio(cliente.getApellidos())) {
            throw new IllegalArgumentException("Los apellidos son obligatorios");
        }

        if (esVacio(cliente.getCc())) {
            throw new IllegalArgumentException("El número de documento es obligatorio");
        }

        if (esVacio(cliente.getTipoDocumento())) {
            throw new IllegalArgumentException("El tipo de documento es obligatorio");
        }

        if (esVacio(cliente.getTipoPersona())) {
            throw new IllegalArgumentException("El tipo de persona es obligatorio");
        }

        if (cliente.getFechaNacimiento() == null) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria");
        }

        if (esVacio(cliente.getEmail())) {
            throw new IllegalArgumentException("El email es obligatorio");
        }

        if (!validarEmail(cliente.getEmail())) {
            throw new IllegalArgumentException("El formato del email no es válido");
        }

        if (esVacio(cliente.getTelefono())) {
            throw new IllegalArgumentException("El teléfono es obligatorio");
        }

        if (esVacio(cliente.getCiudad())) {
            throw new IllegalArgumentException("La ciudad es obligatoria");
        }

        if (esVacio(cliente.getDireccion())) {
            throw new IllegalArgumentException("La dirección es obligatoria");
        }

        if (esVacio(cliente.getNombreContacto())) {
            throw new IllegalArgumentException("El nombre del contacto de emergencia es obligatorio");
        }

        if (esVacio(cliente.getTelefonoContacto())) {
            throw new IllegalArgumentException("El teléfono del contacto de emergencia es obligatorio");
        }
    }

    /**
     * Verifica si una cadena es vacía o nula.
     */
    private boolean esVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    /**
     * Valida el formato básico de un email.
     */
    private boolean validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        // Validación simple de formato email
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(regex);
    }
}
