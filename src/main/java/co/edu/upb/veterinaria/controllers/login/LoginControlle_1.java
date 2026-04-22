package co.edu.upb.veterinaria.controllers.login;

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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

public class LoginControlle_1 {

    private static final String MAIN_MENU_FXML =
            "/co/edu/upb/veterinaria/views/mainMenu-view/mainMenu-view.fxml";
    private static final String EMAIL_RECOVER_FXML =
            "/co/edu/upb/veterinaria/views/emailRecoverPassword-view/emailRecoverPassword-view.fxml";

    // Servicio de usuarios
    private final UsuarioService usuarioService = new UsuarioService();
    
    // Usuario autenticado (para pasar al menú principal)
    private static Usuario usuarioAutenticado;

    @FXML private AnchorPane root;
    @FXML private StackPane leftPane;
    @FXML private AnchorPane rightPane;
    @FXML private StackPane cardWrapper;
    @FXML private StackPane loginCard;
    @FXML private VBox cardContent;

    @FXML private ImageView leftLogo;

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPasswordVisible;
    @FXML private Button btnTogglePwd;
    @FXML private Button btnIngresar;

    @FXML private Button btnPower; // botón de apagado

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setResizable(true);
            stage.setMaximized(true);
        });
        leftLogo.fitWidthProperty().bind(leftPane.widthProperty().multiply(0.75));
        txtUsuario.setMaxWidth(Double.MAX_VALUE);
        txtPassword.setMaxWidth(Double.MAX_VALUE);
        txtPasswordVisible.setMaxWidth(Double.MAX_VALUE);
        btnIngresar.setMaxWidth(480);
    }

    private void swapPasswordVisibility(boolean showPlain) {
        if (showPlain) {
            txtPasswordVisible.setText(txtPassword.getText());
            txtPasswordVisible.setVisible(true);
            txtPasswordVisible.setManaged(true);
            txtPassword.setVisible(false);
            txtPassword.setManaged(false);
            txtPasswordVisible.requestFocus();
            txtPasswordVisible.positionCaret(txtPasswordVisible.getText().length());
        } else {
            txtPassword.setText(txtPasswordVisible.getText());
            txtPassword.setVisible(true);
            txtPassword.setManaged(true);
            txtPasswordVisible.setVisible(false);
            txtPasswordVisible.setManaged(false);
            txtPassword.requestFocus();
            txtPassword.positionCaret(txtPassword.getText().length());
        }
    }

    private void goTo(String fxmlPath) {
        try {
            Parent newRoot = FXMLLoader.load(getClass().getResource(fxmlPath));
            Scene scene = root.getScene();
            scene.setRoot(newRoot);
            Stage stage = (Stage) scene.getWindow();
            stage.centerOnScreen();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir la vista: " + fxmlPath + "\n" + e.getMessage()).showAndWait();
        }
    }

    // ===== Handlers =====
    @FXML private void onTogglePassword() {
        boolean showing = !txtPasswordVisible.isVisible();
        swapPasswordVisibility(showing);
    }

    @FXML private void onIngresar() {
        String user = txtUsuario.getText() == null ? "" : txtUsuario.getText().trim();
        String pass = (txtPassword.isVisible() ? txtPassword.getText() : txtPasswordVisible.getText());
        pass = pass == null ? "" : pass;

        if (user.isEmpty() || pass.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Completa todos los campos.").showAndWait();
            return;
        }

        try {
            // Validar credenciales usando el servicio
            Optional<Usuario> usuarioOpt = usuarioService.validarCredenciales(user, pass);
            
            if (usuarioOpt.isPresent()) {
                // Credenciales correctas
                usuarioAutenticado = usuarioOpt.get();
                
                // Mostrar mensaje de bienvenida
                Alert welcome = new Alert(Alert.AlertType.INFORMATION);
                welcome.setTitle("Bienvenido");
                welcome.setHeaderText("¡Autenticación exitosa!");
                welcome.setContentText("Bienvenido, " + usuarioAutenticado.getNombre() + " " + 
                                     usuarioAutenticado.getApellidos());
                welcome.showAndWait();
                
                // Ir al menú principal
                goTo(MAIN_MENU_FXML);
                
            } else {
                // Credenciales incorrectas
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error de autenticación");
                error.setHeaderText("Credenciales incorrectas");
                error.setContentText("El usuario o contraseña ingresados son incorrectos.\n" +
                                   "Por favor, verifica tus datos e intenta nuevamente.");
                error.showAndWait();
            }
            
        } catch (Exception e) {
            // Error al validar
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("Error al validar credenciales");
            error.setContentText("Ocurrió un error al intentar iniciar sesión:\n" + e.getMessage());
            error.showAndWait();
            e.printStackTrace();
        }
    }
    
    /**
     * Obtiene el usuario autenticado actualmente
     */
    public static Usuario getUsuarioAutenticado() {
        return usuarioAutenticado;
    }
    
    /**
     * Limpia el usuario autenticado (cerrar sesión)
     */
    public static void cerrarSesion() {
        usuarioAutenticado = null;
    }

    @FXML private void onRecuperar() { goTo(EMAIL_RECOVER_FXML); }

    @FXML private void onPowerOff() {
        try {
            Stage stage = (Stage) root.getScene().getWindow();
            if (stage != null) stage.close();
        } finally {
            Platform.exit();
        }
    }
}