package co.edu.upb.veterinaria.controllers.ControllerRegisterProduct;

import co.edu.upb.veterinaria.models.ModeloMarca.Marca;
import co.edu.upb.veterinaria.models.ModeloProveedor.Proveedor;
import co.edu.upb.veterinaria.models.ModeloUnidadMedida.UnidadMedida;
import co.edu.upb.veterinaria.models.ModeloUsuario.Usuario;
import co.edu.upb.veterinaria.services.ServicioMarca.MarcaService;
import co.edu.upb.veterinaria.services.ServicioProducto.ProductoService;
import co.edu.upb.veterinaria.services.ServicioProveedor.ProveedorService;
import co.edu.upb.veterinaria.services.ServicioUnidadMedida.UnidadMedidaService;
import co.edu.upb.veterinaria.services.ServicioTipoProducto.TipoProductoService;
import co.edu.upb.veterinaria.models.ModeloTipoProducto.TipoProducto;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Locale;

public class productRegister {

    // ====== RUTAS DE NAVEGACIÓN ======
    private static final String MAINMENU_FXML =
            "/co/edu/upb/veterinaria/views/mainMenu-view/mainMenu-view.fxml";
    private static final String SEENOTIFICATIONS_FXML =
            "/co/edu/upb/veterinaria/views/SeeNotifications-view/SeeNotifications-view.fxml";
    private static final String PERSONALDATA_FXML =
            "/co/edu/upb/veterinaria/views/personalData-view/personalData.fxml";
    private static final String LOGIN_FXML =
            "/co/edu/upb/veterinaria/views/Login/Login.fxml";

    private static final String INVENTARY_FXML =
            "/co/edu/upb/veterinaria/views/inventary-view/inventary-view.fxml";
    private static final String CREATE_USER_FXML =
            "/co/edu/upb/veterinaria/views/createUser-view/createUser-view.fxml";
    private static final String VISUALIZE_REGISTER_FXML =
            "/co/edu/upb/veterinaria/views/visualizeRegister-view/visualizeRegister-view.fxml";
    private static final String SECTION_SALES_FXML =
            "/co/edu/upb/veterinaria/views/SectionSales-view/SectionSales-view.fxml";
    private static final String ADD_CLIENT_FXML =
            "/co/edu/upb/veterinaria/views/AddClient-view/AddClient-view.fxml";
    private static final String ADD_SUPPLIERS_FXML =
            "/co/edu/upb/veterinaria/views/AddSuppliers-view/AddSuppliers-view.fxml";

    private static final String ADD_MARCA_FXML =
            "/co/edu/upb/veterinaria/views/AddMarca-view/AddMarca-view.fxml";

    // ====== SERVICIOS ======
     private final ProductoService productoService;
     private final MarcaService marcaService;
     private final ProveedorService proveedorService;
     private final UnidadMedidaService unidadMedidaService;
     private final TipoProductoService tipoProductoService;

    // Cached tipo IDs (para decisiones robustas)
    private Integer idTipoMedicamento = null;
    private Integer idTipoAlimento = null;
    private Integer idTipoMaterial = null;
    private Integer idTipoAccesorio = null;

    // ====== DATOS TEMPORALES ======
    private File archivoImagenSeleccionado;
    private byte[] imagenBytes;

    // Usuario actual (temporal - luego vendrá del login)
    private Usuario usuarioActual;

    // ====== RAÍZ / HEADER ======
    @FXML private AnchorPane root;
    @FXML private ImageView topLogo;
    @FXML private MenuButton mbNotificaciones;
    @FXML private MenuItem miVerNotificaciones;
    @FXML private MenuButton mbPerfil;
    @FXML private MenuItem miDatosPersonales, miCerrarSesion;

    // ====== MENÚ SUPERIOR ======
    @FXML private Button btnInventario, btnGestionarUsuario, btnVisualizarRegistros,
            btnVentas, btnAgregarClientes, btnAdministrarProveedores;

    // ====== CAMPOS DEL FORMULARIO ======
    @FXML private ComboBox<TipoProducto> cbTipoProducto;
    @FXML private ComboBox<String> cbEstado;

    @FXML private TextField tfNombre;
    @FXML private ComboBox<String> cbDosisUnidad;

    @FXML private TextField tfReferencia;
    @FXML private TextField tfCodigoBarras;
    @FXML private ComboBox<String> cbUnidadMedida;

    @FXML private TextField tfPrecio;
    @FXML private TextField tfCosto;
    @FXML private TextField tfStock;

    @FXML private ComboBox<String> cbMarca;
    @FXML private Button btnAgregarMarca;
    @FXML private ComboBox<String> cbProveedor;

    @FXML private TextField tfLote;
    @FXML private DatePicker dpVencimiento;
    @FXML private TextField tfSemanasParaAlerta;
    @FXML private TextField tfContenido;

    @FXML private CheckBox chkFraccionable;
    @FXML private CheckBox chkFraccionado;

    @FXML private TextArea taDescripcion;

    // ====== CAMPOS DINÁMICOS (no hay VBox en FXML, se manejan directamente) ======
    // Los campos se ocultarán/mostrarán directamente según el tipo de producto

    // ====== IMAGEN ======
    @FXML private ImageView imgProducto;
    @FXML private Label lblNombreArchivo;

    // ====== Acción principal ======
    @FXML private Button btnAgregar;

    // ====== CONSTRUCTOR ======
    public productRegister() {
        this.productoService = new ProductoService();
        this.marcaService = new MarcaService();
        this.proveedorService = new ProveedorService();
        this.unidadMedidaService = new UnidadMedidaService();
        this.tipoProductoService = new TipoProductoService();

        // ✅ CORREGIDO: Usuario será NULL hasta que se implemente el login
        // No creamos usuario temporal porque causaba error de FK
        this.usuarioActual = null;
    }

    @FXML
    private void initialize() {
        // UX: maximiza la ventana al iniciar
        Platform.runLater(() -> {
            Stage stage = safeStage();
            if (stage != null) {
                stage.setResizable(true);
                stage.setMaximized(true);
            }
        });

        // ✅ PREPOBLAR UNIDADES DE MEDIDA ESTÁNDAR (si no existen)
        prepoblarUnidadesDeMedida();

        // ✅ AGREGAR VALIDACIONES EN TIEMPO REAL PARA CAMPOS NUMÉRICOS
        agregarValidacionSoloNumeros(tfPrecio);
        agregarValidacionSoloNumeros(tfCosto);
        agregarValidacionSoloNumerosEnteros(tfStock);
        agregarValidacionSoloNumerosEnteros(tfSemanasParaAlerta);
        agregarValidacionSoloNumeros(tfContenido);

        // Cargar datos en ComboBox
        cargarTiposDeProducto();
        cargarEstados();
        cargarMarcas();
        cargarProveedores();
        cargarUnidadesDeMedida();
        cargarDosisUnidad();

        // Listener para mostrar/ocultar campos según tipo de producto
        cbTipoProducto.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // newVal es TipoProducto
                ajustarCamposSegunTipo(newVal.getNombreTipo());
            }
        });

        // Intentar cachear los ids de tipos para decisiones robustas
        try {
            idTipoMedicamento = tipoProductoService.obtenerIdMedicamento();
        } catch (SQLException ignore) { idTipoMedicamento = null; }
        try {
            idTipoAlimento = tipoProductoService.obtenerIdAlimento();
        } catch (SQLException ignore) { idTipoAlimento = null; }
        try {
            idTipoMaterial = tipoProductoService.obtenerIdMaterialQuirurgico();
        } catch (SQLException ignore) { idTipoMaterial = null; }
        try {
            idTipoAccesorio = tipoProductoService.obtenerIdAccesorio();
        } catch (SQLException ignore) { idTipoAccesorio = null; }

        // Ocultar todos los campos específicos al inicio
        ocultarTodosLosCamposEspecificos();
    }

    // ============================================================
    //        CARGAR TIPOS DE PRODUCTO DESDE BD (NO HARDCODE)
    // ============================================================
    private void cargarTiposDeProducto() {
        cbTipoProducto.getItems().clear();
        try {
            List<TipoProducto> tipos = tipoProductoService.obtenerTodosTipos();
            if (tipos == null || tipos.isEmpty()) {
                // Si por alguna razón no hay tipos (debería prepoblarlos el servicio), usar fallback
                cbTipoProducto.getItems().addAll(new ArrayList<>());
                return;
            }

            // Añadimos los objetos TipoProducto directamente. ComboBox usará toString() para mostrarlos.
            cbTipoProducto.getItems().addAll(tipos);
        } catch (SQLException e) {
            // Si ocurre error, mostramos fallback y un warning
            cbTipoProducto.getItems().addAll(new ArrayList<>());
            System.err.println("No se pudieron cargar tipos de producto desde BD: " + e.getMessage());
        }
    }

    /** Normaliza texto: minúsculas y sin acentos para matching robusto. */
    private String normalizeKey(String s) {
        if (s == null) return "";
        String n = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim();
        return n;
    }

    // ============================================================
    //         VALIDACIONES EN TIEMPO REAL (SOLO NÚMEROS)
    // ============================================================

    /**
     * Agrega validación para que un TextField solo acepte números decimales.
     */
    private void agregarValidacionSoloNumeros(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                textField.setText(oldValue);
                mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Dato inválido",
                    "Este campo solo acepta números (enteros o decimales)."
                );
            }
        });
    }

    /**
     * Agrega validación para que un TextField solo acepte números enteros.
     */
    private void agregarValidacionSoloNumerosEnteros(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(oldValue);
                mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Dato inválido",
                    "Este campo solo acepta números enteros."
                );
            }
        });
    }

    // ============================================================
    //         CARGAR DATOS EN COMBOBOX DESDE BD
    // ============================================================

    /**
     * Carga los estados posibles (ACTIVO, INACTIVO).
     */
    private void cargarEstados() {
        cbEstado.getItems().clear();
        cbEstado.getItems().addAll("ACTIVO", "INACTIVO");
        cbEstado.setValue("ACTIVO"); // Por defecto ACTIVO
    }

    /**
     * Carga las marcas desde la BD.
     */
    private void cargarMarcas() {
        try {
            List<Marca> marcas = marcaService.listarTodasLasMarcas();
            cbMarca.getItems().clear();

            for (Marca marca : marcas) {
                // Formato: "ID - Nombre"
                cbMarca.getItems().add(marca.getIdMarca() + " - " + marca.getNombreMarca());
            }
        } catch (SQLException e) {
            mostrarAlerta(
                Alert.AlertType.ERROR,
                "Error al cargar marcas",
                "No se pudieron cargar las marcas desde la base de datos.\n\n" + e.getMessage()
            );
            e.printStackTrace();
        }
    }

    /**
     * Carga los proveedores desde la BD.
     */
    private void cargarProveedores() {
        List<Proveedor> proveedores = proveedorService.obtenerTodosLosProveedores();
        cbProveedor.getItems().clear();
        cbProveedor.getItems().add("(Sin proveedor)"); // Opción para no seleccionar proveedor

        for (Proveedor proveedor : proveedores) {
            // Formato: "ID - Nombre Apellido"
            cbProveedor.getItems().add(
                proveedor.getIdProveedor() + " - " +
                proveedor.getNombre() + " " +
                proveedor.getApellido()
            );
        }
        cbProveedor.setValue("(Sin proveedor)");
    }

    /**
     * Carga las unidades de medida desde la BD.
     */
    private void cargarUnidadesDeMedida() {
        try {
            // ✅ CORREGIDO: era listarTodasLasUnidadesDeMedida, ahora es listarTodasLasUnidades
            List<UnidadMedida> unidades = unidadMedidaService.listarTodasLasUnidades();
            cbUnidadMedida.getItems().clear();

            for (UnidadMedida unidad : unidades) {
                // Formato: "ID - Nombre"
                cbUnidadMedida.getItems().add(unidad.getIdUnidadMedida() + " - " + unidad.getNombre());
            }
        } catch (SQLException e) {
            mostrarAlerta(
                Alert.AlertType.ERROR,
                "Error al cargar unidades de medida",
                "No se pudieron cargar las unidades de medida.\n\n" + e.getMessage()
            );
            e.printStackTrace();
        }
    }

    /**
     * Carga las opciones de dosis/unidad (para medicamentos).
     * ✅ OPCIONES MÁS COMPLETAS Y USADAS EN VETERINARIA
     */
    private void cargarDosisUnidad() {
        cbDosisUnidad.getItems().clear();
        cbDosisUnidad.getItems().addAll(
            // Volúmenes líquidos (más comunes)
            "1 ml",
            "2 ml",
            "5 ml",
            "10 ml",
            "20 ml",
            "50 ml",
            "100 ml",
            "250 ml",
            "500 ml",
            "1 L",

            // Pesos sólidos (más comunes)
            "1 g",
            "2 g",
            "5 g",
            "10 g",
            "25 g",
            "50 g",
            "100 g",
            "250 g",
            "500 g",
            "1 kg",

            // Miligramos (medicamentos concentrados)
            "10 mg",
            "25 mg",
            "50 mg",
            "100 mg",
            "250 mg",
            "500 mg",

            // Unidades farmacéuticas
            "UI (Unidades Internacionales)",
            "Tabletas",
            "Cápsulas",
            "Ampollas",
            "Sobres",
            "Frascos"
        );
    }

    // ============================================================
    //      MOSTRAR/OCULTAR CAMPOS SEGÚN TIPO DE PRODUCTO
    // ============================================================

    /**
     * Oculta todos los campos específicos.
     * Como los campos están en el GridPane sin VBox, los ocultamos directamente.
     */
    private void ocultarTodosLosCamposEspecificos() {
        // Ocultar campos específicos que NO son base
        if (tfLote != null) {
            tfLote.setVisible(false);
            tfLote.setManaged(false);
        }

        if (dpVencimiento != null) {
            dpVencimiento.setVisible(false);
            dpVencimiento.setManaged(false);
        }

        if (tfSemanasParaAlerta != null) {
            tfSemanasParaAlerta.setVisible(false);
            tfSemanasParaAlerta.setManaged(false);
        }

        if (tfContenido != null) {
            tfContenido.setVisible(false);
            tfContenido.setManaged(false);
        }

        if (chkFraccionable != null) {
            chkFraccionable.setVisible(false);
            chkFraccionable.setManaged(false);
        }

        if (chkFraccionado != null) {
            chkFraccionado.setVisible(false);
            chkFraccionado.setManaged(false);
        }

        if (cbDosisUnidad != null) {
            cbDosisUnidad.setVisible(false);
            cbDosisUnidad.setManaged(false);
        }

        if (cbUnidadMedida != null) {
            cbUnidadMedida.setVisible(false);
            cbUnidadMedida.setManaged(false);
        }
    }

    /**
     * Ajusta los campos visibles según el tipo de producto seleccionado.
     *
     * CAMPOS BASE (SIEMPRE VISIBLES para todos los tipos):
     * - Tipo de producto, Estado
     * - Nombre, Referencia, Código de barras
     * - Precio, Costo, Stock
     * - Marca, Proveedor
     * - Descripción, Imagen
     *
     * CAMPOS ESPECÍFICOS (según tipo):
     * - Medicamento: Lote, Vencimiento, Semanas alerta, Contenido, Dosis/Unidad, Unidad medida, Fraccionable, Fraccionado
     * - Alimento: Lote, Vencimiento, Semanas alerta, Contenido, Unidad medida
     * - Material Quirúrgico: Lote, Vencimiento (opcional), Semanas alerta, Contenido, Unidad medida, Fraccionable, Fraccionado
     * - Juguete/Accesorio: Solo campos base
     */
    private void ajustarCamposSegunTipo(String tipo) {
        // Primero ocultar todos los campos específicos
        ocultarTodosLosCamposEspecificos();

        String key = normalizeKey(tipo);

        if (key.contains("medic")) {
            mostrarCampo(tfLote);
            mostrarCampo(dpVencimiento);
            mostrarCampo(tfSemanasParaAlerta);
            mostrarCampo(tfContenido);
            mostrarCampo(cbDosisUnidad);
            mostrarCampo(cbUnidadMedida);
            mostrarCampo(chkFraccionable);
            mostrarCampo(chkFraccionado);
            return;
        }

        if (key.contains("aliment")) {
            mostrarCampo(tfLote);
            mostrarCampo(dpVencimiento);
            mostrarCampo(tfSemanasParaAlerta);
            mostrarCampo(tfContenido);
            mostrarCampo(cbUnidadMedida);
            return;
        }

        if (key.contains("material")) {
            mostrarCampo(tfLote);
            mostrarCampo(dpVencimiento);
            mostrarCampo(tfSemanasParaAlerta);
            mostrarCampo(tfContenido);
            mostrarCampo(cbUnidadMedida);
            mostrarCampo(chkFraccionable);
            mostrarCampo(chkFraccionado);
            return;
        }

        if (key.contains("acces") || key.contains("juguet")) {
            // accesorio/juguete: no campos adicionales
            return;
        }
    }

    /**
     * Muestra un campo específico (Control genérico).
     */
    private void mostrarCampo(Control campo) {
        if (campo != null) {
            campo.setVisible(true);
            campo.setManaged(true);
        }
    }

    // ============================================================
    //              GUARDAR PRODUCTO (MÉTODO PRINCIPAL)
    // ============================================================

    /**
     * Guarda el producto en la base de datos según el tipo seleccionado.
     */
    @FXML
    private void onAgregar() {
        try {
            // Validar que se haya seleccionado un tipo de producto
            TipoProducto tipoProdObj = cbTipoProducto.getValue();
            if (tipoProdObj == null) {
                mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Tipo de producto requerido",
                    "Debe seleccionar un tipo de producto."
                );
                return;
            }

            // Validar campos base
            if (!validarCamposBase()) {
                return;
            }

            // Guardar según el tipo seleccionado: usar ID del TipoProducto (más robusto)
            int tipoSeleccionadoId = tipoProdObj.getIdTipoProducto();
            int idProductoCreado;

            if (idTipoMedicamento != null && tipoSeleccionadoId == idTipoMedicamento) {
                idProductoCreado = guardarMedicamento();
            } else if (idTipoAlimento != null && tipoSeleccionadoId == idTipoAlimento) {
                idProductoCreado = guardarAlimento();
            } else if (idTipoMaterial != null && tipoSeleccionadoId == idTipoMaterial) {
                idProductoCreado = guardarMaterialQuirurgico();
            } else if (idTipoAccesorio != null && tipoSeleccionadoId == idTipoAccesorio) {
                idProductoCreado = guardarAccesorio();
            } else {
                // Fallback: usar nombre normalizado (por compatibilidad si no pudimos cachear IDs)
                String tipoProducto = tipoProdObj.getNombreTipo();
                String keyTipo = normalizeKey(tipoProducto);
                if (keyTipo.contains("medic")) {
                    idProductoCreado = guardarMedicamento();
                } else if (keyTipo.contains("aliment")) {
                    idProductoCreado = guardarAlimento();
                } else if (keyTipo.contains("material")) {
                    idProductoCreado = guardarMaterialQuirurgico();
                } else if (keyTipo.contains("acces") || keyTipo.contains("juguet")) {
                    idProductoCreado = guardarAccesorio();
                } else {
                    mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "Tipo no válido",
                        "El tipo de producto seleccionado no es válido."
                    );
                    return;
                }
            }

            // Mostrar mensaje de éxito
            mostrarAlerta(
                Alert.AlertType.INFORMATION,
                "Producto registrado exitosamente",
                "El producto '" + tfNombre.getText() + "' fue registrado con ID: " + idProductoCreado +
                "\n\nYa está disponible en el inventario."
            );

            // Limpiar formulario
            limpiarFormulario();

        } catch (IllegalArgumentException e) {
            mostrarAlerta(
                Alert.AlertType.ERROR,
                "Error de validación",
                e.getMessage()
            );
        } catch (SQLException e) {
            mostrarAlerta(
                Alert.AlertType.ERROR,
                "Error al guardar en la base de datos",
                "No se pudo registrar el producto.\n\n" + e.getMessage()
            );
            e.printStackTrace();
        } catch (Exception e) {
            mostrarAlerta(
                Alert.AlertType.ERROR,
                "Error inesperado",
                "Ocurrió un error al intentar guardar el producto.\n\n" + e.getMessage()
            );
            e.printStackTrace();
        }
    }


    /**
     * Guarda un medicamento.
     */
    private int guardarMedicamento() throws SQLException {
        // ✅ VALIDAR CAMPOS ESPECÍFICOS OBLIGATORIOS PARA MEDICAMENTO
        if (!validarCamposMedicamento()) {
            throw new IllegalArgumentException("Complete todos los campos obligatorios para medicamento");
        }

        // Campos base
        String nombre = tfNombre.getText().trim();
        String referencia = tfReferencia.getText().trim();
        String codigoBarras = tfCodigoBarras.getText().trim();
        double precio = Double.parseDouble(tfPrecio.getText().trim());
        double costo = Double.parseDouble(tfCosto.getText().trim());
        int stock = Integer.parseInt(tfStock.getText().trim());
        Marca marca = extraerMarca();
        UnidadMedida unidadMedida = extraerUnidadMedida();
        Proveedor proveedor = extraerProveedor();
        String descripcion = taDescripcion.getText().trim();
        String estado = cbEstado.getValue();
        byte[] imagen = imagenBytes;

        // Campos específicos de medicamento
        String lote = tfLote.getText().trim();
        Date fechaVencimiento = obtenerFechaVencimiento();
        int semanasParaAlerta = Integer.parseInt(tfSemanasParaAlerta.getText().trim());
        boolean fraccionable = chkFraccionable.isSelected();
        boolean fraccionado = chkFraccionado.isSelected();
        double contenido = Double.parseDouble(tfContenido.getText().trim());
        double dosisPorUnidad = extraerDosisUnidad();

        return productoService.registrarMedicamento(
            nombre, referencia, codigoBarras, precio, costo, stock,
            marca, unidadMedida, proveedor, descripcion, estado, imagen, usuarioActual,
            lote, fechaVencimiento, semanasParaAlerta, fraccionable, fraccionado,
            contenido, dosisPorUnidad
        );
    }

    /**
     * Guarda un alimento.
     */
    private int guardarAlimento() throws SQLException {
        // ✅ VALIDAR CAMPOS ESPECÍFICOS OBLIGATORIOS PARA ALIMENTO
        if (!validarCamposAlimento()) {
            throw new IllegalArgumentException("Complete todos los campos obligatorios para alimento");
        }

        // Campos base
        String nombre = tfNombre.getText().trim();
        String referencia = tfReferencia.getText().trim();
        String codigoBarras = tfCodigoBarras.getText().trim();
        double precio = Double.parseDouble(tfPrecio.getText().trim());
        double costo = Double.parseDouble(tfCosto.getText().trim());
        int stock = Integer.parseInt(tfStock.getText().trim());
        Marca marca = extraerMarca();
        UnidadMedida unidadMedida = extraerUnidadMedida();
        Proveedor proveedor = extraerProveedor();
        String descripcion = taDescripcion.getText().trim();
        String estado = cbEstado.getValue();
        byte[] imagen = imagenBytes;

        // Campos específicos de alimento
        String lote = tfLote.getText().trim();
        Date fechaVencimiento = obtenerFechaVencimiento();
        int semanasParaAlerta = Integer.parseInt(tfSemanasParaAlerta.getText().trim());
        double contenido = Double.parseDouble(tfContenido.getText().trim());

        return productoService.registrarAlimento(
            nombre, referencia, codigoBarras, precio, costo, stock,
            marca, unidadMedida, proveedor, descripcion, estado, imagen, usuarioActual,
            lote, fechaVencimiento, semanasParaAlerta, contenido
        );
    }

    /**
     * Guarda un material quirúrgico.
     */
    private int guardarMaterialQuirurgico() throws SQLException {
        // ✅ VALIDAR CAMPOS ESPECÍFICOS OBLIGATORIOS PARA MATERIAL QUIRÚRGICO
        if (!validarCamposMaterialQuirurgico()) {
            throw new IllegalArgumentException("Complete todos los campos obligatorios para material quirúrgico");
        }

        // Campos base
        String nombre = tfNombre.getText().trim();
        String referencia = tfReferencia.getText().trim();
        String codigoBarras = tfCodigoBarras.getText().trim();
        double precio = Double.parseDouble(tfPrecio.getText().trim());
        double costo = Double.parseDouble(tfCosto.getText().trim());
        int stock = Integer.parseInt(tfStock.getText().trim());
        Marca marca = extraerMarca();
        UnidadMedida unidadMedida = extraerUnidadMedida();
        Proveedor proveedor = extraerProveedor();
        String descripcion = taDescripcion.getText().trim();
        String estado = cbEstado.getValue();
        byte[] imagen = imagenBytes;

        // Campos específicos de material quirúrgico
        String lote = tfLote.getText().trim();
        Date fechaVencimiento = obtenerFechaVencimientoOpcional(); // ✅ OPCIONAL
        int semanasParaAlerta = Integer.parseInt(tfSemanasParaAlerta.getText().trim());
        boolean fraccionable = chkFraccionable.isSelected();
        boolean fraccionado = chkFraccionado.isSelected();
        double contenido = Double.parseDouble(tfContenido.getText().trim());

        return productoService.registrarMaterialQuirurgico(
            nombre, referencia, codigoBarras, precio, costo, stock,
            marca, unidadMedida, proveedor, descripcion, estado, imagen, usuarioActual,
            lote, fechaVencimiento, semanasParaAlerta, fraccionable, fraccionado, contenido
        );
    }

    /**
     * Guarda un accesorio/juguete.
     */
    private int guardarAccesorio() throws SQLException {
        // ✅ ACCESORIO: Solo necesita campos base (ya validados en validarCamposBase)

        // Solo campos base
        String nombre = tfNombre.getText().trim();
        String referencia = tfReferencia.getText().trim();
        String codigoBarras = tfCodigoBarras.getText().trim();
        double precio = Double.parseDouble(tfPrecio.getText().trim());
        double costo = Double.parseDouble(tfCosto.getText().trim());
        int stock = Integer.parseInt(tfStock.getText().trim());
        Marca marca = extraerMarca();
        Proveedor proveedor = extraerProveedor();
        String descripcion = taDescripcion.getText().trim();
        String estado = cbEstado.getValue();
        byte[] imagen = imagenBytes;

        return productoService.registrarAccesorio(
            nombre, referencia, codigoBarras, precio, costo, stock,
            marca, proveedor, descripcion, estado, imagen, usuarioActual
        );
    }

    // ============================================================
    //      VALIDACIONES ESPECÍFICAS POR TIPO DE PRODUCTO
    // ============================================================

    /**
     * Valida campos específicos obligatorios para MEDICAMENTO.
     * Obligatorios: Lote, Vencimiento, Semanas alerta, Contenido, Dosis/Unidad, Unidad medida
     * Opcionales: Fraccionable, Fraccionado
     */
    private boolean validarCamposMedicamento() {
        // Lote (obligatorio)
        if (tfLote.getText() == null || tfLote.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo requerido",
                "El lote es obligatorio para medicamentos.");
            tfLote.requestFocus();
            return false;
        }

        // Fecha de vencimiento (obligatorio)
        if (dpVencimiento.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo requerido",
                "La fecha de vencimiento es obligatoria para medicamentos.");
            dpVencimiento.requestFocus();
            return false;
        }

        // Semanas para alerta (obligatorio)
        if (tfSemanasParaAlerta.getText() == null || tfSemanasParaAlerta.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo requerido",
                "Las semanas para alerta son obligatorias.");
            tfSemanasParaAlerta.requestFocus();
            return false;
        }
        try {
            int semanas = Integer.parseInt(tfSemanasParaAlerta.getText().trim());
            if (semanas <= 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Valor inválido",
                    "Las semanas para alerta deben ser mayor a 0.");
                tfSemanasParaAlerta.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Valor inválido",
                "Ingrese un número válido para semanas de alerta.");
            tfSemanasParaAlerta.requestFocus();
            return false;
        }

        // Contenido (obligatorio)
        if (tfContenido.getText() == null || tfContenido.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo requerido",
                "El contenido es obligatorio para medicamentos.");
            tfContenido.requestFocus();
            return false;
        }
        try {
            double contenido = Double.parseDouble(tfContenido.getText().trim());
            if (contenido <= 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Valor inválido",
                    "El contenido debe ser mayor a 0.");
                tfContenido.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Valor inválido",
                "Ingrese un número válido para el contenido.");
            tfContenido.requestFocus();
            return false;
        }

        // Dosis/Unidad (obligatorio)
        if (cbDosisUnidad.getValue() == null || cbDosisUnidad.getValue().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo requerido",
                "La dosis por unidad es obligatoria para medicamentos.");
            cbDosisUnidad.requestFocus();
            return false;
        }

        // Unidad de medida (obligatorio)
        if (cbUnidadMedida.getValue() == null || cbUnidadMedida.getValue().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo requerido",
                "La unidad de medida es obligatoria para medicamentos.");
            cbUnidadMedida.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Valida campos específicos obligatorios para ALIMENTO.
     * Obligatorios: Lote, Vencimiento, Semanas alerta, Contenido, Unidad medida
     */
    private boolean validarCamposAlimento() {
        // Lote (obligatorio)
        if (tfLote.getText() == null || tfLote.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo requerido",
                "El lote es obligatorio para alimentos.");
            tfLote.requestFocus();
            return false;
        }

        // Fecha de vencimiento (obligatorio)
        if (dpVencimiento.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo requerido",
                "La fecha de vencimiento es obligatoria para alimentos.");
            dpVencimiento.requestFocus();
            return false;
        }

        // Semanas para alerta (obligatorio)
        if (tfSemanasParaAlerta.getText() == null || tfSemanasParaAlerta.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo requerido",
                "Las semanas para alerta son obligatorias.");
            tfSemanasParaAlerta.requestFocus();
            return false;
        }
        try {
            int semanas = Integer.parseInt(tfSemanasParaAlerta.getText().trim());
            if (semanas <= 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Valor inválido",
                    "Las semanas para alerta deben ser mayor a 0.");
                tfSemanasParaAlerta.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Valor inválido",
                "Ingrese un número válido para semanas de alerta.");
            tfSemanasParaAlerta.requestFocus();
            return false;
        }

        // Contenido (obligatorio)
        if (tfContenido.getText() == null || tfContenido.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo requerido",
                "El contenido es obligatorio para alimentos.");
            tfContenido.requestFocus();
            return false;
        }
        try {
            double contenido = Double.parseDouble(tfContenido.getText().trim());
            if (contenido <= 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Valor inválido",
                    "El contenido debe ser mayor a 0.");
                tfContenido.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Valor inválido",
                "Ingrese un número válido para el contenido.");
            tfContenido.requestFocus();
            return false;
        }

        // Unidad de medida (obligatorio)
        if (cbUnidadMedida.getValue() == null || cbUnidadMedida.getValue().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo requerido",
                "La unidad de medida es obligatoria para alimentos.");
            cbUnidadMedida.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Valida campos específicos obligatorios para MATERIAL QUIRÚRGICO.
     * Obligatorios: Lote, Semanas alerta, Contenido
     * Opcionales: Vencimiento, Unidad medida, Fraccionable, Fraccionado
     */
    private boolean validarCamposMaterialQuirurgico() {
        // Lote (obligatorio)
        if (tfLote.getText() == null || tfLote.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo requerido",
                "El lote es obligatorio para material quirúrgico.");
            tfLote.requestFocus();
            return false;
        }

        // Semanas para alerta (obligatorio)
        if (tfSemanasParaAlerta.getText() == null || tfSemanasParaAlerta.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo requerido",
                "Las semanas para alerta son obligatorias.");
            tfSemanasParaAlerta.requestFocus();
            return false;
        }
        try {
            int semanas = Integer.parseInt(tfSemanasParaAlerta.getText().trim());
            if (semanas <= 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Valor inválido",
                    "Las semanas para alerta deben ser mayor a 0.");
                tfSemanasParaAlerta.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Valor inválido",
                "Ingrese un número válido para semanas de alerta.");
            tfSemanasParaAlerta.requestFocus();
            return false;
        }

        // Contenido (obligatorio)
        if (tfContenido.getText() == null || tfContenido.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo requerido",
                "El contenido es obligatorio para material quirúrgico.");
            tfContenido.requestFocus();
            return false;
        }
        try {
            double contenido = Double.parseDouble(tfContenido.getText().trim());
            if (contenido <= 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Valor inválido",
                    "El contenido debe ser mayor a 0.");
                tfContenido.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Valor inválido",
                "Ingrese un número válido para el contenido.");
            tfContenido.requestFocus();
            return false;
        }

        // ✅ Vencimiento es OPCIONAL para material quirúrgico (algunos no vencen)
        // ✅ Unidad de medida es OPCIONAL
        // ✅ Fraccionable y Fraccionado son OPCIONALES (checkboxes)

        return true;
    }

    // ============================================================
    //              VALIDACIONES Y EXTRACCIONES
    // ============================================================

    /**
     * Valida los campos base del formulario.
     */
    private boolean validarCamposBase() {
        if (tfNombre.getText() == null || tfNombre.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo requerido", "El nombre es obligatorio.");
            tfNombre.requestFocus();
            return false;
        }

        if (tfReferencia.getText() == null || tfReferencia.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo requerido", "La referencia es obligatoria.");
            tfReferencia.requestFocus();
            return false;
        }

        if (tfCodigoBarras.getText() == null || tfCodigoBarras.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo requerido", "El código de barras es obligatorio.");
            tfCodigoBarras.requestFocus();
            return false;
        }

        try {
            double precio = Double.parseDouble(tfPrecio.getText().trim());
            if (precio <= 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Precio inválido", "El precio debe ser mayor a 0.");
                tfPrecio.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Precio inválido", "Ingrese un precio válido.");
            tfPrecio.requestFocus();
            return false;
        }

        try {
            double costo = Double.parseDouble(tfCosto.getText().trim());
            if (costo <= 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Costo inválido", "El costo debe ser mayor a 0.");
                tfCosto.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Costo inválido", "Ingrese un costo válido.");
            tfCosto.requestFocus();
            return false;
        }

        try {
            int stock = Integer.parseInt(tfStock.getText().trim());
            if (stock < 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Stock inválido", "El stock no puede ser negativo.");
                tfStock.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Stock inválido", "Ingrese un stock válido.");
            tfStock.requestFocus();
            return false;
        }

        if (cbMarca.getValue() == null || cbMarca.getValue().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Marca requerida", "Debe seleccionar una marca.");
            cbMarca.requestFocus();
            return false;
        }

        if (imagenBytes == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Imagen requerida", "Debe subir una imagen del producto.");
            return false;
        }

        return true;
    }

    /**
     * Extrae la marca seleccionada del ComboBox.
     * Formato del ComboBox: "ID - Nombre"
     */
    private Marca extraerMarca() throws SQLException {
        String seleccion = cbMarca.getValue();
        if (seleccion == null || seleccion.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar una marca");
        }

        // Extraer el ID (antes del " - ")
        int idMarca = Integer.parseInt(seleccion.split(" - ")[0]);

        // Buscar la marca completa
        return marcaService.buscarMarcaPorId(idMarca)
            .orElseThrow(() -> new IllegalArgumentException("Marca no encontrada"));
    }

    /**
     * Extrae el proveedor seleccionado del ComboBox.
     * Retorna null si se seleccionó "(Sin proveedor)".
     */
    private Proveedor extraerProveedor() throws SQLException {
        String seleccion = cbProveedor.getValue();
        if (seleccion == null || seleccion.equals("(Sin proveedor)")) {
            return null;
        }

        // Extraer el ID
        int idProveedor = Integer.parseInt(seleccion.split(" - ")[0]);
        return proveedorService.obtenerProveedorPorId(idProveedor);
    }

    /**
     * Extrae la unidad de medida seleccionada del ComboBox.
     */
    private UnidadMedida extraerUnidadMedida() throws SQLException {
        String seleccion = cbUnidadMedida.getValue();
        if (seleccion == null || seleccion.isEmpty()) {
            return null; // Puede ser opcional según el tipo de producto
        }

        // Extraer el ID
        int idUnidad = Integer.parseInt(seleccion.split(" - ")[0]);
        // ✅ CORREGIDO: era buscarUnidadMedidaPorId, ahora es buscarUnidadPorId
        return unidadMedidaService.buscarUnidadPorId(idUnidad)
            .orElseThrow(() -> new IllegalArgumentException("Unidad de medida no encontrada"));
    }

    /**
     * Extrae la dosis por unidad del ComboBox (solo para medicamentos).
     * Retorna el valor numérico por defecto según la unidad seleccionada.
     */
    private double extraerDosisUnidad() {
        String seleccion = cbDosisUnidad.getValue();
        if (seleccion == null || seleccion.isEmpty()) {
            return 0.0;
        }

        // Por ahora retornamos 1.0 por defecto
        // En una implementación completa, podríamos pedir al usuario que ingrese el valor
        return 1.0;
    }

    /**
     * Obtiene la fecha de vencimiento del DatePicker (REQUERIDA).
     */
    private Date obtenerFechaVencimiento() {
        if (dpVencimiento.getValue() == null) {
            throw new IllegalArgumentException("La fecha de vencimiento es requerida");
        }
        return Date.from(dpVencimiento.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Obtiene la fecha de vencimiento del DatePicker (OPCIONAL para material quirúrgico).
     */
    private Date obtenerFechaVencimientoOpcional() {
        if (dpVencimiento.getValue() == null) {
            return null;
        }
        return Date.from(dpVencimiento.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    // ============================================================
    //                  MANEJO DE IMAGEN
    // ============================================================

    /**
     * Abre diálogo para seleccionar imagen del producto.
     */
    @FXML
    private void onSubirFoto() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar imagen del producto");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );
        File f = fc.showOpenDialog(root.getScene().getWindow());

        if (f != null) {
            archivoImagenSeleccionado = f;
            lblNombreArchivo.setText(f.getName());

            try {
                // Mostrar imagen en el ImageView
                imgProducto.setImage(new Image(f.toURI().toString()));

                // Convertir imagen a byte[] para guardar en BD
                imagenBytes = convertirImagenABytes(f);

            } catch (Exception e) {
                mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error al cargar imagen",
                    "No se pudo cargar la imagen seleccionada.\n\n" + e.getMessage()
                );
                e.printStackTrace();
            }
        }
    }

    /**
     * Quita la imagen seleccionada.
     */
    @FXML
    private void onQuitarFoto() {
        imgProducto.setImage(null);
        lblNombreArchivo.setText("");
        archivoImagenSeleccionado = null;
        imagenBytes = null;
    }

    /**
     * Convierte un archivo de imagen a byte[].
     */
    private byte[] convertirImagenABytes(File archivo) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(archivo);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String extension = archivo.getName().substring(archivo.getName().lastIndexOf('.') + 1);
        ImageIO.write(bufferedImage, extension, baos);
        return baos.toByteArray();
    }

    // ============================================================
    //                  LIMPIAR FORMULARIO
    // ============================================================

    /**
     * Limpia todos los campos del formulario.
     */
    private void limpiarFormulario() {
        tfNombre.clear();
        tfReferencia.clear();
        tfCodigoBarras.clear();
        tfPrecio.clear();
        tfCosto.clear();
        tfStock.clear();
        tfLote.clear();
        tfSemanasParaAlerta.clear();
        tfContenido.clear();
        taDescripcion.clear();

        cbTipoProducto.setValue(null);
        cbEstado.setValue("ACTIVO");
        cbMarca.setValue(null);
        cbProveedor.setValue("(Sin proveedor)");
        cbUnidadMedida.setValue(null);
        cbDosisUnidad.setValue(null);

        dpVencimiento.setValue(null);

        chkFraccionable.setSelected(false);
        chkFraccionado.setSelected(false);

        onQuitarFoto();

        ocultarTodosLosCamposEspecificos();
    }

    // ====== Util: Stage seguro ======
    private Stage safeStage() {
        if (root != null && root.getScene() != null) return (Stage) root.getScene().getWindow();
        Window w = Window.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null);
        return (w instanceof Stage) ? (Stage) w : null;
    }

    // ====== Util: navegación genérica ======
    private void goTo(String fxml) {
        try {
            var url = getClass().getResource(fxml);
            if (url == null) throw new IllegalStateException("NO existe el recurso (classpath): " + fxml);
            FXMLLoader loader = new FXMLLoader(url);
            Parent newRoot = loader.load();
            Scene scene = root.getScene();
            scene.setRoot(newRoot);
            ((Stage) scene.getWindow()).centerOnScreen();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir: " + fxml + "\n" + e.getMessage()).showAndWait();
        }
    }

    // ====== Header ======
    @FXML private void onLogoClick()         { goTo(MAINMENU_FXML); }
    @FXML private void onVerNotificaciones() { goTo(SEENOTIFICATIONS_FXML); }
    @FXML private void onDatosPersonales()   { goTo(PERSONALDATA_FXML); }
    @FXML private void onCerrarSesion()      { goTo(LOGIN_FXML); }

    // ====== Menú superior ======
    @FXML private void onInventario()              { goTo(INVENTARY_FXML); }
    @FXML private void onGestionarUsuario()        { goTo(CREATE_USER_FXML); }
    @FXML private void onVisualizarRegistros()     { goTo(VISUALIZE_REGISTER_FXML); }
    @FXML private void onVentas()                  { goTo(SECTION_SALES_FXML); }
    @FXML private void onAgregarClientes()         { goTo(ADD_CLIENT_FXML); }
    @FXML private void onAdministrarProveedores()  { goTo(ADD_SUPPLIERS_FXML); }

    // ====== Marca: ir a AddMarca ======
    @FXML
    private void onAgregarMarca() {
        goTo(ADD_MARCA_FXML);
    }

    // ====== Util: Alertas ======
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // ============================================================
    //         PREPOBLAR UNIDADES DE MEDIDA
    // ============================================================

    /**
     * Prepobla las unidades de medida estándar en la BD si no existen.
     * Se ejecuta automáticamente al iniciar la vista.
     */
    private void prepoblarUnidadesDeMedida() {
        try {
            int insertadas = unidadMedidaService.prepoblarUnidadesEstandar();
            if (insertadas > 0) {
                System.out.println("✅ Se prepoblaron " + insertadas + " unidades de medida en la BD");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error al prepoblar unidades de medida: " + e.getMessage());
            e.printStackTrace();
            // No mostrar alerta al usuario, ya que es un proceso de fondo
        }
    }
}

