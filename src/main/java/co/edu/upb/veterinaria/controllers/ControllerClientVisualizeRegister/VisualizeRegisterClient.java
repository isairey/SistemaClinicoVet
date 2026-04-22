package co.edu.upb.veterinaria.controllers.ControllerClientVisualizeRegister;

import co.edu.upb.veterinaria.controllers.ControllerSeeSurgicalProcedure.SurgicalProcedureSee;
import co.edu.upb.veterinaria.models.ModeloClienteMascota.ClienteMascotaDTO;
import co.edu.upb.veterinaria.services.ServicioClienteMascota.ClienteMascotaService;
import co.edu.upb.veterinaria.config.DatabaseConfig;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VisualizeRegisterClient {

    // ===== Clases internas para el sistema de Deshacer =====

    /**
     * Representa una acción que se puede deshacer.
     */
    private static class AccionDeshacer {
        enum TipoAccion {
            ELIMINAR_CLIENTE,
            EDITAR_CLIENTE,
            EDITAR_MASCOTA
        }

        TipoAccion tipo;
        ClienteMascotaDTO datosOriginales;
        ClienteMascotaDTO datosNuevos; // Solo para ediciones
        long timestamp;

        AccionDeshacer(TipoAccion tipo, ClienteMascotaDTO datosOriginales, ClienteMascotaDTO datosNuevos) {
            this.tipo = tipo;
            this.datosOriginales = datosOriginales;
            this.datosNuevos = datosNuevos;
            this.timestamp = System.currentTimeMillis();
        }

        String getDescripcion() {
            switch (tipo) {
                case ELIMINAR_CLIENTE:
                    return "Eliminación del cliente: " + datosOriginales.getNombre() + " " +
                           datosOriginales.getApellidos() + " (ID: " + datosOriginales.getIdCliente() + ")";
                case EDITAR_CLIENTE:
                    return "Edición del cliente: " + datosNuevos.getNombre() + " " +
                           datosNuevos.getApellidos() + " (ID: " + datosNuevos.getIdCliente() + ")";
                case EDITAR_MASCOTA:
                    return "Edición de la mascota: " + datosNuevos.getMascotaNombre() +
                           " (ID: " + datosNuevos.getIdMascota() + ")";
                default:
                    return "Acción desconocida";
            }
        }
    }

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
    private static final String SECTION_SALES_FXML =
            "/co/edu/upb/veterinaria/views/SectionSales-view/SectionSales-view.fxml";
    private static final String ADD_CLIENT_FXML =
            "/co/edu/upb/veterinaria/views/AddClient-view/AddClient-view.fxml";
    private static final String ADD_SUPPLIERS_FXML =
            "/co/edu/upb/veterinaria/views/AddSuppliers-view/AddSuppliers-view.fxml";

    private static final String VISUALIZE_REGISTER_FXML =
            "/co/edu/upb/veterinaria/views/visualizeRegister-view/visualizeRegister-view.fxml";
    private static final String SEE_SURGICAL_PROCEDURE_FXML =
            "/co/edu/upb/veterinaria/views/SeeSurgicalProcedure-view/SeeSurgicalProcedure-view.fxml";

    // ===== Root / Header =====
    @FXML private AnchorPane root;
    @FXML private ImageView topLogo;

    @FXML private MenuButton mbNotificaciones;
    @FXML private MenuItem   miVerNotificaciones;

    @FXML private MenuButton mbPerfil;
    @FXML private MenuItem   miDatosPersonales;
    @FXML private MenuItem   miCerrarSesion;

    // Menú superior
    @FXML private Button btnRegistrarProductos, btnInventario, btnGestionarUsuario,
            btnVentasTop, btnAgregarClientesTop, btnAdministrarProveedoresTop;

    // Navegación interna
    @FXML private Button btnVolver;

    // Búsqueda
    @FXML private TextField tfBuscar;
    @FXML private Button btnFiltrar, btnBuscar, btnLimpiar;

    // Tabla + contenedores
    @FXML private ScrollPane scrollTabla;
    @FXML private VBox tableWrapper;
    @FXML private TableView<ClienteMascotaDTO> tblClientes;

    // Columnas (incluye colRaza entre Mascota y Especie)
    @FXML private TableColumn<ClienteMascotaDTO, Integer> colId;
    @FXML private TableColumn<ClienteMascotaDTO, String> colTipoPersona, colTipoDocumento, colNumeroDoc,
            colNombre, colApellidos, colFechaNac, colCiudad, colEmail, colDireccion,
            colTelefono, colContactoNom, colContactoTel,
            colMascotaNombre, colRaza, colEspecie, colSexo, colEdad, colChip;

    // Botonera
    @FXML private Button btnVerProcedimiento, btnDeshacer, btnEditar, btnEliminar;

    // Servicio y datos
    private final ClienteMascotaService service = new ClienteMascotaService();
    private final ObservableList<ClienteMascotaDTO> datosOriginales = FXCollections.observableArrayList();
    private final ObservableList<ClienteMascotaDTO> datosTabla = FXCollections.observableArrayList();

    // Sistema de deshacer - Historial de acciones (máximo 10)
    private final List<AccionDeshacer> historialAcciones = new ArrayList<>();
    private static final int MAX_HISTORIAL = 10;

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            Stage st = safeStage();
            if (st != null) {
                st.setResizable(true);
                st.setMaximized(true);
            }
        });

        if (scrollTabla != null) {
            scrollTabla.setFitToHeight(true);
            scrollTabla.setFitToWidth(false);
            scrollTabla.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            scrollTabla.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        }
        if (tableWrapper != null && tableWrapper.getPrefWidth() < 3850) {
            tableWrapper.setPrefWidth(3850);
        }
        if (tblClientes != null) {
            tblClientes.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        }

        // Configurar columnas de la tabla
        configurarColumnas();

        // Vincular datos a la tabla
        tblClientes.setItems(datosTabla);

        // Cargar datos iniciales automáticamente
        Platform.runLater(this::cargarTodosLosRegistros);
    }

    /**
     * Configura las columnas de la tabla con las propiedades del DTO.
     */
    private void configurarColumnas() {
        // Configurar cada columna con un Callback para extraer el valor
        colId.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getIdCliente()).asObject());

        colTipoPersona.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTipoPersona()));

        colTipoDocumento.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTipoDocumento()));

        colNumeroDoc.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNumeroDocumento()));

        colNombre.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombre()));

        colApellidos.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getApellidos()));

        colFechaNac.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFechaNacimientoFormateada()));

        colCiudad.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCiudad()));

        colEmail.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));

        colDireccion.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDireccion()));

        colTelefono.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTelefono()));

        colContactoNom.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getContactoNombre()));

        colContactoTel.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getContactoTelefono()));

        // Datos de mascota
        colMascotaNombre.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMascotaNombre()));

        colRaza.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRaza()));

        colEspecie.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEspecie()));

        colSexo.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSexo()));

        colEdad.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEdadString()));

        colChip.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNumeroChip()));

        // Centrar algunas columnas
        colId.setStyle("-fx-alignment: CENTER;");
        colSexo.setStyle("-fx-alignment: CENTER;");
        colEdad.setStyle("-fx-alignment: CENTER;");

        System.out.println("✓ Columnas configuradas correctamente con Callbacks");
    }

    /**
     * Carga todos los registros desde la base de datos.
     */
    private void cargarTodosLosRegistros() {
        try {
            System.out.println("=== INICIANDO CARGA DE REGISTROS ===");
            List<ClienteMascotaDTO> registros = service.obtenerTodosLosRegistros();

            System.out.println("Registros obtenidos de BD: " + registros.size());

            // Mostrar los primeros 3 registros para depuración
            for (int i = 0; i < Math.min(3, registros.size()); i++) {
                ClienteMascotaDTO dto = registros.get(i);
                System.out.println("  Registro " + (i+1) + ": ID=" + dto.getIdCliente() +
                                 ", Nombre=" + dto.getNombre() + " " + dto.getApellidos() +
                                 ", Mascota=" + dto.getMascotaNombre());
            }

            // Limpiar y cargar en el hilo de JavaFX
            Platform.runLater(() -> {
                datosOriginales.clear();
                datosOriginales.addAll(registros);

                datosTabla.clear();
                datosTabla.addAll(registros);

                System.out.println("Datos añadidos a la tabla. Total en datosTabla: " + datosTabla.size());
                System.out.println("Items en tblClientes: " + (tblClientes.getItems() != null ? tblClientes.getItems().size() : "null"));

                // Forzar actualización de la tabla
                tblClientes.refresh();

                System.out.println("=== CARGA COMPLETADA ===");
            });

        } catch (SQLException e) {
            System.err.println("ERROR SQL: " + e.getMessage());
            e.printStackTrace();
            Platform.runLater(() -> {
                mostrarError("Error al cargar registros: " + e.getMessage());
            });
        } catch (Exception e) {
            System.err.println("ERROR GENERAL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Acción del botón Buscar: busca por texto en múltiples campos.
     */
    @FXML
    private void onBuscar() {
        String textoBusqueda = tfBuscar.getText();

        if (textoBusqueda == null || textoBusqueda.trim().isEmpty()) {
            mostrarAdvertencia("Por favor ingresa un texto para buscar");
            return;
        }

        try {
            List<ClienteMascotaDTO> resultados = service.buscarRegistros(textoBusqueda.trim());
            datosTabla.clear();
            datosTabla.addAll(resultados);

            mostrarInfo("Búsqueda completada: " + resultados.size() + " resultados encontrados");
        } catch (SQLException e) {
            mostrarError("Error al buscar: " + e.getMessage());
        }
    }

    /**
     * Acción del botón Filtrar: muestra un diálogo para filtrar por múltiples campos.
     */
    @FXML
    private void onFiltrar() {
        // Crear diálogo de filtros
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Filtrar Registros");
        dialog.setHeaderText("Selecciona el campo y el valor por el cual filtrar");

        // Contenido del diálogo
        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setStyle("-fx-padding: 20;");

        // ComboBox para seleccionar el campo
        Label lblCampo = new Label("Filtrar por:");
        ComboBox<String> cbCampo = new ComboBox<>();
        cbCampo.getItems().addAll(
            "Nombre",
            "Apellidos",
            "Tipo Persona",
            "Tipo Documento",
            "Número Documento",
            "Ciudad",
            "Email",
            "Dirección",
            "Teléfono",
            "Contacto Emergencia",
            "Mascota",
            "Raza",
            "Especie"
        );
        cbCampo.setValue("Nombre");
        cbCampo.setPrefWidth(300);

        // TextField para el valor a buscar
        Label lblValor = new Label("Valor a buscar:");
        TextField tfValor = new TextField();
        tfValor.setPromptText("Escribe el valor...");
        tfValor.setPrefWidth(300);

        content.getChildren().addAll(lblCampo, cbCampo, lblValor, tfValor);
        dialog.getDialogPane().setContent(content);

        // Botones
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Procesar resultado
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String campo = cbCampo.getValue();
            String valor = tfValor.getText();

            if (valor == null || valor.trim().isEmpty()) {
                mostrarAdvertencia("Por favor ingresa un valor para filtrar");
                return;
            }

            try {
                List<ClienteMascotaDTO> resultados = service.filtrarPorCampo(campo, valor.trim());
                datosTabla.clear();
                datosTabla.addAll(resultados);

                mostrarInfo("Filtrado completado: " + resultados.size() + " resultados");
            } catch (SQLException e) {
                mostrarError("Error al filtrar: " + e.getMessage());
            }
        }
    }

    /**
     * Acción del botón Limpiar: restaura todos los datos originales y limpia el campo de búsqueda.
     */
    @FXML
    private void onLimpiar() {
        tfBuscar.clear();
        datosTabla.clear();
        datosTabla.addAll(datosOriginales);
        tblClientes.getSelectionModel().clearSelection();

        System.out.println("Se han restaurado todos los registros (" + datosOriginales.size() + ")");
    }

    /**
     * Acción del botón Editar: abre un diálogo emergente para editar el cliente y mascota seleccionados.
     */
    @FXML
    private void onEditar() {
        ClienteMascotaDTO seleccionado = tblClientes.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAdvertencia("Por favor selecciona un registro para editar");
            return;
        }

        // Abrir el diálogo de edición
        showEditDialog(seleccionado);
    }

    /**
     * Verifica si una cadena es null o está vacía.
     */
    private boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    /**
     * Muestra el diálogo de edición con todos los campos del cliente y mascota (si existe).
     */
    private void showEditDialog(ClienteMascotaDTO dto) {
        // Crear una copia del DTO original antes de editar
        ClienteMascotaDTO dtoOriginal = clonarDTO(dto);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editar Cliente / Mascota");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setPrefSize(900, 650);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        grid.setPadding(new Insets(15));

        // ========== SECCIÓN CLIENTE ==========
        Label lblSeccionCliente = new Label("DATOS DEL CLIENTE");
        lblSeccionCliente.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");
        grid.add(lblSeccionCliente, 0, 0, 2, 1);

        int row = 1;

        // ID Cliente (solo lectura)
        TextField tfIdCliente = new TextField(String.valueOf(dto.getIdCliente()));
        tfIdCliente.setDisable(true);
        tfIdCliente.setStyle("-fx-opacity: 1.0; -fx-background-color: #ecf0f1;");
        grid.add(new Label("ID Cliente:"), 0, row);
        grid.add(tfIdCliente, 1, row); row++;

        // Tipo Persona
        ComboBox<String> cbTipoPersona = new ComboBox<>();
        cbTipoPersona.getItems().addAll("Natural", "Jurídica");
        cbTipoPersona.setEditable(true);
        cbTipoPersona.setValue(dto.getTipoPersona() != null ? dto.getTipoPersona() : "");
        cbTipoPersona.setPrefWidth(250);
        grid.add(new Label("Tipo Persona:"), 0, row);
        grid.add(cbTipoPersona, 1, row); row++;

        // Tipo Documento
        ComboBox<String> cbTipoDocumento = new ComboBox<>();
        cbTipoDocumento.getItems().addAll("CC", "CE", "NIT", "Pasaporte");
        cbTipoDocumento.setEditable(true);
        cbTipoDocumento.setValue(dto.getTipoDocumento() != null ? dto.getTipoDocumento() : "");
        cbTipoDocumento.setPrefWidth(250);
        grid.add(new Label("Tipo Documento:"), 0, row);
        grid.add(cbTipoDocumento, 1, row); row++;

        // Número Documento
        TextField tfNumeroDoc = new TextField(dto.getNumeroDocumento());
        tfNumeroDoc.setPrefWidth(250);
        grid.add(new Label("Número Documento: *"), 0, row);
        grid.add(tfNumeroDoc, 1, row); row++;

        // Nombre
        TextField tfNombre = new TextField(dto.getNombre());
        tfNombre.setPrefWidth(250);
        grid.add(new Label("Nombre: *"), 0, row);
        grid.add(tfNombre, 1, row); row++;

        // Apellidos
        TextField tfApellidos = new TextField(dto.getApellidos());
        tfApellidos.setPrefWidth(250);
        grid.add(new Label("Apellidos: *"), 0, row);
        grid.add(tfApellidos, 1, row); row++;

        // Fecha Nacimiento
        DatePicker dpFecha = new DatePicker();
        if (dto.getFechaNacimiento() != null) {
            dpFecha.setValue(new java.sql.Date(dto.getFechaNacimiento().getTime()).toLocalDate());
        }
        dpFecha.setPrefWidth(250);
        grid.add(new Label("Fecha Nacimiento:"), 0, row);
        grid.add(dpFecha, 1, row); row++;

        // Ciudad
        TextField tfCiudad = new TextField(dto.getCiudad());
        tfCiudad.setPrefWidth(250);
        grid.add(new Label("Ciudad:"), 0, row);
        grid.add(tfCiudad, 1, row); row++;

        // Email
        TextField tfEmail = new TextField(dto.getEmail());
        tfEmail.setPrefWidth(250);
        grid.add(new Label("Email:"), 0, row);
        grid.add(tfEmail, 1, row); row++;

        // Dirección
        TextField tfDireccion = new TextField(dto.getDireccion());
        tfDireccion.setPrefWidth(250);
        grid.add(new Label("Dirección:"), 0, row);
        grid.add(tfDireccion, 1, row); row++;

        // Teléfono
        TextField tfTelefono = new TextField(dto.getTelefono());
        tfTelefono.setPrefWidth(250);
        grid.add(new Label("Teléfono:"), 0, row);
        grid.add(tfTelefono, 1, row); row++;

        // Contacto Emergencia (Nombre)
        TextField tfContactoNom = new TextField(dto.getContactoNombre());
        tfContactoNom.setPrefWidth(250);
        grid.add(new Label("Contacto Emergencia (Nombre):"), 0, row);
        grid.add(tfContactoNom, 1, row); row++;

        // Contacto Emergencia (Teléfono)
        TextField tfContactoTel = new TextField(dto.getContactoTelefono());
        tfContactoTel.setPrefWidth(250);
        grid.add(new Label("Contacto Emergencia (Teléfono):"), 0, row);
        grid.add(tfContactoTel, 1, row); row++;

        // ========== SECCIÓN MASCOTA (solo si existe) ==========
        TextField tfIdMascota = null;
        TextField tfMascotaNombre = null;
        TextField tfRaza = null;
        TextField tfEspecie = null;
        ComboBox<String> cbSexo = null;
        TextField tfEdad = null;
        TextField tfNumeroChip = null;

        if (dto.getIdMascota() != null) {
            // Separador
            Separator sep = new Separator();
            grid.add(sep, 0, row, 2, 1); row++;

            Label lblSeccionMascota = new Label(" DATOS DE LA MASCOTA");
            lblSeccionMascota.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");
            grid.add(lblSeccionMascota, 0, row, 2, 1); row++;

            // ID Mascota (solo lectura)
            tfIdMascota = new TextField(String.valueOf(dto.getIdMascota()));
            tfIdMascota.setDisable(true);
            tfIdMascota.setStyle("-fx-opacity: 1.0; -fx-background-color: #ecf0f1;");
            grid.add(new Label("ID Mascota:"), 0, row);
            grid.add(tfIdMascota, 1, row); row++;

            // Nombre Mascota
            tfMascotaNombre = new TextField(dto.getMascotaNombre() != null ? dto.getMascotaNombre() : "");
            tfMascotaNombre.setPrefWidth(250);
            grid.add(new Label("Nombre Mascota:"), 0, row);
            grid.add(tfMascotaNombre, 1, row); row++;

            // Raza
            tfRaza = new TextField(dto.getRaza() != null ? dto.getRaza() : "");
            tfRaza.setPrefWidth(250);
            grid.add(new Label("Raza:"), 0, row);
            grid.add(tfRaza, 1, row); row++;

            // Especie
            tfEspecie = new TextField(dto.getEspecie() != null ? dto.getEspecie() : "");
            tfEspecie.setPrefWidth(250);
            grid.add(new Label("Especie:"), 0, row);
            grid.add(tfEspecie, 1, row); row++;

            // Sexo
            cbSexo = new ComboBox<>();
            cbSexo.getItems().addAll("Macho", "Hembra");
            cbSexo.setValue(dto.getSexo() != null ? dto.getSexo() : null);
            cbSexo.setPrefWidth(250);
            grid.add(new Label("Sexo:"), 0, row);
            grid.add(cbSexo, 1, row); row++;

            // Edad
            tfEdad = new TextField(dto.getEdad() != null ? dto.getEdad().toString() : "");
            tfEdad.setPrefWidth(250);
            grid.add(new Label("Edad:"), 0, row);
            grid.add(tfEdad, 1, row); row++;

            // Número Chip
            tfNumeroChip = new TextField(dto.getNumeroChip() != null ? dto.getNumeroChip() : "");
            tfNumeroChip.setPrefWidth(250);
            grid.add(new Label("N° Chip:"), 0, row);
            grid.add(tfNumeroChip, 1, row); row++;
        }

        // Agregar ScrollPane para permitir desplazamiento
        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setPrefViewportHeight(550);
        dialog.getDialogPane().setContent(scroll);

        // Referencias finales para usar en lambda
        final TextField finalTfIdMascota = tfIdMascota;
        final TextField finalTfMascotaNombre = tfMascotaNombre;
        final TextField finalTfRaza = tfRaza;
        final TextField finalTfEspecie = tfEspecie;
        final ComboBox<String> finalCbSexo = cbSexo;
        final TextField finalTfEdad = tfEdad;
        final TextField finalTfNumeroChip = tfNumeroChip;

        // Mostrar diálogo y procesar resultado
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Validaciones mínimas
            if (isNullOrEmpty(tfNumeroDoc.getText()) || isNullOrEmpty(tfNombre.getText()) ||
                isNullOrEmpty(tfApellidos.getText())) {
                mostrarError("Los campos Número Documento, Nombre y Apellidos son obligatorios.");
                return;
            }

            // Ejecutar actualizaciones en BD
            try (Connection conn = DatabaseConfig.getDataSource().getConnection()) {
                conn.setAutoCommit(false);

                // Actualizar Cliente
                try (PreparedStatement psCliente = conn.prepareStatement(
                        "UPDATE veterinaria.cliente SET tipopersona=?, tipodocumento=?, cc=?, nombre=?, " +
                        "apellidos=?, fechanacimiento=?, ciudad=?, email=?, direccion=?, telefono=?, " +
                        "nombrecontactoemergencia=?, telefonocontactoemergencia=? WHERE idcliente=?"
                )) {
                    psCliente.setString(1, cbTipoPersona.getValue());
                    psCliente.setString(2, cbTipoDocumento.getValue());
                    psCliente.setString(3, tfNumeroDoc.getText().trim());
                    psCliente.setString(4, tfNombre.getText().trim());
                    psCliente.setString(5, tfApellidos.getText().trim());

                    LocalDate fecha = dpFecha.getValue();
                    if (fecha != null) {
                        psCliente.setDate(6, Date.valueOf(fecha));
                    } else {
                        psCliente.setNull(6, java.sql.Types.DATE);
                    }

                    psCliente.setString(7, tfCiudad.getText());
                    psCliente.setString(8, tfEmail.getText());
                    psCliente.setString(9, tfDireccion.getText());
                    psCliente.setString(10, tfTelefono.getText());
                    psCliente.setString(11, tfContactoNom.getText());
                    psCliente.setString(12, tfContactoTel.getText());
                    psCliente.setInt(13, dto.getIdCliente());

                    int affectedCliente = psCliente.executeUpdate();
                    if (affectedCliente == 0) {
                        conn.rollback();
                        mostrarError("No se pudo actualizar el cliente (ID no encontrado).");
                        return;
                    }
                }

                // Actualizar Mascota (solo si existe)
                if (dto.getIdMascota() != null && finalTfMascotaNombre != null) {
                    try (PreparedStatement psMascota = conn.prepareStatement(
                            "UPDATE veterinaria.mascota SET nombre=?, raza=?, especie=?, sexo=?, " +
                            "edad=?, numerochip=? WHERE idmascota=?"
                    )) {
                        psMascota.setString(1, finalTfMascotaNombre.getText());
                        psMascota.setString(2, finalTfRaza.getText());
                        psMascota.setString(3, finalTfEspecie.getText());
                        psMascota.setString(4, finalCbSexo.getValue());

                        if (!isNullOrEmpty(finalTfEdad.getText())) {
                            try {
                                psMascota.setInt(5, Integer.parseInt(finalTfEdad.getText().trim()));
                            } catch (NumberFormatException ex) {
                                psMascota.setNull(5, java.sql.Types.INTEGER);
                            }
                        } else {
                            psMascota.setNull(5, java.sql.Types.INTEGER);
                        }

                        psMascota.setString(6, finalTfNumeroChip.getText());
                        psMascota.setInt(7, dto.getIdMascota());

                        int affectedMascota = psMascota.executeUpdate();
                        if (affectedMascota == 0) {
                            conn.rollback();
                            mostrarError("No se pudo actualizar la mascota (ID no encontrado).");
                            return;
                        }
                    }
                }

                conn.commit();

                // Guardar en el historial para deshacer
                ClienteMascotaDTO dtoNuevo = crearDTODesdeFormulario(
                    dto.getIdCliente(), cbTipoPersona.getValue(), cbTipoDocumento.getValue(),
                    tfNumeroDoc.getText(), tfNombre.getText(), tfApellidos.getText(),
                    dpFecha.getValue(), tfCiudad.getText(), tfEmail.getText(),
                    tfDireccion.getText(), tfTelefono.getText(), tfContactoNom.getText(),
                    tfContactoTel.getText(), dto.getIdMascota(),
                    finalTfMascotaNombre != null ? finalTfMascotaNombre.getText() : null,
                    finalTfRaza != null ? finalTfRaza.getText() : null,
                    finalTfEspecie != null ? finalTfEspecie.getText() : null,
                    finalCbSexo != null ? finalCbSexo.getValue() : null,
                    finalTfEdad != null && !isNullOrEmpty(finalTfEdad.getText()) ?
                        Integer.parseInt(finalTfEdad.getText().trim()) : null,
                    finalTfNumeroChip != null ? finalTfNumeroChip.getText() : null
                );

                historialAcciones.add(new AccionDeshacer(
                    AccionDeshacer.TipoAccion.EDITAR_CLIENTE, dtoOriginal, dtoNuevo));
                if (historialAcciones.size() > MAX_HISTORIAL) {
                    historialAcciones.remove(0);
                }

                mostrarInfo("✓ Datos actualizados correctamente");

                // Recargar tabla automáticamente
                cargarTodosLosRegistros();

            } catch (SQLException e) {
                mostrarError("Error al actualizar en la base de datos:\n" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Crea una copia profunda del DTO.
     */
    private ClienteMascotaDTO clonarDTO(ClienteMascotaDTO original) {
        ClienteMascotaDTO copia = new ClienteMascotaDTO();
        copia.setIdCliente(original.getIdCliente());
        copia.setNombre(original.getNombre());
        copia.setApellidos(original.getApellidos());
        copia.setTipoPersona(original.getTipoPersona());
        copia.setTipoDocumento(original.getTipoDocumento());
        copia.setNumeroDocumento(original.getNumeroDocumento());
        copia.setFechaNacimiento(original.getFechaNacimiento());
        copia.setCiudad(original.getCiudad());
        copia.setEmail(original.getEmail());
        copia.setDireccion(original.getDireccion());
        copia.setTelefono(original.getTelefono());
        copia.setContactoNombre(original.getContactoNombre());
        copia.setContactoTelefono(original.getContactoTelefono());
        copia.setIdMascota(original.getIdMascota());
        copia.setMascotaNombre(original.getMascotaNombre());
        copia.setRaza(original.getRaza());
        copia.setEspecie(original.getEspecie());
        copia.setSexo(original.getSexo());
        copia.setEdad(original.getEdad());
        copia.setNumeroChip(original.getNumeroChip());
        return copia;
    }

    /**
     * Crea un DTO desde los datos del formulario.
     */
    private ClienteMascotaDTO crearDTODesdeFormulario(
            int idCliente, String tipoPersona, String tipoDocumento, String numeroDoc,
            String nombre, String apellidos, LocalDate fechaNac, String ciudad, String email,
            String direccion, String telefono, String contactoNom, String contactoTel,
            Integer idMascota, String mascotaNombre, String raza, String especie,
            String sexo, Integer edad, String numeroChip) {

        ClienteMascotaDTO dto = new ClienteMascotaDTO();
        dto.setIdCliente(idCliente);
        dto.setTipoPersona(tipoPersona);
        dto.setTipoDocumento(tipoDocumento);
        dto.setNumeroDocumento(numeroDoc);
        dto.setNombre(nombre);
        dto.setApellidos(apellidos);
        dto.setFechaNacimiento(fechaNac != null ? Date.valueOf(fechaNac) : null);
        dto.setCiudad(ciudad);
        dto.setEmail(email);
        dto.setDireccion(direccion);
        dto.setTelefono(telefono);
        dto.setContactoNombre(contactoNom);
        dto.setContactoTelefono(contactoTel);
        dto.setIdMascota(idMascota);
        dto.setMascotaNombre(mascotaNombre);
        dto.setRaza(raza);
        dto.setEspecie(especie);
        dto.setSexo(sexo);
        dto.setEdad(edad);
        dto.setNumeroChip(numeroChip);
        return dto;
    }

    /**
     * Acción del botón Eliminar: Marca el cliente como eliminado (SOFT DELETE).
     * NO elimina físicamente de la BD, solo lo oculta de las vistas.
     * Mantiene toda la trazabilidad: ventas, mascotas, historiales.
     */
    @FXML
    private void onEliminar() {
        ClienteMascotaDTO seleccionado = tblClientes.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAdvertencia("Por favor selecciona un registro para eliminar");
            return;
        }

        try {
            // 1. OBTENER INFORMACIÓN DE DEPENDENCIAS
            String infoDependencias = service.obtenerInfoDependencias(seleccionado.getIdCliente());
            boolean tieneVentas = service.clienteTieneVentas(seleccionado.getIdCliente());

            // 2. MOSTRAR CONFIRMACIÓN CON INFORMACIÓN COMPLETA
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación (Soft Delete)");
            confirmacion.setHeaderText("¿Marcar este cliente como eliminado?");

            String mensajeContenido =
                "Cliente seleccionado:\n" +
                "• ID: " + seleccionado.getIdCliente() + "\n" +
                "• Nombre: " + seleccionado.getNombre() + " " + seleccionado.getApellidos() + "\n" +
                "• Documento: " + seleccionado.getNumeroDocumento() + "\n\n" +
                "Dependencias (se mantienen en la BD):\n" + infoDependencias + "\n" +
                "ℹ IMPORTANTE:\n" +
                "• El cliente NO se eliminará físicamente de la base de datos\n" +
                "• Se marcará como 'eliminado' y dejará de aparecer en las vistas\n" +
                "• Se mantiene toda la trazabilidad (ventas, mascotas, historiales)\n" +
                "• Puedes revertir esta acción con el botón 'Deshacer'\n";

            if (tieneVentas) {
                mensajeContenido += "\n✓ Este cliente tiene ventas registradas que se preservarán";
            }

            confirmacion.setContentText(mensajeContenido);

            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {

                // Guardar para deshacer (clonar el DTO completo)
                ClienteMascotaDTO dtoParaDeshacer = clonarDTO(seleccionado);
                historialAcciones.add(new AccionDeshacer(
                    AccionDeshacer.TipoAccion.ELIMINAR_CLIENTE,
                    dtoParaDeshacer,
                    null
                ));
                if (historialAcciones.size() > MAX_HISTORIAL) {
                    historialAcciones.remove(0);
                }

                // 3. MARCAR COMO ELIMINADO (SOFT DELETE)
                service.eliminarCliente(seleccionado.getIdCliente());

                // 4. ELIMINAR DE LA VISTA (no de la BD)
                datosTabla.removeIf(dto -> dto.getIdCliente() == seleccionado.getIdCliente());
                datosOriginales.removeIf(dto -> dto.getIdCliente() == seleccionado.getIdCliente());

                mostrarInfo(
                    "✓ Cliente marcado como eliminado correctamente\n\n" +
                    "• Los datos permanecen en la base de datos para auditoría\n" +
                    "• No se afectaron ventas, mascotas ni historiales\n" +
                    "• El cliente dejará de aparecer en las consultas\n" +
                    "• Puedes usar el botón 'Deshacer' para revertir esta acción"
                );
            }

        } catch (SQLException e) {
            String mensajeError = e.getMessage();

            if (mensajeError.contains("ya está marcado como eliminado")) {
                mostrarError("El cliente seleccionado ya está marcado como eliminado.\n\n" +
                           "Actualiza la vista o usa el botón 'Limpiar' para ver los datos actuales.");
            } else {
                mostrarError("Error al marcar cliente como eliminado:\n\n" + mensajeError);
            }

            e.printStackTrace();
        } catch (Exception e) {
            mostrarError("Error inesperado:\n\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Acción del botón Deshacer: revierte la última acción realizada (eliminar o editar).
     */
    @FXML
    private void onDeshacer() {
        // Verificar si hay acciones para deshacer
        if (historialAcciones.isEmpty()) {
            mostrarAdvertencia("No hay acciones para deshacer");
            return;
        }

        // Obtener la última acción
        AccionDeshacer ultimaAccion = historialAcciones.get(historialAcciones.size() - 1);

        // Crear diálogo de confirmación con detalles de la acción
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Deshacer");
        confirmacion.setHeaderText("¿Deseas revertir la última acción?");
        confirmacion.setContentText("Acción a revertir:\n" + ultimaAccion.getDescripcion() +
                                   "\n\nEsta operación restaurará los datos originales en la base de datos.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                switch (ultimaAccion.tipo) {
                    case ELIMINAR_CLIENTE:
                        revertirEliminacionCliente(ultimaAccion);
                        break;
                    case EDITAR_CLIENTE:
                    case EDITAR_MASCOTA:
                        revertirEdicion(ultimaAccion);
                        break;
                }

                // Eliminar la acción del historial
                historialAcciones.remove(historialAcciones.size() - 1);

                // Recargar datos
                cargarTodosLosRegistros();
                mostrarInfo("✓ Acción revertida exitosamente");

            } catch (SQLException e) {
                mostrarError("Error al revertir la acción:\n" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Revierte la eliminación de un cliente (restaura el soft delete).
     * En este caso, simplemente quita la marca de "eliminado" del manager.
     */
    private void revertirEliminacionCliente(AccionDeshacer accion) throws SQLException {
        ClienteMascotaDTO cliente = accion.datosOriginales;

        // Restaurar usando el servicio (quita la marca de eliminado)
        service.restaurarCliente(cliente.getIdCliente());

        System.out.println("✓ Cliente restaurado (soft delete revertido): ID=" + cliente.getIdCliente());
    }

    /**
     * Revierte una edición restaurando los valores originales.
     */
    private void revertirEdicion(AccionDeshacer accion) throws SQLException {
        ClienteMascotaDTO datosOriginales = accion.datosOriginales;

        try (Connection conn = DatabaseConfig.getDataSource().getConnection()) {
            conn.setAutoCommit(false);

            // Restaurar datos del cliente
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE veterinaria.cliente SET tipopersona=?, tipodocumento=?, cc=?, nombre=?, " +
                    "apellidos=?, fechanacimiento=?, ciudad=?, email=?, direccion=?, telefono=?, " +
                    "nombrecontactoemergencia=?, telefonocontactoemergencia=? WHERE idcliente=?"
            )) {
                ps.setString(1, datosOriginales.getTipoPersona());
                ps.setString(2, datosOriginales.getTipoDocumento());
                ps.setString(3, datosOriginales.getNumeroDocumento());
                ps.setString(4, datosOriginales.getNombre());
                ps.setString(5, datosOriginales.getApellidos());

                if (datosOriginales.getFechaNacimiento() != null) {
                    ps.setDate(6, new Date(datosOriginales.getFechaNacimiento().getTime()));
                } else {
                    ps.setNull(6, java.sql.Types.DATE);
                }

                ps.setString(7, datosOriginales.getCiudad());
                ps.setString(8, datosOriginales.getEmail());
                ps.setString(9, datosOriginales.getDireccion());
                ps.setString(10, datosOriginales.getTelefono());
                ps.setString(11, datosOriginales.getContactoNombre());
                ps.setString(12, datosOriginales.getContactoTelefono());
                ps.setInt(13, datosOriginales.getIdCliente());

                ps.executeUpdate();
            }

            // Restaurar datos de la mascota si existe
            if (datosOriginales.getIdMascota() != null) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE veterinaria.mascota SET nombre=?, raza=?, especie=?, sexo=?, " +
                        "edad=?, numerochip=? WHERE idmascota=?"
                )) {
                    ps.setString(1, datosOriginales.getMascotaNombre());
                    ps.setString(2, datosOriginales.getRaza());
                    ps.setString(3, datosOriginales.getEspecie());
                    ps.setString(4, datosOriginales.getSexo());

                    if (datosOriginales.getEdad() != null) {
                        ps.setInt(5, datosOriginales.getEdad());
                    } else {
                        ps.setNull(5, java.sql.Types.INTEGER);
                    }

                    ps.setString(6, datosOriginales.getNumeroChip());
                    ps.setInt(7, datosOriginales.getIdMascota());

                    ps.executeUpdate();
                }
            }

            conn.commit();
            System.out.println("✓ Edición revertida para cliente ID=" + datosOriginales.getIdCliente());
        }
    }

    // ===== Utilidades de navegación =====
    private Stage safeStage() {
        if (root != null && root.getScene() != null) return (Stage) root.getScene().getWindow();
        Window w = Window.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null);
        return (w instanceof Stage) ? (Stage) w : null;
    }

    private void goTo(String fxml) {
        try {
            var url = getClass().getResource(fxml);
            if (url == null) throw new IllegalStateException("NO existe el recurso (classpath): " + fxml);
            Parent newRoot = FXMLLoader.load(url);

            Stage stage = safeStage();
            if (stage == null) throw new IllegalStateException("No hay Stage activo para navegar.");

            Scene scene = stage.getScene();
            if (scene == null) {
                stage.setScene(new Scene(newRoot));
            } else {
                scene.setRoot(newRoot);
            }
            stage.centerOnScreen();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir: " + fxml + "\n" + e.getMessage()).showAndWait();
        }
    }

    // ===== Utilidades para mostrar mensajes =====
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
        alert.setHeaderText(null);
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

    // ===== Header (logo + menús) =====
    @FXML private void onLogoClick()         { goTo(MAINMENU_FXML); }
    @FXML private void onVerNotificaciones() { goTo(SEENOTIFICATIONS_FXML); }
    @FXML private void onDatosPersonales()   { goTo(PERSONALDATA_FXML); }
    @FXML private void onCerrarSesion()      { goTo(LOGIN_FXML); }

    // ===== Menú superior =====
    @FXML private void onRegistrarProductos()        { goTo(REGISTER_PRODUCT_FXML); }
    @FXML private void onInventario()                { goTo(INVENTARY_FXML); }
    @FXML private void onGestionarUsuario()          { goTo(CREATE_USER_FXML); }
    @FXML private void onVentasTop()                 { goTo(SECTION_SALES_FXML); }
    @FXML private void onAgregarClientesTop()        { goTo(ADD_CLIENT_FXML); }
    @FXML private void onAdministrarProveedoresTop() { goTo(ADD_SUPPLIERS_FXML); }

    // ===== Navegación interna =====
    @FXML private void onVolver()            { goTo(VISUALIZE_REGISTER_FXML); }

    @FXML private void onVerProcedimiento() {
        ClienteMascotaDTO seleccionado = tblClientes.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAdvertencia("Por favor selecciona un registro de la tabla");
            return;
        }

        // Verificar que el registro tenga una mascota
        if (seleccionado.getIdMascota() == null) {
            mostrarAdvertencia("El cliente seleccionado no tiene mascotas registradas.\n" +
                             "Por favor selecciona un registro que incluya una mascota para ver su historial clínico.");
            return;
        }

        try {
            // Obtener todas las mascotas del cliente
            co.edu.upb.veterinaria.services.ServicioMascota.MascotaService mascotaService =
                new co.edu.upb.veterinaria.services.ServicioMascota.MascotaService();

            List<co.edu.upb.veterinaria.models.ModeloMascota.Mascota> mascotas =
                mascotaService.buscarPorCliente(seleccionado.getIdCliente());

            if (mascotas == null || mascotas.isEmpty()) {
                mostrarAdvertencia("El cliente seleccionado no tiene mascotas registradas.");
                return;
            }

            // Si el cliente tiene múltiples mascotas, mostrar diálogo de selección
            co.edu.upb.veterinaria.models.ModeloMascota.Mascota mascotaSeleccionada;

            if (mascotas.size() > 1) {
                mascotaSeleccionada = mostrarDialogoSeleccionMascota(mascotas, seleccionado);
                if (mascotaSeleccionada == null) {
                    return; // Usuario canceló la selección
                }
            } else {
                mascotaSeleccionada = mascotas.get(0);
            }

            // Navegar a la vista de historial clínico pasando los datos de la mascota
            FXMLLoader loader = new FXMLLoader(getClass().getResource(SEE_SURGICAL_PROCEDURE_FXML));
            Parent newRoot = loader.load();

            // Obtener el controlador y pasarle los datos
            SurgicalProcedureSee controller = loader.getController();
            controller.cargarDatosMascota(
                mascotaSeleccionada.getIdMascota(),
                seleccionado.getNombre() + " " + seleccionado.getApellidos(),
                mascotaSeleccionada.getNombre() + " (" + mascotaSeleccionada.getEspecie() + ")"
            );

            // Cambiar la escena
            Stage stage = safeStage();
            if (stage == null) {
                mostrarError("No se pudo obtener el Stage actual");
                return;
            }

            Scene scene = stage.getScene();
            if (scene == null) {
                stage.setScene(new Scene(newRoot));
            } else {
                scene.setRoot(newRoot);
            }
            stage.centerOnScreen();

            System.out.println("✓ Navegando a historial clínico de mascota: " + mascotaSeleccionada.getNombre());

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al abrir el historial clínico:\n" + e.getMessage());
        }
    }

    /**
     * Muestra un diálogo para seleccionar una mascota cuando el cliente tiene múltiples mascotas.
     */
    private co.edu.upb.veterinaria.models.ModeloMascota.Mascota mostrarDialogoSeleccionMascota(
            List<co.edu.upb.veterinaria.models.ModeloMascota.Mascota> mascotas,
            ClienteMascotaDTO cliente) {

        Stage dialog = new Stage();
        dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialog.setTitle("Seleccionar Mascota");

        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(25));
        vbox.setStyle("-fx-background-color: #f5f5f5;");

        // Título
        Label titulo = new Label("Seleccione la Mascota");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #113051;");

        // Subtítulo con nombre del cliente
        Label subtitulo = new Label("Cliente: " + cliente.getNombre() + " " + cliente.getApellidos());
        subtitulo.setStyle("-fx-font-size: 14px; -fx-text-fill: #607489;");

        // Separador
        Label separador = new Label("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        separador.setStyle("-fx-text-fill: #0FB9BA;");

        // ComboBox de mascotas
        ComboBox<co.edu.upb.veterinaria.models.ModeloMascota.Mascota> cbMascotas = new ComboBox<>();
        cbMascotas.getItems().addAll(mascotas);
        cbMascotas.setPromptText("Seleccione una mascota...");
        cbMascotas.setPrefWidth(300);
        cbMascotas.setConverter(new javafx.util.StringConverter<co.edu.upb.veterinaria.models.ModeloMascota.Mascota>() {
            @Override
            public String toString(co.edu.upb.veterinaria.models.ModeloMascota.Mascota m) {
                if (m == null) return "";
                return m.getNombre() + " (" + m.getEspecie() + " - " + m.getRaza() + ")";
            }

            @Override
            public co.edu.upb.veterinaria.models.ModeloMascota.Mascota fromString(String string) {
                return null;
            }
        });

        // Contenedor para guardar la mascota seleccionada
        final co.edu.upb.veterinaria.models.ModeloMascota.Mascota[] resultado = {null};

        // Botones
        Button btnAceptar = new Button("Aceptar");
        Button btnCancelar = new Button("Cancelar");

        btnAceptar.setStyle("-fx-background-color: #0FB9BA; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 30;");
        btnCancelar.setStyle("-fx-background-color: #E45858; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 30;");

        btnAceptar.setOnAction(event -> {
            co.edu.upb.veterinaria.models.ModeloMascota.Mascota seleccionada =
                cbMascotas.getSelectionModel().getSelectedItem();
            if (seleccionada == null) {
                mostrarAdvertencia("Debe seleccionar una mascota.");
                return;
            }
            resultado[0] = seleccionada;
            dialog.close();
        });

        btnCancelar.setOnAction(event -> dialog.close());

        VBox buttonsBox = new VBox(10, btnAceptar, btnCancelar);
        buttonsBox.setAlignment(Pos.CENTER);

        vbox.getChildren().addAll(titulo, subtitulo, separador,
                                   new Label("Mascota:"), cbMascotas, buttonsBox);
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 450, 350);
        dialog.setScene(scene);
        dialog.showAndWait();

        return resultado[0];
    }
}
