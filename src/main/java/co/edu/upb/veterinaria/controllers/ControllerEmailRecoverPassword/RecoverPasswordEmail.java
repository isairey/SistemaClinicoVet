package co.edu.upb.veterinaria.controllers.ControllerEmailRecoverPassword;

import co.edu.upb.veterinaria.controllers.ControllerverifyCodeDialog.CodeDialogverify;
import co.edu.upb.veterinaria.services.ServicioUsuario.UsuarioService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.regex.Pattern;

public class RecoverPasswordEmail {

    // === Rutas de navegación ===
    private static final String RECOVER_FXML =
            "/co/edu/upb/veterinaria/views/recoverPasswordPrueba/recoverPasswordPrueba.fxml";
    private static final String LOGIN_FXML =
            "/co/edu/upb/veterinaria/views/Login/Login.fxml";

    // Regex simple para validar email
    private static final Pattern EMAIL_RE =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);

    // Servicio de usuarios
    private final UsuarioService usuarioService = new UsuarioService();
    
    // Variable para almacenar el email validado
    private static String emailValidado;

    // ===== Root y contenedores =====
    @FXML private AnchorPane root;
    @FXML private StackPane leftPane;
    @FXML private AnchorPane rightPane;
    @FXML private StackPane cardWrapper;
    @FXML private StackPane card;
    @FXML private VBox cardContent;

    // Imagen izquierda
    @FXML private ImageView leftImage;

    // Campos UI
    @FXML private TextField txtEmail;
    @FXML private Button btnEnviarCodigo;

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            Stage stage = (Stage) root.getScene().getWindow();
            if (stage != null) {
                stage.setResizable(true);
                stage.setMaximized(true);
            }
        });

        if (leftImage != null && leftPane != null) {
            leftImage.fitWidthProperty().bind(leftPane.widthProperty());
            leftImage.fitHeightProperty().bind(leftPane.heightProperty());
        }

        if (btnEnviarCodigo != null) btnEnviarCodigo.setMaxWidth(420);
    }

    // ====== HANDLERS ======
    @FXML
    private void onVolver() { goTo(LOGIN_FXML); }

    @FXML
    private void onEnviarCodigo() {
        String email = txtEmail.getText() == null ? "" : txtEmail.getText().trim();

        if (email.isEmpty()) {
            alert(Alert.AlertType.WARNING, "Escribe tu correo electrónico.");
            return;
        }
        if (!EMAIL_RE.matcher(email).matches()) {
            alert(Alert.AlertType.WARNING, "El correo no tiene un formato válido.");
            return;
        }

        // Deshabilitar botón mientras procesa
        btnEnviarCodigo.setDisable(true);
        
        // Intentar enviar código OTP
        try {
            usuarioService.iniciarRecuperacionContrasena(email);
            
            // Almacenar email validado para uso posterior
            emailValidado = email;
            
            // Mostrar mensaje de éxito
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("Código Enviado");
            infoAlert.setHeaderText("✅ Código enviado exitosamente");
            infoAlert.setContentText(
                "Se ha enviado un código de verificación a tu correo electrónico.\n\n" +
                "📧 Revisa tu bandeja de entrada: " + email + "\n\n" +
                "⏰ El código expirará en 15 minutos.\n\n" +
                "💡 Si no lo encuentras, revisa la carpeta de SPAM."
            );
            infoAlert.showAndWait();
            
            // Mostrar diálogo para ingresar código
            Stage owner = (Stage) root.getScene().getWindow();
            Optional<String> maybeCode = CodeDialogverify.show(owner, email);

            if (maybeCode.isPresent()) {
                String codigoIngresado = maybeCode.get();
                
                // Validar código OTP
                if (usuarioService.validarCodigoOTP(email, codigoIngresado)) {
                    // Código válido, ir a pantalla de cambio de contraseña
                    goTo(RECOVER_FXML);
                } else {
                    alert(Alert.AlertType.ERROR, 
                          "El código ingresado es incorrecto o ha expirado.\n" +
                          "Por favor, solicita un nuevo código.");
                }
            }
            
        } catch (IllegalArgumentException e) {
            // Usuario no encontrado
            alert(Alert.AlertType.ERROR, e.getMessage());
        } catch (Exception e) {
            // Error general
            e.printStackTrace();
            alert(Alert.AlertType.ERROR, 
                  "Error al procesar la solicitud:\n" + e.getMessage() + 
                  "\n\nVerifica tu conexión a internet y que el servidor de correo esté disponible.");
        } finally {
            // Rehabilitar botón
            btnEnviarCodigo.setDisable(false);
        }
    }
    
    /**
     * Obtiene el email que fue validado en el proceso de recuperación
     */
    public static String getEmailValidado() {
        return emailValidado;
    }
    
    /**
     * Limpia el email validado (debe llamarse después de completar el proceso)
     */
    public static void limpiarEmailValidado() {
        emailValidado = null;
    }

    // ====== Utilidades ======
    private void goTo(String fxmlPath) {
        try {
            Parent newRoot = FXMLLoader.load(getClass().getResource(fxmlPath));
            Scene scene = root.getScene();
            scene.setRoot(newRoot);
            ((Stage) scene.getWindow()).centerOnScreen();
        } catch (Exception e) {
            alert(Alert.AlertType.ERROR, "No se pudo abrir la vista: " + fxmlPath + "\n" + e.getMessage());
        }
    }

    private void alert(Alert.AlertType type, String msg) {
        new Alert(type, msg).showAndWait();
    }
}