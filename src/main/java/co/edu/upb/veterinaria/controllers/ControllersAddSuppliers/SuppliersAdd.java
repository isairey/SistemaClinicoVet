package co.edu.upb.veterinaria.controllers.ControllersAddSuppliers;

import co.edu.upb.veterinaria.models.ModeloProveedor.Proveedor;
import co.edu.upb.veterinaria.services.ServicioProveedor.ProveedorService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** Agregar Proveedores: funcionalidad completa con conexión a la base de datos. */
public class SuppliersAdd {

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
    private static final String SECTION_SUPPLIERS_FXML =
            "/co/edu/upb/veterinaria/views/SectionSuppliers-view/SectionSuppliers-view.fxml";

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

    // ===== Combos =====
    @FXML private ComboBox<String> cbTipoProveedor;
    @FXML private ComboBox<String> cbTipoDocumento;

    // ===== Campos =====
    @FXML private TextField tfNombre, tfApellido, tfDireccion, tfCiudad, tfNIT, tfEmail, tfTelefono;

    // ===== Botones =====
    @FXML private Button btnGuardar;
    @FXML private Button btnVerProveedores;

    // ===== Servicio =====
    private ProveedorService proveedorService;

    @FXML
    private void initialize() {
        // Inicializar el servicio
        try {
            proveedorService = new ProveedorService();
            System.out.println("✓ ProveedorService inicializado correctamente");
        } catch (Exception e) {
            mostrarError("Error de Inicialización",
                    "No se pudo conectar con la base de datos: " + e.getMessage());
            e.printStackTrace();
        }

        // Cargar opciones en los ComboBox
        cbTipoProveedor.getItems().addAll("Natural", "Jurídica");
        cbTipoDocumento.getItems().addAll("NIT", "RUT");

        // Abrir maximizado
        Platform.runLater(() -> {
            Stage st = (Stage) root.getScene().getWindow();
            if (st != null) {
                st.setResizable(true);
                st.setMaximized(true);
            }
        });
    }

    // ===== Helper de navegación =====
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
    @FXML private void onRegistrarProductos()    { goTo(REGISTER_PRODUCT_FXML); }
    @FXML private void onInventario()            { goTo(INVENTARY_FXML); }
    @FXML private void onGestionarUsuario()      { goTo(CREATE_USER_FXML); }
    @FXML private void onVisualizarRegistros()   { goTo(VISUALIZE_REGISTER_FXML); }
    @FXML private void onVentas()                { goTo(SECTION_SALES_FXML); }
    @FXML private void onAgregarClientes()       { goTo(ADD_CLIENT_FXML); }
    @FXML private void onAdministrarProveedores(){ goTo(ADD_SUPPLIERS_FXML); }

    // ===== MÉTODO PRINCIPAL: Guardar Proveedor =====
    @FXML
    private void onGuardar() {
        try {
            // 1. Validar que todos los campos estén llenos
            if (!validarCampos()) {
                return; // Ya se mostró el mensaje de error en validarCampos()
            }

            // 2. Crear el objeto Proveedor con los datos del formulario
            Proveedor proveedor = new Proveedor();
            proveedor.setTipoPersona(cbTipoProveedor.getValue());
            proveedor.setTipoDocumento(cbTipoDocumento.getValue());
            proveedor.setNombre(tfNombre.getText().trim());
            proveedor.setApellido(tfApellido.getText().trim());
            proveedor.setNit_rut(tfNIT.getText().trim());
            proveedor.setTelefono(tfTelefono.getText().trim());
            proveedor.setEmail(tfEmail.getText().trim());
            proveedor.setDireccion(tfDireccion.getText().trim());
            proveedor.setCiudad(tfCiudad.getText().trim());

            // 3. Guardar en la base de datos usando el servicio
            int idGenerado = proveedorService.crearProveedor(proveedor);

            // 4. Mostrar mensaje de éxito
            if (idGenerado > 0) {
                mostrarExito("Éxito", "Proveedor guardado exitosamente");

                // 5. Limpiar el formulario
                limpiarCampos();
            } else {
                mostrarError("Error al Guardar",
                        "No se pudo guardar el proveedor. Intente nuevamente.");
            }

        } catch (IllegalArgumentException e) {
            // Errores de validación del servicio
            mostrarAdvertencia("Validación Fallida", e.getMessage());
        } catch (Exception e) {
            // Errores inesperados
            mostrarError("Error Inesperado",
                    "Ocurrió un error al guardar el proveedor:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Valida que todos los campos del formulario estén llenos.
     *
     * @return true si todos los campos son válidos, false en caso contrario.
     */
    private boolean validarCampos() {
        // Validar ComboBox Tipo Persona
        if (cbTipoProveedor.getValue() == null || cbTipoProveedor.getValue().trim().isEmpty()) {
            mostrarAdvertencia("Campo Requerido", "Debe seleccionar el tipo de persona.");
            cbTipoProveedor.requestFocus();
            return false;
        }

        // Validar ComboBox Tipo Documento
        if (cbTipoDocumento.getValue() == null || cbTipoDocumento.getValue().trim().isEmpty()) {
            mostrarAdvertencia("Campo Requerido", "Debe seleccionar el tipo de documento.");
            cbTipoDocumento.requestFocus();
            return false;
        }

        // Validar Nombre
        if (tfNombre.getText() == null || tfNombre.getText().trim().isEmpty()) {
            mostrarAdvertencia("Campo Requerido", "Debe ingresar el nombre del proveedor.");
            tfNombre.requestFocus();
            return false;
        }

        // Validar Apellido
        if (tfApellido.getText() == null || tfApellido.getText().trim().isEmpty()) {
            mostrarAdvertencia("Campo Requerido", "Debe ingresar el apellido del proveedor.");
            tfApellido.requestFocus();
            return false;
        }

        // Validar NIT/RUT
        if (tfNIT.getText() == null || tfNIT.getText().trim().isEmpty()) {
            mostrarAdvertencia("Campo Requerido", "Debe ingresar el NIT/RUT.");
            tfNIT.requestFocus();
            return false;
        }

        // Validar Teléfono
        if (tfTelefono.getText() == null || tfTelefono.getText().trim().isEmpty()) {
            mostrarAdvertencia("Campo Requerido", "Debe ingresar el teléfono.");
            tfTelefono.requestFocus();
            return false;
        }

        // Validar Email
        if (tfEmail.getText() == null || tfEmail.getText().trim().isEmpty()) {
            mostrarAdvertencia("Campo Requerido", "Debe ingresar el email.");
            tfEmail.requestFocus();
            return false;
        }

        // Validar Dirección
        if (tfDireccion.getText() == null || tfDireccion.getText().trim().isEmpty()) {
            mostrarAdvertencia("Campo Requerido", "Debe ingresar la dirección.");
            tfDireccion.requestFocus();
            return false;
        }

        // Validar Ciudad
        if (tfCiudad.getText() == null || tfCiudad.getText().trim().isEmpty()) {
            mostrarAdvertencia("Campo Requerido", "Debe ingresar la ciudad.");
            tfCiudad.requestFocus();
            return false;
        }

        return true; // Todos los campos son válidos
    }

    /**
     * Limpia todos los campos del formulario.
     */
    private void limpiarCampos() {
        cbTipoProveedor.setValue(null);
        cbTipoDocumento.setValue(null);
        tfNombre.clear();
        tfApellido.clear();
        tfNIT.clear();
        tfTelefono.clear();
        tfEmail.clear();
        tfDireccion.clear();
        tfCiudad.clear();

        // Enfocar el primer campo
        cbTipoProveedor.requestFocus();
    }

    // "Entrar" -> listado/Sección de Proveedores
    @FXML private void onVerProveedores() { goTo(SECTION_SUPPLIERS_FXML); }

    // ===== Métodos auxiliares para mostrar alertas =====

    private void mostrarExito(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
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

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}