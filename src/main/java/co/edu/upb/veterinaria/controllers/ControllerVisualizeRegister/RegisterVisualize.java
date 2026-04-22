package co.edu.upb.veterinaria.controllers.ControllerVisualizeRegister;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;

public class RegisterVisualize {

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
    private static final String SECTION_SALES_FXML =
            "/co/edu/upb/veterinaria/views/SectionSales-view/SectionSales-view.fxml";
    private static final String ADD_CLIENT_FXML =
            "/co/edu/upb/veterinaria/views/AddClient-view/AddClient-view.fxml";
    private static final String ADD_SUPPLIERS_FXML =
            "/co/edu/upb/veterinaria/views/AddSuppliers-view/AddSuppliers-view.fxml";

    private static final String VISUALIZE_REGISTER_FXML =
            "/co/edu/upb/veterinaria/views/visualizeRegister-view/visualizeRegister-view.fxml";
    private static final String SAILS_VISUALIZE_REGISTER_FXML =
            "/co/edu/upb/veterinaria/views/SailsVisualizeRegister-view/SailsVisualizeRegister-view.fxml";
    private static final String CLIENT_VISUALIZE_REGISTER_FXML =
            "/co/edu/upb/veterinaria/views/ClientVisualizeRegister-view/ClientVisualizeRegister-view.fxml";

    // ===== Raíz / header =====
    @FXML private AnchorPane root;
    @FXML private ImageView topLogo;

    // Header: campana / perfil
    @FXML private MenuButton mbNotificaciones;
    @FXML private MenuItem   miVerNotificaciones;
    @FXML private MenuButton mbPerfil;
    @FXML private MenuItem   miDatosPersonales;
    @FXML private MenuItem   miCerrarSesion;

    // Menú superior
    @FXML private Button btnRegistrarProductos, btnInventario, btnGestionarUsuario,
            btnVentasTop, btnAgregarClientesTop, btnAdministrarProveedoresTop;

    // Tabs
    @FXML private ToggleButton btnTabVentas;
    @FXML private ToggleButton btnTabClientes;

    // Búsqueda
    @FXML private TextField tfBuscar;
    @FXML private Button btnFiltrar, btnBuscar, btnLimpiar;

    // Tabla + columnas
    @FXML private ScrollPane scrollTabla;
    @FXML private TableView<?> tblRegistros;
    @FXML private TableColumn<?, ?> colNada, colNadaa, colNadaaa, colNadaaaa, colNadaaaaa;

    @FXML
    private void initialize() {
        // Maximizar
        Platform.runLater(() -> {
            Stage st = safeStage();
            if (st != null) {
                st.setResizable(true);
                st.setMaximized(true);
            }
        });
    }

    // ===== Navegación util =====
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
                stage.setScene(new Scene(newRoot));
            } else {
                scene.setRoot(newRoot);
            }
            stage.centerOnScreen();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir: " + fxml + "\n" + e.getMessage()).showAndWait();
        }
    }

    // ===== Header actions =====
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

    // ===== Tabs (navegación a sub-vistas) =====
    @FXML private void onTabVentas()   { goTo(SAILS_VISUALIZE_REGISTER_FXML); }
    @FXML
    private void onTabClientes() {
        // Ir directamente a la vista de registros de clientes
        goTo(CLIENT_VISUALIZE_REGISTER_FXML);
    }

    // ===== Search actions (stubs) =====
    @FXML private void onFiltrar() { /* placeholder */ }
    @FXML private void onBuscar()  { /* placeholder */ }
    @FXML private void onLimpiar() { /* placeholder */ }
}