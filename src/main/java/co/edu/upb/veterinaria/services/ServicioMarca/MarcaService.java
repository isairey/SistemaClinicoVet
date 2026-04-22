package co.edu.upb.veterinaria.services.ServicioMarca;

import co.edu.upb.veterinaria.models.ModeloMarca.Marca;
import co.edu.upb.veterinaria.repositories.RepositorioMarca.MarcaRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * MarcaService
 * ------------
 * Servicio de LÓGICA DE NEGOCIO para marcas.
 *
 * Responsabilidades:
 *  - Validación de datos de negocio (campos requeridos, duplicados, etc.)
 *  - Orquestación del repositorio MarcaRepository
 *  - Manejo de reglas de negocio antes de persistir
 *  - Conversión entre modelos de vista y modelos de persistencia
 *
 * IMPORTANTE:
 * - La marca tiene AUTO INCREMENT en BD, por lo que el ID se genera automáticamente
 * - El nombre de la marca es REQUERIDO y debe ser ÚNICO (case-insensitive)
 * - La descripción es OPCIONAL
 * - DELETE es físico y permanente (no se puede deshacer)
 *
 * Flujo de registro desde vista:
 *  1. Controller captura nombre y descripción del formulario addMarca-view.fxml
 *  2. Llama a registrarMarca(nombre, descripcion)
 *  3. Service valida los datos
 *  4. Delega al repository que inserta en veterinaria.marca
 *  5. Retorna el ID generado
 *  6. Controller actualiza el ComboBox de marcas en registerProduct-view.fxml
 */
public class MarcaService {

    // ============================================================
    //                    REPOSITORIO
    // ============================================================

    private final MarcaRepository marcaRepo;

    // ============================================================
    //                    CONSTRUCTOR
    // ============================================================

    public MarcaService() {
        this.marcaRepo = new MarcaRepository();
    }

    // ============================================================
    //              REGISTRO DE MARCA
    // ============================================================

    /**
     * Registra una nueva marca en el sistema.
     *
     * Campos requeridos:
     *  - nombreMarca (no puede estar vacío ni duplicado)
     *  - descripcion (NOT NULL en BD, se usa cadena vacía si no se proporciona)
     *
     * Validaciones:
     *  - El nombre no puede estar vacío
     *  - El nombre debe ser único (case-insensitive)
     *  - El nombre no debe contener solo espacios
     *
     * @param nombreMarca Nombre de la marca (REQUERIDO)
     * @param descripcion Descripción de la marca (si es null o vacío, se guarda "")
     * @return ID de la marca creada (generado automáticamente por BD)
     * @throws IllegalArgumentException si el nombre es inválido o ya existe
     * @throws SQLException si hay error en BD
     */
    public int registrarMarca(String nombreMarca, String descripcion) throws SQLException {

        // ===== VALIDACIONES DE NEGOCIO =====
        validarNombreMarca(nombreMarca);

        // Verificar que no exista una marca con el mismo nombre
        if (marcaRepo.existsByNombre(nombreMarca)) {
            throw new IllegalArgumentException("Ya existe una marca con el nombre '" + nombreMarca + "'");
        }

        // ===== CREAR MODELO MARCA =====
        Marca marca = new Marca();
        marca.setNombreMarca(nombreMarca.trim());

        // ⚠️ IMPORTANTE: descripcion es NOT NULL en BD
        // Si no se proporciona, usamos cadena vacía
        if (descripcion != null && !descripcion.isBlank()) {
            marca.setDescripcion(descripcion.trim());
        } else {
            marca.setDescripcion(""); // Cadena vacía para cumplir NOT NULL
        }

        // ===== PERSISTIR EN BD =====
        // El ID se genera automáticamente (AUTO INCREMENT)
        int idGenerado = marcaRepo.create(marca);

        return idGenerado;
    }

    // ============================================================
    //              ACTUALIZACIÓN DE MARCA
    // ============================================================

    /**
     * Actualiza una marca existente.
     *
     * Validaciones:
     *  - La marca debe existir
     *  - El nuevo nombre no puede estar vacío
     *  - Si se cambia el nombre, debe ser único
     *
     * @param idMarca ID de la marca a actualizar
     * @param nombreMarca Nuevo nombre de la marca
     * @param descripcion Nueva descripción (si es null o vacío, se guarda "")
     * @return true si se actualizó correctamente
     * @throws IllegalArgumentException si los datos son inválidos
     * @throws SQLException si hay error en BD
     */
    public boolean actualizarMarca(int idMarca, String nombreMarca, String descripcion) throws SQLException {

        // ===== VALIDACIONES =====
        validarNombreMarca(nombreMarca);

        // Verificar que la marca exista
        Optional<Marca> marcaExistenteOpt = marcaRepo.findById(idMarca);
        if (marcaExistenteOpt.isEmpty()) {
            throw new IllegalArgumentException("No existe una marca con el ID " + idMarca);
        }

        Marca marcaExistente = marcaExistenteOpt.get();

        // Si se cambió el nombre, verificar que no exista otro con ese nombre
        if (!marcaExistente.getNombreMarca().equalsIgnoreCase(nombreMarca.trim())) {
            if (marcaRepo.existsByNombre(nombreMarca)) {
                throw new IllegalArgumentException("Ya existe otra marca con el nombre '" + nombreMarca + "'");
            }
        }

        // ===== ACTUALIZAR MODELO =====
        marcaExistente.setNombreMarca(nombreMarca.trim());

        // ⚠️ IMPORTANTE: descripcion es NOT NULL en BD
        if (descripcion != null && !descripcion.isBlank()) {
            marcaExistente.setDescripcion(descripcion.trim());
        } else {
            marcaExistente.setDescripcion(""); // Cadena vacía para cumplir NOT NULL
        }

        // ===== PERSISTIR EN BD =====
        return marcaRepo.update(marcaExistente);
    }

    // ============================================================
    //              ELIMINACIÓN DE MARCA
    // ============================================================

    /**
     * Elimina una marca FÍSICAMENTE de la base de datos.
     *
     * ⚠️ ADVERTENCIA: Esta operación elimina la marca permanentemente.
     * NO se puede deshacer.
     *
     * IMPORTANTE: Si hay productos usando esta marca, la BD lanzará error
     * de violación de FK. Antes de eliminar, verificar que no haya productos
     * asociados (se puede hacer con una consulta adicional si es necesario).
     *
     * @param idMarca ID de la marca a eliminar
     * @return true si se eliminó correctamente, false si no existía
     * @throws SQLException si hay error de BD (ej. violación de FK porque hay productos asociados)
     */
    public boolean eliminarMarca(int idMarca) throws SQLException {
        // Verificar que la marca exista antes de intentar eliminar
        if (!marcaRepo.existsById(idMarca)) {
            return false;
        }

        try {
            return marcaRepo.delete(idMarca);
        } catch (SQLException e) {
            // Si hay violación de FK, lanzar excepción más clara
            if (e.getMessage().contains("foreign key") || e.getMessage().contains("constraint")) {
                throw new SQLException("No se puede eliminar la marca porque hay productos asociados a ella", e);
            }
            throw e;
        }
    }

    // ============================================================
    //              BÚSQUEDAS Y CONSULTAS
    // ============================================================

    /**
     * Busca una marca por ID.
     *
     * @param idMarca ID de la marca
     * @return Optional con la marca si existe, vacío si no
     */
    public Optional<Marca> buscarMarcaPorId(int idMarca) throws SQLException {
        return marcaRepo.findById(idMarca);
    }

    /**
     * Busca una marca por nombre exacto (case-insensitive).
     *
     * @param nombreMarca Nombre de la marca
     * @return Optional con la marca si existe, vacío si no
     */
    public Optional<Marca> buscarMarcaPorNombre(String nombreMarca) throws SQLException {
        return marcaRepo.findByNombre(nombreMarca);
    }

    /**
     * Busca marcas con filtro, orden y paginación.
     *
     * @param q Texto de búsqueda (busca en nombre y descripción)
     * @param sort Columna para ordenar (nombre, descripcion, id)
     * @param asc true para orden ascendente, false para descendente
     * @param limit Cantidad de resultados por página (0 = sin límite)
     * @param offset Desplazamiento para paginación
     * @return Lista de marcas que coinciden con el filtro
     */
    public List<Marca> buscarMarcas(String q, String sort, boolean asc, int limit, int offset) throws SQLException {
        return marcaRepo.findAll(q, sort, asc, limit, offset);
    }

    /**
     * Cuenta marcas que coinciden con el filtro.
     * Útil para paginación.
     *
     * @param q Texto de búsqueda
     * @return Total de marcas que coinciden
     */
    public long contarMarcas(String q) throws SQLException {
        return marcaRepo.count(q);
    }

    /**
     * Lista TODAS las marcas ordenadas alfabéticamente.
     * Útil para poblar ComboBox/ChoiceBox en vistas de registro de productos.
     *
     * @return Lista completa de marcas ordenadas por nombre
     */
    public List<Marca> listarTodasLasMarcas() throws SQLException {
        return marcaRepo.findAll();
    }

    // ============================================================
    //              VALIDACIONES Y UTILIDADES
    // ============================================================

    /**
     * Verifica si existe una marca con el nombre dado.
     *
     * @param nombreMarca Nombre a verificar
     * @return true si existe, false si no
     */
    public boolean existeMarca(String nombreMarca) throws SQLException {
        return marcaRepo.existsByNombre(nombreMarca);
    }

    /**
     * Verifica si existe una marca con el ID dado.
     *
     * @param idMarca ID a verificar
     * @return true si existe, false si no
     */
    public boolean existeMarca(int idMarca) throws SQLException {
        return marcaRepo.existsById(idMarca);
    }

    // ============================================================
    //              VALIDACIONES PRIVADAS
    // ============================================================

    /**
     * Valida el nombre de la marca.
     *
     * Reglas:
     *  - No puede ser null ni vacío
     *  - No puede contener solo espacios
     *  - Debe tener al menos 2 caracteres (después de trim)
     *
     * @param nombreMarca Nombre a validar
     * @throws IllegalArgumentException si el nombre es inválido
     */
    private void validarNombreMarca(String nombreMarca) {
        if (nombreMarca == null || nombreMarca.isBlank()) {
            throw new IllegalArgumentException("El nombre de la marca es requerido");
        }

        String nombreTrim = nombreMarca.trim();

        if (nombreTrim.length() < 2) {
            throw new IllegalArgumentException("El nombre de la marca debe tener al menos 2 caracteres");
        }

        // Opcional: validar caracteres especiales si es necesario
        // Por ahora permitimos cualquier carácter después de las validaciones básicas
    }
}
