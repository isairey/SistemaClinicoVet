package co.edu.upb.veterinaria.services.ServicioMedicamento;

import co.edu.upb.veterinaria.models.ModeloMedicamento.Medicamento;
import co.edu.upb.veterinaria.repositories.RepositorioMedicamento.MedicamentoRepository;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * MedicamentoService
 * ==================
 * Servicio de lógica de negocio para productos tipo MEDICAMENTO.
 *
 * RESPONSABILIDADES:
 * - Validación de datos de negocio (lote, fechas, dosis, contenido)
 * - Orquestación del repositorio MedicamentoRepository
 * - Manejo de excepciones con mensajes amigables
 * - Validaciones específicas: fechas de vencimiento, dosis, alertas
 */
public class MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;

    public MedicamentoService() {
        this.medicamentoRepository = new MedicamentoRepository();
    }

    // ============================================================
    //                    OPERACIONES CRUD
    // ============================================================

    /**
     * Crea un nuevo medicamento en el inventario.
     * Valida datos antes de persistir.
     *
     * @param medicamento Objeto con datos del medicamento
     * @return ID del medicamento creado
     * @throws IllegalArgumentException Si faltan datos o son inválidos
     * @throws SQLException Si hay error en BD
     */
    public int crearMedicamento(Medicamento medicamento) throws SQLException {
        validarMedicamento(medicamento);
        return medicamentoRepository.create(medicamento);
    }

    /**
     * Actualiza un medicamento existente.
     *
     * @param medicamento Medicamento con datos actualizados
     * @param idProveedor ID del proveedor a vincular (null para no cambiar)
     * @return true si se actualizó correctamente
     * @throws IllegalArgumentException Si datos son inválidos
     * @throws SQLException Si hay error en BD
     */
    public boolean actualizarMedicamento(Medicamento medicamento, Integer idProveedor) throws SQLException {
        validarMedicamento(medicamento);
        if (medicamento.getIdProducto() <= 0) {
            throw new IllegalArgumentException("ID de producto inválido");
        }
        return medicamentoRepository.update(medicamento, idProveedor);
    }

    /**
     * Elimina un medicamento del inventario (eliminación física).
     *
     * @param idProducto ID del medicamento a eliminar
     * @return true si se eliminó correctamente
     * @throws SQLException Si hay error en BD
     */
    public boolean eliminarMedicamento(int idProducto) throws SQLException {
        if (idProducto <= 0) {
            throw new IllegalArgumentException("ID de producto inválido");
        }
        return medicamentoRepository.delete(idProducto);
    }

    // ============================================================
    //                    BÚSQUEDAS INDIVIDUALES
    // ============================================================

    /**
     * Obtiene un medicamento por su ID.
     *
     * @param idProducto ID del producto
     * @return Optional con el medicamento si existe
     * @throws SQLException Si hay error en BD
     */
    public Optional<Medicamento> obtenerPorId(int idProducto) throws SQLException {
        return medicamentoRepository.findById(idProducto);
    }

    /**
     * Busca un medicamento por código de barras.
     *
     * @param codigoBarras Código de barras del producto
     * @return Optional con el medicamento si existe
     * @throws SQLException Si hay error en BD
     */
    public Optional<Medicamento> obtenerPorCodigoBarras(String codigoBarras) throws SQLException {
        if (codigoBarras == null || codigoBarras.isBlank()) {
            throw new IllegalArgumentException("Código de barras no puede estar vacío");
        }
        return medicamentoRepository.findByCodigoBarras(codigoBarras);
    }

    /**
     * Busca un medicamento por referencia.
     *
     * @param referencia Referencia del producto
     * @return Optional con el medicamento si existe
     * @throws SQLException Si hay error en BD
     */
    public Optional<Medicamento> obtenerPorReferencia(String referencia) throws SQLException {
        if (referencia == null || referencia.isBlank()) {
            throw new IllegalArgumentException("Referencia no puede estar vacía");
        }
        return medicamentoRepository.findByReferencia(referencia);
    }

    /**
     * Busca medicamentos por lote.
     *
     * @param lote Lote a buscar
     * @return Lista de medicamentos de ese lote
     * @throws SQLException Si hay error en BD
     */
    public List<Medicamento> buscarPorLote(String lote) throws SQLException {
        if (lote == null || lote.isBlank()) {
            throw new IllegalArgumentException("Lote no puede estar vacío");
        }
        return medicamentoRepository.findByLote(lote);
    }

    /**
     * Busca medicamentos por marca.
     *
     * @param idMarca ID de la marca
     * @return Lista de medicamentos de esa marca
     * @throws SQLException Si hay error en BD
     */
    public List<Medicamento> buscarPorMarca(int idMarca) throws SQLException {
        if (idMarca <= 0) {
            throw new IllegalArgumentException("ID de marca inválido");
        }
        return medicamentoRepository.findByMarca(idMarca);
    }

    /**
     * Busca medicamentos por estado.
     *
     * @param estado Estado del producto (ACTIVO/INACTIVO)
     * @return Lista de medicamentos con ese estado
     * @throws SQLException Si hay error en BD
     */
    public List<Medicamento> buscarPorEstado(String estado) throws SQLException {
        return medicamentoRepository.findByEstado(estado);
    }

    // ============================================================
    //                    BÚSQUEDAS ESPECIALES
    // ============================================================

    /**
     * Obtiene medicamentos con stock bajo.
     *
     * @param umbral Cantidad mínima considerada como stock bajo
     * @return Lista de medicamentos con stock <= umbral
     * @throws SQLException Si hay error en BD
     */
    public List<Medicamento> obtenerStockBajo(int umbral) throws SQLException {
        if (umbral < 0) {
            throw new IllegalArgumentException("Umbral no puede ser negativo");
        }
        return medicamentoRepository.findStockBajo(umbral);
    }

    /**
     * Obtiene medicamentos próximos a vencer.
     *
     * @param diasUmbral Días de anticipación para la alerta
     * @return Lista de medicamentos que vencen en los próximos X días
     * @throws SQLException Si hay error en BD
     */
    public List<Medicamento> obtenerProximosAVencer(int diasUmbral) throws SQLException {
        if (diasUmbral < 0) {
            throw new IllegalArgumentException("Días umbral no puede ser negativo");
        }
        return medicamentoRepository.findProximosAVencer(diasUmbral);
    }

    /**
     * Lista todos los medicamentos activos (sin filtros).
     *
     * @return Lista de todos los medicamentos activos
     * @throws SQLException Si hay error en BD
     */
    public List<Medicamento> listarTodos() throws SQLException {
        return medicamentoRepository.search(null, "ACTIVO", null, null, null, null, null, null, "nombre", true, 0, 0);
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
     * @return Lista de medicamentos que cumplen los criterios
     * @throws SQLException Si hay error en BD
     */
    public List<Medicamento> buscarAvanzado(
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

        return medicamentoRepository.search(
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
     * Cuenta medicamentos que cumplen los criterios de búsqueda.
     * Útil para paginación.
     *
     * @return Cantidad de medicamentos que cumplen los criterios
     * @throws SQLException Si hay error en BD
     */
    public long contarMedicamentos(
            String textoBusqueda,
            String estado,
            String lote,
            Date fechaVencimientoDesde,
            Date fechaVencimientoHasta,
            Boolean fraccionable,
            Integer stockMinimo,
            Integer stockMaximo) throws SQLException {

        return medicamentoRepository.count(
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
        return medicamentoRepository.existsByCodigoBarras(codigoBarras);
    }

    /**
     * Verifica si una referencia ya existe en el inventario.
     *
     * @param referencia Referencia a verificar
     * @return true si ya existe
     * @throws SQLException Si hay error en BD
     */
    public boolean existeReferencia(String referencia) throws SQLException {
        return medicamentoRepository.existsByReferencia(referencia);
    }

    /**
     * Valida que un medicamento tenga todos los datos requeridos.
     *
     * @param medicamento Medicamento a validar
     * @throws IllegalArgumentException Si faltan datos o son inválidos
     */
    private void validarMedicamento(Medicamento medicamento) {
        if (medicamento == null) {
            throw new IllegalArgumentException("El medicamento no puede ser null");
        }

        // Validaciones base
        if (medicamento.getNombre() == null || medicamento.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es requerido");
        }

        if (medicamento.getReferencia() == null || medicamento.getReferencia().isBlank()) {
            throw new IllegalArgumentException("La referencia es requerida");
        }

        if (medicamento.getCodigoBarras() == null || medicamento.getCodigoBarras().isBlank()) {
            throw new IllegalArgumentException("El código de barras es requerido");
        }

        if (medicamento.getPrecio() < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }

        if (medicamento.getCosto() < 0) {
            throw new IllegalArgumentException("El costo no puede ser negativo");
        }

        if (medicamento.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }

        if (medicamento.getMarca() == null || medicamento.getMarca().getIdMarca() <= 0) {
            throw new IllegalArgumentException("La marca es requerida");
        }

        if (medicamento.getEstado() == null || medicamento.getEstado().isBlank()) {
            throw new IllegalArgumentException("El estado es requerido");
        }

        if (medicamento.getImagenProducto() == null || medicamento.getImagenProducto().length == 0) {
            throw new IllegalArgumentException("La imagen del producto es requerida");
        }

        // Validaciones específicas de medicamento
        if (medicamento.getLote() == null || medicamento.getLote().isBlank()) {
            throw new IllegalArgumentException("El lote es requerido para medicamentos");
        }

        if (medicamento.getFechaVencimiento() == null) {
            throw new IllegalArgumentException("La fecha de vencimiento es requerida para medicamentos");
        }

        if (medicamento.getFechaVencimiento().before(new Date())) {
            throw new IllegalArgumentException("La fecha de vencimiento no puede ser anterior a hoy");
        }

        if (medicamento.getSemanasParaAlerta() < 1) {
            throw new IllegalArgumentException("Las semanas para alerta deben ser al menos 1");
        }

        if (medicamento.getContenido() <= 0) {
            throw new IllegalArgumentException("El contenido debe ser mayor a 0");
        }

        if (medicamento.getUnidadMedida() == null || medicamento.getUnidadMedida().getIdUnidadMedida() <= 0) {
            throw new IllegalArgumentException("La unidad de medida es requerida para medicamentos");
        }

        // Validación específica de medicamento: dosis por unidad
        if (medicamento.getDosisPorUnidad() < 0) {
            throw new IllegalArgumentException("La dosis por unidad no puede ser negativa");
        }
    }
}

