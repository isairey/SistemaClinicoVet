package co.edu.upb.veterinaria.controllers.ControllerMainMenu;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainMenuController {

    // === RUTAS (coinciden con tu tree de resources) ===
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

    // ===== Raíz y contenedores =====
    @FXML private AnchorPane root;
    @FXML private HBox topBar;
    @FXML private HBox menuBar;
    @FXML private VBox contentBox;

    // ===== Logos =====
    @FXML private ImageView topLogo;   // clic para recargar MainMenu
    @FXML private ImageView centerLogo;

    // ===== Header: MenuButtons =====
    @FXML private MenuButton mbNotificaciones;
    @FXML private MenuItem   miVerNotificaciones;
    @FXML private MenuButton mbPerfil;
    @FXML private MenuItem   miDatosPersonales;
    @FXML private MenuItem   miCerrarSesion;

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setResizable(true);
            stage.setMaximized(true);
        });

        topBar.prefWidthProperty().bind(root.widthProperty());
        menuBar.prefWidthProperty().bind(root.widthProperty());
        contentBox.prefWidthProperty().bind(root.widthProperty());
        contentBox.prefHeightProperty().bind(root.heightProperty().subtract(170));

        centerLogo.fitWidthProperty().bind(root.widthProperty().multiply(0.60));
        centerLogo.fitHeightProperty().bind(root.heightProperty().subtract(170).multiply(0.65));
        centerLogo.setPreserveRatio(true);

        topBar.widthProperty().addListener((obs, oldW, newW) -> {
            double w = newW.doubleValue();
            double target = (w < 900) ? 110 : 150;
            topLogo.setFitWidth(target);
            topLogo.setPreserveRatio(true);
        });
    }

    // ====== Helper de navegación ======
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

    // ====== Header actions ======
    @FXML private void onLogoClick()         { goTo(MAINMENU_FXML); }
    @FXML private void onVerNotificaciones() { goTo(SEENOTIFICATIONS_FXML); }
    @FXML private void onDatosPersonales()   { goTo(PERSONALDATA_FXML); }
    @FXML private void onCerrarSesion()      { goTo(LOGIN_FXML); }

    // ====== Botones del menú ======
    @FXML private void onRegistrarProductos()      { goTo(REGISTER_PRODUCT_FXML); }
    @FXML private void onInventario()              { goTo(INVENTARY_FXML); }
    @FXML private void onGestionarUsuario()        { goTo(CREATE_USER_FXML); }
    @FXML private void onVisualizarRegistros()     { goTo(VISUALIZE_REGISTER_FXML); }
    @FXML private void onVentas()                  { goTo(SECTION_SALES_FXML); }
    @FXML private void onAgregarClientes()         { goTo(ADD_CLIENT_FXML); }
    @FXML private void onAdministrarProveedores()  { goTo(ADD_SUPPLIERS_FXML); }
}