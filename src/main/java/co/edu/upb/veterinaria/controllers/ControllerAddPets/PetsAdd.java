package co.edu.upb.veterinaria.controllers.ControllerAddPets;

import co.edu.upb.veterinaria.controllers.ClienteTemporalData;
import co.edu.upb.veterinaria.models.ModeloMascota.Mascota;
import co.edu.upb.veterinaria.services.ServicioMascota.MascotaService;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/** Agregar Mascota: controlador completo con lógica de negocio. */
public class PetsAdd {

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

    // ===== Header: campana / perfil =====
    @FXML private MenuButton mbNotificaciones;
    @FXML private MenuItem   miVerNotificaciones;
    @FXML private MenuButton mbPerfil;
    @FXML private MenuItem   miDatosPersonales, miCerrarSesion;

    // ===== Menú superior (módulos) =====
    @FXML private Button btnRegistrarProductos, btnInventario, btnGestionarUsuario,
            btnVisualizarRegistros, btnVentas, btnAgregarClientes, btnAdministrarProveedores;

    // ===== Formulario =====
    @FXML private TextField tfNombre;
    @FXML private TextField tfRaza;
    @FXML private ComboBox<String> cbEspecie;
    @FXML private ComboBox<String> cbSexo;
    @FXML private TextField tfEdad;
    @FXML private TextField tfNumeroChip;

    // ===== Botones =====
    @FXML private Button btnCancelar;
    @FXML private Button btnGuardar;

    // ===== Servicio =====
    private final MascotaService mascotaService = new MascotaService();

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            Stage stage = (Stage) root.getScene().getWindow();
            if (stage != null) {
                stage.setResizable(true);
                stage.setMaximized(true);
            }
        });

        // Configurar ComboBoxes
        configurarComboBoxes();
    }

    /**
     * Configura los valores de los ComboBox.
     */
    private void configurarComboBoxes() {
        // Especies comunes
        cbEspecie.setItems(FXCollections.observableArrayList(
            "Perro",
            "Gato",
            "Ave",
            "Conejo",
            "Hámster",
            "Reptil",
            "Pez",
            "Otro"
        ));

        // Sexo
        cbSexo.setItems(FXCollections.observableArrayList(
            "Macho",
            "Hembra",
            "No especificado"
        ));
    }

    // ===== Acciones principales del formulario =====

    /**
     * Guarda la mascota en la BASE DE DATOS (sin cliente asignado) y regresa a AddClient.
     */
    @FXML
    private void onGuardar() {
        try {
            // Validar campos
            if (!validarCampos()) {
                return;
            }

            // Crear objeto Mascota
            Mascota mascota = new Mascota();
            mascota.setNombre(tfNombre.getText().trim());
            mascota.setRaza(tfRaza.getText().trim());
            mascota.setEspecie(cbEspecie.getValue());

            // Convertir sexo de texto a char
            char sexo = convertirSexo(cbSexo.getValue());
            mascota.setSexo(sexo);

            // Convertir edad a int
            int edad = Integer.parseInt(tfEdad.getText().trim());
            mascota.setEdad(edad);

            // Número de chip (puede ser vacío)
            String numeroChip = tfNumeroChip.getText().trim();
            mascota.setNumeroChip(numeroChip.isEmpty() ? "N/A" : numeroChip);

            // Guardar la mascota en la BASE DE DATOS (sin cliente asignado = NULL)
            int idMascota = mascotaService.registrarMascota(mascota);
            System.out.println("✓ Mascota guardada en BD con ID: " + idMascota);

            // IMPORTANTE: Actualizar el objeto mascota con el ID devuelto
            mascota.setIdMascota(idMascota);
            System.out.println("✓ ID actualizado en objeto mascota: " + mascota.getIdMascota());

            // Agregar mascota a ClienteTemporalData (ahora con el ID correcto)
            ClienteTemporalData.agregarMascota(mascota);
            System.out.println("✓ Mascota agregada a ClienteTemporalData");

            // Mostrar confirmación
            mostrarToastExito("Mascota guardada en BD. ID: " + idMascota);

            // Limpiar formulario
            limpiarFormulario();

            // Pequeña pausa para que se vea el mensaje antes de cambiar de vista
            PauseTransition pause = new PauseTransition(Duration.millis(800));
            pause.setOnFinished(e -> goTo(ADD_CLIENT_FXML));
            pause.play();

        } catch (NumberFormatException e) {
            mostrarError("La edad debe ser un número válido");
        } catch (Exception e) {
            mostrarError("Error al guardar mascota: " + e.getMessage());
        }
    }

    /**
     * Cancela la operación y regresa a la vista de agregar cliente.
     */
    @FXML
    private void onCancelar() {
        goTo(ADD_CLIENT_FXML);
    }

    /**
     * Valida que todos los campos requeridos estén completos.
     */
    private boolean validarCampos() {
        List<String> errores = new ArrayList<>();

        if (tfNombre.getText() == null || tfNombre.getText().trim().isEmpty()) {
            errores.add("Nombre de la mascota");
        }
        if (tfRaza.getText() == null || tfRaza.getText().trim().isEmpty()) {
            errores.add("Raza");
        }
        if (cbEspecie.getValue() == null || cbEspecie.getValue().isEmpty()) {
            errores.add("Especie");
        }
        if (cbSexo.getValue() == null || cbSexo.getValue().isEmpty()) {
            errores.add("Sexo");
        }
        if (tfEdad.getText() == null || tfEdad.getText().trim().isEmpty()) {
            errores.add("Edad");
        } else {
            try {
                int edad = Integer.parseInt(tfEdad.getText().trim());
                if (edad < 0) {
                    errores.add("Edad (debe ser un número positivo)");
                }
            } catch (NumberFormatException e) {
                errores.add("Edad (debe ser un número válido)");
            }
        }

        if (!errores.isEmpty()) {
            StringBuilder mensaje = new StringBuilder("Los siguientes campos son obligatorios o inválidos:\n\n");
            for (String error : errores) {
                mensaje.append("• ").append(error).append("\n");
            }
            mostrarAdvertencia(mensaje.toString());
            return false;
        }

        return true;
    }

    /**
     * Convierte el valor del ComboBox de sexo a char.
     */
    private char convertirSexo(String sexoTexto) {
        if (sexoTexto == null) return 'N';

        return switch (sexoTexto) {
            case "Macho" -> 'M';
            case "Hembra" -> 'F';
            default -> 'N';
        };
    }

    /**
     * Limpia todos los campos del formulario.
     */
    private void limpiarFormulario() {
        tfNombre.clear();
        tfRaza.clear();
        cbEspecie.setValue(null);
        cbSexo.setValue(null);
        tfEdad.clear();
        tfNumeroChip.clear();
    }

    /**
     * Muestra un Toast de éxito en la esquina inferior derecha con un chulito.
     */
    private void mostrarToastExito(String mensaje) {
        // Crear el ícono de check
        Label checkIcon = new Label("✔");
        checkIcon.setTextFill(Color.WHITE);
        checkIcon.setFont(Font.font(20));
        checkIcon.setStyle("-fx-font-weight: bold;");

        // Crear el mensaje
        Label toastLabel = new Label(mensaje);
        toastLabel.setTextFill(Color.WHITE);
        toastLabel.setFont(Font.font(14));

        // Crear el contenedor con el ícono y el mensaje
        HBox toastBox = new HBox(10, checkIcon, toastLabel);
        toastBox.setStyle("-fx-background-color: #4CAF50; -fx-padding: 15px 20px; -fx-background-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);");
        toastBox.setOpacity(0);
        toastBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // Posicionar en la esquina inferior derecha
        AnchorPane.setBottomAnchor(toastBox, 20.0);
        AnchorPane.setRightAnchor(toastBox, 20.0);
        root.getChildren().add(toastBox);

        // Animación de aparición y desaparición
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.3), toastBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition pause = new PauseTransition(Duration.seconds(2));

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), toastBox);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> root.getChildren().remove(toastBox));

        SequentialTransition toastTransition = new SequentialTransition(fadeIn, pause, fadeOut);
        toastTransition.play();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Ha ocurrido un error");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAdvertencia(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText("Campos incompletos");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // ===== Utilidad de navegación segura =====
    private void goTo(String fxml) {
        try {
            var url = getClass().getResource(fxml);
            if (url == null) throw new IllegalStateException("Recurso no encontrado: " + fxml);
            Parent newRoot = FXMLLoader.load(url);
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
}
