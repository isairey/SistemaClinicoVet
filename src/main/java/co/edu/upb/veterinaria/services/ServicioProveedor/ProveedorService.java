package co.edu.upb.veterinaria.services.ServicioProveedor;

import co.edu.upb.veterinaria.config.DatabaseConfig;
import co.edu.upb.veterinaria.models.ModeloProveedor.Proveedor;
import co.edu.upb.veterinaria.repositories.RepositorioProveedor.ProveedorRepository;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Servicio de lógica de negocio para Proveedores.
 * Aplica validaciones antes de interactuar con el repositorio.
 */
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public ProveedorService() {
        this.proveedorRepository = new ProveedorRepository(DatabaseConfig.getDataSource());
    }

    /**
     * Crea un nuevo proveedor con validación completa.
     * @return ID del proveedor creado
     * @throws SQLException si hay error en la base de datos
     * @throws IllegalArgumentException si los datos no son válidos
     */
    public int crearProveedor(Proveedor p) throws SQLException {
        validarProveedor(p);

        // Verificar que no exista otro proveedor con el mismo NIT/RUT
        if (proveedorRepository.existsByNit(p.getNit_rut())) {
            throw new IllegalArgumentException("Ya existe un proveedor con ese NIT/RUT: " + p.getNit_rut());
        }

        return proveedorRepository.create(p);
    }

    /**
     * Obtiene todos los proveedores registrados.
     */
    public List<Proveedor> obtenerTodosLosProveedores() {
        try {
            return proveedorRepository.findAll();
        } catch (SQLException e) {
            System.err.println("Error al obtener proveedores: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Busca un proveedor por su ID.
     */
    public Proveedor obtenerProveedorPorId(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del proveedor debe ser mayor a 0.");
        }
        return proveedorRepository.findById(id);
    }

    /**
     * Actualiza los datos de un proveedor existente.
     */
    public boolean actualizarProveedor(Proveedor p) throws SQLException {
        validarProveedor(p);

        if (p.getIdProveedor() <= 0) {
            throw new IllegalArgumentException("El ID del proveedor es inválido.");
        }

        return proveedorRepository.update(p);
    }

    /**
     * Elimina un proveedor de la base de datos.
     * ADVERTENCIA: Esta operación es permanente y elimina el registro físicamente.
     * @param idProveedor ID del proveedor a eliminar
     * @return true si se eliminó correctamente
     */
    public boolean eliminarProveedor(int idProveedor) throws SQLException {
        if (idProveedor <= 0) {
            throw new IllegalArgumentException("El ID del proveedor debe ser mayor a 0.");
        }
        return proveedorRepository.delete(idProveedor);
    }

    /**
     * Validaciones de negocio para los campos del proveedor.
     */
    private void validarProveedor(Proveedor p) {
        if (p == null) {
            throw new IllegalArgumentException("El proveedor no puede ser nulo.");
        }

        // Validar campos obligatorios
        if (isEmpty(p.getNombre())) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (isEmpty(p.getApellido())) {
            throw new IllegalArgumentException("El apellido es obligatorio.");
        }
        if (isEmpty(p.getTipoPersona())) {
            throw new IllegalArgumentException("El tipo de persona es obligatorio.");
        }
        if (isEmpty(p.getNit_rut())) {
            throw new IllegalArgumentException("El NIT/RUT es obligatorio.");
        }
        if (isEmpty(p.getTelefono())) {
            throw new IllegalArgumentException("El teléfono es obligatorio.");
        }
        if (isEmpty(p.getEmail())) {
            throw new IllegalArgumentException("El email es obligatorio.");
        }
        if (isEmpty(p.getDireccion())) {
            throw new IllegalArgumentException("La dirección es obligatoria.");
        }
        if (isEmpty(p.getCiudad())) {
            throw new IllegalArgumentException("La ciudad es obligatoria.");
        }

        // Validar longitud mínima del NIT/RUT
        if (p.getNit_rut().length() < 5) {
            throw new IllegalArgumentException("El NIT/RUT debe tener al menos 5 caracteres.");
        }

        // Validar formato del email
        if (!Pattern.matches("^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,6}$", p.getEmail())) {
            throw new IllegalArgumentException("El formato del email no es válido.");
        }

        // Validar formato del teléfono
        if (!Pattern.matches("^[0-9\\-+()\\s]{7,15}$", p.getTelefono())) {
            throw new IllegalArgumentException("El teléfono debe tener entre 7 y 15 caracteres numéricos.");
        }
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
