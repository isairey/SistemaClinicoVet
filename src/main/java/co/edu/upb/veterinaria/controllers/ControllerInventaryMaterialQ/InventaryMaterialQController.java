package co.edu.upb.veterinaria.controllers.ControllerInventaryMaterialQ;

import co.edu.upb.veterinaria.models.ModeloMaterialQuirurgico.MaterialQuirurgico;
import co.edu.upb.veterinaria.models.ModeloMarca.Marca;
import co.edu.upb.veterinaria.models.ModeloProveedor.Proveedor;
import co.edu.upb.veterinaria.models.ModeloUnidadMedida.UnidadMedida;
import co.edu.upb.veterinaria.services.ServicioMaterialQuirurgico.MaterialQuirurgicoService;
import co.edu.upb.veterinaria.services.ServicioMarca.MarcaService;
import co.edu.upb.veterinaria.services.ServicioProveedor.ProveedorService;
import co.edu.upb.veterinaria.services.ServicioUnidadMedida.UnidadMedidaService;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/** Inventario (Material Quirúrgico) – con funcionalidad completa */
public class InventaryMaterialQController {

    // ===== RUTAS GENERALES =====
    private static final String MAINMENU_FXML =
            "/co/edu/upb/veterinaria/views/mainMenu-view/mainMenu-view.fxml";
    private static final String SEENOTIFICATIONS_FXML =
            "/co/edu/upb/veterinaria/views/SeeNotifications-view/SeeNotifications-view.fxml";
    private static final String PERSONALDATA_FXML =
            "/co/edu/upb/veterinaria/views/personalData-view/personalData.fxml";
    private static final String LOGIN_FXML =
            "/co/edu/upb/veterinaria/views/Login/Login.fxml";

    private static final String REGISTER_PRODUCT_FXML =
            "/co/edu/upb/veterinaria/views/registerProduct-view/registerProduct-view.fxml";
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

    // ===== ENTRE INVENTARIOS =====
    private static final String INVENTORY_FXML =
            "/co/edu/upb/veterinaria/views/inventary-view/inventary-view.fxml";
    private static final String INVENTORY_MEDICAMENT_FXML =
            "/co/edu/upb/veterinaria/views/inventaryMedicamento-view/inventaryMedicament-view.fxml";
    private static final String INVENTORY_ALIMENT_FXML =
            "/co/edu/upb/veterinaria/views/inventaryAliment-view/inventaryAliment-view.fxml";
    private static final String INVENTORY_JUGUETE_FXML =
            "/co/edu/upb/veterinaria/views/inventaryJugueteAccs-view/inventaryJugueteAccs-view.fxml";

    // ===== SERVICIOS =====
    private final MaterialQuirurgicoService materialService;
    private final MarcaService marcaService;
    private final ProveedorService proveedorService;
    private final UnidadMedidaService unidadMedidaService;

    // ===== DATOS =====
    private final ObservableList<MaterialQuirurgico> listaMateriales;
    private final ObservableList<MaterialQuirurgico> listaFiltrada;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    // ===== Root / contenedores =====
    @FXML private AnchorPane root;
    @FXML private ScrollPane scrollTabla;
    @FXML private VBox tableWrapper;
    @FXML private TableView<MaterialQuirurgico> tblInventario;

    // ===== Header =====
    @FXML private ImageView topLogo;
    @FXML private MenuButton mbNotificaciones;
    @FXML private MenuItem   miVerNotificaciones;
    @FXML private MenuButton mbPerfil;
    @FXML private MenuItem   miDatosPersonales, miCerrarSesion;

    // ===== Menú superior =====
    @FXML private Button btnRegistrarProductos, btnGestionarUsuario, btnVisualizarRegistros,
            btnVentas, btnAgregarClientes, btnAdministrarProveedores;

    // ===== Botones de categoría =====
    @FXML private Button btnMedicamento, btnAlimento, btnJuguete;

    // ===== Buscador =====
    @FXML private TextField tfBuscar;
    @FXML private Button btnFiltrar, btnBuscar, btnLimpiar;

    // ===== Acciones =====
    @FXML private Button btnDeshacer, btnEditarSeleccion, btnEliminarSeleccion;

    // ===== Volver =====
    @FXML private Button btnVolver;

    // ===== Columnas =====
    @FXML private TableColumn<MaterialQuirurgico, Integer> colIdProducto;
    @FXML private TableColumn<MaterialQuirurgico, String> colNombre;
    @FXML private TableColumn<MaterialQuirurgico, ImageView> colFoto;
    @FXML private TableColumn<MaterialQuirurgico, String> colReferencia;
    @FXML private TableColumn<MaterialQuirurgico, String> colCodigoBarras;
    @FXML private TableColumn<MaterialQuirurgico, String> colPrecio;
    @FXML private TableColumn<MaterialQuirurgico, String> colCosto;
    @FXML private TableColumn<MaterialQuirurgico, String> colMarca;
    @FXML private TableColumn<MaterialQuirurgico, String> colDescripcion;
    @FXML private TableColumn<MaterialQuirurgico, String> colProveedor;
    @FXML private TableColumn<MaterialQuirurgico, Integer> colStock;
    @FXML private TableColumn<MaterialQuirurgico, String> colLote;
    @FXML private TableColumn<MaterialQuirurgico, String> colFechaCad;
    @FXML private TableColumn<MaterialQuirurgico, Integer> colSemanaAlerta;
    @FXML private TableColumn<MaterialQuirurgico, String> colFraccionable;
    @FXML private TableColumn<MaterialQuirurgico, String> colFraccionado;
    @FXML private TableColumn<MaterialQuirurgico, String> colContenido;

    public InventaryMaterialQController() {
        this.materialService = new MaterialQuirurgicoService();
        this.marcaService = new MarcaService();
        this.proveedorService = new ProveedorService();
        this.unidadMedidaService = new UnidadMedidaService();
        this.listaMateriales = FXCollections.observableArrayList();
        this.listaFiltrada = FXCollections.observableArrayList();
    }

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            Stage stage = (Stage) root.getScene().getWindow();
            if (stage != null) {
                stage.setResizable(true);
                stage.setMaximized(true);
            }
        });

        if (tblInventario != null) {
            tblInventario.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        }

        if (scrollTabla != null) {
            scrollTabla.setFitToWidth(false);
            scrollTabla.setFitToHeight(true);
            scrollTabla.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            scrollTabla.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        }

        configurarColumnas();
        cargarMateriales();
    }

    private void configurarColumnas() {
        colIdProducto.setCellValueFactory(data ->
            new SimpleObjectProperty<>(data.getValue().getIdProducto()));

        colNombre.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getNombre()));

        colFoto.setCellValueFactory(data -> {
            MaterialQuirurgico m = data.getValue();
            ImageView imgView = new ImageView();
            imgView.setFitWidth(80);
            imgView.setFitHeight(80);
            imgView.setPreserveRatio(true);

            if (m.getImagenProducto() != null && m.getImagenProducto().length > 0) {
                try {
                    Image img = new Image(new ByteArrayInputStream(m.getImagenProducto()));
                    imgView.setImage(img);
                } catch (Exception e) {
                    imgView.setImage(null);
                }
            }
            return new SimpleObjectProperty<>(imgView);
        });

        colReferencia.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getReferencia()));

        colCodigoBarras.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getCodigoBarras()));

        colPrecio.setCellValueFactory(data ->
            new SimpleStringProperty(String.format("$%.2f", data.getValue().getPrecio())));

        colCosto.setCellValueFactory(data ->
            new SimpleStringProperty(String.format("$%.2f", data.getValue().getCosto())));

        colMarca.setCellValueFactory(data -> {
            Marca marca = data.getValue().getMarca();
            return new SimpleStringProperty(marca != null ? marca.getNombreMarca() : "N/A");
        });

        colDescripcion.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getDescripcion()));

        colProveedor.setCellValueFactory(data -> {
            Proveedor prov = data.getValue().getProveedor();
            return new SimpleStringProperty(prov != null ? prov.getNombre() : "N/A");
        });

        colStock.setCellValueFactory(data ->
            new SimpleObjectProperty<>(data.getValue().getStock()));

        colLote.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getLote()));

        colFechaCad.setCellValueFactory(data -> {
            Date fecha = data.getValue().getFechaVencimiento();
            return new SimpleStringProperty(fecha != null ? dateFormat.format(fecha) : "N/A");
        });

        colSemanaAlerta.setCellValueFactory(data ->
            new SimpleObjectProperty<>(data.getValue().getSemanaParaAlerta()));

        colFraccionable.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().isFraccionable() ? "Sí" : "No"));

        colFraccionado.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().isFraccionado() ? "Sí" : "No"));

        colContenido.setCellValueFactory(data -> {
            MaterialQuirurgico m = data.getValue();
            UnidadMedida um = m.getUnidadMedida();
            String unidad = um != null ? um.getNombre() : "";
            return new SimpleStringProperty(String.format("%.2f %s", m.getContenido(), unidad));
        });
    }

    private void cargarMateriales() {
        try {
            System.out.println("🔄 Intentando cargar materiales quirúrgicos...");
            List<MaterialQuirurgico> materiales = materialService.listarTodos();
            System.out.println("✅ Materiales quirúrgicos cargados desde BD: " + materiales.size());

            listaMateriales.clear();
            listaMateriales.addAll(materiales);
            listaFiltrada.clear();
            listaFiltrada.addAll(materiales);
            tblInventario.setItems(listaFiltrada);

            System.out.println("✅ Materiales quirúrgicos mostrados en tabla: " + listaFiltrada.size());

            if (materiales.isEmpty()) {
                mostrarInfo("Información", "No hay materiales quirúrgicos registrados en el sistema.\nRegistre productos tipo 'Material Quirúrgico' desde el módulo de Registro de Productos.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al cargar materiales quirúrgicos: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al cargar materiales quirúrgicos",
                "No se pudieron cargar los materiales quirúrgicos: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Error inesperado: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error inesperado", "Error: " + e.getMessage());
        }
    }

    // ===== BÚSQUEDA Y FILTROS =====
    @FXML
    private void onBuscar() {
        String textoBusqueda = tfBuscar.getText();
        if (textoBusqueda == null || textoBusqueda.trim().isEmpty()) {
            listaFiltrada.clear();
            listaFiltrada.addAll(listaMateriales);
            tblInventario.setItems(listaFiltrada);
            return;
        }

        String busqueda = textoBusqueda.toLowerCase().trim();
        listaFiltrada.clear();

        for (MaterialQuirurgico m : listaMateriales) {
            if ((m.getNombre() != null && m.getNombre().toLowerCase().contains(busqueda)) ||
                (m.getReferencia() != null && m.getReferencia().toLowerCase().contains(busqueda)) ||
                (m.getCodigoBarras() != null && m.getCodigoBarras().toLowerCase().contains(busqueda)) ||
                (m.getLote() != null && m.getLote().toLowerCase().contains(busqueda)) ||
                (m.getMarca() != null && m.getMarca().getNombreMarca() != null &&
                    m.getMarca().getNombreMarca().toLowerCase().contains(busqueda)) ||
                (m.getProveedor() != null && m.getProveedor().getNombre() != null &&
                    m.getProveedor().getNombre().toLowerCase().contains(busqueda)) ||
                (m.getDescripcion() != null && m.getDescripcion().toLowerCase().contains(busqueda))) {
                listaFiltrada.add(m);
            }
        }

        tblInventario.setItems(listaFiltrada);
    }

    @FXML
    private void onFiltrar() {
        mostrarDialogoFiltrosAvanzados();
    }

    private void mostrarDialogoFiltrosAvanzados() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Filtros Avanzados - Material Quirúrgico");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        grid.setPadding(new Insets(25));
        grid.setStyle("-fx-background-color: #f5f5f5;");

        int row = 0;

        // Título
        Label titulo = new Label("Filtrar Material Quirúrgico");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #113051;");
        grid.add(titulo, 0, row++, 2, 1);

        // Separador
        Label separador1 = new Label("━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        separador1.setStyle("-fx-text-fill: #0FB9BA;");
        grid.add(separador1, 0, row++, 2, 1);

        // Filtro por marca
        grid.add(new Label("Marca:"), 0, row);
        ComboBox<Marca> cbFiltroMarca = new ComboBox<>();
        cbFiltroMarca.setPromptText("Todas las marcas");
        cbFiltroMarca.setPrefWidth(250);
        try {
            List<Marca> marcas = marcaService.listarTodasLasMarcas();
            cbFiltroMarca.getItems().add(null);
            cbFiltroMarca.getItems().addAll(marcas);
            cbFiltroMarca.setConverter(new javafx.util.StringConverter<>() {
                @Override
                public String toString(Marca marca) {
                    return marca != null ? marca.getNombreMarca() : "Todas las marcas";
                }
                @Override
                public Marca fromString(String string) {
                    return null;
                }
            });
            cbFiltroMarca.getSelectionModel().selectFirst();
        } catch (SQLException e) {
            mostrarError("Error", "No se pudieron cargar las marcas");
        }
        grid.add(cbFiltroMarca, 1, row++);

        // Filtro por proveedor
        grid.add(new Label("Proveedor:"), 0, row);
        ComboBox<Proveedor> cbFiltroProveedor = new ComboBox<>();
        cbFiltroProveedor.setPromptText("Todos los proveedores");
        cbFiltroProveedor.setPrefWidth(250);
        try {
            List<Proveedor> proveedores = proveedorService.obtenerTodosLosProveedores();
            cbFiltroProveedor.getItems().add(null);
            cbFiltroProveedor.getItems().addAll(proveedores);
            cbFiltroProveedor.setConverter(new javafx.util.StringConverter<>() {
                @Override
                public String toString(Proveedor prov) {
                    return prov != null ? prov.getNombre() : "Todos los proveedores";
                }
                @Override
                public Proveedor fromString(String string) {
                    return null;
                }
            });
            cbFiltroProveedor.getSelectionModel().selectFirst();
        } catch (Exception e) {
            mostrarError("Error", "No se pudieron cargar los proveedores");
        }
        grid.add(cbFiltroProveedor, 1, row++);

        // Filtro por stock
        grid.add(new Label("Stock mínimo:"), 0, row);
        TextField tfStockMin = new TextField();
        tfStockMin.setPromptText("Ej: 10");
        tfStockMin.setPrefWidth(250);
        grid.add(tfStockMin, 1, row++);

        grid.add(new Label("Stock máximo:"), 0, row);
        TextField tfStockMax = new TextField();
        tfStockMax.setPromptText("Ej: 100");
        tfStockMax.setPrefWidth(250);
        grid.add(tfStockMax, 1, row++);

        // Filtro por fraccionable
        grid.add(new Label("Fraccionable:"), 0, row);
        ComboBox<String> cbFraccionable = new ComboBox<>();
        cbFraccionable.getItems().addAll("Todos", "Sí", "No");
        cbFraccionable.getSelectionModel().selectFirst();
        cbFraccionable.setPrefWidth(250);
        grid.add(cbFraccionable, 1, row++);

        // Filtro por lote
        grid.add(new Label("Lote:"), 0, row);
        TextField tfLote = new TextField();
        tfLote.setPromptText("Buscar por lote");
        tfLote.setPrefWidth(250);
        grid.add(tfLote, 1, row++);

        // Filtro por fecha vencimiento
        Label separador2 = new Label("━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        separador2.setStyle("-fx-text-fill: #0FB9BA;");
        grid.add(separador2, 0, row++, 2, 1);

        grid.add(new Label("Fecha vencimiento desde:"), 0, row);
        DatePicker dpFechaDesde = new DatePicker();
        dpFechaDesde.setPromptText("Desde");
        dpFechaDesde.setPrefWidth(250);
        grid.add(dpFechaDesde, 1, row++);

        grid.add(new Label("Fecha vencimiento hasta:"), 0, row);
        DatePicker dpFechaHasta = new DatePicker();
        dpFechaHasta.setPromptText("Hasta");
        dpFechaHasta.setPrefWidth(250);
        grid.add(dpFechaHasta, 1, row++);

        // CheckBox para próximos a vencer
        CheckBox chkProximosVencer = new CheckBox("Solo próximos a vencer (30 días)");
        chkProximosVencer.setStyle("-fx-font-weight: bold; -fx-text-fill: #E45858;");
        grid.add(chkProximosVencer, 0, row++, 2, 1);

        // CheckBox para stock bajo
        CheckBox chkStockBajo = new CheckBox("Solo stock bajo (≤ 10 unidades)");
        chkStockBajo.setStyle("-fx-font-weight: bold; -fx-text-fill: #ff9800;");
        grid.add(chkStockBajo, 0, row++, 2, 1);

        // Botones
        Button btnAplicar = new Button("Aplicar Filtros");
        Button btnLimpiarFiltros = new Button("Limpiar Filtros");
        Button btnCerrar = new Button("Cerrar");

        btnAplicar.setStyle("-fx-background-color: #0FB9BA; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnLimpiarFiltros.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnCerrar.setStyle("-fx-background-color: #607489; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");

        btnAplicar.setOnAction(event -> {
            listaFiltrada.clear();

            for (MaterialQuirurgico m : listaMateriales) {
                boolean coincide = true;

                if (cbFiltroMarca.getValue() != null) {
                    if (m.getMarca() == null ||
                        m.getMarca().getIdMarca() != cbFiltroMarca.getValue().getIdMarca()) {
                        coincide = false;
                    }
                }

                if (cbFiltroProveedor.getValue() != null) {
                    if (m.getProveedor() == null ||
                        m.getProveedor().getIdProveedor() != cbFiltroProveedor.getValue().getIdProveedor()) {
                        coincide = false;
                    }
                }

                if (!tfStockMin.getText().trim().isEmpty()) {
                    try {
                        int stockMin = Integer.parseInt(tfStockMin.getText().trim());
                        if (m.getStock() < stockMin) {
                            coincide = false;
                        }
                    } catch (NumberFormatException ignored) {}
                }

                if (!tfStockMax.getText().trim().isEmpty()) {
                    try {
                        int stockMax = Integer.parseInt(tfStockMax.getText().trim());
                        if (m.getStock() > stockMax) {
                            coincide = false;
                        }
                    } catch (NumberFormatException ignored) {}
                }

                if (!cbFraccionable.getValue().equals("Todos")) {
                    boolean esFraccionable = cbFraccionable.getValue().equals("Sí");
                    if (m.isFraccionable() != esFraccionable) {
                        coincide = false;
                    }
                }

                if (!tfLote.getText().trim().isEmpty()) {
                    if (m.getLote() == null ||
                        !m.getLote().toLowerCase().contains(tfLote.getText().toLowerCase().trim())) {
                        coincide = false;
                    }
                }

                if (dpFechaDesde.getValue() != null && m.getFechaVencimiento() != null) {
                    Date fechaDesde = Date.from(dpFechaDesde.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                    if (m.getFechaVencimiento().before(fechaDesde)) {
                        coincide = false;
                    }
                }

                if (dpFechaHasta.getValue() != null && m.getFechaVencimiento() != null) {
                    Date fechaHasta = Date.from(dpFechaHasta.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                    if (m.getFechaVencimiento().after(fechaHasta)) {
                        coincide = false;
                    }
                }

                if (chkProximosVencer.isSelected() && m.getFechaVencimiento() != null) {
                    long diff = m.getFechaVencimiento().getTime() - new Date().getTime();
                    long dias = diff / (1000 * 60 * 60 * 24);
                    if (dias < 0 || dias > 30) {
                        coincide = false;
                    }
                }

                if (chkStockBajo.isSelected()) {
                    if (m.getStock() > 10) {
                        coincide = false;
                    }
                }

                if (coincide) {
                    listaFiltrada.add(m);
                }
            }

            tblInventario.setItems(listaFiltrada);
            mostrarInfo("Filtros aplicados",
                "Se encontraron " + listaFiltrada.size() + " material(es) quirúrgico(s) que coinciden con los filtros.");
        });

        btnLimpiarFiltros.setOnAction(event -> {
            cbFiltroMarca.getSelectionModel().selectFirst();
            cbFiltroProveedor.getSelectionModel().selectFirst();
            tfStockMin.clear();
            tfStockMax.clear();
            cbFraccionable.getSelectionModel().selectFirst();
            tfLote.clear();
            dpFechaDesde.setValue(null);
            dpFechaHasta.setValue(null);
            chkProximosVencer.setSelected(false);
            chkStockBajo.setSelected(false);
            listaFiltrada.clear();
            listaFiltrada.addAll(listaMateriales);
            tblInventario.setItems(listaFiltrada);
            mostrarInfo("Filtros limpiados", "Se han restaurado todos los materiales quirúrgicos.");
        });

        btnCerrar.setOnAction(event -> dialog.close());

        VBox buttonsBox = new VBox(12, btnAplicar, btnLimpiarFiltros, btnCerrar);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setStyle("-fx-padding: 15 0 0 0;");
        grid.add(buttonsBox, 0, row, 2, 1);

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #f5f5f5;");

        Scene scene = new Scene(scrollPane, 500, 750);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    @FXML
    private void onLimpiar() {
        tfBuscar.clear();
        listaFiltrada.clear();
        listaFiltrada.addAll(listaMateriales);
        tblInventario.setItems(listaFiltrada);
    }

    // ===== EDITAR =====
    @FXML
    private void onEditarSeleccion() {
        MaterialQuirurgico seleccionado = tblInventario.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAdvertencia("Selección requerida", "Debe seleccionar un material quirúrgico para editar.");
            return;
        }

        mostrarDialogoEditar(seleccionado);
    }

    private void mostrarDialogoEditar(MaterialQuirurgico material) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Editar Material Quirúrgico");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        int row = 0;

        // ID (solo lectura)
        grid.add(new Label("ID:"), 0, row);
        TextField tfId = new TextField(String.valueOf(material.getIdProducto()));
        tfId.setEditable(false);
        tfId.setStyle("-fx-background-color: #e0e0e0;");
        grid.add(tfId, 1, row++);

        grid.add(new Label("Nombre:*"), 0, row);
        TextField tfNombre = new TextField(material.getNombre());
        grid.add(tfNombre, 1, row++);

        grid.add(new Label("Referencia:*"), 0, row);
        TextField tfReferencia = new TextField(material.getReferencia());
        grid.add(tfReferencia, 1, row++);

        grid.add(new Label("Código Barras:"), 0, row);
        TextField tfCodigo = new TextField(material.getCodigoBarras());
        grid.add(tfCodigo, 1, row++);

        grid.add(new Label("Precio:*"), 0, row);
        TextField tfPrecio = new TextField(String.valueOf(material.getPrecio()));
        grid.add(tfPrecio, 1, row++);

        grid.add(new Label("Costo:*"), 0, row);
        TextField tfCosto = new TextField(String.valueOf(material.getCosto()));
        grid.add(tfCosto, 1, row++);

        grid.add(new Label("Stock:*"), 0, row);
        TextField tfStock = new TextField(String.valueOf(material.getStock()));
        grid.add(tfStock, 1, row++);

        grid.add(new Label("Descripción:"), 0, row);
        TextArea taDescripcion = new TextArea(material.getDescripcion());
        taDescripcion.setPrefRowCount(3);
        grid.add(taDescripcion, 1, row++);

        grid.add(new Label("Lote:"), 0, row);
        TextField tfLote = new TextField(material.getLote());
        grid.add(tfLote, 1, row++);

        grid.add(new Label("Fecha Vencimiento:"), 0, row);
        DatePicker dpFecha = new DatePicker();
        if (material.getFechaVencimiento() != null) {
            dpFecha.setValue(material.getFechaVencimiento().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate());
        }
        grid.add(dpFecha, 1, row++);

        grid.add(new Label("Semanas Alerta:"), 0, row);
        TextField tfSemanas = new TextField(String.valueOf(material.getSemanaParaAlerta()));
        grid.add(tfSemanas, 1, row++);

        grid.add(new Label("Fraccionable:"), 0, row);
        CheckBox cbFraccionable = new CheckBox();
        cbFraccionable.setSelected(material.isFraccionable());
        grid.add(cbFraccionable, 1, row++);

        grid.add(new Label("Fraccionado:"), 0, row);
        CheckBox cbFraccionado = new CheckBox();
        cbFraccionado.setSelected(material.isFraccionado());
        grid.add(cbFraccionado, 1, row++);

        grid.add(new Label("Contenido:"), 0, row);
        TextField tfContenido = new TextField(String.valueOf(material.getContenido()));
        grid.add(tfContenido, 1, row++);

        // Marca
        grid.add(new Label("Marca:*"), 0, row);
        ComboBox<Marca> cbMarca = new ComboBox<>();
        try {
            List<Marca> marcas = marcaService.listarTodasLasMarcas();
            cbMarca.getItems().addAll(marcas);
            cbMarca.setConverter(new javafx.util.StringConverter<>() {
                @Override
                public String toString(Marca marca) {
                    return marca != null ? marca.getNombreMarca() : "";
                }
                @Override
                public Marca fromString(String string) {
                    return null;
                }
            });
            if (material.getMarca() != null) {
                cbMarca.getSelectionModel().select(
                    marcas.stream()
                        .filter(m -> m.getIdMarca() == material.getMarca().getIdMarca())
                        .findFirst()
                        .orElse(null)
                );
            }
        } catch (SQLException e) {
            mostrarError("Error", "No se pudieron cargar las marcas: " + e.getMessage());
        }
        grid.add(cbMarca, 1, row++);

        // Proveedor
        grid.add(new Label("Proveedor:"), 0, row);
        ComboBox<Proveedor> cbProveedor = new ComboBox<>();
        try {
            List<Proveedor> proveedores = proveedorService.obtenerTodosLosProveedores();
            cbProveedor.getItems().addAll(proveedores);
            cbProveedor.setConverter(new javafx.util.StringConverter<>() {
                @Override
                public String toString(Proveedor prov) {
                    return prov != null ? prov.getNombre() : "";
                }
                @Override
                public Proveedor fromString(String string) {
                    return null;
                }
            });
            if (material.getProveedor() != null) {
                cbProveedor.getSelectionModel().select(
                    proveedores.stream()
                        .filter(p -> p.getIdProveedor() == material.getProveedor().getIdProveedor())
                        .findFirst()
                        .orElse(null)
                );
            }
        } catch (Exception e) {
            mostrarError("Error", "No se pudieron cargar los proveedores: " + e.getMessage());
        }
        grid.add(cbProveedor, 1, row++);

        // Unidad de medida
        grid.add(new Label("Unidad Medida:"), 0, row);
        ComboBox<UnidadMedida> cbUnidad = new ComboBox<>();
        try {
            List<UnidadMedida> unidades = unidadMedidaService.listarTodasLasUnidades();
            cbUnidad.getItems().addAll(unidades);
            cbUnidad.setConverter(new javafx.util.StringConverter<>() {
                @Override
                public String toString(UnidadMedida um) {
                    return um != null ? um.getNombre() : "";
                }
                @Override
                public UnidadMedida fromString(String string) {
                    return null;
                }
            });
            if (material.getUnidadMedida() != null) {
                cbUnidad.getSelectionModel().select(
                    unidades.stream()
                        .filter(u -> u.getIdUnidadMedida() == material.getUnidadMedida().getIdUnidadMedida())
                        .findFirst()
                        .orElse(null)
                );
            }
        } catch (SQLException e) {
            mostrarError("Error", "No se pudieron cargar las unidades: " + e.getMessage());
        }
        grid.add(cbUnidad, 1, row++);

        // Imagen
        grid.add(new Label("Imagen:"), 0, row);
        Button btnImagen = new Button("Cambiar Imagen");
        final byte[][] nuevaImagen = {material.getImagenProducto()};
        btnImagen.setOnAction(event -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
            );
            File archivo = fc.showOpenDialog(dialog);
            if (archivo != null) {
                try {
                    nuevaImagen[0] = Files.readAllBytes(archivo.toPath());
                    mostrarInfo("Imagen cargada", "La imagen se actualizará al guardar.");
                } catch (Exception ex) {
                    mostrarError("Error", "No se pudo cargar la imagen: " + ex.getMessage());
                }
            }
        });
        grid.add(btnImagen, 1, row++);

        // Botones
        Button btnGuardar = new Button("Guardar");
        Button btnCancelar = new Button("Cancelar");

        btnGuardar.setStyle("-fx-background-color: #0FB9BA; -fx-text-fill: white; -fx-font-weight: bold;");
        btnCancelar.setStyle("-fx-background-color: #E45858; -fx-text-fill: white; -fx-font-weight: bold;");

        btnGuardar.setOnAction(event -> {
            try {
                if (tfNombre.getText().trim().isEmpty()) {
                    mostrarAdvertencia("Campo requerido", "El nombre es obligatorio.");
                    return;
                }
                if (tfReferencia.getText().trim().isEmpty()) {
                    mostrarAdvertencia("Campo requerido", "La referencia es obligatoria.");
                    return;
                }
                if (cbMarca.getValue() == null) {
                    mostrarAdvertencia("Campo requerido", "La marca es obligatoria.");
                    return;
                }

                material.setNombre(tfNombre.getText().trim());
                material.setReferencia(tfReferencia.getText().trim());
                material.setCodigoBarras(tfCodigo.getText().trim());
                material.setPrecio(Double.parseDouble(tfPrecio.getText()));
                material.setCosto(Double.parseDouble(tfCosto.getText()));
                material.setStock(Integer.parseInt(tfStock.getText()));
                material.setDescripcion(taDescripcion.getText());
                material.setLote(tfLote.getText());

                if (dpFecha.getValue() != null) {
                    material.setFechaVencimiento(
                        Date.from(dpFecha.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant())
                    );
                }

                material.setSemanaParaAlerta(Integer.parseInt(tfSemanas.getText()));
                material.setFraccionable(cbFraccionable.isSelected());
                material.setFraccionado(cbFraccionado.isSelected());
                material.setContenido(Double.parseDouble(tfContenido.getText()));
                material.setMarca(cbMarca.getValue());
                material.setProveedor(cbProveedor.getValue());
                material.setUnidadMedida(cbUnidad.getValue());
                material.setImagenProducto(nuevaImagen[0]);

                Integer idProveedor = cbProveedor.getValue() != null ?
                    cbProveedor.getValue().getIdProveedor() : null;

                boolean actualizado = materialService.actualizarMaterialQuirurgico(material, idProveedor);

                if (actualizado) {
                    mostrarInfo("Éxito", "Material quirúrgico actualizado correctamente.");
                    cargarMateriales();
                    dialog.close();
                } else {
                    mostrarError("Error", "No se pudo actualizar el material quirúrgico.");
                }
            } catch (NumberFormatException ex) {
                mostrarError("Error de formato", "Verifique que los números estén correctos.");
            } catch (SQLException ex) {
                mostrarError("Error de BD", "Error al actualizar: " + ex.getMessage());
            } catch (Exception ex) {
                mostrarError("Error", "Error inesperado: " + ex.getMessage());
            }
        });

        btnCancelar.setOnAction(event -> dialog.close());

        VBox buttonsBox = new VBox(10, btnGuardar, btnCancelar);
        buttonsBox.setAlignment(Pos.CENTER);
        grid.add(buttonsBox, 0, row, 2, 1);

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 500, 650);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    // ===== ELIMINAR =====
    @FXML
    private void onEliminarSeleccion() {
        MaterialQuirurgico seleccionado = tblInventario.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAdvertencia("Selección requerida", "Debe seleccionar un material quirúrgico para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar este material quirúrgico?");
        confirmacion.setContentText("Material: " + seleccionado.getNombre() +
            "\nID: " + seleccionado.getIdProducto() +
            "\n\nEsta acción no se puede deshacer.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                boolean eliminado = materialService.eliminarMaterialQuirurgico(seleccionado.getIdProducto());
                if (eliminado) {
                    mostrarInfo("Éxito", "Material quirúrgico eliminado correctamente.");
                    cargarMateriales();
                } else {
                    mostrarError("Error", "No se pudo eliminar el material quirúrgico.");
                }
            } catch (SQLException e) {
                mostrarError("Error de BD", "Error al eliminar: " + e.getMessage());
            }
        }
    }

    @FXML
    private void onDeshacer() {
        cargarMateriales();
        tfBuscar.clear();
    }

    // ===== NAVEGACIÓN =====
    private void goTo(String fxml) {
        try {
            var url = getClass().getResource(fxml);
            if (url == null) throw new IllegalStateException("NO existe el recurso (classpath): " + fxml);
            Parent newRoot = new FXMLLoader(url).load();
            Scene scene = root.getScene();
            scene.setRoot(newRoot);
            Stage stage = (Stage) scene.getWindow();
            stage.centerOnScreen();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir: " + fxml + "\n" + e.getMessage()).showAndWait();
        }
    }

    @FXML private void onLogoClick()         { goTo(MAINMENU_FXML); }
    @FXML private void onVerNotificaciones() { goTo(SEENOTIFICATIONS_FXML); }
    @FXML private void onDatosPersonales()   { goTo(PERSONALDATA_FXML); }
    @FXML private void onCerrarSesion()      { goTo(LOGIN_FXML); }
    @FXML private void onRegistrarProductos()     { goTo(REGISTER_PRODUCT_FXML); }
    @FXML private void onGestionarUsuario()       { goTo(CREATE_USER_FXML); }
    @FXML private void onVisualizarRegistros()    { goTo(VISUALIZE_REGISTER_FXML); }
    @FXML private void onVentas()                 { goTo(SECTION_SALES_FXML); }
    @FXML private void onAgregarClientes()        { goTo(ADD_CLIENT_FXML); }
    @FXML private void onAdministrarProveedores() { goTo(ADD_SUPPLIERS_FXML); }
    @FXML private void onMedicamento() { goTo(INVENTORY_MEDICAMENT_FXML); }
    @FXML private void onAlimento() { goTo(INVENTORY_ALIMENT_FXML); }
    @FXML private void onJuguete()  { goTo(INVENTORY_JUGUETE_FXML); }
    @FXML private void onVolver() { goTo(INVENTORY_FXML); }

    // ===== UTILIDADES =====
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAdvertencia(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

