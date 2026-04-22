package co.edu.upb.veterinaria.services.ServicioAlimento;

import co.edu.upb.veterinaria.models.ModeloAlimento.Alimento;
import co.edu.upb.veterinaria.repositories.RepositorioAlimento.AlimentoRepository;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * AlimentoService
 * ===============
 * Servicio de lógica de negocio para productos tipo ALIMENTO.
 *
 * RESPONSABILIDADES:
 * - Validación de datos de negocio (lote, fechas, contenido)
 * - Orquestación del repositorio AlimentoRepository
 * - Manejo de excepciones con mensajes amigables
 * - Validaciones específicas: fechas de vencimiento, alertas
 */
public class AlimentoService {

    private final AlimentoRepository alimentoRepository;

    public AlimentoService() {
        this.alimentoRepository = new AlimentoRepository();
    }

    // ============================================================
    //                    OPERACIONES CRUD
    // ============================================================

    /**
     * Crea un nuevo alimento en el inventario.
     * Valida datos antes de persistir.
     *
     * @param alimento Objeto con datos del alimento
     * @return ID del alimento creado
     * @throws IllegalArgumentException Si faltan datos o son inválidos
     * @throws SQLException Si hay error en BD
     */
    public int crearAlimento(Alimento alimento) throws SQLException {
        validarAlimento(alimento);
        return alimentoRepository.create(alimento);
    }

    /**
     * Actualiza un alimento existente.
     *
     * @param alimento Alimento con datos actualizados
     * @param idProveedor ID del proveedor a vincular (null para no cambiar)
     * @return true si se actualizó correctamente
     * @throws IllegalArgumentException Si datos son inválidos
     * @throws SQLException Si hay error en BD
     */
    public boolean actualizarAlimento(Alimento alimento, Integer idProveedor) throws SQLException {
        validarAlimento(alimento);
        if (alimento.getIdProducto() <= 0) {
            throw new IllegalArgumentException("ID de producto inválido");
        }
        return alimentoRepository.update(alimento, idProveedor);
    }

    /**
     * Elimina un alimento del inventario (eliminación física).
     *
     * @param idProducto ID del alimento a eliminar
     * @return true si se eliminó correctamente
     * @throws SQLException Si hay error en BD
     */
    public boolean eliminarAlimento(int idProducto) throws SQLException {
        if (idProducto <= 0) {
            throw new IllegalArgumentException("ID de producto inválido");
        }
        return alimentoRepository.delete(idProducto);
    }

    // ============================================================
    //                    BÚSQUEDAS INDIVIDUALES
    // ============================================================

    /**
     * Obtiene un alimento por su ID.
     *
     * @param idProducto ID del producto
     * @return Optional con el alimento si existe
     * @throws SQLException Si hay error en BD
     */
    public Optional<Alimento> obtenerPorId(int idProducto) throws SQLException {
        return alimentoRepository.findById(idProducto);
    }

    /**
     * Busca un alimento por código de barras.
     *
     * @param codigoBarras Código de barras del producto
     * @return Optional con el alimento si existe
     * @throws SQLException Si hay error en BD
     */
    public Optional<Alimento> obtenerPorCodigoBarras(String codigoBarras) throws SQLException {
        if (codigoBarras == null || codigoBarras.isBlank()) {
            throw new IllegalArgumentException("Código de barras no puede estar vacío");
        }
        return alimentoRepository.findByCodigoBarras(codigoBarras);
    }

    /**
     * Busca un alimento por referencia.
     *
     * @param referencia Referencia del producto
     * @return Optional con el alimento si existe
     * @throws SQLException Si hay error en BD
     */
    public Optional<Alimento> obtenerPorReferencia(String referencia) throws SQLException {
        if (referencia == null || referencia.isBlank()) {
            throw new IllegalArgumentException("Referencia no puede estar vacía");
        }
        return alimentoRepository.findByReferencia(referencia);
    }

    /**
     * Busca alimentos por lote.
     *
     * @param lote Lote a buscar
     * @return Lista de alimentos de ese lote
     * @throws SQLException Si hay error en BD
     */
    public List<Alimento> buscarPorLote(String lote) throws SQLException {
        if (lote == null || lote.isBlank()) {
            throw new IllegalArgumentException("Lote no puede estar vacío");
        }
        return alimentoRepository.findByLote(lote);
    }

    /**
     * Busca alimentos por marca.
     *
     * @param idMarca ID de la marca
     * @return Lista de alimentos de esa marca
     * @throws SQLException Si hay error en BD
     */
    public List<Alimento> buscarPorMarca(int idMarca) throws SQLException {
        if (idMarca <= 0) {
            throw new IllegalArgumentException("ID de marca inválido");
        }
        return alimentoRepository.findByMarca(idMarca);
    }

    /**
     * Busca alimentos por estado.
     *
     * @param estado Estado del producto (ACTIVO/INACTIVO)
     * @return Lista de alimentos con ese estado
     * @throws SQLException Si hay error en BD
     */
    public List<Alimento> buscarPorEstado(String estado) throws SQLException {
        return alimentoRepository.findByEstado(estado);
    }

    // ============================================================
    //                    BÚSQUEDAS ESPECIALES
    // ============================================================

    /**
     * Obtiene alimentos con stock bajo.
     *
     * @param umbral Cantidad mínima considerada como stock bajo
     * @return Lista de alimentos con stock <= umbral
     * @throws SQLException Si hay error en BD
     */
    public List<Alimento> obtenerStockBajo(int umbral) throws SQLException {
        if (umbral < 0) {
            throw new IllegalArgumentException("Umbral no puede ser negativo");
        }
        return alimentoRepository.findStockBajo(umbral);
    }

    /**
     * Obtiene alimentos próximos a vencer.
     *
     * @param diasUmbral Días de anticipación para la alerta
     * @return Lista de alimentos que vencen en los próximos X días
     * @throws SQLException Si hay error en BD
     */
    public List<Alimento> obtenerProximosAVencer(int diasUmbral) throws SQLException {
        if (diasUmbral < 0) {
            throw new IllegalArgumentException("Días umbral no puede ser negativo");
        }
        return alimentoRepository.findProximosAVencer(diasUmbral);
    }

    /**
     * Lista todos los alimentos activos (sin filtros).
     *
     * @return Lista de todos los alimentos activos
     * @throws SQLException Si hay error en BD
     */
    public List<Alimento> listarTodos() throws SQLException {
        return alimentoRepository.search(null, "ACTIVO", null, null, null, null, null, null, "nombre", true, 0, 0);
    }

    // ============================================================
    //                    BÚSQUEDA AVANZADA
    // ============================================================

    /**
     * Búsqueda avanzada con múltiples filtros.
     *
     * @param textoBusqueda Texto a buscar en nombre, referencia, código, lote, marca
     * @param estado Estado del producto (ACTIVO/INACTIVO, null para todos)
     * @param lote Filtro por lote específico
     * @param fechaVencimientoDesde Fecha mínima de vencimiento
     * @param fechaVencimientoHasta Fecha máxima de vencimiento
     * @param fraccionable Filtro por si es fraccionable (true/false/null)
     * @param stockMinimo Stock mínimo (null para sin filtro)
     * @param stockMaximo Stock máximo (null para sin filtro)
     * @param ordenarPor Columna para ordenar
     * @param ascendente true = ascendente, false = descendente
     * @param limite Límite de resultados (0 = sin límite)
     * @param desplazamiento Offset para paginación
     * @return Lista de alimentos que cumplen los criterios
     * @throws SQLException Si hay error en BD
     */
    public List<Alimento> buscarAvanzado(
            String textoBusqueda,
            String estado,
            String lote,
            Date fechaVencimientoDesde,
            Date fechaVencimientoHasta,
            Boolean fraccionable,
            Integer stockMinimo,
            Integer stockMaximo,
            String ordenarPor,
            boolean ascendente,
            int limite,
            int desplazamiento) throws SQLException {

        return alimentoRepository.search(
            textoBusqueda,
            estado,
            lote,
            fechaVencimientoDesde,
            fechaVencimientoHasta,
            fraccionable,
            stockMinimo,
            stockMaximo,
            ordenarPor,
            ascendente,
            limite,
            desplazamiento
        );
    }

    /**
     * Cuenta alimentos que cumplen los criterios de búsqueda.
     * Útil para paginación.
     *
     * @return Cantidad de alimentos que cumplen los criterios
     * @throws SQLException Si hay error en BD
     */
    public long contarAlimentos(
            String textoBusqueda,
            String estado,
            String lote,
            Date fechaVencimientoDesde,
            Date fechaVencimientoHasta,
            Boolean fraccionable,
            Integer stockMinimo,
            Integer stockMaximo) throws SQLException {

        return alimentoRepository.count(
            textoBusqueda,
            estado,
            lote,
            fechaVencimientoDesde,
            fechaVencimientoHasta,
            fraccionable,
            stockMinimo,
            stockMaximo
        );
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
        return alimentoRepository.existsByCodigoBarras(codigoBarras);
    }

    /**
     * Verifica si una referencia ya existe en el inventario.
     *
     * @param referencia Referencia a verificar
     * @return true si ya existe
     * @throws SQLException Si hay error en BD
     */
    public boolean existeReferencia(String referencia) throws SQLException {
        return alimentoRepository.existsByReferencia(referencia);
    }

    /**
     * Valida que un alimento tenga todos los datos requeridos.
     *
     * @param alimento Alimento a validar
     * @throws IllegalArgumentException Si faltan datos o son inválidos
     */
    private void validarAlimento(Alimento alimento) {
        if (alimento == null) {
            throw new IllegalArgumentException("El alimento no puede ser null");
        }

        // Validaciones base (igual que accesorio)
        if (alimento.getNombre() == null || alimento.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es requerido");
        }

        if (alimento.getReferencia() == null || alimento.getReferencia().isBlank()) {
            throw new IllegalArgumentException("La referencia es requerida");
        }

        if (alimento.getCodigoBarras() == null || alimento.getCodigoBarras().isBlank()) {
            throw new IllegalArgumentException("El código de barras es requerido");
        }

        if (alimento.getPrecio() < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }

        if (alimento.getCosto() < 0) {
            throw new IllegalArgumentException("El costo no puede ser negativo");
        }

        if (alimento.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }

        if (alimento.getMarca() == null || alimento.getMarca().getIdMarca() <= 0) {
            throw new IllegalArgumentException("La marca es requerida");
        }

        if (alimento.getEstado() == null || alimento.getEstado().isBlank()) {
            throw new IllegalArgumentException("El estado es requerido");
        }

        if (alimento.getImagenProducto() == null || alimento.getImagenProducto().length == 0) {
            throw new IllegalArgumentException("La imagen del producto es requerida");
        }

        // Validaciones específicas de alimento
        if (alimento.getLote() == null || alimento.getLote().isBlank()) {
            throw new IllegalArgumentException("El lote es requerido para alimentos");
        }

        if (alimento.getFechaVencimiento() == null) {
            throw new IllegalArgumentException("La fecha de vencimiento es requerida para alimentos");
        }

        // Validar que la fecha de vencimiento no sea pasada
        if (alimento.getFechaVencimiento().before(new Date())) {
            throw new IllegalArgumentException("La fecha de vencimiento no puede ser anterior a hoy");
        }

        if (alimento.getSemanasParaAlerta() < 1) {
            throw new IllegalArgumentException("Las semanas para alerta deben ser al menos 1");
        }

        if (alimento.getContenido() <= 0) {
            throw new IllegalArgumentException("El contenido debe ser mayor a 0");
        }

        if (alimento.getUnidadMedida() == null || alimento.getUnidadMedida().getIdUnidadMedida() <= 0) {
            throw new IllegalArgumentException("La unidad de medida es requerida para alimentos");
        }
    }
}

