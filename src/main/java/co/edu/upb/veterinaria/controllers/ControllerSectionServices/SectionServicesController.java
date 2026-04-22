package co.edu.upb.veterinaria.controllers.ControllerSectionServices;

import co.edu.upb.veterinaria.config.DatabaseConfig;
import co.edu.upb.veterinaria.models.ModeloServicio.Servicio;
import co.edu.upb.veterinaria.services.ServicioServicio.ServicioService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.sql.DataSource;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class SectionServicesController {

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

    // Volver a AddServices
    private static final String ADD_SERVICES_FXML =
            "/co/edu/upb/veterinaria/views/AddServices-view/AddServices-view.fxml";

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

    // ===== Búsqueda =====
    @FXML private TextField tfBuscar;
    @FXML private Button btnFiltrar, btnBuscar, btnLimpiar;

    // ===== Tabla / contenedores =====
    @FXML private ScrollPane scrollTabla;
    @FXML private VBox tableWrapper;
    @FXML private TableView<Servicio> tblServicios;
    @FXML private TableColumn<Servicio, Integer> colIdServicio;
    @FXML private TableColumn<Servicio, String> colNombreServicio;
    @FXML private TableColumn<Servicio, Double> colValor;
    @FXML private TableColumn<Servicio, String> colDescripcion;

    // ===== Acciones =====
    @FXML private Button btnDeshacer, btnEditarSeleccion, btnEliminarSeleccion, btnVolver;

    // ===== Servicio =====
    private ServicioService servicioService;
    private ObservableList<Servicio> masterData = FXCollections.observableArrayList();
    private List<Servicio> originalList = List.of();
    private List<Servicio> serviciosEliminados = new ArrayList<>(); // Stack para restaurar servicios

    @FXML
    private void initialize() {
        // Abrir maximizado (sin alterar tamaños de tabla/columnas)
        Platform.runLater(() -> {
            Stage stage = safeStage();
            if (stage != null) {
                stage.setResizable(true);
                stage.setMaximized(true);
            }
        });

        // Scroll horizontal REAL y sin autoajustes que cambian el diseño
        if (scrollTabla != null) {
            scrollTabla.setFitToWidth(false);
            scrollTabla.setFitToHeight(true);
            scrollTabla.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            scrollTabla.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        }

        if (tblServicios != null) {
            tblServicios.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        }

        // Inicializar el servicio
        try {
            DataSource dataSource = DatabaseConfig.getDataSource();
            servicioService = new ServicioService(dataSource);
        } catch (Exception e) {
            mostrarError("Error al conectar con la base de datos: " + e.getMessage());
            e.printStackTrace();
        }

        // Configurar las columnas de la tabla
        configurarTabla();

        // Cargar los servicios
        cargarServicios();
    }

    private void configurarTabla() {
        // Configurar las columnas usando cellValueFactory con lambdas
        colIdServicio.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getIdServicio()).asObject());

        colNombreServicio.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombreServicio()));

        colValor.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getPrecio()).asObject());

        colDescripcion.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescripcion()));

        // Formatear la columna de precio como moneda
        colValor.setCellFactory(column -> new TableCell<Servicio, Double>() {
            @Override
            protected void updateItem(Double precio, boolean empty) {
                super.updateItem(precio, empty);
                if (empty || precio == null) {
                    setText(null);
                } else {
                    NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
                    setText(formatter.format(precio));
                }
            }
        });

        // IMPORTANTE: Forzar que la tabla sea visible y se actualice
        tblServicios.setVisible(true);
        tblServicios.setManaged(true);
        tblServicios.setPlaceholder(new Label("No hay servicios disponibles"));

        // Permitir selección
        tblServicios.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private void cargarServicios() {
        try {
            System.out.println(">>> Iniciando carga de servicios...");
            List<Servicio> servicios = servicioService.listarTodosLosServicios();
            System.out.println(">>> Servicios obtenidos: " + servicios.size());

            // Limpiar datos anteriores
            masterData.clear();

            // Agregar servicios uno por uno para debug
            for (Servicio s : servicios) {
                System.out.println(">>> Agregando: " + s.getIdServicio() + " - " + s.getNombreServicio() + " - $" + s.getPrecio());
                masterData.add(s);
            }

            originalList = new ArrayList<>(servicios);

            // Asignar los items a la tabla
            tblServicios.setItems(masterData);

            // FORZAR actualización visual de la tabla
            tblServicios.refresh();

            // Forzar actualización del layout
            Platform.runLater(() -> {
                tblServicios.refresh();
                tblServicios.layout();
                System.out.println(">>> Tabla actualizada. Items en tabla: " + tblServicios.getItems().size());
            });

            if (servicios.isEmpty()) {
                System.out.println("⚠️ No hay servicios en la base de datos");
            } else {
                System.out.println("✓ Cargados " + servicios.size() + " servicios en la tabla");
            }
        } catch (Exception e) {
            System.err.println("ERROR al cargar servicios: " + e.getMessage());
            mostrarError("Error al cargar los servicios: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Stage seguro. */
    private Stage safeStage() {
        if (root != null && root.getScene() != null) return (Stage) root.getScene().getWindow();
        Window w = Window.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null);
        return (w instanceof Stage) ? (Stage) w : null;
    }

    /** Navegación robusta. */
    private void goTo(String fxml) {
        try {
            var url = getClass().getResource(fxml);
            if (url == null) throw new IllegalStateException("NO existe el recurso (classpath): " + fxml);
            Parent newRoot = FXMLLoader.load(url);

            Stage stage = safeStage();
            if (stage == null) throw new IllegalStateException("No hay Stage activo para navegar.");

            Scene scene = stage.getScene();
            if (scene == null) {
                scene = new Scene(newRoot);
                stage.setScene(scene);
            } else {
                scene.setRoot(newRoot);
            }
            stage.centerOnScreen();
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

    // ===== Búsqueda/acciones =====
    @FXML
    private void onBuscar() {
        String textoBusqueda = tfBuscar.getText();
        if (textoBusqueda == null || textoBusqueda.trim().isEmpty()) {
            tblServicios.setItems(masterData);
            return;
        }

        String q = textoBusqueda.trim().toLowerCase();
        List<Servicio> filtrados = originalList.stream()
                .filter(s -> containsIgnoreCase(s.getNombreServicio(), q)
                        || containsIgnoreCase(s.getDescripcion(), q)
                        || String.valueOf(s.getPrecio()).contains(q))
                .collect(Collectors.toList());

        tblServicios.setItems(FXCollections.observableArrayList(filtrados));

        if (filtrados.isEmpty()) {
            mostrarInfo("No se encontraron servicios con ese criterio de búsqueda");
        }
    }

    @FXML
    private void onLimpiar() {
        tfBuscar.clear();
        cargarServicios();
    }

    @FXML
    private void onFiltrar() {
        // Crear diálogo para seleccionar columna y valor a filtrar
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Filtrar Servicios");
        dialog.setHeaderText("Seleccione el criterio de filtrado");

        // Botones
        ButtonType btnFiltrar = new ButtonType("Filtrar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnFiltrar, btnCancelar);

        // Crear formulario
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // ComboBox para seleccionar la columna
        ComboBox<String> cbColumna = new ComboBox<>();
        cbColumna.getItems().addAll("ID", "Nombre del Servicio", "Precio", "Descripción");
        cbColumna.setValue("Nombre del Servicio");

        // Campo de texto para el valor a buscar
        TextField tfValor = new TextField();
        tfValor.setPromptText("Ingrese el valor a buscar...");

        grid.add(new Label("Filtrar por:"), 0, 0);
        grid.add(cbColumna, 1, 0);
        grid.add(new Label("Valor:"), 0, 1);
        grid.add(tfValor, 1, 1);

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> tfValor.requestFocus());

        dialog.setResultConverter(dialogButton -> dialogButton == btnFiltrar);

        Optional<Boolean> resultado = dialog.showAndWait();

        if (resultado.isPresent() && resultado.get()) {
            String columna = cbColumna.getValue();
            String valor = tfValor.getText().trim();

            if (valor.isEmpty()) {
                mostrarAdvertencia("Debe ingresar un valor para filtrar.");
                return;
            }

            filtrarPorColumna(columna, valor);
        }
    }

    private void filtrarPorColumna(String columna, String valor) {
        String valorLower = valor.toLowerCase();
        List<Servicio> filtrados = originalList.stream()
            .filter(s -> {
                switch (columna) {
                    case "ID":
                        return String.valueOf(s.getIdServicio()).contains(valorLower);
                    case "Nombre del Servicio":
                        return containsIgnoreCase(s.getNombreServicio(), valorLower);
                    case "Precio":
                        return String.valueOf(s.getPrecio()).contains(valorLower);
                    case "Descripción":
                        return containsIgnoreCase(s.getDescripcion(), valorLower);
                    default:
                        return false;
                }
            })
            .collect(Collectors.toList());

        if (filtrados.isEmpty()) {
            mostrarInfo("No se encontraron servicios que coincidan con: '" + valor + "' en " + columna);
        }

        tblServicios.setItems(FXCollections.observableArrayList(filtrados));
    }

    @FXML
    private void onDeshacer() {
        if (serviciosEliminados.isEmpty()) {
            mostrarInfo("No hay servicios eliminados para restaurar en esta sesión.");
            return;
        }

        // Obtener el último servicio eliminado
        Servicio ultimoEliminado = serviciosEliminados.get(serviciosEliminados.size() - 1);

        // Mostrar diálogo de confirmación
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deshacer Eliminación");
        alert.setHeaderText("Restaurar servicio eliminado");
        alert.setContentText("¿Desea restaurar el servicio:\n\n" +
                "Nombre: " + ultimoEliminado.getNombreServicio() + "\n" +
                "Precio: $" + String.format("%,.2f", ultimoEliminado.getPrecio()) + "\n\n" +
                "Este servicio será reinsertado al sistema.\n" +
                "Hay " + serviciosEliminados.size() + " servicio(s) eliminado(s) en esta sesión.");

        ButtonType btnRestaurar = new ButtonType("Restaurar");
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btnRestaurar, btnCancelar);

        Optional<ButtonType> resultado = alert.showAndWait();

        if (resultado.isPresent() && resultado.get() == btnRestaurar) {
            try {
                // Restaurar el servicio en la base de datos
                int nuevoId = servicioService.crearServicio(ultimoEliminado);

                if (nuevoId > 0) {
                    // Remover del stack de eliminados
                    serviciosEliminados.remove(serviciosEliminados.size() - 1);

                    // Recargar la tabla para mostrar el servicio restaurado
                    cargarServicios();

                    mostrarExito("Servicio '" + ultimoEliminado.getNombreServicio() +
                               "' ha sido restaurado al sistema.");
                } else {
                    mostrarError("No se pudo restaurar el servicio.");
                }
            } catch (Exception e) {
                mostrarError("Error al restaurar el servicio: " + e.getMessage());
            }
        }
    }

    @FXML
    private void onEditarSeleccion() {
        Servicio seleccionado = tblServicios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAdvertencia("Por favor, selecciona un servicio para editar");
            return;
        }

        // Crear diálogo de edición
        Dialog<Servicio> dialog = crearDialogoEdicion(seleccionado);
        Optional<Servicio> resultado = dialog.showAndWait();

        resultado.ifPresent(servicioEditado -> {
            try {
                boolean actualizado = servicioService.actualizarServicio(servicioEditado);
                if (actualizado) {
                    mostrarExito("Servicio actualizado correctamente.");
                    cargarServicios();
                } else {
                    mostrarError("No se pudo actualizar el servicio.");
                }
            } catch (Exception e) {
                mostrarError("Error al actualizar: " + e.getMessage());
            }
        });
    }

    /**
     * Crea un diálogo personalizado para editar un servicio.
     */
    private Dialog<Servicio> crearDialogoEdicion(Servicio servicio) {
        Dialog<Servicio> dialog = new Dialog<>();
        dialog.setTitle("Editar Servicio");
        dialog.setHeaderText("Editar información del servicio: " + servicio.getNombreServicio());

        // Botones
        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, btnCancelar);

        // Crear formulario
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField tfNombre = new TextField(servicio.getNombreServicio());
        TextField tfPrecio = new TextField(String.valueOf(servicio.getPrecio()));
        TextArea taDescripcion = new TextArea(servicio.getDescripcion());
        taDescripcion.setPrefRowCount(4);

        int row = 0;
        grid.add(new Label("Nombre del Servicio:"), 0, row);
        grid.add(tfNombre, 1, row++);

        grid.add(new Label("Precio:"), 0, row);
        grid.add(tfPrecio, 1, row++);

        grid.add(new Label("Descripción:"), 0, row);
        grid.add(taDescripcion, 1, row++);

        dialog.getDialogPane().setContent(grid);

        // Convertir resultado cuando se presiona Guardar
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnGuardar) {
                try {
                    Servicio editado = new Servicio();
                    editado.setIdServicio(servicio.getIdServicio());
                    editado.setNombreServicio(tfNombre.getText().trim());
                    editado.setPrecio(Double.parseDouble(tfPrecio.getText().trim()));
                    editado.setDescripcion(taDescripcion.getText().trim());
                    return editado;
                } catch (NumberFormatException e) {
                    mostrarError("El precio debe ser un número válido.");
                    return null;
                }
            }
            return null;
        });

        Platform.runLater(() -> tfNombre.requestFocus());

        return dialog;
    }

    @FXML
    private void onEliminarSeleccion() {
        Servicio seleccionado = tblServicios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAdvertencia("Por favor, selecciona un servicio para eliminar");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Estás seguro de eliminar este servicio?");
        confirmacion.setContentText("Servicio: " + seleccionado.getNombreServicio() + "\n" +
                                   "Precio: $" + String.format("%,.2f", seleccionado.getPrecio()));

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                // Eliminar de la base de datos
                boolean eliminado = servicioService.eliminarServicio(seleccionado.getIdServicio());

                if (eliminado) {
                    // Agregar el servicio completo al stack de eliminados (para poder deshacerlo)
                    serviciosEliminados.add(seleccionado);

                    // Remover de la vista actual
                    masterData.remove(seleccionado);
                    originalList = new ArrayList<>(masterData);

                    mostrarExito("Servicio eliminado exitosamente. Use 'Deshacer' para restaurarlo.");
                } else {
                    mostrarError("No se pudo eliminar el servicio");
                }
            } catch (Exception e) {
                mostrarError("Error al eliminar el servicio: " + e.getMessage());
            }
        }
    }

    // ===== Volver =====
    @FXML private void onVolver() { goTo(ADD_SERVICES_FXML); }

    /**
     * Método público para recargar la lista de servicios.
     * Puede ser llamado desde otras vistas después de guardar un servicio.
     */
    public void actualizarTabla() {
        cargarServicios();
    }

    private boolean containsIgnoreCase(String source, String q) {
        return source != null && source.toLowerCase().contains(q);
    }

    // ===== Métodos auxiliares =====
    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
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

