package co.edu.upb.veterinaria.controllers.ControllerSailsVisualizeRegister;

import co.edu.upb.veterinaria.models.ModeloCliente.Cliente;
import co.edu.upb.veterinaria.models.ModeloLineaVenta.LineaVenta;
import co.edu.upb.veterinaria.models.ModeloMascota.Mascota;
import co.edu.upb.veterinaria.models.ModeloProducto.Producto;
import co.edu.upb.veterinaria.models.ModeloServicio.Servicio;
import co.edu.upb.veterinaria.models.ModeloVenta.Venta;
import co.edu.upb.veterinaria.services.ServicioCliente.ClienteService;
import co.edu.upb.veterinaria.services.ServicioLineaVenta.LineaVentaService;
import co.edu.upb.veterinaria.services.ServicioVenta.VentaService;
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
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class VisualizeRegisterSails {

    // ===== RUTAS =====
    private static final String MAINMENU_FXML =
            "/co/edu/upb/veterinaria/views/mainMenu-view/mainMenu-view.fxml";
    private static final String SEENOTIFICATIONS_FXML =
            "/co/edu/upb/veterinaria/views/SeeNotifications-view/SeeNotifications-view.fxml";
    private static final String PERSONALDATA_FXML =
            "/co/edu/upb/veterinaria/views/personalData-view/personalData.fxml";
    private static final String LOGIN_FXML =
            "/co/edu/upb/veterinaria/views/Login/Login.fxml";

    private static final String GENERATE_REPORT_FXML =
            "/co/edu/upb/veterinaria/views/generateReport-view/generateReport-view.fxml";

    private static final String REGISTER_PRODUCT_FXML =
            "/co/edu/upb/veterinaria/views/registerProduct-view/registerProduct-view.fxml";
    private static final String INVENTARY_FXML =
            "/co/edu/upb/veterinaria/views/inventary-view/inventary-view.fxml";
    private static final String CREATE_USER_FXML =
            "/co/edu/upb/veterinaria/views/createUser-view/createUser-view.fxml";
    private static final String SECTION_SALES_FXML =
            "/co/edu/upb/veterinaria/views/SectionSales-view/SectionSales-view.fxml";
    private static final String ADD_CLIENT_FXML =
            "/co/edu/upb/veterinaria/views/AddClient-view/AddClient-view.fxml";
    private static final String ADD_SUPPLIERS_FXML =
            "/co/edu/upb/veterinaria/views/AddSuppliers-view/AddSuppliers-view.fxml";

    private static final String VISUALIZE_REGISTER_FXML =
            "/co/edu/upb/veterinaria/views/visualizeRegister-view/visualizeRegister-view.fxml";

    // ===== SERVICIOS =====
    private final VentaService ventaService;
    private final LineaVentaService lineaVentaService;
    private final ClienteService clienteService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    // ===== DATOS =====
    private final ObservableList<VentaRow> listaVentas;
    private final ObservableList<VentaRow> listaFiltrada;
    private final Stack<VentaRow> ventasEliminadas;

    // ===== Root / Header =====
    @FXML private AnchorPane root;
    @FXML private ImageView topLogo;

    // Header: menús
    @FXML private MenuButton mbNotificaciones;
    @FXML private MenuItem   miVerNotificaciones;
    @FXML private MenuButton mbPerfil;
    @FXML private MenuItem   miDatosPersonales, miCerrarSesion;

    // Menú superior
    @FXML private Button btnRegistrarProductos, btnInventario, btnGestionarUsuario,
            btnVentasTop, btnAgregarClientesTop, btnAdministrarProveedoresTop;

    // Navegación interna (volver)
    @FXML private Button btnVolver;

    // Búsqueda
    @FXML private TextField tfBuscar;
    @FXML private Button btnFiltrar, btnBuscar, btnLimpiar;

    // Tabla + contenedores
    @FXML private ScrollPane scrollTabla;
    @FXML private VBox tableWrapper;
    @FXML private TableView<VentaRow> tblVentas;

    // Columnas
    @FXML private TableColumn<VentaRow, Integer> colId;
    @FXML private TableColumn<VentaRow, String> colProductos;
    @FXML private TableColumn<VentaRow, String> colTipoProducto;
    @FXML private TableColumn<VentaRow, String> colReferencia;
    @FXML private TableColumn<VentaRow, String> colCodigoBarras;
    @FXML private TableColumn<VentaRow, String> colCliente;
    @FXML private TableColumn<VentaRow, String> colMascota;
    @FXML private TableColumn<VentaRow, String> colFecha;
    @FXML private TableColumn<VentaRow, String> colTotal;

    // Botonera inferior
    @FXML private Button btnGenerarReporte, btnDeshacer, btnEditar, btnEliminar;

    public VisualizeRegisterSails() {
        this.ventaService = new VentaService();
        this.lineaVentaService = new LineaVentaService(co.edu.upb.veterinaria.config.DatabaseConfig.getDataSource());
        this.clienteService = new ClienteService();
        this.listaVentas = FXCollections.observableArrayList();
        this.listaFiltrada = FXCollections.observableArrayList();
        this.ventasEliminadas = new Stack<>();
    }

    // ===== Utilidades de navegación =====
    private Stage safeStage() {
        if (root != null && root.getScene() != null) return (Stage) root.getScene().getWindow();
        Window w = Window.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null);
        return (w instanceof Stage) ? (Stage) w : null;
    }

    @FXML
    private void initialize() {
        // Abrir maximizado
        Platform.runLater(() -> {
            Stage st = safeStage();
            if (st != null) {
                st.setResizable(true);
                st.setMaximized(true);
            }
        });

        // Scroll horizontal real
        if (scrollTabla != null) {
            scrollTabla.setFitToHeight(true);
            scrollTabla.setFitToWidth(false);
            scrollTabla.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            scrollTabla.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        }
        if (tblVentas != null) {
            tblVentas.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        }
        if (tableWrapper != null && tableWrapper.getPrefWidth() < 2800) {
            tableWrapper.setPrefWidth(2800);
        }

        configurarColumnas();
        cargarVentas();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().idVenta));

        colProductos.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().productos));
        colProductos.setStyle("-fx-wrap-text: true;");

        colTipoProducto.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().tiposProducto));
        colTipoProducto.setStyle("-fx-wrap-text: true;");

        colReferencia.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().referencias));
        colReferencia.setStyle("-fx-wrap-text: true;");

        colCodigoBarras.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().codigosBarras));
        colCodigoBarras.setStyle("-fx-wrap-text: true;");

        colCliente.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().cliente));

        colMascota.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().mascota));

        colFecha.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().fecha));

        colTotal.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().total));
    }

    private void cargarVentas() {
        try {
            System.out.println("🔄 Cargando ventas desde la base de datos...");
            List<Venta> ventas = ventaService.listarTodas();
            System.out.println("✅ Ventas obtenidas: " + ventas.size());

            listaVentas.clear();

            for (Venta venta : ventas) {
                // Obtener las líneas de venta
                List<LineaVenta> lineas = lineaVentaService.obtenerLineasPorVenta(venta.getIdVenta());
                venta.setLineasVenta(lineas);

                // Crear la fila de la tabla
                VentaRow row = new VentaRow(venta);
                listaVentas.add(row);
            }

            listaFiltrada.clear();
            listaFiltrada.addAll(listaVentas);
            tblVentas.setItems(listaFiltrada);

            System.out.println("✅ Ventas mostradas en tabla: " + listaFiltrada.size());

            if (ventas.isEmpty()) {
                mostrarInfo("Información", "No hay ventas registradas en el sistema.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al cargar ventas: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al cargar ventas", "No se pudieron cargar las ventas: " + e.getMessage());
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
            listaFiltrada.addAll(listaVentas);
            tblVentas.setItems(listaFiltrada);
            return;
        }

        String busqueda = textoBusqueda.toLowerCase().trim();
        listaFiltrada.clear();

        for (VentaRow row : listaVentas) {
            // Buscar en todos los campos
            if (String.valueOf(row.idVenta).contains(busqueda) ||
                (row.productos != null && row.productos.toLowerCase().contains(busqueda)) ||
                (row.tiposProducto != null && row.tiposProducto.toLowerCase().contains(busqueda)) ||
                (row.referencias != null && row.referencias.toLowerCase().contains(busqueda)) ||
                (row.codigosBarras != null && row.codigosBarras.toLowerCase().contains(busqueda)) ||
                (row.cliente != null && row.cliente.toLowerCase().contains(busqueda)) ||
                (row.mascota != null && row.mascota.toLowerCase().contains(busqueda)) ||
                (row.fecha != null && row.fecha.toLowerCase().contains(busqueda)) ||
                (row.total != null && row.total.toLowerCase().contains(busqueda))) {
                listaFiltrada.add(row);
            }
        }

        tblVentas.setItems(listaFiltrada);
    }

    @FXML
    private void onFiltrar() {
        mostrarDialogoFiltrosAvanzados();
    }

    private void mostrarDialogoFiltrosAvanzados() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Filtros Avanzados - Ventas");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        grid.setPadding(new Insets(25));
        grid.setStyle("-fx-background-color: #f5f5f5;");

        int row = 0;

        // Título
        Label titulo = new Label("Filtrar Ventas");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #113051;");
        grid.add(titulo, 0, row++, 2, 1);

        // Separador
        Label separador1 = new Label("━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        separador1.setStyle("-fx-text-fill: #0FB9BA;");
        grid.add(separador1, 0, row++, 2, 1);

        // Filtro por ID de venta
        grid.add(new Label("ID Venta:"), 0, row);
        TextField tfIdVenta = new TextField();
        tfIdVenta.setPromptText("Número de venta");
        tfIdVenta.setPrefWidth(250);
        grid.add(tfIdVenta, 1, row++);

        // Filtro por cliente
        grid.add(new Label("Cliente:"), 0, row);
        ComboBox<Cliente> cbCliente = new ComboBox<>();
        cbCliente.setPromptText("Todos los clientes");
        cbCliente.setPrefWidth(250);
        try {
            List<Cliente> clientes = clienteService.listarClientes(1000, 0);
            cbCliente.getItems().add(null);
            cbCliente.getItems().addAll(clientes);
            cbCliente.setConverter(new javafx.util.StringConverter<>() {
                @Override
                public String toString(Cliente c) {
                    return c != null ? c.getNombre() + " " + c.getApellidos() : "Todos los clientes";
                }
                @Override
                public Cliente fromString(String string) {
                    return null;
                }
            });
            cbCliente.getSelectionModel().selectFirst();
        } catch (Exception e) {
            mostrarError("Error", "No se pudieron cargar los clientes");
        }
        grid.add(cbCliente, 1, row++);

        // Filtro por rango de fechas
        grid.add(new Label("Fecha desde:"), 0, row);
        DatePicker dpFechaDesde = new DatePicker();
        dpFechaDesde.setPromptText("Desde");
        dpFechaDesde.setPrefWidth(250);
        grid.add(dpFechaDesde, 1, row++);

        grid.add(new Label("Fecha hasta:"), 0, row);
        DatePicker dpFechaHasta = new DatePicker();
        dpFechaHasta.setPromptText("Hasta");
        dpFechaHasta.setPrefWidth(250);
        grid.add(dpFechaHasta, 1, row++);

        // Filtro por total mínimo
        grid.add(new Label("Total mínimo:"), 0, row);
        TextField tfTotalMin = new TextField();
        tfTotalMin.setPromptText("Ej: 50000");
        tfTotalMin.setPrefWidth(250);
        grid.add(tfTotalMin, 1, row++);

        // Filtro por total máximo
        grid.add(new Label("Total máximo:"), 0, row);
        TextField tfTotalMax = new TextField();
        tfTotalMax.setPromptText("Ej: 500000");
        tfTotalMax.setPrefWidth(250);
        grid.add(tfTotalMax, 1, row++);

        // Botones
        Button btnAplicar = new Button("Aplicar Filtros");
        Button btnLimpiarFiltros = new Button("Limpiar Filtros");
        Button btnCerrar = new Button("Cerrar");

        btnAplicar.setStyle("-fx-background-color: #0FB9BA; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnLimpiarFiltros.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnCerrar.setStyle("-fx-background-color: #607489; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");

        btnAplicar.setOnAction(event -> {
            listaFiltrada.clear();

            for (VentaRow ventaRow : listaVentas) {
                boolean coincide = true;

                // Filtrar por ID
                if (!tfIdVenta.getText().trim().isEmpty()) {
                    try {
                        int idBuscar = Integer.parseInt(tfIdVenta.getText().trim());
                        if (ventaRow.idVenta != idBuscar) {
                            coincide = false;
                        }
                    } catch (NumberFormatException ignored) {}
                }

                // Filtrar por cliente
                if (cbCliente.getValue() != null) {
                    if (!ventaRow.cliente.contains(cbCliente.getValue().getNombre()) ||
                        !ventaRow.cliente.contains(cbCliente.getValue().getApellidos())) {
                        coincide = false;
                    }
                }

                // Filtrar por fecha desde
                if (dpFechaDesde.getValue() != null) {
                    try {
                        Date fechaDesde = Date.from(dpFechaDesde.getValue().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
                        Date fechaVenta = dateFormat.parse(ventaRow.fecha);
                        if (fechaVenta.before(fechaDesde)) {
                            coincide = false;
                        }
                    } catch (Exception ignored) {}
                }

                // Filtrar por fecha hasta
                if (dpFechaHasta.getValue() != null) {
                    try {
                        Date fechaHasta = Date.from(dpFechaHasta.getValue().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
                        Date fechaVenta = dateFormat.parse(ventaRow.fecha);
                        if (fechaVenta.after(fechaHasta)) {
                            coincide = false;
                        }
                    } catch (Exception ignored) {}
                }

                // Filtrar por total mínimo
                if (!tfTotalMin.getText().trim().isEmpty()) {
                    try {
                        double totalMin = Double.parseDouble(tfTotalMin.getText().trim());
                        double totalVenta = Double.parseDouble(ventaRow.total.replace("$", "").replace(",", ""));
                        if (totalVenta < totalMin) {
                            coincide = false;
                        }
                    } catch (NumberFormatException ignored) {}
                }

                // Filtrar por total máximo
                if (!tfTotalMax.getText().trim().isEmpty()) {
                    try {
                        double totalMax = Double.parseDouble(tfTotalMax.getText().trim());
                        double totalVenta = Double.parseDouble(ventaRow.total.replace("$", "").replace(",", ""));
                        if (totalVenta > totalMax) {
                            coincide = false;
                        }
                    } catch (NumberFormatException ignored) {}
                }

                if (coincide) {
                    listaFiltrada.add(ventaRow);
                }
            }

            tblVentas.setItems(listaFiltrada);
            mostrarInfo("Filtros aplicados", "Se encontraron " + listaFiltrada.size() + " venta(s) que coinciden.");
            dialog.close();
        });

        btnLimpiarFiltros.setOnAction(event -> {
            tfIdVenta.clear();
            cbCliente.getSelectionModel().selectFirst();
            dpFechaDesde.setValue(null);
            dpFechaHasta.setValue(null);
            tfTotalMin.clear();
            tfTotalMax.clear();
            listaFiltrada.clear();
            listaFiltrada.addAll(listaVentas);
            tblVentas.setItems(listaFiltrada);
            mostrarInfo("Filtros limpiados", "Se han restaurado todas las ventas.");
        });

        btnCerrar.setOnAction(event -> dialog.close());

        VBox buttonsBox = new VBox(12, btnAplicar, btnLimpiarFiltros, btnCerrar);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setStyle("-fx-padding: 15 0 0 0;");
        grid.add(buttonsBox, 0, row, 2, 1);

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #f5f5f5;");

        Scene scene = new Scene(scrollPane, 500, 600);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    @FXML
    private void onLimpiar() {
        tfBuscar.clear();
        listaFiltrada.clear();
        listaFiltrada.addAll(listaVentas);
        tblVentas.setItems(listaFiltrada);
    }

    // ===== EDITAR =====
    @FXML
    private void onEditar() {
        VentaRow seleccionada = tblVentas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAdvertencia("Selección requerida", "Debe seleccionar una venta para editar.");
            return;
        }

        mostrarDialogoEditar(seleccionada);
    }

    private void mostrarDialogoEditar(VentaRow ventaRow) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Editar Venta");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        int row = 0;

        // ID (solo lectura)
        grid.add(new Label("ID Venta:"), 0, row);
        TextField tfId = new TextField(String.valueOf(ventaRow.idVenta));
        tfId.setEditable(false);
        tfId.setStyle("-fx-background-color: #e0e0e0;");
        grid.add(tfId, 1, row++);

        // Cliente
        grid.add(new Label("Cliente:*"), 0, row);
        TextField tfCliente = new TextField(ventaRow.cliente);
        grid.add(tfCliente, 1, row++);

        // Mascota
        grid.add(new Label("Mascota:"), 0, row);
        TextField tfMascota = new TextField(ventaRow.mascota);
        grid.add(tfMascota, 1, row++);

        // Productos (solo lectura)
        grid.add(new Label("Productos:"), 0, row);
        TextArea taProductos = new TextArea(ventaRow.productos);
        taProductos.setEditable(false);
        taProductos.setPrefRowCount(3);
        taProductos.setStyle("-fx-background-color: #f0f0f0;");
        grid.add(taProductos, 1, row++);

        // Tipos de producto (solo lectura)
        grid.add(new Label("Tipos:"), 0, row);
        TextArea taTipos = new TextArea(ventaRow.tiposProducto);
        taTipos.setEditable(false);
        taTipos.setPrefRowCount(3);
        taTipos.setStyle("-fx-background-color: #f0f0f0;");
        grid.add(taTipos, 1, row++);

        // Referencias (solo lectura)
        grid.add(new Label("Referencias:"), 0, row);
        TextArea taReferencias = new TextArea(ventaRow.referencias);
        taReferencias.setEditable(false);
        taReferencias.setPrefRowCount(2);
        taReferencias.setStyle("-fx-background-color: #f0f0f0;");
        grid.add(taReferencias, 1, row++);

        // Fecha
        grid.add(new Label("Fecha:*"), 0, row);
        DatePicker dpFecha = new DatePicker();
        try {
            Date fecha = dateFormat.parse(ventaRow.fecha);
            dpFecha.setValue(fecha.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
        } catch (Exception e) {
            dpFecha.setValue(java.time.LocalDate.now());
        }
        grid.add(dpFecha, 1, row++);

        // Total (solo lectura)
        grid.add(new Label("Total:"), 0, row);
        TextField tfTotal = new TextField(ventaRow.total);
        tfTotal.setEditable(false);
        tfTotal.setStyle("-fx-background-color: #e0e0e0;");
        grid.add(tfTotal, 1, row++);

        // Botones
        Button btnGuardar = new Button("Guardar");
        Button btnCancelar = new Button("Cancelar");

        btnGuardar.setStyle("-fx-background-color: #0FB9BA; -fx-text-fill: white; -fx-font-weight: bold;");
        btnCancelar.setStyle("-fx-background-color: #E45858; -fx-text-fill: white; -fx-font-weight: bold;");

        btnGuardar.setOnAction(event -> {
            try {
                // Validaciones básicas
                if (tfCliente.getText().trim().isEmpty()) {
                    mostrarAdvertencia("Campo requerido", "El cliente es obligatorio.");
                    return;
                }
                if (dpFecha.getValue() == null) {
                    mostrarAdvertencia("Campo requerido", "La fecha es obligatoria.");
                    return;
                }

                // Actualizar la venta (solo campos editables: fecha)
                // Nota: En este caso solo se puede editar la fecha, los demás datos son de consulta
                mostrarInfo("Información", "La edición de ventas está limitada a consulta.\nPara modificar productos o servicios, cree una nueva venta.");
                dialog.close();

            } catch (Exception ex) {
                mostrarError("Error", "Error al editar: " + ex.getMessage());
            }
        });

        btnCancelar.setOnAction(event -> dialog.close());

        VBox buttonsBox = new VBox(10, btnGuardar, btnCancelar);
        buttonsBox.setAlignment(Pos.CENTER);
        grid.add(buttonsBox, 0, row, 2, 1);

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 500, 600);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    // ===== ELIMINAR =====
    @FXML
    private void onEliminar() {
        VentaRow seleccionada = tblVentas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAdvertencia("Selección requerida", "Debe seleccionar una venta para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar esta venta?");
        confirmacion.setContentText("Venta ID: " + seleccionada.idVenta +
            "\nCliente: " + seleccionada.cliente +
            "\nTotal: " + seleccionada.total +
            "\n\nEsta acción se puede deshacer con el botón 'Deshacer'.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                // Guardar para poder deshacer
                ventasEliminadas.push(seleccionada);

                // Eliminar de la lista
                listaVentas.remove(seleccionada);
                listaFiltrada.remove(seleccionada);
                tblVentas.setItems(listaFiltrada);

                mostrarInfo("Éxito", "Venta eliminada correctamente.\nPuede deshacer esta acción con el botón 'Deshacer'.");

            } catch (Exception e) {
                mostrarError("Error", "Error al eliminar: " + e.getMessage());
            }
        }
    }

    // ===== DESHACER =====
    @FXML
    private void onDeshacer() {
        if (ventasEliminadas.isEmpty()) {
            // Si no hay eliminaciones, solo recargar
            cargarVentas();
            tfBuscar.clear();
            mostrarInfo("Datos recargados", "Se han recargado las ventas desde la base de datos.");
        } else {
            // Restaurar última venta eliminada
            VentaRow ventaRestaurada = ventasEliminadas.pop();
            listaVentas.add(ventaRestaurada);
            listaFiltrada.clear();
            listaFiltrada.addAll(listaVentas);

            // Ordenar por ID
            listaFiltrada.sort(Comparator.comparing(v -> v.idVenta));

            tblVentas.setItems(listaFiltrada);
            mostrarInfo("Venta restaurada", "Se ha restaurado la venta ID: " + ventaRestaurada.idVenta);
        }
    }

    // ===== Header (logo + menús) =====
    @FXML private void onLogoClick()         { goTo(MAINMENU_FXML); }
    @FXML private void onVerNotificaciones() { goTo(SEENOTIFICATIONS_FXML); }
    @FXML private void onDatosPersonales()   { goTo(PERSONALDATA_FXML); }
    @FXML private void onCerrarSesion()      { goTo(LOGIN_FXML); }

    // ===== Menú superior =====
    @FXML private void onRegistrarProductos()        { goTo(REGISTER_PRODUCT_FXML); }
    @FXML private void onInventario()                { goTo(INVENTARY_FXML); }
    @FXML private void onGestionarUsuario()          { goTo(CREATE_USER_FXML); }
    @FXML private void onVentasTop()                 { goTo(SECTION_SALES_FXML); }
    @FXML private void onAgregarClientesTop()        { goTo(ADD_CLIENT_FXML); }
    @FXML private void onAdministrarProveedoresTop() { goTo(ADD_SUPPLIERS_FXML); }

    // ===== Navegación interna =====
    @FXML private void onVolver() { goTo(VISUALIZE_REGISTER_FXML); }

    // ===== Generar reporte =====
    @FXML private void onGenerarReporte() { goTo(GENERATE_REPORT_FXML); }

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

    private void goTo(String fxml) {
        try {
            var url = getClass().getResource(fxml);
            if (url == null) throw new IllegalStateException("NO existe el recurso (classpath): " + fxml);
            Parent newRoot = FXMLLoader.load(url);

            Stage stage = safeStage();
            if (stage == null) throw new IllegalStateException("No hay Stage activo para navegar.");

            Scene scene = stage.getScene();
            if (scene == null) {
                stage.setScene(new Scene(newRoot));
            } else {
                scene.setRoot(newRoot);
            }
            stage.centerOnScreen();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir: " + fxml + "\n" + e.getMessage()).showAndWait();
        }
    }

    // ===== CLASE INTERNA: VentaRow =====
    /**
     * Representa una fila de la tabla de ventas.
     * Consolida todos los datos de una venta para mostrarlos en la tabla.
     */
    public static class VentaRow {
        private final int idVenta;
        private final String productos;
        private final String tiposProducto;
        private final String referencias;
        private final String codigosBarras;
        private final String cliente;
        private final String mascota;
        private final String fecha;
        private final String total;
        private final Venta ventaOriginal;

        public VentaRow(Venta venta) {
            this.ventaOriginal = venta;
            this.idVenta = venta.getIdVenta();

            // Cliente
            Cliente c = venta.getComprador();
            this.cliente = c != null ? c.getNombre() + " " + c.getApellidos() : "N/A";

            // Mascota (obtener la primera mascota del cliente si existe)
            if (c != null && c.getMascotas() != null && !c.getMascotas().isEmpty()) {
                Mascota m = c.getMascotas().get(0);
                this.mascota = m.getNombre() + " (" + m.getEspecie() + ")";
            } else {
                this.mascota = "N/A";
            }

            // Fecha
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            this.fecha = venta.getFecha() != null ? sdf.format(venta.getFecha()) : "N/A";

            // Total
            this.total = String.format("$%,.2f", venta.getTotalVenta());

            // Procesar líneas de venta
            List<LineaVenta> lineas = venta.getLineasVenta();
            if (lineas != null && !lineas.isEmpty()) {
                List<String> nombresProductos = new ArrayList<>();
                List<String> tipos = new ArrayList<>();
                List<String> refs = new ArrayList<>();
                List<String> codigos = new ArrayList<>();

                for (LineaVenta linea : lineas) {
                    if (linea.getProducto() != null) {
                        Producto p = linea.getProducto();
                        nombresProductos.add(p.getNombre() + " (x" + linea.getCantidad() + ")");

                        // Determinar tipo de producto por la clase
                        String tipoProducto = obtenerTipoProducto(p);
                        tipos.add(tipoProducto);

                        refs.add(p.getReferencia() != null ? p.getReferencia() : "N/A");
                        codigos.add(p.getCodigoBarras() != null ? p.getCodigoBarras() : "N/A");
                    } else if (linea.getServicio() != null) {
                        Servicio s = linea.getServicio();
                        nombresProductos.add(s.getNombreServicio() + " (x" + linea.getCantidad() + ")");
                        tipos.add("Servicio");
                        refs.add("N/A");
                        codigos.add("N/A");
                    }
                }

                this.productos = String.join(", ", nombresProductos);
                this.tiposProducto = String.join(", ", tipos);
                this.referencias = String.join(", ", refs);
                this.codigosBarras = String.join(", ", codigos);
            } else {
                this.productos = "Sin productos";
                this.tiposProducto = "N/A";
                this.referencias = "N/A";
                this.codigosBarras = "N/A";
            }
        }

        /**
         * Obtiene el tipo de producto basándose en la clase del objeto.
         */
        private String obtenerTipoProducto(Producto p) {
            String className = p.getClass().getSimpleName();
            switch (className) {
                case "Medicamento":
                    return "Medicamento";
                case "Alimento":
                    return "Alimento";
                case "MaterialQuirurgico":
                    return "Material Quirúrgico";
                case "Accesorio":
                    return "Juguete/Accesorio";
                default:
                    return "Producto";
            }
        }

        public int getIdVenta() { return idVenta; }
        public String getProductos() { return productos; }
        public String getTiposProducto() { return tiposProducto; }
        public String getReferencias() { return referencias; }
        public String getCodigosBarras() { return codigosBarras; }
        public String getCliente() { return cliente; }
        public String getMascota() { return mascota; }
        public String getFecha() { return fecha; }
        public String getTotal() { return total; }
        public Venta getVentaOriginal() { return ventaOriginal; }
    }
}
