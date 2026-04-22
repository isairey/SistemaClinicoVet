package co.edu.upb.veterinaria.services.ServicioMaterialQuirurgico;

import co.edu.upb.veterinaria.models.ModeloMaterialQuirurgico.MaterialQuirurgico;
import co.edu.upb.veterinaria.repositories.RepositorioMaterialQuirurgico.MaterialQuirurgicoRepository;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * MaterialQuirurgicoService
 * =========================
 * Servicio de lógica de negocio para productos tipo MATERIAL QUIRÚRGICO.
 *
 * RESPONSABILIDADES:
 * - Validación de datos de negocio (lote, fechas opcionales, contenido)
 * - Orquestación del repositorio MaterialQuirurgicoRepository
 * - Manejo de excepciones con mensajes amigables
 * - Validaciones específicas: algunos materiales tienen fecha de vencimiento
 */
public class MaterialQuirurgicoService {

    private final MaterialQuirurgicoRepository materialQuirurgicoRepository;

    public MaterialQuirurgicoService() {
        this.materialQuirurgicoRepository = new MaterialQuirurgicoRepository();
    }

    // ============================================================
    //                    OPERACIONES CRUD
    // ============================================================

    /**
     * Crea un nuevo material quirúrgico en el inventario.
     * Valida datos antes de persistir.
     *
     * @param materialQuirurgico Objeto con datos del material
     * @return ID del material creado
     * @throws IllegalArgumentException Si faltan datos o son inválidos
     * @throws SQLException Si hay error en BD
     */
    public int crearMaterialQuirurgico(MaterialQuirurgico materialQuirurgico) throws SQLException {
        validarMaterialQuirurgico(materialQuirurgico);
        return materialQuirurgicoRepository.create(materialQuirurgico);
    }

    /**
     * Actualiza un material quirúrgico existente.
     *
     * @param materialQuirurgico Material con datos actualizados
     * @param idProveedor ID del proveedor a vincular (null para no cambiar)
     * @return true si se actualizó correctamente
     * @throws IllegalArgumentException Si datos son inválidos
     * @throws SQLException Si hay error en BD
     */
    public boolean actualizarMaterialQuirurgico(MaterialQuirurgico materialQuirurgico, Integer idProveedor) throws SQLException {
        validarMaterialQuirurgico(materialQuirurgico);
        if (materialQuirurgico.getIdProducto() <= 0) {
            throw new IllegalArgumentException("ID de producto inválido");
        }
        return materialQuirurgicoRepository.update(materialQuirurgico, idProveedor);
    }

    /**
     * Elimina un material quirúrgico del inventario (eliminación física).
     *
     * @param idProducto ID del material a eliminar
     * @return true si se eliminó correctamente
     * @throws SQLException Si hay error en BD
     */
    public boolean eliminarMaterialQuirurgico(int idProducto) throws SQLException {
        if (idProducto <= 0) {
            throw new IllegalArgumentException("ID de producto inválido");
        }
        return materialQuirurgicoRepository.delete(idProducto);
    }

    // ============================================================
    //                    BÚSQUEDAS INDIVIDUALES
    // ============================================================

    /**
     * Obtiene un material quirúrgico por su ID.
     *
     * @param idProducto ID del producto
     * @return Optional con el material si existe
     * @throws SQLException Si hay error en BD
     */
    public Optional<MaterialQuirurgico> obtenerPorId(int idProducto) throws SQLException {
        return materialQuirurgicoRepository.findById(idProducto);
    }

    /**
     * Busca un material quirúrgico por código de barras.
     *
     * @param codigoBarras Código de barras del producto
     * @return Optional con el material si existe
     * @throws SQLException Si hay error en BD
     */
    public Optional<MaterialQuirurgico> obtenerPorCodigoBarras(String codigoBarras) throws SQLException {
        if (codigoBarras == null || codigoBarras.isBlank()) {
            throw new IllegalArgumentException("Código de barras no puede estar vacío");
        }
        return materialQuirurgicoRepository.findByCodigoBarras(codigoBarras);
    }

    /**
     * Busca un material quirúrgico por referencia.
     *
     * @param referencia Referencia del producto
     * @return Optional con el material si existe
     * @throws SQLException Si hay error en BD
     */
    public Optional<MaterialQuirurgico> obtenerPorReferencia(String referencia) throws SQLException {
        if (referencia == null || referencia.isBlank()) {
            throw new IllegalArgumentException("Referencia no puede estar vacía");
        }
        return materialQuirurgicoRepository.findByReferencia(referencia);
    }

    /**
     * Busca materiales quirúrgicos por lote.
     *
     * @param lote Lote a buscar
     * @return Lista de materiales de ese lote
     * @throws SQLException Si hay error en BD
     */
    public List<MaterialQuirurgico> buscarPorLote(String lote) throws SQLException {
        if (lote == null || lote.isBlank()) {
            throw new IllegalArgumentException("Lote no puede estar vacío");
        }
        return materialQuirurgicoRepository.findByLote(lote);
    }

    /**
     * Busca materiales quirúrgicos por marca.
     *
     * @param idMarca ID de la marca
     * @return Lista de materiales de esa marca
     * @throws SQLException Si hay error en BD
     */
    public List<MaterialQuirurgico> buscarPorMarca(int idMarca) throws SQLException {
        if (idMarca <= 0) {
            throw new IllegalArgumentException("ID de marca inválido");
        }
        return materialQuirurgicoRepository.findByMarca(idMarca);
    }

    /**
     * Busca materiales quirúrgicos por estado.
     *
     * @param estado Estado del producto (ACTIVO/INACTIVO)
     * @return Lista de materiales con ese estado
     * @throws SQLException Si hay error en BD
     */
    public List<MaterialQuirurgico> buscarPorEstado(String estado) throws SQLException {
        return materialQuirurgicoRepository.findByEstado(estado);
    }

    // ============================================================
    //                    BÚSQUEDAS ESPECIALES
    // ============================================================

    /**
     * Obtiene materiales quirúrgicos con stock bajo.
     *
     * @param umbral Cantidad mínima considerada como stock bajo
     * @return Lista de materiales con stock <= umbral
     * @throws SQLException Si hay error en BD
     */
    public List<MaterialQuirurgico> obtenerStockBajo(int umbral) throws SQLException {
        if (umbral < 0) {
            throw new IllegalArgumentException("Umbral no puede ser negativo");
        }
        return materialQuirurgicoRepository.findStockBajo(umbral);
    }

    /**
     * Obtiene materiales quirúrgicos próximos a vencer.
     * NOTA: Solo algunos materiales quirúrgicos tienen fecha de vencimiento.
     *
     * @param diasUmbral Días de anticipación para la alerta
     * @return Lista de materiales que vencen en los próximos X días
     * @throws SQLException Si hay error en BD
     */
    public List<MaterialQuirurgico> obtenerProximosAVencer(int diasUmbral) throws SQLException {
        if (diasUmbral < 0) {
            throw new IllegalArgumentException("Días umbral no puede ser negativo");
        }
        return materialQuirurgicoRepository.findProximosAVencer(diasUmbral);
    }

    /**
     * Lista todos los materiales quirúrgicos activos (sin filtros).
     *
     * @return Lista de todos los materiales activos
     * @throws SQLException Si hay error en BD
     */
    public List<MaterialQuirurgico> listarTodos() throws SQLException {
        return materialQuirurgicoRepository.search(null, "ACTIVO", null, null, null, null, null, null, "nombre", true, 0, 0);
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
     * @return Lista de materiales que cumplen los criterios
     * @throws SQLException Si hay error en BD
     */
    public List<MaterialQuirurgico> buscarAvanzado(
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

        return materialQuirurgicoRepository.search(
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
     * Cuenta materiales quirúrgicos que cumplen los criterios de búsqueda.
     * Útil para paginación.
     *
     * @return Cantidad de materiales que cumplen los criterios
     * @throws SQLException Si hay error en BD
     */
    public long contarMateriales(
            String textoBusqueda,
            String estado,
            String lote,
            Date fechaVencimientoDesde,
            Date fechaVencimientoHasta,
            Boolean fraccionable,
            Integer stockMinimo,
            Integer stockMaximo) throws SQLException {

        return materialQuirurgicoRepository.count(
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
        return materialQuirurgicoRepository.existsByCodigoBarras(codigoBarras);
    }

    /**
     * Verifica si una referencia ya existe en el inventario.
     *
     * @param referencia Referencia a verificar
     * @return true si ya existe
     * @throws SQLException Si hay error en BD
     */
    public boolean existeReferencia(String referencia) throws SQLException {
        return materialQuirurgicoRepository.existsByReferencia(referencia);
    }

    /**
     * Valida que un material quirúrgico tenga todos los datos requeridos.
     *
     * @param material Material a validar
     * @throws IllegalArgumentException Si faltan datos o son inválidos
     */
    private void validarMaterialQuirurgico(MaterialQuirurgico material) {
        if (material == null) {
            throw new IllegalArgumentException("El material quirúrgico no puede ser null");
        }

        // Validaciones base
        if (material.getNombre() == null || material.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es requerido");
        }

        if (material.getReferencia() == null || material.getReferencia().isBlank()) {
            throw new IllegalArgumentException("La referencia es requerida");
        }

        if (material.getCodigoBarras() == null || material.getCodigoBarras().isBlank()) {
            throw new IllegalArgumentException("El código de barras es requerido");
        }

        if (material.getPrecio() < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }

        if (material.getCosto() < 0) {
            throw new IllegalArgumentException("El costo no puede ser negativo");
        }

        if (material.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }

        if (material.getMarca() == null || material.getMarca().getIdMarca() <= 0) {
            throw new IllegalArgumentException("La marca es requerida");
        }

        if (material.getEstado() == null || material.getEstado().isBlank()) {
            throw new IllegalArgumentException("El estado es requerido");
        }

        if (material.getImagenProducto() == null || material.getImagenProducto().length == 0) {
            throw new IllegalArgumentException("La imagen del producto es requerida");
        }

        // Validaciones específicas de material quirúrgico
        if (material.getLote() == null || material.getLote().isBlank()) {
            throw new IllegalArgumentException("El lote es requerido para material quirúrgico");
        }

        // Fecha de vencimiento es OPCIONAL para material quirúrgico
        // (algunos materiales no vencen, como instrumentos metálicos)
        if (material.getFechaVencimiento() != null) {
            if (material.getFechaVencimiento().before(new Date())) {
                throw new IllegalArgumentException("La fecha de vencimiento no puede ser anterior a hoy");
            }
        }

        if (material.getSemanaParaAlerta() < 1) {
            throw new IllegalArgumentException("Las semanas para alerta deben ser al menos 1");
        }

        if (material.getContenido() <= 0) {
            throw new IllegalArgumentException("El contenido debe ser mayor a 0");
        }

        if (material.getUnidadMedida() == null || material.getUnidadMedida().getIdUnidadMedida() <= 0) {
            throw new IllegalArgumentException("La unidad de medida es requerida para material quirúrgico");
        }
    }
}

