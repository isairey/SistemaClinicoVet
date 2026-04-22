package co.edu.upb.veterinaria.controllers.ControllerUserSection;

import co.edu.upb.veterinaria.models.ModeloModulo.Modulo;
import co.edu.upb.veterinaria.models.ModeloUsuario.Usuario;
import co.edu.upb.veterinaria.services.ServicioUsuario.UsuarioService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;
import java.util.stream.Collectors;

public class SectionUser {
    private static final String MAINMENU_FXML = "/co/edu/upb/veterinaria/views/mainMenu-view/mainMenu-view.fxml";
    private static final String SEENOTIFICATIONS_FXML = "/co/edu/upb/veterinaria/views/SeeNotifications-view/SeeNotifications-view.fxml";
    private static final String PERSONALDATA_FXML = "/co/edu/upb/veterinaria/views/personalData-view/personalData.fxml";
    private static final String LOGIN_FXML = "/co/edu/upb/veterinaria/views/Login/Login.fxml";
    private static final String REGISTER_PRODUCT_FXML = "/co/edu/upb/veterinaria/views/registerProduct-view/registerProduct-view.fxml";
    private static final String INVENTARY_FXML = "/co/edu/upb/veterinaria/views/inventary-view/inventary-view.fxml";
    private static final String VISUALIZE_REGISTER_FXML = "/co/edu/upb/veterinaria/views/visualizeRegister-view/visualizeRegister-view.fxml";
    private static final String SECTION_SALES_FXML = "/co/edu/upb/veterinaria/views/SectionSales-view/SectionSales-view.fxml";
    private static final String ADD_CLIENT_FXML = "/co/edu/upb/veterinaria/views/AddClient-view/AddClient-view.fxml";
    private static final String ADD_SUPPLIERS_FXML = "/co/edu/upb/veterinaria/views/AddSuppliers-view/AddSuppliers-view.fxml";

    @FXML private AnchorPane root;
    @FXML private ScrollPane scrollTabla;
    @FXML private VBox tableWrapper;
    @FXML private TableView<Usuario> tblUsuarios;
    @FXML private ImageView topLogo;
    @FXML private MenuButton mbNotificaciones, mbPerfil;
    @FXML private MenuItem miVerNotificaciones, miDatosPersonales, miCerrarSesion;
    @FXML private Button btnRegistrarProductos, btnInventario, btnVisualizarRegistros, btnVentas, btnAgregarClientes, btnAdministrarProveedores;
    @FXML private Label lblTotalUsuarios;
    @FXML private TextField tfBuscarUsuario;
    @FXML private Button btnFiltrar, btnBuscar, btnLimpiar;
    @FXML private TableColumn<Usuario, String> colId, colCC, colNombre, colApellidos, colUsuario, colEmail, colTelefono, colDireccion, colPermisos;
    @FXML private Button btnDeshacer, btnEditarSeleccion, btnEliminarSeleccion;

    private final UsuarioService usuarioService = new UsuarioService();
    private final ObservableList<Usuario> todosLosUsuarios = FXCollections.observableArrayList();
    private final ObservableList<Usuario> usuariosFiltrados = FXCollections.observableArrayList();
    private final Stack<List<Usuario>> historialCambios = new Stack<>();

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            Stage stage = (Stage) root.getScene().getWindow();
            if (stage != null) {
                stage.setResizable(true);
                stage.setMaximized(true);
            }
        });
        configurarTabla();
        cargarUsuarios();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getIdUsuario())));
        colCC.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCc()));
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombre()));
        colApellidos.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getApellidos()));
        colUsuario.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsuario()));
        colEmail.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        colTelefono.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTelefono()));
        colDireccion.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDireccion()));
        
        colPermisos.setCellValueFactory(data -> {
            Usuario usuario = data.getValue();
            List<Modulo> modulos;
            try {
                modulos = usuarioService.obtenerModulosDeUsuario(usuario.getIdUsuario());
            } catch (Exception e) {
                modulos = List.of();
            }
            String permisos = modulos.stream()
                .map(Modulo::getNombreModulo)
                .collect(Collectors.joining(", "));
            return new SimpleStringProperty(permisos.isEmpty() ? "Sin permisos" : permisos);
        });

        tblUsuarios.setItems(usuariosFiltrados);
        
        tblUsuarios.setRowFactory(tv -> {
            TableRow<Usuario> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    mostrarDialogoEdicion(row.getItem());
                }
            });
            return row;
        });
    }

    private void cargarUsuarios() {
        try {
            todosLosUsuarios.clear();
            usuariosFiltrados.clear();
            
            List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
            todosLosUsuarios.addAll(usuarios);
            usuariosFiltrados.addAll(usuarios);
            
            actualizarContador();
        } catch (Exception e) {
            mostrarError("Error al cargar usuarios", e.getMessage());
        }
    }

    private void actualizarContador() {
        lblTotalUsuarios.setText("Total de usuarios: " + usuariosFiltrados.size());
    }

    @FXML
    private void onBuscar() {
        String termino = tfBuscarUsuario.getText().trim();
        
        if (termino.isEmpty()) {
            usuariosFiltrados.setAll(todosLosUsuarios);
        } else {
            try {
                List<Usuario> resultados = usuarioService.buscarUsuarios(termino);
                usuariosFiltrados.setAll(resultados);
            } catch (Exception e) {
                mostrarError("Error en la busqueda", e.getMessage());
            }
        }
        
        actualizarContador();
    }

    @FXML
    private void onFiltrar() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Filtrar Usuarios");
        dialog.setHeaderText("Ingrese el termino de busqueda");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField tfFiltro = new TextField();
        tfFiltro.setPromptText("Buscar por nombre, apellido, usuario, email o cedula");

        grid.add(new Label("Buscar:"), 0, 0);
        grid.add(tfFiltro, 1, 0);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return tfFiltro.getText().trim();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(filtro -> {
            if (filtro.isEmpty()) {
                usuariosFiltrados.setAll(todosLosUsuarios);
            } else {
                try {
                    List<Usuario> resultados = usuarioService.buscarUsuarios(filtro);
                    usuariosFiltrados.setAll(resultados);
                } catch (Exception e) {
                    mostrarError("Error al filtrar", e.getMessage());
                }
            }
            actualizarContador();
        });
    }

    @FXML
    private void onLimpiar() {
        tfBuscarUsuario.clear();
        usuariosFiltrados.setAll(todosLosUsuarios);
        actualizarContador();
    }

    @FXML
    private void onEditarSeleccion() {
        Usuario seleccionado = tblUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAdvertencia("Seleccione un usuario", "Debe seleccionar un usuario de la tabla");
            return;
        }
        mostrarDialogoEdicion(seleccionado);
    }

    private void mostrarDialogoEdicion(Usuario usuario) {
        Dialog<Usuario> dialog = new Dialog<>();
        dialog.setTitle("Editar Usuario");
        dialog.setHeaderText("Editando: " + usuario.getNombre() + " " + usuario.getApellidos());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField tfCC = new TextField(usuario.getCc());
        TextField tfNombre = new TextField(usuario.getNombre());
        TextField tfApellidos = new TextField(usuario.getApellidos());
        TextField tfUsuario = new TextField(usuario.getUsuario());
        TextField tfEmail = new TextField(usuario.getEmail());
        TextField tfTelefono = new TextField(usuario.getTelefono());
        TextField tfDireccion = new TextField(usuario.getDireccion());
        PasswordField pfContrasena = new PasswordField();
        pfContrasena.setPromptText("Dejar vacio para mantener contrasena actual");

        grid.add(new Label("Cedula:"), 0, 0);
        grid.add(tfCC, 1, 0);
        grid.add(new Label("Nombre:"), 0, 1);
        grid.add(tfNombre, 1, 1);
        grid.add(new Label("Apellidos:"), 0, 2);
        grid.add(tfApellidos, 1, 2);
        grid.add(new Label("Usuario:"), 0, 3);
        grid.add(tfUsuario, 1, 3);
        grid.add(new Label("Email:"), 0, 4);
        grid.add(tfEmail, 1, 4);
        grid.add(new Label("Telefono:"), 0, 5);
        grid.add(tfTelefono, 1, 5);
        grid.add(new Label("Direccion:"), 0, 6);
        grid.add(tfDireccion, 1, 6);
        grid.add(new Label("Nueva Contrasena:"), 0, 7);
        grid.add(pfContrasena, 1, 7);

        VBox vboxModulos = new VBox(5);
        vboxModulos.setPadding(new Insets(10, 0, 0, 0));
        Label lblModulos = new Label("Permisos (Modulos):");
        vboxModulos.getChildren().add(lblModulos);

        List<Modulo> todosModulos;
        List<Modulo> modulosUsuario;
        try {
            todosModulos = usuarioService.obtenerTodosLosModulos();
            modulosUsuario = usuarioService.obtenerModulosDeUsuario(usuario.getIdUsuario());
        } catch (Exception e) {
            mostrarError("Error al cargar modulos", e.getMessage());
            return;
        }

        Set<Integer> modulosActivosIds = modulosUsuario.stream()
            .map(Modulo::getIdModulo)
            .collect(Collectors.toSet());

        Map<Integer, CheckBox> mapaCheckboxes = new HashMap<>();
        
        for (Modulo modulo : todosModulos) {
            CheckBox cb = new CheckBox(modulo.getNombreModulo());
            cb.setSelected(modulosActivosIds.contains(modulo.getIdModulo()));
            mapaCheckboxes.put(modulo.getIdModulo(), cb);
            vboxModulos.getChildren().add(cb);
        }

        grid.add(vboxModulos, 0, 8, 2, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                usuario.setCc(tfCC.getText().trim());
                usuario.setNombre(tfNombre.getText().trim());
                usuario.setApellidos(tfApellidos.getText().trim());
                usuario.setUsuario(tfUsuario.getText().trim());
                usuario.setEmail(tfEmail.getText().trim());
                usuario.setTelefono(tfTelefono.getText().trim());
                usuario.setDireccion(tfDireccion.getText().trim());
                
                if (!pfContrasena.getText().isEmpty()) {
                    usuario.setContrasena(pfContrasena.getText());
                }
                
                return usuario;
            }
            return null;
        });

        Optional<Usuario> result = dialog.showAndWait();
        result.ifPresent(usuarioEditado -> {
            try {
                guardarEstadoActual();
                
                List<Integer> modulosSeleccionados = mapaCheckboxes.entrySet().stream()
                    .filter(entry -> entry.getValue().isSelected())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
                
                usuarioService.actualizarUsuario(usuarioEditado, modulosSeleccionados);
                
                cargarUsuarios();
                mostrarInformacion("Usuario actualizado", "El usuario se ha actualizado correctamente");
            } catch (Exception e) {
                mostrarError("Error al actualizar usuario", e.getMessage());
            }
        });
    }

    @FXML
    private void onEliminarSeleccion() {
        Usuario seleccionado = tblUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAdvertencia("Seleccione un usuario", "Debe seleccionar un usuario de la tabla");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminacion");
        alert.setHeaderText("Eliminar usuario?");
        alert.setContentText("Esta seguro de eliminar al usuario " + seleccionado.getNombre() + 
                             " " + seleccionado.getApellidos() + "?\nEsta accion no se puede deshacer.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                guardarEstadoActual();
                usuarioService.eliminarUsuario(seleccionado.getIdUsuario());
                cargarUsuarios();
                mostrarInformacion("Usuario eliminado", "El usuario se ha eliminado correctamente");
            } catch (Exception e) {
                mostrarError("Error al eliminar usuario", e.getMessage());
            }
        }
    }

    @FXML
    private void onDeshacer() {
        if (historialCambios.isEmpty()) {
            mostrarAdvertencia("Sin cambios", "No hay cambios que deshacer");
            return;
        }
        
        List<Usuario> estadoAnterior = historialCambios.pop();
        usuariosFiltrados.setAll(estadoAnterior);
        todosLosUsuarios.setAll(estadoAnterior);
        actualizarContador();
    }

    private void guardarEstadoActual() {
        historialCambios.push(new ArrayList<>(todosLosUsuarios));
    }

    @FXML
    private void goToRegisterProduct() {
        goTo(REGISTER_PRODUCT_FXML);
    }

    @FXML
    private void onRegistrarProductos() {
        goTo(REGISTER_PRODUCT_FXML);
    }

    @FXML
    private void goToInventary() {
        goTo(INVENTARY_FXML);
    }

    @FXML
    private void onInventario() {
        goTo(INVENTARY_FXML);
    }

    @FXML
    private void goToVisualizeRegister() {
        goTo(VISUALIZE_REGISTER_FXML);
    }

    @FXML
    private void onVisualizarRegistros() {
        goTo(VISUALIZE_REGISTER_FXML);
    }

    @FXML
    private void goToSectionSales() {
        goTo(SECTION_SALES_FXML);
    }

    @FXML
    private void onVentas() {
        goTo(SECTION_SALES_FXML);
    }

    @FXML
    private void goToAddClient() {
        goTo(ADD_CLIENT_FXML);
    }

    @FXML
    private void onAgregarClientes() {
        goTo(ADD_CLIENT_FXML);
    }

    @FXML
    private void goToAddSuppliers() {
        goTo(ADD_SUPPLIERS_FXML);
    }

    @FXML
    private void onAdministrarProveedores() {
        goTo(ADD_SUPPLIERS_FXML);
    }

    @FXML
    private void goToSeeNotifications() {
        goTo(SEENOTIFICATIONS_FXML);
    }

    @FXML
    private void onVerNotificaciones() {
        goTo(SEENOTIFICATIONS_FXML);
    }

    @FXML
    private void goToPersonalData() {
        goTo(PERSONALDATA_FXML);
    }

    @FXML
    private void onDatosPersonales() {
        goTo(PERSONALDATA_FXML);
    }

    @FXML
    private void logoutButtonOnAction() {
        goTo(LOGIN_FXML);
    }

    @FXML
    private void onCerrarSesion() {
        goTo(LOGIN_FXML);
    }

    @FXML
    private void onEditar() {
        onEditarSeleccion();
    }

    @FXML
    private void onEliminar() {
        onEliminarSeleccion();
    }

    @FXML
    private void onLogoClick() {
        goTo(MAINMENU_FXML);
    }

    private void goTo(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent newRoot = loader.load();
            
            Stage stage = (Stage) root.getScene().getWindow();
            Scene scene = new Scene(newRoot);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            mostrarError("Error de navegacion", "No se pudo cargar la vista: " + e.getMessage());
        }
    }

    private void mostrarInformacion(String titulo, String mensaje) {
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