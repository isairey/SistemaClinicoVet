package co.edu.upb.veterinaria.controllers.ControllerSeeSurgicalProcedure;

import co.edu.upb.veterinaria.models.ModeloServicio.Servicio;
import co.edu.upb.veterinaria.services.ServicioHistorialClinica.HistorialClinicaService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.stage.Modality;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

public class SurgicalProcedureSee {

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

    private static final String CLIENT_VISUALIZE_REGISTER_FXML =
            "/co/edu/upb/veterinaria/views/ClientVisualizeRegister-view/ClientVisualizeRegister-view.fxml";

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
            btnVisualizarRegistrosTop, btnVentasTop, btnAgregarClientesTop, btnAdministrarProveedoresTop;

    // Navegación interna
    @FXML private Button btnVolver;

    // Cabecera info
    @FXML private Label lbCliente, lbMascota;

    // Tabla y contenedores
    @FXML private ScrollPane scrollTabla;
    @FXML private VBox tableWrapper;
    @FXML private TableView<Servicio> tblServicios;

    // Columnas de servicio
    @FXML private TableColumn<Servicio, Integer> colIdServicio;
    @FXML private TableColumn<Servicio, String> colNombreServicio, colDescripcion;
    @FXML private TableColumn<Servicio, Double> colPrecio;

    // Descripción
    @FXML private TextArea taDescripcion;

    // Botonera
    @FXML private Button btnDeshacer, btnEditar, btnEliminar;

    private static final double BASE_WIDTH = 1500.0;

    // Servicio y datos
    private final HistorialClinicaService historialService = new HistorialClinicaService();
    private final ObservableList<Servicio> serviciosData = FXCollections.observableArrayList();
    private final ObservableList<Servicio> serviciosOriginales = FXCollections.observableArrayList();
    private final Stack<Servicio> serviciosEliminados = new Stack<>();

    // Datos de la mascota cargada
    private int idMascotaActual = -1;
    private String nombreCliente = "";
    private String nombreMascota = "";

    @FXML
    private void initialize() {
        // Abrir maximizado
        Platform.runLater(() -> {
            Stage stage = safeStage();
            if (stage != null) {
                stage.setResizable(true);
                stage.setMaximized(true);
            }
        });

        // Configurar columnas de la tabla
        configurarColumnas();

        // Vincular datos a la tabla
        tblServicios.setItems(serviciosData);

        // Listener para mostrar descripción al seleccionar un servicio
        tblServicios.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    String descripcion = newSelection.getDescripcion();
                    taDescripcion.setText(descripcion != null ? descripcion : "Sin descripción");
                } else {
                    taDescripcion.clear();
                }
            }
        );

        // Placeholder y scroll horizontal real
        if (tblServicios != null && tblServicios.getPlaceholder() == null) {
            tblServicios.setPlaceholder(new Label("Sin servicios registrados para esta mascota."));
        }
        if (tableWrapper != null) tableWrapper.setPrefWidth(BASE_WIDTH);
        if (tblServicios != null) tblServicios.setPrefWidth(BASE_WIDTH);

        if (scrollTabla != null) {
            scrollTabla.viewportBoundsProperty().addListener((obs, o, bounds) -> adjustWidths(bounds));
            scrollTabla.setFitToHeight(true);
            scrollTabla.setFitToWidth(false);
            scrollTabla.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            scrollTabla.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        }
    }

    /**
     * Configura las columnas de la tabla de servicios.
     */
    private void configurarColumnas() {
        colIdServicio.setCellValueFactory(new PropertyValueFactory<>("idServicio"));
        colNombreServicio.setCellValueFactory(new PropertyValueFactory<>("nombreServicio"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        // Centrar columna ID y Precio
        colIdServicio.setStyle("-fx-alignment: CENTER;");
        colPrecio.setStyle("-fx-alignment: CENTER-RIGHT;");

        System.out.println("✓ Columnas de servicios configuradas");
    }

    /**
     * Carga los servicios de una mascota específica.
     * Este método debe ser llamado desde el controlador anterior al navegar.
     *
     * @param idMascota ID de la mascota
     * @param nombreCliente Nombre del cliente (para mostrar en el label)
     * @param nombreMascota Nombre de la mascota (para mostrar en el label)
     */
    public void cargarDatosMascota(int idMascota, String nombreCliente, String nombreMascota) {
        this.idMascotaActual = idMascota;
        this.nombreCliente = nombreCliente;
        this.nombreMascota = nombreMascota;

        // Actualizar labels de la cabecera
        if (lbCliente != null) {
            lbCliente.setText("Cliente: " + nombreCliente);
        }
        if (lbMascota != null) {
            lbMascota.setText("Mascota: " + nombreMascota);
        }

        // Cargar servicios de la mascota
        cargarServicios();
    }

    /**
     * Carga los servicios asociados a la mascota actual desde la base de datos.
     */
    private void cargarServicios() {
        if (idMascotaActual <= 0) {
            System.out.println("⚠ No hay mascota seleccionada para cargar servicios");
            return;
        }

        try {
            System.out.println("=== CARGANDO SERVICIOS DE MASCOTA ID=" + idMascotaActual + " ===");

            List<Servicio> servicios = historialService.obtenerServiciosDeMascota(idMascotaActual);

            System.out.println("✓ Servicios obtenidos: " + servicios.size());

            Platform.runLater(() -> {
                serviciosOriginales.clear();
                serviciosOriginales.addAll(servicios);

                serviciosData.clear();
                serviciosData.addAll(servicios);

                System.out.println("✓ Tabla actualizada con " + serviciosData.size() + " servicios");

                if (servicios.isEmpty()) {
                    mostrarInfo("Información", "No hay servicios registrados para esta mascota.");
                }
            });

        } catch (SQLException e) {
            System.err.println("❌ Error al cargar servicios: " + e.getMessage());
            e.printStackTrace();

            Platform.runLater(() -> {
                mostrarError("Error al cargar servicios", "No se pudieron cargar los servicios de la mascota:\n" + e.getMessage());
            });
        }
    }

    private void adjustWidths(Bounds viewport) {
        double target = Math.max(BASE_WIDTH, viewport.getWidth() + 200);
        if (tableWrapper != null && tableWrapper.getPrefWidth() != target) tableWrapper.setPrefWidth(target);
        if (tblServicios != null && tblServicios.getPrefWidth() != target) {
            tblServicios.setPrefWidth(target);
        }
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
    @FXML private void onVisualizarRegistrosTop()    { goTo(VISUALIZE_REGISTER_FXML); }
    @FXML private void onVentasTop()                 { goTo(SECTION_SALES_FXML); }
    @FXML private void onAgregarClientesTop()        { goTo(ADD_CLIENT_FXML); }
    @FXML private void onAdministrarProveedoresTop() { goTo(ADD_SUPPLIERS_FXML); }

    // ===== Navegación interna =====
    @FXML private void onVolver()                    { goTo(CLIENT_VISUALIZE_REGISTER_FXML); }

    // ===== EDITAR SERVICIO =====
    @FXML
    private void onEditar() {
        Servicio seleccionado = tblServicios.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAdvertencia("Selección requerida", "Debe seleccionar un servicio para editar.");
            return;
        }

        mostrarDialogoEditar(seleccionado);
    }

    /**
     * Muestra un diálogo para editar un servicio.
     */
    private void mostrarDialogoEditar(Servicio servicio) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Editar Servicio");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: #f5f5f5;");

        int row = 0;

        // Título
        Label titulo = new Label("Editar Servicio");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #113051;");
        grid.add(titulo, 0, row++, 2, 1);

        // Separador
        Label separador = new Label("━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        separador.setStyle("-fx-text-fill: #0FB9BA;");
        grid.add(separador, 0, row++, 2, 1);

        // ID (solo lectura)
        grid.add(new Label("ID Servicio:"), 0, row);
        TextField tfId = new TextField(String.valueOf(servicio.getIdServicio()));
        tfId.setEditable(false);
        tfId.setStyle("-fx-background-color: #e0e0e0;");
        grid.add(tfId, 1, row++);

        // Nombre del servicio
        grid.add(new Label("Nombre:*"), 0, row);
        TextField tfNombre = new TextField(servicio.getNombreServicio());
        tfNombre.setPrefWidth(300);
        grid.add(tfNombre, 1, row++);

        // Descripción
        grid.add(new Label("Descripción:"), 0, row);
        TextArea taDescripcionEdit = new TextArea(servicio.getDescripcion() != null ? servicio.getDescripcion() : "");
        taDescripcionEdit.setPrefRowCount(5);
        taDescripcionEdit.setPrefWidth(300);
        taDescripcionEdit.setWrapText(true);
        grid.add(taDescripcionEdit, 1, row++);

        // Precio
        grid.add(new Label("Precio:*"), 0, row);
        TextField tfPrecio = new TextField(String.valueOf(servicio.getPrecio()));
        tfPrecio.setPrefWidth(300);
        grid.add(tfPrecio, 1, row++);

        // Botones
        Button btnGuardar = new Button("Guardar");
        Button btnCancelar = new Button("Cancelar");

        btnGuardar.setStyle("-fx-background-color: #0FB9BA; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 30;");
        btnCancelar.setStyle("-fx-background-color: #E45858; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 30;");

        btnGuardar.setOnAction(event -> {
            // Validaciones
            if (tfNombre.getText().trim().isEmpty()) {
                mostrarAdvertencia("Campo requerido", "El nombre del servicio es obligatorio.");
                return;
            }

            try {
                double precio = Double.parseDouble(tfPrecio.getText().trim());
                if (precio < 0) {
                    mostrarAdvertencia("Valor inválido", "El precio no puede ser negativo.");
                    return;
                }

                // Actualizar el servicio
                servicio.setNombreServicio(tfNombre.getText().trim());
                servicio.setDescripcion(taDescripcionEdit.getText().trim());
                servicio.setPrecio(precio);

                // Actualizar en la tabla
                tblServicios.refresh();

                // Actualizar en la lista original
                for (int i = 0; i < serviciosOriginales.size(); i++) {
                    if (serviciosOriginales.get(i).getIdServicio() == servicio.getIdServicio()) {
                        serviciosOriginales.set(i, servicio);
                        break;
                    }
                }

                mostrarInfo("Éxito", "Servicio actualizado correctamente.");
                dialog.close();

            } catch (NumberFormatException e) {
                mostrarAdvertencia("Valor inválido", "El precio debe ser un número válido.");
            }
        });

        btnCancelar.setOnAction(event -> dialog.close());

        VBox buttonsBox = new VBox(10, btnGuardar, btnCancelar);
        buttonsBox.setAlignment(Pos.CENTER);
        grid.add(buttonsBox, 0, row, 2, 1);

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 500, 550);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    // ===== ELIMINAR SERVICIO =====
    @FXML
    private void onEliminar() {
        Servicio seleccionado = tblServicios.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAdvertencia("Selección requerida", "Debe seleccionar un servicio para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar este servicio?");
        confirmacion.setContentText("Servicio: " + seleccionado.getNombreServicio() +
            "\nPrecio: $" + String.format("%,.2f", seleccionado.getPrecio()) +
            "\n\nEsta acción se puede deshacer con el botón 'Deshacer'.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            // Guardar para poder deshacer
            serviciosEliminados.push(seleccionado);

            // Eliminar de las listas
            serviciosData.remove(seleccionado);
            serviciosOriginales.remove(seleccionado);

            mostrarInfo("Éxito", "Servicio eliminado correctamente.\nPuede deshacer esta acción con el botón 'Deshacer'.");
        }
    }

    // ===== DESHACER =====
    @FXML
    private void onDeshacer() {
        if (serviciosEliminados.isEmpty()) {
            // Si no hay eliminaciones, solo recargar
            cargarServicios();
            mostrarInfo("Datos recargados", "Se han recargado los servicios desde la base de datos.");
        } else {
            // Restaurar último servicio eliminado
            Servicio servicioRestaurado = serviciosEliminados.pop();
            serviciosData.add(servicioRestaurado);
            serviciosOriginales.add(servicioRestaurado);

            // Ordenar por ID
            serviciosData.sort(Comparator.comparing(Servicio::getIdServicio));

            mostrarInfo("Servicio restaurado", "Se ha restaurado el servicio: " + servicioRestaurado.getNombreServicio());
        }
    }

    // ===== UTILIDADES =====
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

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
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
}
