package co.edu.upb.veterinaria.controllers.ControllerInventaryAliment;

import co.edu.upb.veterinaria.models.ModeloAlimento.Alimento;
import co.edu.upb.veterinaria.models.ModeloMarca.Marca;
import co.edu.upb.veterinaria.models.ModeloProveedor.Proveedor;
import co.edu.upb.veterinaria.models.ModeloUnidadMedida.UnidadMedida;
import co.edu.upb.veterinaria.services.ServicioAlimento.AlimentoService;
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

/** Inventario (Alimentos) – con funcionalidad completa */
public class InventaryAlimentController {

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
    private static final String INVENTORY_MATERIALQ_FXML =
            "/co/edu/upb/veterinaria/views/inventaryMaterialQ-view/inventaryMaterialQ-view.fxml";
    private static final String INVENTORY_JUGUETE_FXML =
            "/co/edu/upb/veterinaria/views/inventaryJugueteAccs-view/inventaryJugueteAccs-view.fxml";

    // ===== SERVICIOS =====
    private final AlimentoService alimentoService;
    private final MarcaService marcaService;
    private final ProveedorService proveedorService;
    private final UnidadMedidaService unidadMedidaService;

    // ===== DATOS =====
    private final ObservableList<Alimento> listaAlimentos;
    private final ObservableList<Alimento> listaFiltrada;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    // ===== Root / contenedores =====
    @FXML private AnchorPane root;
    @FXML private ScrollPane scrollTabla;
    @FXML private VBox tableWrapper;
    @FXML private TableView<Alimento> tblInventario;

    // ===== Header =====
    @FXML private ImageView topLogo;
    @FXML private MenuButton mbNotificaciones;
    @FXML private MenuItem   miVerNotificaciones;
    @FXML private MenuButton mbPerfil;
    @FXML private MenuItem   miDatosPersonales, miCerrarSesion;

    // ===== Menú superior (módulos) =====
    @FXML private Button btnRegistrarProductos, btnGestionarUsuario, btnVisualizarRegistros,
            btnVentas, btnAgregarClientes, btnAdministrarProveedores;

    // ===== Filtros =====
    @FXML private Button btnMedicamento, btnMaterial, btnJuguete;

    // ===== Búsqueda =====
    @FXML private TextField tfBuscar;
    @FXML private Button btnFiltrar, btnBuscar, btnLimpiar;

    // ===== Acciones =====
    @FXML private Button btnDeshacer, btnEditarSeleccion, btnEliminarSeleccion;

    // ===== Volver =====
    @FXML private Button btnVolver;

    // ===== Columnas =====
    @FXML private TableColumn<Alimento, Integer> colIdProducto;
    @FXML private TableColumn<Alimento, String> colNombre;
    @FXML private TableColumn<Alimento, ImageView> colFoto;
    @FXML private TableColumn<Alimento, String> colReferencia;
    @FXML private TableColumn<Alimento, String> colCodigoBarras;
    @FXML private TableColumn<Alimento, String> colPrecio;
    @FXML private TableColumn<Alimento, String> colCosto;
    @FXML private TableColumn<Alimento, String> colMarca;
    @FXML private TableColumn<Alimento, String> colDescripcion;
    @FXML private TableColumn<Alimento, String> colProveedor;
    @FXML private TableColumn<Alimento, Integer> colStock;
    @FXML private TableColumn<Alimento, String> colLote;
    @FXML private TableColumn<Alimento, String> colFechaCaducidad;
    @FXML private TableColumn<Alimento, Integer> colSemanasParaAlerta;
    @FXML private TableColumn<Alimento, String> colFraccionable;
    @FXML private TableColumn<Alimento, String> colFraccionado;
    @FXML private TableColumn<Alimento, String> colContenido;

    public InventaryAlimentController() {
        this.alimentoService = new AlimentoService();
        this.marcaService = new MarcaService();
        this.proveedorService = new ProveedorService();
        this.unidadMedidaService = new UnidadMedidaService();
        this.listaAlimentos = FXCollections.observableArrayList();
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
        cargarAlimentos();
    }

    private void configurarColumnas() {
        colIdProducto.setCellValueFactory(data ->
            new SimpleObjectProperty<>(data.getValue().getIdProducto()));

        colNombre.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getNombre()));

        colFoto.setCellValueFactory(data -> {
            Alimento a = data.getValue();
            ImageView imgView = new ImageView();
            imgView.setFitWidth(80);
            imgView.setFitHeight(80);
            imgView.setPreserveRatio(true);

            if (a.getImagenProducto() != null && a.getImagenProducto().length > 0) {
                try {
                    Image img = new Image(new ByteArrayInputStream(a.getImagenProducto()));
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

        colFechaCaducidad.setCellValueFactory(data -> {
            Date fecha = data.getValue().getFechaVencimiento();
            return new SimpleStringProperty(fecha != null ? dateFormat.format(fecha) : "N/A");
        });

        colSemanasParaAlerta.setCellValueFactory(data ->
            new SimpleObjectProperty<>(data.getValue().getSemanasParaAlerta()));

        colFraccionable.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().isFraccionable() ? "Sí" : "No"));

        colFraccionado.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().isFraccionado() ? "Sí" : "No"));

        colContenido.setCellValueFactory(data -> {
            Alimento a = data.getValue();
            UnidadMedida um = a.getUnidadMedida();
            String unidad = um != null ? um.getNombre() : "";
            return new SimpleStringProperty(String.format("%.2f %s", a.getContenido(), unidad));
        });
    }

    private void cargarAlimentos() {
        try {
            System.out.println("🔄 Intentando cargar alimentos...");
            List<Alimento> alimentos = alimentoService.listarTodos();
            System.out.println("✅ Alimentos cargados desde BD: " + alimentos.size());

            listaAlimentos.clear();
            listaAlimentos.addAll(alimentos);
            listaFiltrada.clear();
            listaFiltrada.addAll(alimentos);
            tblInventario.setItems(listaFiltrada);

            System.out.println("✅ Alimentos mostrados en tabla: " + listaFiltrada.size());

            if (alimentos.isEmpty()) {
                mostrarInfo("Información", "No hay alimentos registrados en el sistema.\nRegistre alimentos tipo 'Alimento' desde el módulo de Registro de Productos.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al cargar alimentos: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al cargar alimentos",
                "No se pudieron cargar los alimentos: " + e.getMessage());
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
            listaFiltrada.addAll(listaAlimentos);
            tblInventario.setItems(listaFiltrada);
            return;
        }

        String busqueda = textoBusqueda.toLowerCase().trim();
        listaFiltrada.clear();

        for (Alimento a : listaAlimentos) {
            if ((a.getNombre() != null && a.getNombre().toLowerCase().contains(busqueda)) ||
                (a.getReferencia() != null && a.getReferencia().toLowerCase().contains(busqueda)) ||
                (a.getCodigoBarras() != null && a.getCodigoBarras().toLowerCase().contains(busqueda)) ||
                (a.getLote() != null && a.getLote().toLowerCase().contains(busqueda)) ||
                (a.getMarca() != null && a.getMarca().getNombreMarca() != null &&
                    a.getMarca().getNombreMarca().toLowerCase().contains(busqueda)) ||
                (a.getProveedor() != null && a.getProveedor().getNombre() != null &&
                    a.getProveedor().getNombre().toLowerCase().contains(busqueda)) ||
                (a.getDescripcion() != null && a.getDescripcion().toLowerCase().contains(busqueda))) {
                listaFiltrada.add(a);
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
        dialog.setTitle("Filtros Avanzados - Alimentos");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        grid.setPadding(new Insets(25));
        grid.setStyle("-fx-background-color: #f5f5f5;");

        int row = 0;

        // Título
        Label titulo = new Label("Filtrar Alimentos");
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

            for (Alimento a : listaAlimentos) {
                boolean coincide = true;

                if (cbFiltroMarca.getValue() != null) {
                    if (a.getMarca() == null ||
                        a.getMarca().getIdMarca() != cbFiltroMarca.getValue().getIdMarca()) {
                        coincide = false;
                    }
                }

                if (cbFiltroProveedor.getValue() != null) {
                    if (a.getProveedor() == null ||
                        a.getProveedor().getIdProveedor() != cbFiltroProveedor.getValue().getIdProveedor()) {
                        coincide = false;
                    }
                }

                if (!tfStockMin.getText().trim().isEmpty()) {
                    try {
                        int stockMin = Integer.parseInt(tfStockMin.getText().trim());
                        if (a.getStock() < stockMin) {
                            coincide = false;
                        }
                    } catch (NumberFormatException ignored) {}
                }

                if (!tfStockMax.getText().trim().isEmpty()) {
                    try {
                        int stockMax = Integer.parseInt(tfStockMax.getText().trim());
                        if (a.getStock() > stockMax) {
                            coincide = false;
                        }
                    } catch (NumberFormatException ignored) {}
                }

                if (!cbFraccionable.getValue().equals("Todos")) {
                    boolean esFraccionable = cbFraccionable.getValue().equals("Sí");
                    if (a.isFraccionable() != esFraccionable) {
                        coincide = false;
                    }
                }

                if (!tfLote.getText().trim().isEmpty()) {
                    if (a.getLote() == null ||
                        !a.getLote().toLowerCase().contains(tfLote.getText().toLowerCase().trim())) {
                        coincide = false;
                    }
                }

                if (dpFechaDesde.getValue() != null && a.getFechaVencimiento() != null) {
                    Date fechaDesde = Date.from(dpFechaDesde.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                    if (a.getFechaVencimiento().before(fechaDesde)) {
                        coincide = false;
                    }
                }

                if (dpFechaHasta.getValue() != null && a.getFechaVencimiento() != null) {
                    Date fechaHasta = Date.from(dpFechaHasta.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                    if (a.getFechaVencimiento().after(fechaHasta)) {
                        coincide = false;
                    }
                }

                if (chkProximosVencer.isSelected() && a.getFechaVencimiento() != null) {
                    long diff = a.getFechaVencimiento().getTime() - new Date().getTime();
                    long dias = diff / (1000 * 60 * 60 * 24);
                    if (dias < 0 || dias > 30) {
                        coincide = false;
                    }
                }

                if (chkStockBajo.isSelected()) {
                    if (a.getStock() > 10) {
                        coincide = false;
                    }
                }

                if (coincide) {
                    listaFiltrada.add(a);
                }
            }

            tblInventario.setItems(listaFiltrada);
            mostrarInfo("Filtros aplicados",
                "Se encontraron " + listaFiltrada.size() + " alimento(s) que coinciden con los filtros.");
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
            listaFiltrada.addAll(listaAlimentos);
            tblInventario.setItems(listaFiltrada);
            mostrarInfo("Filtros limpiados", "Se han restaurado todos los alimentos.");
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
        listaFiltrada.addAll(listaAlimentos);
        tblInventario.setItems(listaFiltrada);
    }

    // ===== EDITAR =====
    @FXML
    private void onEditarSeleccion() {
        Alimento seleccionado = tblInventario.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAdvertencia("Selección requerida", "Debe seleccionar un alimento para editar.");
            return;
        }

        mostrarDialogoEditar(seleccionado);
    }

    private void mostrarDialogoEditar(Alimento alimento) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Editar Alimento");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        int row = 0;

        // ID (solo lectura)
        grid.add(new Label("ID:"), 0, row);
        TextField tfId = new TextField(String.valueOf(alimento.getIdProducto()));
        tfId.setEditable(false);
        tfId.setStyle("-fx-background-color: #e0e0e0;");
        grid.add(tfId, 1, row++);

        // ...existing code... (todos los campos igual a medicamentos)
        grid.add(new Label("Nombre:*"), 0, row);
        TextField tfNombre = new TextField(alimento.getNombre());
        grid.add(tfNombre, 1, row++);

        grid.add(new Label("Referencia:*"), 0, row);
        TextField tfReferencia = new TextField(alimento.getReferencia());
        grid.add(tfReferencia, 1, row++);

        grid.add(new Label("Código Barras:"), 0, row);
        TextField tfCodigo = new TextField(alimento.getCodigoBarras());
        grid.add(tfCodigo, 1, row++);

        grid.add(new Label("Precio:*"), 0, row);
        TextField tfPrecio = new TextField(String.valueOf(alimento.getPrecio()));
        grid.add(tfPrecio, 1, row++);

        grid.add(new Label("Costo:*"), 0, row);
        TextField tfCosto = new TextField(String.valueOf(alimento.getCosto()));
        grid.add(tfCosto, 1, row++);

        grid.add(new Label("Stock:*"), 0, row);
        TextField tfStock = new TextField(String.valueOf(alimento.getStock()));
        grid.add(tfStock, 1, row++);

        grid.add(new Label("Descripción:"), 0, row);
        TextArea taDescripcion = new TextArea(alimento.getDescripcion());
        taDescripcion.setPrefRowCount(3);
        grid.add(taDescripcion, 1, row++);

        grid.add(new Label("Lote:"), 0, row);
        TextField tfLote = new TextField(alimento.getLote());
        grid.add(tfLote, 1, row++);

        grid.add(new Label("Fecha Vencimiento:"), 0, row);
        DatePicker dpFecha = new DatePicker();
        if (alimento.getFechaVencimiento() != null) {
            dpFecha.setValue(alimento.getFechaVencimiento().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate());
        }
        grid.add(dpFecha, 1, row++);

        grid.add(new Label("Semanas Alerta:"), 0, row);
        TextField tfSemanas = new TextField(String.valueOf(alimento.getSemanasParaAlerta()));
        grid.add(tfSemanas, 1, row++);

        grid.add(new Label("Fraccionable:"), 0, row);
        CheckBox cbFraccionable = new CheckBox();
        cbFraccionable.setSelected(alimento.isFraccionable());
        grid.add(cbFraccionable, 1, row++);

        grid.add(new Label("Fraccionado:"), 0, row);
        CheckBox cbFraccionado = new CheckBox();
        cbFraccionado.setSelected(alimento.isFraccionado());
        grid.add(cbFraccionado, 1, row++);

        grid.add(new Label("Contenido:"), 0, row);
        TextField tfContenido = new TextField(String.valueOf(alimento.getContenido()));
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
            if (alimento.getMarca() != null) {
                cbMarca.getSelectionModel().select(
                    marcas.stream()
                        .filter(m -> m.getIdMarca() == alimento.getMarca().getIdMarca())
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
            if (alimento.getProveedor() != null) {
                cbProveedor.getSelectionModel().select(
                    proveedores.stream()
                        .filter(p -> p.getIdProveedor() == alimento.getProveedor().getIdProveedor())
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
            if (alimento.getUnidadMedida() != null) {
                cbUnidad.getSelectionModel().select(
                    unidades.stream()
                        .filter(u -> u.getIdUnidadMedida() == alimento.getUnidadMedida().getIdUnidadMedida())
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
        final byte[][] nuevaImagen = {alimento.getImagenProducto()};
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

                alimento.setNombre(tfNombre.getText().trim());
                alimento.setReferencia(tfReferencia.getText().trim());
                alimento.setCodigoBarras(tfCodigo.getText().trim());
                alimento.setPrecio(Double.parseDouble(tfPrecio.getText()));
                alimento.setCosto(Double.parseDouble(tfCosto.getText()));
                alimento.setStock(Integer.parseInt(tfStock.getText()));
                alimento.setDescripcion(taDescripcion.getText());
                alimento.setLote(tfLote.getText());

                if (dpFecha.getValue() != null) {
                    alimento.setFechaVencimiento(
                        Date.from(dpFecha.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant())
                    );
                }

                alimento.setSemanasParaAlerta(Integer.parseInt(tfSemanas.getText()));
                alimento.setFraccionable(cbFraccionable.isSelected());
                alimento.setFraccionado(cbFraccionado.isSelected());
                alimento.setContenido(Double.parseDouble(tfContenido.getText()));
                alimento.setMarca(cbMarca.getValue());
                alimento.setProveedor(cbProveedor.getValue());
                alimento.setUnidadMedida(cbUnidad.getValue());
                alimento.setImagenProducto(nuevaImagen[0]);

                Integer idProveedor = cbProveedor.getValue() != null ?
                    cbProveedor.getValue().getIdProveedor() : null;

                boolean actualizado = alimentoService.actualizarAlimento(alimento, idProveedor);

                if (actualizado) {
                    mostrarInfo("Éxito", "Alimento actualizado correctamente.");
                    cargarAlimentos();
                    dialog.close();
                } else {
                    mostrarError("Error", "No se pudo actualizar el alimento.");
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
        Alimento seleccionado = tblInventario.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAdvertencia("Selección requerida", "Debe seleccionar un alimento para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar este alimento?");
        confirmacion.setContentText("Alimento: " + seleccionado.getNombre() +
            "\nID: " + seleccionado.getIdProducto() +
            "\n\nEsta acción no se puede deshacer.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                boolean eliminado = alimentoService.eliminarAlimento(seleccionado.getIdProducto());
                if (eliminado) {
                    mostrarInfo("Éxito", "Alimento eliminado correctamente.");
                    cargarAlimentos();
                } else {
                    mostrarError("Error", "No se pudo eliminar el alimento.");
                }
            } catch (SQLException e) {
                mostrarError("Error de BD", "Error al eliminar: " + e.getMessage());
            }
        }
    }

    @FXML
    private void onDeshacer() {
        cargarAlimentos();
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
    @FXML private void onMaterial() { goTo(INVENTORY_MATERIALQ_FXML); }
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

