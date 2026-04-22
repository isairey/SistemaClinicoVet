package co.edu.upb.veterinaria.controllers.ControllerGenerateReport;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** Generar Reporte: navegación cableada, sin lógica de negocio. */
public class ReportGenerate {

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

    // ===== Raíz / header =====
    @FXML private AnchorPane root;
    @FXML private ImageView topLogo;

    @FXML private MenuButton mbNotificaciones;
    @FXML private MenuItem   miVerNotificaciones;

    @FXML private MenuButton mbPerfil;
    @FXML private MenuItem   miDatosPersonales;
    @FXML private MenuItem   miCerrarSesion;

    // ===== Menú superior (módulos) =====
    @FXML private Button btnRegistrarProductos, btnInventario, btnGestionarUsuario,
            btnVisualizarRegistros, btnVentas, btnAgregarClientes, btnAdministrarProveedores;

    // ===== Filtros (rango fechas) =====
    @FXML private DatePicker dpDesde;   // inicio
    @FXML private DatePicker dpHasta;   // fin

    // ===== Acciones =====
    @FXML private Button btnGenerar;
    @FXML private Button btnVisualizar;

    // ===== Salida del reporte =====
    @FXML private TextArea taReporte;

    @FXML
    private void initialize() {
        // Abrir maximizada
        Platform.runLater(() -> {
            Stage stage = (Stage) root.getScene().getWindow();
            if (stage != null) {
                stage.setResizable(true);
                stage.setMaximized(true);
            }
        });
    }

    // ===== Helper de navegación =====
    private void goTo(String fxml) {
        try {
            var url = getClass().getResource(fxml);
            if (url == null) throw new IllegalStateException("NO existe el recurso (classpath): " + fxml);
            FXMLLoader loader = new FXMLLoader(url);
            loader.setLocation(url);
            Parent newRoot = loader.load();
            Scene scene = root.getScene();
            scene.setRoot(newRoot);
            ((Stage) scene.getWindow()).centerOnScreen();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir: " + fxml + "\n" + e.getMessage()).showAndWait();
        }
    }

    // ===== Header (logo + menús) =====
    @FXML private void onLogoClick()         { goTo(MAINMENU_FXML); }
    @FXML private void onVerNotificaciones() { goTo(SEENOTIFICATIONS_FXML); }
    @FXML private void onDatosPersonales()   { goTo(PERSONALDATA_FXML); }
    @FXML private void onCerrarSesion()      { goTo(LOGIN_FXML); }

    // ===== Menú superior (módulos) =====
    @FXML private void onRegistrarProductos()    { goTo(REGISTER_PRODUCT_FXML); }
    @FXML private void onInventario()            { goTo(INVENTARY_FXML); }
    @FXML private void onGestionarUsuario()      { goTo(CREATE_USER_FXML); }
    @FXML private void onVisualizarRegistros()   { goTo(VISUALIZE_REGISTER_FXML); }
    @FXML private void onVentas()                { goTo(SECTION_SALES_FXML); }
    @FXML private void onAgregarClientes()       { goTo(ADD_CLIENT_FXML); }
    @FXML private void onAdministrarProveedores(){ goTo(ADD_SUPPLIERS_FXML); }

    // ===== Placeholders (sin lógica de negocio) =====
    @FXML private void onGenerar()   {}
    @FXML private void onVisualizar(){}
}