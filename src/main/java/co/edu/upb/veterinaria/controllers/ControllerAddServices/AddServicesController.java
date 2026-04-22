package co.edu.upb.veterinaria.controllers.ControllerAddServices;

import co.edu.upb.veterinaria.config.DatabaseConfig;
import co.edu.upb.veterinaria.models.ModeloServicio.Servicio;
import co.edu.upb.veterinaria.services.ServicioServicio.ServicioService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.sql.DataSource;

public class AddServicesController {

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
    private static final String SECTION_SERVICES_FXML =
            "/co/edu/upb/veterinaria/views/SectionServices-view/SectionServices-view.fxml";

    // ===== Root / header =====
    @FXML private AnchorPane root;
    @FXML private ImageView topLogo;

    @FXML private MenuButton mbNotificaciones;
    @FXML private MenuItem   miVerNotificaciones;
    @FXML private MenuButton mbPerfil;
    @FXML private MenuItem   miDatosPersonales, miCerrarSesion;

    // ===== Menú superior =====
    @FXML private Button btnRegistrarProductos, btnInventario, btnGestionarUsuario,
            btnVisualizarRegistros, btnVentas, btnAgregarClientes, btnAdministrarProveedores;

    // ===== Form =====
    @FXML private TextField tfNombreServicio, tfPrecio;
    @FXML private TextArea  taDescripcion;
    @FXML private Button    btnGuardar, btnVerServicios, btnVolver;

    // ===== Servicio =====
    private ServicioService servicioService;

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            Stage stage = (Stage) root.getScene().getWindow();
            if (stage != null) {
                stage.setResizable(true);
                stage.setMaximized(true);
            }
        });

        // Inicializar el servicio con el DataSource
        try {
            DataSource dataSource = DatabaseConfig.getDataSource();
            servicioService = new ServicioService(dataSource);
        } catch (Exception e) {
            mostrarError("Error al conectar con la base de datos: " + e.getMessage());
        }
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
    @FXML
    private void onGuardar() {
        try {
            // Validar campos
            String nombre = tfNombreServicio.getText();
            String precioTexto = tfPrecio.getText();
            String descripcion = taDescripcion.getText();

            if (nombre == null || nombre.trim().isEmpty()) {
                mostrarAdvertencia("El nombre del servicio es obligatorio");
                return;
            }

            if (precioTexto == null || precioTexto.trim().isEmpty()) {
                mostrarAdvertencia("El precio es obligatorio");
                return;
            }

            // Validar que el precio sea un número válido
            double precio;
            try {
                precio = Double.parseDouble(precioTexto);
                if (precio <= 0) {
                    mostrarAdvertencia("El precio debe ser mayor a 0");
                    return;
                }
            } catch (NumberFormatException e) {
                mostrarAdvertencia("El precio debe ser un número válido. Ejemplo: 65000");
                return;
            }

            // Crear el objeto servicio
            Servicio servicio = new Servicio();
            servicio.setNombreServicio(nombre.trim());
            servicio.setPrecio(precio);
            servicio.setDescripcion(descripcion != null ? descripcion.trim() : "");

            // Guardar en la base de datos
            System.out.println(">>> Guardando servicio: " + nombre + " - Precio: $" + precio);
            int idGenerado = servicioService.crearServicio(servicio);

            if (idGenerado > 0) {
                System.out.println(">>> Servicio guardado exitosamente" );
                mostrarExito("Servicio guardado exitosamente ");
                limpiarFormulario();
            } else {
                mostrarError("No se pudo registrar el servicio");
            }

        } catch (IllegalArgumentException e) {
            mostrarAdvertencia(e.getMessage());
        } catch (Exception e) {
            System.err.println(">>> ERROR al guardar servicio: " + e.getMessage());
            mostrarError("Error al guardar el servicio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onVerServicios() { goTo(SECTION_SERVICES_FXML);  }

    @FXML private void onVolver() { goTo(SECTION_SALES_FXML); }

    // ===== Métodos auxiliares =====
    private void limpiarFormulario() {
        tfNombreServicio.clear();
        tfPrecio.clear();
        taDescripcion.clear();
        tfNombreServicio.requestFocus();
    }

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAdvertencia(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }


}