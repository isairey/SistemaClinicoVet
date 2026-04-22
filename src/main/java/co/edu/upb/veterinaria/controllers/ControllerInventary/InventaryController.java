package co.edu.upb.veterinaria.controllers.ControllerInventary;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class InventaryController {

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

    // Nueva vista de medicamentos
    private static final String INVENTORY_MEDICAMENT_FXML =
            "/co/edu/upb/veterinaria/views/inventaryMedicamento-view/inventaryMedicament-view.fxml";

    private static final String INVENTORY_ALIMENT_FXML =
            "/co/edu/upb/veterinaria/views/inventaryAliment-view/inventaryAliment-view.fxml";

    private static final String INVENTORY_MATERIALQ_FXML =
            "/co/edu/upb/veterinaria/views/inventaryMaterialQ-view/inventaryMaterialQ-view.fxml";

    private static final String INVENTORY_JUGUETE_FXML =
            "/co/edu/upb/veterinaria/views/inventaryJugueteAccs-view/inventaryJugueteAccs-view.fxml";

    // ===== Raíz / contenedores =====
    @FXML private AnchorPane root;
    @FXML private ScrollPane scrollTabla;
    @FXML private VBox tableWrapper;
    @FXML private TableView<?> tblInventario;

    // ===== Header =====
    @FXML private ImageView topLogo;
    @FXML private MenuButton mbNotificaciones;
    @FXML private MenuItem   miVerNotificaciones;
    @FXML private MenuButton mbPerfil;
    @FXML private MenuItem   miDatosPersonales, miCerrarSesion;

    // ===== Menú superior (botones de otros módulos) =====
    @FXML private Button btnRegistrarProductos, btnGestionarUsuario, btnVisualizarRegistros,
            btnVentas, btnAgregarClientes, btnAdministrarProveedores;

    // ===== Filtros / búsqueda =====
    @FXML private TextField tfBuscar;
    @FXML private Button btnFiltrar, btnBuscar, btnLimpiar;
    @FXML private Button btnMedicamento, btnAlimento, btnMaterial, btnJuguete;

    // ===== Acciones tabla =====
    @FXML private Button btnDeshacer, btnEditarSeleccion, btnEliminarSeleccion;

    // ===== Columnas =====
    @FXML private TableColumn<?, ?> colId;

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
    }

    // ===== Navegación común =====
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

    // ===== Header (logo + menús) =====
    @FXML private void onLogoClick()         { goTo(MAINMENU_FXML); }
    @FXML private void onVerNotificaciones() { goTo(SEENOTIFICATIONS_FXML); }
    @FXML private void onDatosPersonales()   { goTo(PERSONALDATA_FXML); }
    @FXML private void onCerrarSesion()      { goTo(LOGIN_FXML); }

    // ===== Menú superior (módulos) =====
    @FXML private void onRegistrarProductos()     { goTo(REGISTER_PRODUCT_FXML); }
    @FXML private void onGestionarUsuario()       { goTo(CREATE_USER_FXML); }
    @FXML private void onVisualizarRegistros()    { goTo(VISUALIZE_REGISTER_FXML); }
    @FXML private void onVentas()                 { goTo(SECTION_SALES_FXML); }
    @FXML private void onAgregarClientes()        { goTo(ADD_CLIENT_FXML); }
    @FXML private void onAdministrarProveedores() { goTo(ADD_SUPPLIERS_FXML); }

    // ===== Acciones (sin lógica de negocio) =====
    @FXML private void onBuscar()           {}
    @FXML private void onFiltrar()          {}
    @FXML private void onLimpiar()          {}
    @FXML private void onAlimento() { goTo(INVENTORY_ALIMENT_FXML); }
    @FXML private void onMaterial()         {goTo(INVENTORY_MATERIALQ_FXML);}
    @FXML private void onJuguete()          {goTo(INVENTORY_JUGUETE_FXML);}
    @FXML private void onDeshacer()         {}
    @FXML private void onEditarSeleccion()  {}
    @FXML private void onEliminarSeleccion() {}

    // Ir a la vista de Medicamentos
    @FXML private void onMedicamento() { goTo(INVENTORY_MEDICAMENT_FXML); }


}