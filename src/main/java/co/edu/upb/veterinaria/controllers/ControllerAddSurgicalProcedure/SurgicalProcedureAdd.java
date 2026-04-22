package co.edu.upb.veterinaria.controllers.ControllerAddSurgicalProcedure;

import co.edu.upb.veterinaria.config.DatabaseConfig;
import co.edu.upb.veterinaria.controllers.ClienteTemporalData;
import co.edu.upb.veterinaria.models.ModeloCliente.Cliente;
import co.edu.upb.veterinaria.models.ModeloMascota.Mascota;
import co.edu.upb.veterinaria.models.ModeloServicio.Servicio;
import co.edu.upb.veterinaria.repositories.RepositorioServicio.ServicioRepository;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/** Añadir Servicios — con funcionalidad completa */
public class SurgicalProcedureAdd {

    // ===== RUTAS =====
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

    // ===== Root / header =====
    @FXML private AnchorPane root;
    @FXML private ImageView topLogo;
    @FXML private MenuButton mbNotificaciones;
    @FXML private MenuItem   miVerNotificaciones;
    @FXML private MenuButton mbPerfil;
    @FXML private MenuItem   miDatosPersonales, miCerrarSesion;

    // Menú superior
    @FXML private Button btnRegistrarProductos, btnInventario, btnGestionarUsuario,
            btnVisualizarRegistros, btnVentas, btnAgregarClientes, btnAdministrarProveedores;

    // Botón Volver
    @FXML private Button btnVolver;

    // ===== Búsqueda =====
    @FXML private TextField tfBuscar;
    @FXML private Button btnFiltrar, btnBuscar, btnLimpiar;

    // ===== IZQUIERDA: Servicios disponibles =====
    @FXML private ScrollPane scrollDisponibles;
    @FXML private TableView<Servicio> tblServiciosDisponibles;
    @FXML private TableColumn<Servicio, String> colDispId, colDispNombre, colDispPrecio, colDispDescripcion;
    @FXML private Button btnSeleccionarServicio;

    // ===== DERECHA: Servicios seleccionados =====
    @FXML private Label lbCliente, lbMascota;
    @FXML private Button btnLimpiarSeleccion;
    @FXML private ScrollPane scrollSeleccionados;
    @FXML private TableView<Servicio> tblServiciosSeleccionados;
    @FXML private TableColumn<Servicio, String> colSelId, colSelNombre, colSelPrecio, colSelDescripcion;

    // ===== Descripción + Guardar =====
    @FXML private TextArea taDescripcion;
    @FXML private Button btnGuardar;

    // ===== SERVICIOS Y DATOS =====
    private final ServicioRepository servicioRepository = new ServicioRepository(DatabaseConfig.getDataSource());
    private final ObservableList<Servicio> serviciosDisponibles = FXCollections.observableArrayList();
    private final ObservableList<Servicio> serviciosSeleccionados = FXCollections.observableArrayList();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
    private Cliente clienteActual = null;
    private Mascota mascotaActual = null;

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            Stage stage = safeStage();
            if (stage != null) {
                stage.setResizable(true);
                stage.setMaximized(true);
            }
        });

        // Cargar datos del cliente y mascota
        cargarDatosTemporales();

        // Configurar tablas
        configurarTablas();

        // Cargar servicios disponibles
        cargarServiciosDisponibles();
    }

    private void cargarDatosTemporales() {
        clienteActual = ClienteTemporalData.getCliente();
        mascotaActual = ClienteTemporalData.getMascota();

        if (clienteActual != null) {
            lbCliente.setText(clienteActual.getNombre() + " " + clienteActual.getApellidos());
        } else {
            lbCliente.setText("N/A");
        }

        if (mascotaActual != null) {
            lbMascota.setText(mascotaActual.getNombre() + " - " + mascotaActual.getEspecie());
        } else {
            lbMascota.setText("N/A");
        }
    }

    private void configurarTablas() {
        // Tabla de servicios disponibles
        if (tblServiciosDisponibles != null) {
            tblServiciosDisponibles.setItems(serviciosDisponibles);
            colDispId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getIdServicio())));
            colDispNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombreServicio()));
            colDispPrecio.setCellValueFactory(data -> new SimpleStringProperty(currencyFormat.format(data.getValue().getPrecio())));
            colDispDescripcion.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescripcion()));
        }

        // Tabla de servicios seleccionados
        if (tblServiciosSeleccionados != null) {
            tblServiciosSeleccionados.setItems(serviciosSeleccionados);
            colSelId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getIdServicio())));
            colSelNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombreServicio()));
            colSelPrecio.setCellValueFactory(data -> new SimpleStringProperty(currencyFormat.format(data.getValue().getPrecio())));
            colSelDescripcion.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescripcion()));
        }
    }

    private void cargarServiciosDisponibles() {
        try {
            List<Servicio> servicios = servicioRepository.findAll();
            serviciosDisponibles.clear();
            serviciosDisponibles.addAll(servicios);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar servicios: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // ===== Navegación =====
    private Stage safeStage() {
        if (root != null && root.getScene() != null) return (Stage) root.getScene().getWindow();
        Window w = Window.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null);
        return (w instanceof Stage) ? (Stage) w : null;
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
                scene = new Scene(newRoot);
                stage.setScene(scene);
            } else {
                scene.setRoot(newRoot);
            }
            stage.centerOnScreen();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir: " + fxml + "\n" + e.getMessage()).showAndWait();
        }
    }

    // ===== Header (logo + menús) =====
    @FXML private void onLogoClick()         { goTo(MAINMENU_FXML); }
    @FXML private void onVerNotificaciones() { goTo(SEENOTIFICATIONS_FXML); }
    @FXML private void onDatosPersonales()   { goTo(PERSONALDATA_FXML); }
    @FXML private void onCerrarSesion()      { goTo(LOGIN_FXML); }

    // ===== Menú superior =====
    @FXML private void onRegistrarProductos()     { goTo(REGISTER_PRODUCT_FXML); }
    @FXML private void onInventario()             { goTo(INVENTARY_FXML); }
    @FXML private void onGestionarUsuario()       { goTo(CREATE_USER_FXML); }
    @FXML private void onVisualizarRegistros()    { goTo(VISUALIZE_REGISTER_FXML); }
    @FXML private void onVentas()                 { goTo(SECTION_SALES_FXML); }
    @FXML private void onAgregarClientes()        { goTo(ADD_CLIENT_FXML); }
    @FXML private void onAdministrarProveedores() { goTo(ADD_SUPPLIERS_FXML); }

    // ===== Volver =====
    @FXML private void onVolver() { goTo(SECTION_SALES_FXML); }

    // ===== BÚSQUEDA =====
    @FXML
    private void onBuscar() {
        String busqueda = tfBuscar.getText();
        if (busqueda == null || busqueda.trim().isEmpty()) {
            cargarServiciosDisponibles();
            return;
        }

        try {
            List<Servicio> todosServicios = servicioRepository.findAll();
            serviciosDisponibles.clear();

            String busquedaLower = busqueda.toLowerCase().trim();
            for (Servicio s : todosServicios) {
                if (s.getNombreServicio().toLowerCase().contains(busquedaLower) ||
                    s.getDescripcion().toLowerCase().contains(busquedaLower) ||
                    String.valueOf(s.getIdServicio()).contains(busquedaLower)) {
                    serviciosDisponibles.add(s);
                }
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al buscar servicios: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onLimpiar() {
        tfBuscar.clear();
        cargarServiciosDisponibles();
    }

    @FXML
    private void onFiltrar() {
        // Placeholder para filtrado avanzado si se necesita
        mostrarAlerta("Filtrar", "Funcionalidad de filtrado disponible próximamente.", Alert.AlertType.INFORMATION);
    }

    // ===== SELECCIONAR SERVICIO =====
    @FXML
    private void onSeleccionarServicio() {
        Servicio seleccionado = tblServiciosDisponibles.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Sin selección", "Por favor seleccione un servicio de la lista.", Alert.AlertType.WARNING);
            return;
        }

        // Verificar si ya está en la lista de seleccionados
        for (Servicio s : serviciosSeleccionados) {
            if (s.getIdServicio() == seleccionado.getIdServicio()) {
                mostrarAlerta("Servicio duplicado", "Este servicio ya está seleccionado.", Alert.AlertType.WARNING);
                return;
            }
        }

        // Agregar a servicios seleccionados
        serviciosSeleccionados.add(seleccionado);
    }

    @FXML
    private void onLimpiarSeleccion() {
        serviciosSeleccionados.clear();
        taDescripcion.clear();
    }

    // ===== GUARDAR =====
    @FXML
    private void onGuardar() {
        // Validaciones
        if (clienteActual == null) {
            mostrarAlerta("Cliente requerido", "Debe seleccionar un cliente antes de guardar.", Alert.AlertType.WARNING);
            return;
        }

        if (serviciosSeleccionados.isEmpty()) {
            mostrarAlerta("Sin servicios", "Debe seleccionar al menos un servicio.", Alert.AlertType.WARNING);
            return;
        }

        // Guardar los servicios seleccionados en ClienteTemporalData para que aparezcan en la venta
        ClienteTemporalData.setServiciosSeleccionados(serviciosSeleccionados);

        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Cliente: ").append(clienteActual.getNombre()).append(" ").append(clienteActual.getApellidos()).append("\n");

        if (mascotaActual != null) {
            mensaje.append("Mascota: ").append(mascotaActual.getNombre()).append("\n");
        }

        mensaje.append("\nServicios agregados a la venta:\n");
        for (Servicio s : serviciosSeleccionados) {
            mensaje.append("- ").append(s.getNombreServicio()).append(" (").append(currencyFormat.format(s.getPrecio())).append(")\n");
        }

        String observaciones = taDescripcion.getText();
        if (observaciones != null && !observaciones.trim().isEmpty()) {
            mensaje.append("\nObservaciones: ").append(observaciones);
            // Guardar observaciones para el historial clínico (implementar más adelante)
            ClienteTemporalData.setObservacionesServicio(observaciones);
        }

        mostrarAlerta("Servicios agregados",
            mensaje.toString() + "\n\nLos servicios se han agregado a la venta.",
            Alert.AlertType.INFORMATION);

        // Limpiar y volver a la vista de ventas
        onLimpiarSeleccion();
        goTo(SECTION_SALES_FXML);
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

