package co.edu.upb.veterinaria.controllers.ControllerAddClient;

import co.edu.upb.veterinaria.controllers.ClienteTemporalData;
import co.edu.upb.veterinaria.models.ModeloCliente.Cliente;
import co.edu.upb.veterinaria.models.ModeloMascota.Mascota;
import co.edu.upb.veterinaria.services.ServicioCliente.ClienteService;
import co.edu.upb.veterinaria.services.ServicioMascota.MascotaService;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** Agregar Clientes: controlador completo con lógica de negocio. */
public class ClientAdd {

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
    private static final String ADD_PETS_FXML =
            "/co/edu/upb/veterinaria/views/AddPets-view/AddPets-view.fxml";

    // ===== Raíz / header =====
    @FXML private AnchorPane root;
    @FXML private ImageView topLogo;

    // ===== Header (MenuButtons) =====
    @FXML private MenuButton mbNotificaciones;
    @FXML private MenuItem   miVerNotificaciones;
    @FXML private MenuButton mbPerfil;
    @FXML private MenuItem   miDatosPersonales;
    @FXML private MenuItem   miCerrarSesion;

    // ===== Menú superior (módulos) =====
    @FXML private Button btnRegistrarProductos, btnInventario, btnGestionarUsuario,
            btnVisualizarRegistros, btnVentas, btnAgregarClientes, btnAdministrarProveedores;

    // ===== Datos del cliente =====
    @FXML private ComboBox<String> cbTipoPersona;
    @FXML private ComboBox<String> cbTipoDocumento;
    @FXML private TextField tfDocumento;
    @FXML private TextField tfNombre;
    @FXML private TextField tfApellidos;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private TextField tfCiudad;
    @FXML private TextField tfEmail;
    @FXML private TextField tfDireccion;
    @FXML private TextField tfTelefono;
    @FXML private TextField tfContactoNombre;
    @FXML private TextField tfContactoTelefono;

    // ===== Mascotas asignadas =====
    @FXML private ListView<String> lstMascotas;
    @FXML private Button btnAsignarMascota;
    @FXML private Button btnQuitarMascota;

    // ===== Acción principal =====
    @FXML private Button btnGuardar;

    // ===== Servicio y datos =====
    private final ClienteService clienteService = new ClienteService();
    private final MascotaService mascotaService = new MascotaService();
    private final ObservableList<Mascota> mascotasAsignadas = FXCollections.observableArrayList();
    private final ObservableList<String> mascotasDisplay = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            Stage stage = (Stage) root.getScene().getWindow();
            if (stage != null) {
                stage.setResizable(true);
                stage.setMaximized(true);
            }
        });

        // Configurar ComboBoxes con opciones
        configurarComboBoxes();

        // Vincular lista de mascotas (muestra solo mascotas añadidas temporalmente)
        lstMascotas.setItems(mascotasDisplay);

        // Cargar mascotas añadidas temporalmente (si el usuario vino desde AddPets)
        cargarMascotasTemporales();

        // Restaurar datos del cliente si existen
        restaurarDatosCliente();
    }

    /**
     * Restaura los datos del cliente desde el almacenamiento temporal.
     */
    private void restaurarDatosCliente() {
        if (ClienteTemporalData.tieneDatos()) {
            cbTipoPersona.setValue(ClienteTemporalData.getTipoPersona());
            cbTipoDocumento.setValue(ClienteTemporalData.getTipoDocumento());
            tfDocumento.setText(ClienteTemporalData.getDocumento());
            tfNombre.setText(ClienteTemporalData.getNombre());
            tfApellidos.setText(ClienteTemporalData.getApellidos());
            dpFechaNacimiento.setValue(ClienteTemporalData.getFechaNacimiento());
            tfCiudad.setText(ClienteTemporalData.getCiudad());
            tfEmail.setText(ClienteTemporalData.getEmail());
            tfDireccion.setText(ClienteTemporalData.getDireccion());
            tfTelefono.setText(ClienteTemporalData.getTelefono());
            tfContactoNombre.setText(ClienteTemporalData.getContactoNombre());
            tfContactoTelefono.setText(ClienteTemporalData.getContactoTelefono());
        }
    }

    /**
     * Carga las mascotas que el usuario ha añadido temporalmente
     * (solo esas aparecen en la lista de la derecha, NO todas las del sistema).
     */
    private void cargarMascotasTemporales() {
        Platform.runLater(() -> {
            try {
                List<Mascota> temp = ClienteTemporalData.getMascotasSeleccionadas();
                mascotasAsignadas.clear();
                if (temp != null && !temp.isEmpty()) {
                    mascotasAsignadas.addAll(temp);
                }
                actualizarListaMascotas();
            } catch (Exception e) {
                System.err.println("Error cargando mascotas temporales: " + e.getMessage());
            }
        });
    }

    /**
     * Configura los valores de los ComboBox (Tipo Persona y Tipo Documento).
     */
    private void configurarComboBoxes() {
        // Tipo de Persona
        cbTipoPersona.setItems(FXCollections.observableArrayList(
            "Natural",
            "Jurídica"
        ));

        // Tipo de Documento
        cbTipoDocumento.setItems(FXCollections.observableArrayList(
            "CC",          // Cédula de Ciudadanía
            "CE",          // Cédula de Extranjería
            "TI",          // Tarjeta de Identidad
            "NIT",         // Número de Identificación Tributaria
            "Pasaporte"
        ));
    }

    /**
     * Navega a la vista de agregar mascotas.
     */
    @FXML
    private void onAsignarMascota() {
        // Guardar los datos del cliente antes de navegar
        guardarDatosTemporales();
        goTo(ADD_PETS_FXML);
    }

    /**
     * Guarda los datos del cliente temporalmente antes de navegar a otra vista.
     */
    private void guardarDatosTemporales() {
        ClienteTemporalData.guardarDatos(
            cbTipoPersona.getValue(),
            cbTipoDocumento.getValue(),
            tfDocumento.getText(),
            tfNombre.getText(),
            tfApellidos.getText(),
            dpFechaNacimiento.getValue(),
            tfCiudad.getText(),
            tfEmail.getText(),
            tfDireccion.getText(),
            tfTelefono.getText(),
            tfContactoNombre.getText(),
            tfContactoTelefono.getText()
        );
    }

    /**
     * CORREGIDO: Solo quita la selección de la mascota, NO la elimina de la lista.
     */
    @FXML
    private void onQuitarMascota() {
        int selectedIndex = lstMascotas.getSelectionModel().getSelectedIndex();

        if (selectedIndex >= 0 && selectedIndex < mascotasAsignadas.size()) {
            // Obtener la mascota a eliminar
            Mascota mascotaARemover = mascotasAsignadas.get(selectedIndex);

            // Eliminar de la lista local
            mascotasAsignadas.remove(selectedIndex);

            // Actualizar ClienteTemporalData
            ClienteTemporalData.setMascotasSeleccionadas(new ArrayList<>(mascotasAsignadas));

            // Actualizar la vista
            actualizarListaMascotas();

            mostrarInfo("Mascota eliminada de la lista");
        } else {
            mostrarAdvertencia("No hay ninguna mascota seleccionada");
        }
    }

    /**
     * Actualiza la visualización de la lista de mascotas.
     */
    private void actualizarListaMascotas() {
        mascotasDisplay.clear();
        for (Mascota m : mascotasAsignadas) {
            String display = String.format("🐾 %s (%s - %s, %d años, Sexo: %c)",
                m.getNombre(), m.getEspecie(), m.getRaza(), m.getEdad(), m.getSexo());
            mascotasDisplay.add(display);
        }
    }

    /**
     * Guarda el cliente en la base de datos.
     */
    @FXML
    private void onGuardar() {
        try {
            // Validar campos antes de crear el objeto
            if (!validarCampos()) {
                return;
            }

            // Crear objeto Cliente
            Cliente cliente = new Cliente();
            cliente.setNombre(tfNombre.getText().trim());
            cliente.setApellidos(tfApellidos.getText().trim());
            cliente.setCc(tfDocumento.getText().trim());
            cliente.setTipoDocumento(cbTipoDocumento.getValue());
            cliente.setTipoPersona(cbTipoPersona.getValue());

            // Convertir LocalDate a Date
            if (dpFechaNacimiento.getValue() != null) {
                Date fechaNacimiento = Date.from(
                    dpFechaNacimiento.getValue()
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                );
                cliente.setFechaNacimiento(fechaNacimiento);
            }

            cliente.setEmail(tfEmail.getText().trim());
            cliente.setTelefono(tfTelefono.getText().trim());
            cliente.setCiudad(tfCiudad.getText().trim());
            cliente.setDireccion(tfDireccion.getText().trim());
            cliente.setNombreContacto(tfContactoNombre.getText().trim());
            cliente.setTelefonoContacto(tfContactoTelefono.getText().trim());

            // NO asignar mascotas aquí, se asignarán después
            cliente.setMascotas(new ArrayList<>());

            // Guardar cliente en BD (SIN mascotas)
            int idCliente = clienteService.registrarCliente(cliente);
            System.out.println("✓ Cliente guardado con ID: " + idCliente);

            // Asignar las mascotas seleccionadas al cliente en la BD
            int mascotasAsignadas_contador = 0;
            for (Mascota mascota : mascotasAsignadas) {
                int idMascota = mascota.getIdMascota();
                System.out.println("→ Asignando mascota ID=" + idMascota + " (" + mascota.getNombre() + ") al cliente ID=" + idCliente);

                if (idMascota <= 0) {
                    System.err.println("⚠ ERROR: La mascota '" + mascota.getNombre() + "' tiene ID inválido: " + idMascota);
                    throw new IllegalStateException("La mascota '" + mascota.getNombre() + "' no tiene un ID válido. Verifique que se haya guardado correctamente.");
                }

                boolean asignada = mascotaService.asignarMascotaACliente(idMascota, idCliente);
                if (asignada) {
                    mascotasAsignadas_contador++;
                    System.out.println("  ✓ Mascota asignada correctamente");
                } else {
                    System.err.println("  ✗ Error al asignar mascota");
                }
            }

            // Mostrar confirmación
            mostrarToastExito("Cliente registrado exitosamente. ID: " + idCliente +
                            " con " + mascotasAsignadas_contador + " mascota(s) asignada(s)");

            // Limpiar formulario
            limpiarFormulario();

            // Limpiar los datos temporales
            ClienteTemporalData.limpiar();

        } catch (IllegalArgumentException e) {
            mostrarError("Validación: " + e.getMessage());
        } catch (Exception e) {
            mostrarError("Error al guardar cliente: " + e.getMessage());
        }
    }

    /**
     * Valida que todos los campos requeridos estén completos.
     */
    private boolean validarCampos() {
        List<String> errores = new ArrayList<>();

        if (cbTipoPersona.getValue() == null || cbTipoPersona.getValue().isEmpty()) {
            errores.add("Tipo de Persona");
        }
        if (cbTipoDocumento.getValue() == null || cbTipoDocumento.getValue().isEmpty()) {
            errores.add("Tipo de Documento");
        }
        if (tfDocumento.getText() == null || tfDocumento.getText().trim().isEmpty()) {
            errores.add("Número de Documento");
        }
        if (tfNombre.getText() == null || tfNombre.getText().trim().isEmpty()) {
            errores.add("Nombre");
        }
        if (tfApellidos.getText() == null || tfApellidos.getText().trim().isEmpty()) {
            errores.add("Apellidos");
        }
        if (dpFechaNacimiento.getValue() == null) {
            errores.add("Fecha de Nacimiento");
        }
        if (tfCiudad.getText() == null || tfCiudad.getText().trim().isEmpty()) {
            errores.add("Ciudad");
        }
        if (tfEmail.getText() == null || tfEmail.getText().trim().isEmpty()) {
            errores.add("Email");
        }
        if (tfDireccion.getText() == null || tfDireccion.getText().trim().isEmpty()) {
            errores.add("Dirección");
        }
        if (tfTelefono.getText() == null || tfTelefono.getText().trim().isEmpty()) {
            errores.add("Teléfono");
        }
        if (tfContactoNombre.getText() == null || tfContactoNombre.getText().trim().isEmpty()) {
            errores.add("Nombre del Contacto de Emergencia");
        }
        if (tfContactoTelefono.getText() == null || tfContactoTelefono.getText().trim().isEmpty()) {
            errores.add("Teléfono del Contacto de Emergencia");
        }

        if (!errores.isEmpty()) {
            StringBuilder mensaje = new StringBuilder("Los siguientes campos son obligatorios:\n\n");
            for (String error : errores) {
                mensaje.append("• ").append(error).append("\n");
            }
            mostrarAdvertencia(mensaje.toString());
            return false;
        }

        return true;
    }

    /**
     * Limpia todos los campos del formulario.
     */
    private void limpiarFormulario() {
        cbTipoPersona.setValue(null);
        cbTipoDocumento.setValue(null);
        tfDocumento.clear();
        tfNombre.clear();
        tfApellidos.clear();
        dpFechaNacimiento.setValue(null);
        tfCiudad.clear();
        tfEmail.clear();
        tfDireccion.clear();
        tfTelefono.clear();
        tfContactoNombre.clear();
        tfContactoTelefono.clear();

        mascotasAsignadas.clear();
        mascotasDisplay.clear();
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

        PauseTransition pause = new PauseTransition(Duration.seconds(3));

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), toastBox);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> root.getChildren().remove(toastBox));

        // Secuencial: Mostrar -> Esperar -> Ocultar
        SequentialTransition toastTransition = new SequentialTransition(fadeIn, pause, fadeOut);
        toastTransition.play();
    }

    // ===== Métodos de utilidad para mostrar alertas =====

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

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // ===== Utilidad de navegación =====
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

    // ===== Menú superior =====
    @FXML private void onRegistrarProductos()    { goTo(REGISTER_PRODUCT_FXML); }
    @FXML private void onInventario()            { goTo(INVENTARY_FXML); }
    @FXML private void onGestionarUsuario()      { goTo(CREATE_USER_FXML); }
    @FXML private void onVisualizarRegistros()   { goTo(VISUALIZE_REGISTER_FXML); }
    @FXML private void onVentas()                { goTo(SECTION_SALES_FXML); }
    @FXML private void onAgregarClientes()       { goTo(ADD_CLIENT_FXML); }
    @FXML private void onAdministrarProveedores(){ goTo(ADD_SUPPLIERS_FXML); }
}
