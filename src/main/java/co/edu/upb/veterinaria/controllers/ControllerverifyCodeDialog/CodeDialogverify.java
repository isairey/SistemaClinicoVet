package co.edu.upb.veterinaria.controllers.ControllerverifyCodeDialog;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.Optional;

public class CodeDialogverify {

    @FXML private Label lbEmail;
    @FXML private Label lbError;
    @FXML private TextField tf1, tf2, tf3, tf4, tf5, tf6;
    @FXML private Button btnConfirm;

    private Stage stage;
    private String resultCode = null;

    /** Muestra el diálogo y devuelve el código si el usuario confirma. */
    public static Optional<String> show(Stage owner, String email) {
        try {
            URL url = CodeDialogverify.class.getResource(
                    "/co/edu/upb/veterinaria/views/verifyCodeDialog-view/verifyCodeDialog-view.fxml");

            if (url == null) {
                new Alert(Alert.AlertType.ERROR,
                        "No se encontró verifyCodeDialog-view.fxml en:\n" +
                                "/co/edu/upb/veterinaria/views/verifyCodeDialog-view/").showAndWait();
                return Optional.empty();
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            CodeDialogverify controller = loader.getController();
            controller.lbEmail.setText(email);

            Stage dialog = new Stage(StageStyle.UNDECORATED);
            dialog.initModality(Modality.WINDOW_MODAL);
            if (owner != null) dialog.initOwner(owner);
            dialog.setResizable(false);
            dialog.setScene(new Scene(root));
            controller.stage = dialog;

            dialog.centerOnScreen();
            dialog.showAndWait();
            return Optional.ofNullable(controller.resultCode);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir el diálogo de verificación:\n" + e.getMessage()).showAndWait();
            return Optional.empty();
        }
    }

    @FXML
    private void initialize() {
        TextField[] cells = {tf1, tf2, tf3, tf4, tf5, tf6};
        for (int i = 0; i < cells.length; i++) {
            final int idx = i;
            TextField tf = cells[i];

            // Solo 1 dígito y auto-avance
            tf.textProperty().addListener((obs, oldV, newV) -> {
                if (!newV.matches("\\d?")) {
                    tf.setText(newV.replaceAll("\\D", ""));
                }
                if (tf.getText().length() > 1) {
                    tf.setText(tf.getText().substring(0, 1));
                }
                if (tf.getText().length() == 1 && idx < cells.length - 1) {
                    cells[idx + 1].requestFocus();
                }
                setErrorVisible(false);
            });

            // Navegación con teclas
            tf.setOnKeyPressed((KeyEvent ev) -> {
                switch (ev.getCode()) {
                    case BACK_SPACE -> {
                        if (tf.getText().isEmpty() && idx > 0) {
                            cells[idx - 1].requestFocus();
                            cells[idx - 1].clear();
                        }
                    }
                    case LEFT -> { if (idx > 0) cells[idx - 1].requestFocus(); }
                    case RIGHT -> { if (idx < cells.length - 1) cells[idx + 1].requestFocus(); }
                    default -> {}
                }
            });

            // Pegar código completo (Ctrl/Cmd + V)
            tf.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
                boolean pasteCombo = (e.isControlDown() || e.isMetaDown()) && e.getCode() == KeyCode.V;
                if (!pasteCombo) return;

                String clip = Clipboard.getSystemClipboard().getString();
                if (clip != null) {
                    String digits = clip.replaceAll("\\D", "");
                    for (int k = 0; k < Math.min(6, digits.length()); k++) {
                        cells[k].setText(String.valueOf(digits.charAt(k)));
                    }
                    if (digits.length() >= 6) {
                        btnConfirm.requestFocus();
                    }
                }
                e.consume();
            });
        }

        tf1.requestFocus();
    }

    @FXML
    private void onConfirm() {
        String code = tf1.getText()+tf2.getText()+tf3.getText()+tf4.getText()+tf5.getText()+tf6.getText();
        if (code.length() != 6) {
            setErrorVisible(true);
            return;
        }
        resultCode = code;
        if (stage != null) stage.close();
    }

    private void setErrorVisible(boolean show) {
        lbError.setVisible(show);
        lbError.setManaged(show);
    }
}