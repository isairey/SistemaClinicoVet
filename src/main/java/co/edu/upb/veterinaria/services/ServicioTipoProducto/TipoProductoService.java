package co.edu.upb.veterinaria.services.ServicioTipoProducto;

import co.edu.upb.veterinaria.models.ModeloTipoProducto.TipoProducto;
import co.edu.upb.veterinaria.repositories.RepositorioTipoProducto.TipoProductoRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * TipoProductoService
 * -------------------
 * Servicio para gestionar tipos de producto.
 * Prepobla automáticamente los 4 tipos estándar al inicializar.
 */
public class TipoProductoService {

    private final TipoProductoRepository tipoProductoRepo;

    // Constantes para los nombres de los tipos (deben coincidir con la BD)
    public static final String TIPO_MEDICAMENTO = "Medicamento";
    public static final String TIPO_ALIMENTO = "Alimento";
    public static final String TIPO_MATERIAL_QUIRURGICO = "Material Quirúrgico";
    public static final String TIPO_ACCESORIO = "Accesorio/Juguete";

    public TipoProductoService() {
        this.tipoProductoRepo = new TipoProductoRepository();
        prepoblarTiposProducto();
    }

    /**
     * Prepobla los 4 tipos de producto estándar SI NO EXISTEN.
     * Se ejecuta automáticamente al crear el servicio.
     */
    private void prepoblarTiposProducto() {
        try {
            // Solo prepoblar si la tabla está vacía
            if (tipoProductoRepo.count() == 0) {
                System.out.println("📦 Prepoblando tipos de producto...");

                String[] tiposBase = {
                    TIPO_MEDICAMENTO,
                    TIPO_ALIMENTO,
                    TIPO_MATERIAL_QUIRURGICO,
                    TIPO_ACCESORIO
                };

                for (String nombreTipo : tiposBase) {
                    TipoProducto tipo = new TipoProducto();
                    tipo.setNombreTipo(nombreTipo);
                    tipoProductoRepo.create(tipo);
                }

                System.out.println("✅ Tipos de producto cargados correctamente.");
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Error al prepoblar tipos de producto: " + e.getMessage());
        }
    }

    /**
     * Obtiene todos los tipos de producto disponibles.
     */
    public List<TipoProducto> obtenerTodosTipos() throws SQLException {
        return tipoProductoRepo.findAll();
    }

    /**
     * Busca un tipo de producto por su nombre exacto.
     */
    public Optional<TipoProducto> buscarPorNombre(String nombreTipo) throws SQLException {
        return tipoProductoRepo.findByNombre(nombreTipo);
    }

    /**
     * Busca un tipo de producto por su ID.
     */
    public Optional<TipoProducto> buscarPorId(int idTipoProducto) throws SQLException {
        return tipoProductoRepo.findById(idTipoProducto);
    }

    /**
     * Obtiene el ID del tipo "Medicamento".
     * @return ID del tipo Medicamento
     * @throws SQLException si no existe
     */
    public int obtenerIdMedicamento() throws SQLException {
        return buscarPorNombre(TIPO_MEDICAMENTO)
                .map(TipoProducto::getIdTipoProducto)
                .orElseThrow(() -> new SQLException("Tipo 'Medicamento' no encontrado en BD"));
    }

    /**
     * Obtiene el ID del tipo "Alimento".
     * @return ID del tipo Alimento
     * @throws SQLException si no existe
     */
    public int obtenerIdAlimento() throws SQLException {
        return buscarPorNombre(TIPO_ALIMENTO)
                .map(TipoProducto::getIdTipoProducto)
                .orElseThrow(() -> new SQLException("Tipo 'Alimento' no encontrado en BD"));
    }

    /**
     * Obtiene el ID del tipo "Material Quirúrgico".
     * @return ID del tipo Material Quirúrgico
     * @throws SQLException si no existe
     */
    public int obtenerIdMaterialQuirurgico() throws SQLException {
        return buscarPorNombre(TIPO_MATERIAL_QUIRURGICO)
                .map(TipoProducto::getIdTipoProducto)
                .orElseThrow(() -> new SQLException("Tipo 'Material Quirúrgico' no encontrado en BD"));
    }

    /**
     * Obtiene el ID del tipo "Accesorio/Juguete".
     * @return ID del tipo Accesorio/Juguete
     * @throws SQLException si no existe
     */
    public int obtenerIdAccesorio() throws SQLException {
        return buscarPorNombre(TIPO_ACCESORIO)
                .map(TipoProducto::getIdTipoProducto)
                .orElseThrow(() -> new SQLException("Tipo 'Accesorio/Juguete' no encontrado en BD"));
    }
}

