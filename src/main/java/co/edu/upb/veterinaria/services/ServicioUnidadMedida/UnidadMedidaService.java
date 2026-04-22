package co.edu.upb.veterinaria.services.ServicioUnidadMedida;

import co.edu.upb.veterinaria.models.ModeloUnidadMedida.UnidadMedida;
import co.edu.upb.veterinaria.repositories.RepositorioUnidadMedida.UnidadMedidaRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * UnidadMedidaService
 * -------------------
 * Servicio de LÓGICA DE NEGOCIO para unidades de medida.
 *
 * Responsabilidades:
 *  - Validación de datos de negocio (campos requeridos, duplicados, etc.)
 *  - Orquestación del repositorio UnidadMedidaRepository
 *  - Manejo de reglas de negocio antes de persistir
 *
 * IMPORTANTE:
 * - La unidad de medida tiene AUTO INCREMENT en BD, por lo que el ID se genera automáticamente
 * - El nombre de la unidad es REQUERIDO y debe ser ÚNICO (case-insensitive)
 * - DELETE es físico y permanente (no se puede deshacer)
 * - NO hay vista dedicada para gestionar unidades de medida (se gestionan programáticamente)
 *
 * USO PRINCIPAL:
 * - Poblar ComboBox de unidades en registerProduct-view.fxml
 * - Las unidades se precargan en BD (kg, g, ml, l, unidades, etc.)
 * - Opcionalmente se pueden agregar más desde código o admin
 */
public class UnidadMedidaService {

    // ============================================================
    //                    REPOSITORIO
    // ============================================================

    private final UnidadMedidaRepository unidadMedidaRepo;

    // ✅ FLAG ESTÁTICO para asegurar que el prepoblado solo se ejecute UNA VEZ
    private static boolean unidadesYaPrepobladas = false;

    // ============================================================
    //                    CONSTRUCTOR
    // ============================================================

    public UnidadMedidaService() {
        this.unidadMedidaRepo = new UnidadMedidaRepository();
    }

    // ============================================================
    //              REGISTRO DE UNIDAD DE MEDIDA
    // ============================================================

    /**
     * Registra una nueva unidad de medida en el sistema.
     *
     * Campos requeridos:
     *  - nombre (no puede estar vacío ni duplicado)
     *
     * Validaciones:
     *  - El nombre no puede estar vacío
     *  - El nombre debe ser único (case-insensitive)
     *  - El nombre no debe contener solo espacios
     *
     * @param nombre Nombre de la unidad de medida (ej: "kg", "ml", "unidades")
     * @return ID de la unidad creada (generado automáticamente por BD)
     * @throws IllegalArgumentException si el nombre es inválido o ya existe
     * @throws SQLException si hay error en BD
     */
    public int registrarUnidadMedida(String nombre) throws SQLException {

        // ===== VALIDACIONES DE NEGOCIO =====
        validarNombreUnidad(nombre);

        // Verificar que no exista una unidad con el mismo nombre
        if (unidadMedidaRepo.existsByNombre(nombre)) {
            throw new IllegalArgumentException("Ya existe una unidad de medida con el nombre '" + nombre + "'");
        }

        // ===== CREAR MODELO =====
        UnidadMedida unidad = new UnidadMedida();
        unidad.setNombre(nombre.trim());

        // ===== PERSISTIR EN BD =====
        // El ID se genera automáticamente (AUTO INCREMENT)
        return unidadMedidaRepo.create(unidad);
    }

    // ============================================================
    //              ACTUALIZACIÓN DE UNIDAD DE MEDIDA
    // ============================================================

    /**
     * Actualiza una unidad de medida existente.
     *
     * Validaciones:
     *  - La unidad debe existir
     *  - El nuevo nombre no puede estar vacío
     *  - Si se cambia el nombre, debe ser único
     *
     * @param idUnidad ID de la unidad a actualizar
     * @param nombre Nuevo nombre de la unidad
     * @return true si se actualizó correctamente
     * @throws IllegalArgumentException si los datos son inválidos
     * @throws SQLException si hay error en BD
     */
    public boolean actualizarUnidadMedida(int idUnidad, String nombre) throws SQLException {

        // ===== VALIDACIONES =====
        validarNombreUnidad(nombre);

        // Verificar que la unidad exista
        Optional<UnidadMedida> unidadExistenteOpt = unidadMedidaRepo.findById(idUnidad);
        if (unidadExistenteOpt.isEmpty()) {
            throw new IllegalArgumentException("No existe una unidad de medida con el ID " + idUnidad);
        }

        UnidadMedida unidadExistente = unidadExistenteOpt.get();

        // Si se cambió el nombre, verificar que no exista otra con ese nombre
        if (!unidadExistente.getNombre().equalsIgnoreCase(nombre.trim())) {
            if (unidadMedidaRepo.existsByNombre(nombre)) {
                throw new IllegalArgumentException("Ya existe otra unidad de medida con el nombre '" + nombre + "'");
            }
        }

        // ===== ACTUALIZAR MODELO =====
        unidadExistente.setNombre(nombre.trim());

        // ===== PERSISTIR EN BD =====
        return unidadMedidaRepo.update(unidadExistente);
    }

    // ============================================================
    //              ELIMINACIÓN DE UNIDAD DE MEDIDA
    // ============================================================

    /**
     * Elimina una unidad de medida FÍSICAMENTE de la base de datos.
     *
     * ⚠️ ADVERTENCIA: Esta operación elimina la unidad permanentemente.
     * NO se puede deshacer.
     *
     * IMPORTANTE: Si hay productos usando esta unidad, la BD lanzará error
     * de violación de FK.
     *
     * @param idUnidad ID de la unidad a eliminar
     * @return true si se eliminó correctamente, false si no existía
     * @throws SQLException si hay error de BD (ej. violación de FK porque hay productos asociados)
     */
    public boolean eliminarUnidadMedida(int idUnidad) throws SQLException {
        // Verificar que la unidad exista antes de intentar eliminar
        if (!unidadMedidaRepo.existsById(idUnidad)) {
            return false;
        }

        try {
            return unidadMedidaRepo.delete(idUnidad);
        } catch (SQLException e) {
            // Si hay violación de FK, lanzar excepción más clara
            if (e.getMessage().contains("foreign key") || e.getMessage().contains("constraint")) {
                throw new SQLException("No se puede eliminar la unidad de medida porque hay productos asociados a ella", e);
            }
            throw e;
        }
    }

    // ============================================================
    //              BÚSQUEDAS Y CONSULTAS
    // ============================================================

    /**
     * Busca una unidad de medida por ID.
     *
     * @param idUnidad ID de la unidad
     * @return Optional con la unidad si existe, vacío si no
     */
    public Optional<UnidadMedida> buscarUnidadPorId(int idUnidad) throws SQLException {
        return unidadMedidaRepo.findById(idUnidad);
    }

    /**
     * Busca una unidad de medida por nombre exacto (case-insensitive).
     *
     * @param nombre Nombre de la unidad
     * @return Optional con la unidad si existe, vacío si no
     */
    public Optional<UnidadMedida> buscarUnidadPorNombre(String nombre) throws SQLException {
        return unidadMedidaRepo.findByNombre(nombre);
    }

    /**
     * Busca unidades con filtro, orden y paginación.
     *
     * @param q Texto de búsqueda (busca en nombre)
     * @param sort Columna para ordenar (nombre, id)
     * @param asc true para orden ascendente, false para descendente
     * @param limit Cantidad de resultados por página (0 = sin límite)
     * @param offset Desplazamiento para paginación
     * @return Lista de unidades que coinciden con el filtro
     */
    public List<UnidadMedida> buscarUnidades(String q, String sort, boolean asc, int limit, int offset) throws SQLException {
        return unidadMedidaRepo.findAll(q, sort, asc, limit, offset);
    }

    /**
     * Cuenta unidades que coinciden con el filtro.
     * Útil para paginación.
     *
     * @param q Texto de búsqueda
     * @return Total de unidades que coinciden
     */
    public long contarUnidades(String q) throws SQLException {
        return unidadMedidaRepo.count(q);
    }

    /**
     * Lista TODAS las unidades de medida ordenadas alfabéticamente.
     *
     * ⭐ MÉTODO PRINCIPAL para poblar ComboBox/ChoiceBox en vistas de registro de productos.
     *
     * @return Lista completa de unidades ordenadas por nombre
     */
    public List<UnidadMedida> listarTodasLasUnidades() throws SQLException {
        return unidadMedidaRepo.findAll();
    }

    // ============================================================
    //              VALIDACIONES Y UTILIDADES
    // ============================================================

    /**
     * Verifica si existe una unidad con el nombre dado.
     *
     * @param nombre Nombre a verificar
     * @return true si existe, false si no
     */
    public boolean existeUnidad(String nombre) throws SQLException {
        return unidadMedidaRepo.existsByNombre(nombre);
    }

    /**
     * Verifica si existe una unidad con el ID dado.
     *
     * @param idUnidad ID a verificar
     * @return true si existe, false si no
     */
    public boolean existeUnidad(int idUnidad) throws SQLException {
        return unidadMedidaRepo.existsById(idUnidad);
    }

    // ============================================================
    //        INICIALIZACIÓN DE UNIDADES COMUNES (OPCIONAL)
    // ============================================================

    /**
     * Inicializa unidades de medida comunes si no existen en la BD.
     *
     * Unidades predeterminadas:
     *  - Masa: kg, g, mg
     *  - Volumen: l, ml
     *  - Cantidad: unidades, tabletas, cápsulas, sobres
     *  - Longitud: m, cm
     *
     * Este método es IDEMPOTENTE (se puede llamar múltiples veces sin duplicar).
     * Útil para inicializar la BD en el primer arranque.
     *
     * @return Cantidad de unidades creadas
     * @throws SQLException si hay error en BD
     */
    public int inicializarUnidadesComunes() throws SQLException {
        String[] unidadesComunes = {
            // Masa
            "kg", "g", "mg",
            // Volumen
            "l", "ml", "cc",
            // Cantidad
            "unidades", "tabletas", "cápsulas", "sobres", "ampollas",
            // Longitud
            "m", "cm"
        };

        int creadas = 0;
        for (String nombre : unidadesComunes) {
            try {
                if (!unidadMedidaRepo.existsByNombre(nombre)) {
                    registrarUnidadMedida(nombre);
                    creadas++;
                }
            } catch (IllegalArgumentException e) {
                // Ya existe, continuar
            }
        }
        return creadas;
    }

    /**
     * Prepobla la tabla unidadmedida con las unidades estándar más comunes
     * usadas en veterinarias.
     *
     * ✅ Este método SOLO SE EJECUTA UNA VEZ durante toda la ejecución de la aplicación.
     * Usa un flag estático para evitar consultas innecesarias a la BD.
     *
     * Unidades que se precargan:
     * - Unidad, Caja, Kilogramo, Gramo, Litro, Mililitro
     * - Tableta, Cápsula, Frasco, Ampolla
     * - Paquete, Tarro, Rollo, Bolsa
     *
     * USO:
     * - Se llama automáticamente al abrir registerProduct-view
     * - Solo se ejecuta en el primer acceso
     *
     * @return Cantidad de unidades nuevas insertadas (0 si ya se ejecutó antes)
     */
    public int prepoblarUnidadesEstandar() {
        // ✅ Si ya se ejecutó antes, salir inmediatamente
        if (unidadesYaPrepobladas) {
            System.out.println("⏭️  Unidades de medida ya fueron prepobladas anteriormente. Omitiendo...");
            return 0;
        }

        System.out.println("🔄 Prepoblando unidades de medida estándar...");

        String[] unidadesEstandar = {
            "Unidad",
            "Caja",
            "Kilogramo",
            "Gramo",
            "Litro",
            "Mililitro",
            "Tableta",
            "Cápsula",
            "Frasco",
            "Ampolla",
            "Paquete",
            "Tarro",
            "Rollo",
            "Bolsa"
        };

        int insertadas = 0;

        for (String nombreUnidad : unidadesEstandar) {
            try {
                // Solo insertar si NO existe
                if (!unidadMedidaRepo.existsByNombre(nombreUnidad)) {
                    UnidadMedida unidad = new UnidadMedida();
                    unidad.setNombre(nombreUnidad);
                    unidadMedidaRepo.create(unidad);
                    insertadas++;
                    System.out.println("  ✅ Unidad de medida creada: " + nombreUnidad);
                }
            } catch (SQLException e) {
                System.err.println("  ❌ Error al insertar unidad '" + nombreUnidad + "': " + e.getMessage());
            }
        }

        // ✅ Marcar que ya se ejecutó el prepoblado
        unidadesYaPrepobladas = true;

        if (insertadas > 0) {
            System.out.println("📦 Prepoblado completado: " + insertadas + " unidades nuevas insertadas");
        } else {
            System.out.println("✅ Todas las unidades de medida ya existían en la BD");
        }

        return insertadas;
    }

    // ============================================================
    //              VALIDACIONES PRIVADAS
    // ============================================================

    /**
     * Valida el nombre de una unidad de medida.
     *
     * @throws IllegalArgumentException si el nombre es inválido
     */
    private void validarNombreUnidad(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la unidad de medida es requerido");
        }

        if (nombre.trim().length() < 1) {
            throw new IllegalArgumentException("El nombre de la unidad debe tener al menos 1 carácter");
        }

        if (nombre.trim().length() > 50) {
            throw new IllegalArgumentException("El nombre de la unidad no puede superar 50 caracteres");
        }
    }
}
