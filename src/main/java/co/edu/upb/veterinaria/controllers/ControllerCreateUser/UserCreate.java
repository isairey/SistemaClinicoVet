package co.edu.upb.veterinaria.controllers.ControllerCreateUser;

import co.edu.upb.veterinaria.models.ModeloModulo.Modulo;
import co.edu.upb.veterinaria.models.ModeloUsuario.Usuario;
import co.edu.upb.veterinaria.services.ServicioUsuario.UsuarioService;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador para crear nuevos usuarios con asignación de módulos
 */
public class UserCreate {

    // ===== RUTAS =====
    private static final String MAINMENU_FXML =
            "/co/edu/upb/veterinaria/views/mainMenu-view/mainMenu-view.fxml";
    private static final String USER_SECTION_FXML =
            "/co/edu/upb/veterinaria/views/UserSection-view/UserSection-view.fxml";

    // ===== Raíz / UI =====
    @FXML private AnchorPane root;
    @FXML private ImageView topLogo;

    // Campos de formulario
    @FXML private TextField tfCedula;
    @FXML private TextField tfNombre;
    @FXML private TextField tfApellidos;
    @FXML private TextField tfNombreUsuario;
    @FXML private TextField tfEmail;
    @FXML private TextField tfTelefono;
    @FXML private TextField tfDireccion;
    @FXML private PasswordField pfNuevaContrasena;
    @FXML private PasswordField pfConfirmarContrasena;
    @FXML private VBox vboxModulos;

    // Botones
    @FXML private Button btnCrear;
    @FXML private Button btnModificar;
    @FXML private Button btnVolver;

    // Servicio y estado
    private final UsuarioService usuarioService = new UsuarioService();
    private final Map<Integer, CheckBox> moduloCheckBoxes = new HashMap<>();

    @FXML
    private void initialize() {
        // Maximizar ventana
        Platform.runLater(() -> {
            Stage stage = (Stage) root.getScene().getWindow();
            if (stage != null) {
                stage.setResizable(true);
                stage.setMaximized(true);
            }
        });

        // Cargar módulos disponibles
        cargarModulos();
    }

    /**
     * Carga los módulos del sistema y crea checkboxes dinámicamente
     */
    private void cargarModulos() {
        try {
            List<Modulo> modulos = usuarioService.obtenerTodosLosModulos();

            for (Modulo modulo : modulos) {
                CheckBox cb = new CheckBox(modulo.getNombreModulo());
                cb.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                cb.setUserData(modulo.getIdModulo());

                moduloCheckBoxes.put(modulo.getIdModulo(), cb);
                vboxModulos.getChildren().add(cb);
            }

        } catch (Exception e) {
            mostrarError("Error al cargar módulos", "No se pudieron cargar los módulos del sistema: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handler para crear un nuevo usuario
     */
    @FXML
    private void onCrear() {
        try {
            // Validar campos
            if (!validarCampos()) {
                return;
            }

            // Verificar que las contraseñas coincidan
            String contrasena = pfNuevaContrasena.getText().trim();
            String confirmar = pfConfirmarContrasena.getText().trim();

            if (!contrasena.equals(confirmar)) {
                mostrarAdvertencia("Contraseñas no coinciden", "Las contraseñas ingresadas no coinciden. Por favor, verifíquelas.");
                return;
            }

            // Obtener módulos seleccionados
            List<Integer> modulosSeleccionados = obtenerModulosSeleccionados();
            if (modulosSeleccionados.isEmpty()) {
                mostrarAdvertencia("Sin módulos", "Debe seleccionar al menos un módulo para el usuario.");
                return;
            }

            // Crear usuario
            Usuario usuario = new Usuario();
            usuario.setCc(tfCedula.getText().trim());
            usuario.setNombre(tfNombre.getText().trim());
            usuario.setApellidos(tfApellidos.getText().trim());
            usuario.setUsuario(tfNombreUsuario.getText().trim());
            usuario.setEmail(tfEmail.getText().trim());
            usuario.setContrasena(contrasena); // Se encripta en el servicio
            usuario.setTelefono(tfTelefono.getText().trim());
            usuario.setDireccion(tfDireccion.getText().trim());

            // Guardar en la base de datos
            int usuarioId = usuarioService.crearUsuario(usuario, modulosSeleccionados);

            mostrarExito("Usuario creado", 
                "El usuario '" + usuario.getUsuario() + "' se creó exitosamente con ID: " + usuarioId);

            // Limpiar formulario
            limpiarFormulario();

        } catch (IllegalArgumentException e) {
            mostrarAdvertencia("Validación", e.getMessage());
        } catch (Exception e) {
            mostrarError("Error al crear usuario", "No se pudo crear el usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Valida que todos los campos obligatorios estén llenos
     */
    private boolean validarCampos() {
        if (tfCedula.getText().trim().isEmpty()) {
            mostrarAdvertencia("Campo vacío", "La cédula es obligatoria");
            return false;
        }
        if (tfNombre.getText().trim().isEmpty()) {
            mostrarAdvertencia("Campo vacío", "El nombre es obligatorio");
            return false;
        }
        if (tfApellidos.getText().trim().isEmpty()) {
            mostrarAdvertencia("Campo vacío", "Los apellidos son obligatorios");
            return false;
        }
        if (tfNombreUsuario.getText().trim().isEmpty()) {
            mostrarAdvertencia("Campo vacío", "El nombre de usuario es obligatorio");
            return false;
        }
        if (tfEmail.getText().trim().isEmpty()) {
            mostrarAdvertencia("Campo vacío", "El email es obligatorio");
            return false;
        }
        if (tfTelefono.getText().trim().isEmpty()) {
            mostrarAdvertencia("Campo vacío", "El teléfono es obligatorio");
            return false;
        }
        if (tfDireccion.getText().trim().isEmpty()) {
            mostrarAdvertencia("Campo vacío", "La dirección es obligatoria");
            return false;
        }
        if (pfNuevaContrasena.getText().trim().isEmpty()) {
            mostrarAdvertencia("Campo vacío", "La contraseña es obligatoria");
            return false;
        }
        if (pfConfirmarContrasena.getText().trim().isEmpty()) {
            mostrarAdvertencia("Campo vacío", "Debe confirmar la contraseña");
            return false;
        }

        return true;
    }

    /**
     * Obtiene los IDs de los módulos seleccionados
     */
    private List<Integer> obtenerModulosSeleccionados() {
        List<Integer> seleccionados = new ArrayList<>();

        for (Map.Entry<Integer, CheckBox> entry : moduloCheckBoxes.entrySet()) {
            if (entry.getValue().isSelected()) {
                seleccionados.add(entry.getKey());
            }
        }

        return seleccionados;
    }

    /**
     * Limpia todos los campos del formulario
     */
    private void limpiarFormulario() {
        tfCedula.clear();
        tfNombre.clear();
        tfApellidos.clear();
        tfNombreUsuario.clear();
        tfEmail.clear();
        tfTelefono.clear();
        tfDireccion.clear();
        pfNuevaContrasena.clear();
        pfConfirmarContrasena.clear();

        // Desmarcar todos los checkboxes
        for (CheckBox cb : moduloCheckBoxes.values()) {
            cb.setSelected(false);
        }
    }

    // ===== Navegación =====
    @FXML private void onLogoClick() { goTo(MAINMENU_FXML); }
    @FXML private void onVolver() { goTo(MAINMENU_FXML); }
    @FXML private void onModificar() { goTo(USER_SECTION_FXML); }

    private void goTo(String fxml) {
        try {
            var url = getClass().getResource(fxml);
            if (url == null) throw new IllegalStateException("NO existe el recurso: " + fxml);
            FXMLLoader loader = new FXMLLoader(url);
            Parent newRoot = loader.load();
            Scene scene = root.getScene();
            scene.setRoot(newRoot);
            ((Stage) scene.getWindow()).centerOnScreen();
        } catch (Exception e) {
            mostrarError("Error de navegación", "No se pudo abrir: " + fxml + "\n" + e.getMessage());
        }
    }

    // ===== Utilidades de UI =====
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

    private void mostrarExito(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
