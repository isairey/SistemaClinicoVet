package co.edu.upb.veterinaria.services.ServicioAccesorio;

import co.edu.upb.veterinaria.models.ModeloAccesorio.Accesorio;
import co.edu.upb.veterinaria.repositories.RepositorioAccesorio.AccesorioRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * AccesorioService
 * ================
 * Servicio de lógica de negocio para productos tipo ACCESORIO/JUGUETE.
 *
 * RESPONSABILIDADES:
 * - Validación de datos de negocio
 * - Orquestación del repositorio AccesorioRepository
 * - Manejo de excepciones con mensajes amigables
 * - Conversión entre DTOs y modelos (si fuera necesario)
 *
 * NO contiene lógica de BD (eso es del repositorio).
 */
public class AccesorioService {

    private final AccesorioRepository accesorioRepository;

    public AccesorioService() {
        this.accesorioRepository = new AccesorioRepository();
    }

    // ============================================================
    //                    OPERACIONES CRUD
    // ============================================================

    /**
     * Crea un nuevo accesorio/juguete en el inventario.
     * Valida datos antes de persistir.
     *
     * @param accesorio Objeto con datos del accesorio
     * @return ID del accesorio creado
     * @throws IllegalArgumentException Si faltan datos o son inválidos
     * @throws SQLException Si hay error en BD
     */
    public int crearAccesorio(Accesorio accesorio) throws SQLException {
        validarAccesorio(accesorio);
        return accesorioRepository.create(accesorio);
    }

    /**
     * Actualiza un accesorio existente.
     *
     * @param accesorio Accesorio con datos actualizados
     * @param idProveedor ID del proveedor a vincular (null para no cambiar)
     * @return true si se actualizó correctamente
     * @throws IllegalArgumentException Si datos son inválidos
     * @throws SQLException Si hay error en BD
     */
    public boolean actualizarAccesorio(Accesorio accesorio, Integer idProveedor) throws SQLException {
        validarAccesorio(accesorio);
        if (accesorio.getIdProducto() <= 0) {
            throw new IllegalArgumentException("ID de producto inválido");
        }
        return accesorioRepository.update(accesorio, idProveedor);
    }

    /**
     * Elimina un accesorio del inventario (eliminación física).
     *
     * @param idProducto ID del accesorio a eliminar
     * @return true si se eliminó correctamente
     * @throws SQLException Si hay error en BD
     */
    public boolean eliminarAccesorio(int idProducto) throws SQLException {
        if (idProducto <= 0) {
            throw new IllegalArgumentException("ID de producto inválido");
        }
        return accesorioRepository.delete(idProducto);
    }

    // ============================================================
    //                    BÚSQUEDAS INDIVIDUALES
    // ============================================================

    /**
     * Obtiene un accesorio por su ID.
     *
     * @param idProducto ID del producto
     * @return Optional con el accesorio si existe
     * @throws SQLException Si hay error en BD
     */
    public Optional<Accesorio> obtenerPorId(int idProducto) throws SQLException {
        return accesorioRepository.findById(idProducto);
    }

    /**
     * Busca un accesorio por código de barras.
     *
     * @param codigoBarras Código de barras del producto
     * @return Optional con el accesorio si existe
     * @throws SQLException Si hay error en BD
     */
    public Optional<Accesorio> obtenerPorCodigoBarras(String codigoBarras) throws SQLException {
        if (codigoBarras == null || codigoBarras.isBlank()) {
            throw new IllegalArgumentException("Código de barras no puede estar vacío");
        }
        return accesorioRepository.findByCodigoBarras(codigoBarras);
    }

    /**
     * Busca un accesorio por referencia.
     *
     * @param referencia Referencia del producto
     * @return Optional con el accesorio si existe
     * @throws SQLException Si hay error en BD
     */
    public Optional<Accesorio> obtenerPorReferencia(String referencia) throws SQLException {
        if (referencia == null || referencia.isBlank()) {
            throw new IllegalArgumentException("Referencia no puede estar vacía");
        }
        return accesorioRepository.findByReferencia(referencia);
    }

    /**
     * Busca accesorios por nombre (búsqueda parcial).
     *
     * @param nombre Nombre a buscar
     * @return Lista de accesorios que coinciden
     * @throws SQLException Si hay error en BD
     */
    public List<Accesorio> buscarPorNombre(String nombre) throws SQLException {
        return accesorioRepository.findByNombre(nombre);
    }

    /**
     * Busca accesorios por marca.
     *
     * @param idMarca ID de la marca
     * @return Lista de accesorios de esa marca
     * @throws SQLException Si hay error en BD
     */
    public List<Accesorio> buscarPorMarca(int idMarca) throws SQLException {
        if (idMarca <= 0) {
            throw new IllegalArgumentException("ID de marca inválido");
        }
        return accesorioRepository.findByMarca(idMarca);
    }

    /**
     * Busca accesorios por estado.
     *
     * @param estado Estado del producto (ACTIVO/INACTIVO)
     * @return Lista de accesorios con ese estado
     * @throws SQLException Si hay error en BD
     */
    public List<Accesorio> buscarPorEstado(String estado) throws SQLException {
        return accesorioRepository.findByEstado(estado);
    }

    // ============================================================
    //                    BÚSQUEDAS ESPECIALES
    // ============================================================

    /**
     * Obtiene accesorios con stock bajo.
     *
     * @param umbral Cantidad mínima considerada como stock bajo
     * @return Lista de accesorios con stock <= umbral
     * @throws SQLException Si hay error en BD
     */
    public List<Accesorio> obtenerStockBajo(int umbral) throws SQLException {
        if (umbral < 0) {
            throw new IllegalArgumentException("Umbral no puede ser negativo");
        }
        return accesorioRepository.findStockBajo(umbral);
    }

    /**
     * Lista todos los accesorios activos (sin filtros).
     *
     * @return Lista de todos los accesorios activos
     * @throws SQLException Si hay error en BD
     */
    public List<Accesorio> listarTodos() throws SQLException {
        return accesorioRepository.search(null, "ACTIVO", null, null, "nombre", true, 0, 0);
    }

    // ============================================================
    //                    BÚSQUEDA AVANZADA
    // ============================================================

    /**
     * Búsqueda avanzada con múltiples filtros.
     *
     * @param textoBusqueda Texto a buscar en nombre, referencia, código, marca
     * @param estado Estado del producto (ACTIVO/INACTIVO, null para todos)
     * @param stockMinimo Stock mínimo (null para sin filtro)
     * @param stockMaximo Stock máximo (null para sin filtro)
     * @param ordenarPor Columna para ordenar (nombre, precio, stock, etc.)
     * @param ascendente true = ascendente, false = descendente
     * @param limite Límite de resultados (0 = sin límite)
     * @param desplazamiento Offset para paginación
     * @return Lista de accesorios que cumplen los criterios
     * @throws SQLException Si hay error en BD
     */
    public List<Accesorio> buscarAvanzado(
            String textoBusqueda,
            String estado,
            Integer stockMinimo,
            Integer stockMaximo,
            String ordenarPor,
            boolean ascendente,
            int limite,
            int desplazamiento) throws SQLException {

        return accesorioRepository.search(
            textoBusqueda,
            estado,
            stockMinimo,
            stockMaximo,
            ordenarPor,
            ascendente,
            limite,
            desplazamiento
        );
    }

    /**
     * Cuenta accesorios que cumplen los criterios de búsqueda.
     * Útil para paginación.
     *
     * @param textoBusqueda Texto a buscar
     * @param estado Estado del producto
     * @param stockMinimo Stock mínimo
     * @param stockMaximo Stock máximo
     * @return Cantidad de accesorios que cumplen los criterios
     * @throws SQLException Si hay error en BD
     */
    public long contarAccesorios(
            String textoBusqueda,
            String estado,
            Integer stockMinimo,
            Integer stockMaximo) throws SQLException {

        return accesorioRepository.count(textoBusqueda, estado, stockMinimo, stockMaximo);
    }

    // ============================================================
    //                    VALIDACIONES
    // ============================================================

    /**
     * Verifica si un código de barras ya existe en el inventario.
     *
     * @param codigoBarras Código a verificar
     * @return true si ya existe
     * @throws SQLException Si hay error en BD
     */
    public boolean existeCodigoBarras(String codigoBarras) throws SQLException {
        return accesorioRepository.existsByCodigoBarras(codigoBarras);
    }

    /**
     * Verifica si una referencia ya existe en el inventario.
     *
     * @param referencia Referencia a verificar
     * @return true si ya existe
     * @throws SQLException Si hay error en BD
     */
    public boolean existeReferencia(String referencia) throws SQLException {
        return accesorioRepository.existsByReferencia(referencia);
    }

    /**
     * Valida que un accesorio tenga todos los datos requeridos.
     *
     * @param accesorio Accesorio a validar
     * @throws IllegalArgumentException Si faltan datos o son inválidos
     */
    private void validarAccesorio(Accesorio accesorio) {
        if (accesorio == null) {
            throw new IllegalArgumentException("El accesorio no puede ser null");
        }

        // Validar nombre
        if (accesorio.getNombre() == null || accesorio.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es requerido");
        }

        // Validar referencia
        if (accesorio.getReferencia() == null || accesorio.getReferencia().isBlank()) {
            throw new IllegalArgumentException("La referencia es requerida");
        }

        // Validar código de barras
        if (accesorio.getCodigoBarras() == null || accesorio.getCodigoBarras().isBlank()) {
            throw new IllegalArgumentException("El código de barras es requerido");
        }

        // Validar precio
        if (accesorio.getPrecio() < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }

        // Validar costo
        if (accesorio.getCosto() < 0) {
            throw new IllegalArgumentException("El costo no puede ser negativo");
        }

        // Validar stock
        if (accesorio.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }

        // Validar marca
        if (accesorio.getMarca() == null || accesorio.getMarca().getIdMarca() <= 0) {
            throw new IllegalArgumentException("La marca es requerida");
        }

        // Validar estado
        if (accesorio.getEstado() == null || accesorio.getEstado().isBlank()) {
            throw new IllegalArgumentException("El estado es requerido");
        }

        // Validar imagen
        if (accesorio.getImagenProducto() == null || accesorio.getImagenProducto().length == 0) {
            throw new IllegalArgumentException("La imagen del producto es requerida");
        }
    }
}

