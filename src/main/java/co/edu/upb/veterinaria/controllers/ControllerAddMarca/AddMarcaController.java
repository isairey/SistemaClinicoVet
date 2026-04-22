package co.edu.upb.veterinaria.controllers.ControllerAddMarca;

import co.edu.upb.veterinaria.services.ServicioMarca.MarcaService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.sql.SQLException;

public class AddMarcaController {

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

    // Futura lista de marcas (si la creas más tarde)
    private static final String SECTION_BRANDS_FXML =
            "/co/edu/upb/veterinaria/views/SectionBrands-view/SectionBrands-view.fxml";

    // ===== SERVICIO =====
    private final MarcaService marcaService;

    // ===== Constructor =====
    public AddMarcaController() {
        this.marcaService = new MarcaService();
    }

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

    // Form
    @FXML private TextField tfNombreMarca;
    @FXML private TextArea  taDescripcion;
    @FXML private Button    btnGuardar, btnVerMarcas, btnVolver;

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            Stage stage = (Stage) root.getScene().getWindow();
            if (stage != null) {
                stage.setResizable(true);
                stage.setMaximized(true);
            }
        });
    }

    // ===== Navegación utilitaria =====
    private void goTo(String fxml) {
        try {
            var url = getClass().getResource(fxml);
            if (url == null) throw new IllegalStateException("NO existe el recurso (classpath): " + fxml);
            Parent newRoot = FXMLLoader.load(url);
            Scene scene = root.getScene();
            if (scene == null) {
                scene = new Scene(newRoot);
                ((Stage) root.getScene().getWindow()).setScene(scene);
            } else {
                scene.setRoot(newRoot);
            }
            ((Stage) scene.getWindow()).centerOnScreen();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir: " + fxml + "\n" + e.getMessage()).showAndWait();
        }
    }

    // ===== Header =====
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

    // ===== Acciones formulario =====

    /**
     * Guarda una nueva marca en la base de datos.
     *
     * Validaciones:
     *  - Nombre requerido (mínimo 2 caracteres)
     *  - Nombre único (no puede existir otra marca con el mismo nombre)
     *  - Descripción opcional
     *
     * Si el guardado es exitoso:
     *  - Muestra mensaje de confirmación
     *  - Limpia los campos del formulario
     *  - La marca queda disponible para seleccionar en registro de productos
     */
    @FXML
    private void onGuardar() {
        // Obtener datos del formulario
        String nombre = tfNombreMarca.getText();
        String descripcion = taDescripcion.getText();

        try {
            // Validación visual básica
            if (nombre == null || nombre.trim().isEmpty()) {
                mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Campos incompletos",
                    "Debe ingresar el nombre de la marca"
                );
                tfNombreMarca.requestFocus();
                return;
            }

            // Registrar marca usando el servicio
            // El servicio se encarga de:
            //  - Validar el nombre (mínimo 2 caracteres, único)
            //  - Insertar en BD con ID auto-generado
            //  - Manejar la descripción opcional
            int idMarcaCreada = marcaService.registrarMarca(nombre, descripcion);

            // Mostrar mensaje de éxito
            mostrarAlerta(
                Alert.AlertType.INFORMATION,
                "Marca registrada exitosamente",
                "La marca '" + nombre.trim() + "' fue creada con ID: " + idMarcaCreada +
                "\n\nYa puede seleccionarla al registrar productos."
            );

            // Limpiar formulario para agregar otra marca
            limpiarFormulario();

        } catch (IllegalArgumentException e) {
            // Error de validación de negocio (nombre vacío, duplicado, etc.)
            mostrarAlerta(
                Alert.AlertType.ERROR,
                "Error de validación",
                e.getMessage()
            );
            tfNombreMarca.requestFocus();

        } catch (SQLException e) {
            // Error de base de datos
            mostrarAlerta(
                Alert.AlertType.ERROR,
                "Error al guardar en la base de datos",
                "No se pudo registrar la marca.\n\n" +
                "Detalles técnicos: " + e.getMessage()
            );
            e.printStackTrace();

        } catch (Exception e) {
            // Cualquier otro error inesperado
            mostrarAlerta(
                Alert.AlertType.ERROR,
                "Error inesperado",
                "Ocurrió un error al intentar guardar la marca.\n\n" + e.getMessage()
            );
            e.printStackTrace();
        }
    }

    @FXML
    private void onVerMarcas() {
        // Si aún no existe la vista, puedes dejar vacío o apuntar cuando la crees:
        goTo(SECTION_BRANDS_FXML);
    }

    @FXML
    private void onVolver() {
        // Como pediste: vuelve a registerProduct-view.fxml
        goTo(REGISTER_PRODUCT_FXML);
    }

    // ===== MÉTODOS AUXILIARES =====

    /**
     * Limpia todos los campos del formulario.
     */
    private void limpiarFormulario() {
        tfNombreMarca.clear();
        taDescripcion.clear();
        tfNombreMarca.requestFocus();
    }

    /**
     * Muestra una alerta con el tipo, título y mensaje especificados.
     *
     * @param tipo Tipo de alerta (INFORMATION, WARNING, ERROR)
     * @param titulo Título de la ventana
     * @param mensaje Mensaje a mostrar
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        // Hacer que la alerta sea modal y esperar a que se cierre
        alert.showAndWait();
    }
}