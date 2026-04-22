package co.edu.upb.veterinaria.controllers.ControllersSectionSuppliers;

import co.edu.upb.veterinaria.models.ModeloProveedor.Proveedor;
import co.edu.upb.veterinaria.services.ServicioProveedor.ProveedorService;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/** Sección de Proveedores: funcionalidad completa */
public class SuppliersSection {

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
            "/co.edu.upb/veterinaria/views/visualizeRegister-view/visualizeRegister-view.fxml";
    private static final String SECTION_SALES_FXML =
            "/co/edu.upb/veterinaria/views/SectionSales-view/SectionSales-view.fxml";
    private static final String ADD_CLIENT_FXML =
            "/co/edu.upb.veterinaria/views/AddClient-view/AddClient-view.fxml";
    private static final String ADD_SUPPLIERS_FXML =
            "/co/edu.upb.veterinaria/views/AddSuppliers-view/AddSuppliers-view.fxml";

    // ===== Raíz / header =====
    @FXML private AnchorPane root;
    @FXML private ImageView topLogo;

    @FXML private MenuButton mbNotificaciones;
    @FXML private MenuItem   miVerNotificaciones;

    @FXML private MenuButton mbPerfil;
    @FXML private MenuItem   miDatosPersonales;
    @FXML private MenuItem   miCerrarSesion;

    // ===== Menú superior (módulos) =====
    @FXML private Button btnRegistrarProductos, btnInventario, btnGestionarUsuarios,
            btnVisualizarRegistros, btnVentas, btnAgregarClientes;

    // ===== Tabla + scroll =====
    @FXML private ScrollPane scrollTabla;
    @FXML private VBox tableWrapper;
    @FXML private TableView<Proveedor> tblProveedores;

    // Columnas
    @FXML private TableColumn<Proveedor, Number> colID;
    @FXML private TableColumn<Proveedor, String> colNombre, colApellido, colTipoDocumento, colNIT,
            colCorreo, colTelefono, colDireccion, colCiudad, colTipoPersona;

    // ===== Filtros / búsqueda =====
    @FXML private Label lblTotalProveedores;
    @FXML private TextField tfBuscarProveedor;
    @FXML private Button btnFiltrar, btnBuscar, btnLimpiar;

    // ===== Acciones =====
    @FXML private Button btnDeshacer, btnEditarSeleccion, btnEliminarSeleccion;

    // ===== Servicio / datos =====
    private final ProveedorService proveedorService = new ProveedorService();
    private ObservableList<Proveedor> masterData = FXCollections.observableArrayList();
    private List<Proveedor> originalList = List.of();

    // Variable para almacenar el estado anterior de un proveedor editado (para deshacer)
    private Proveedor proveedorAntesDeEditar = null;

    @FXML
    private void initialize() {

        Platform.runLater(() -> {
            Stage stage = (Stage) root.getScene().getWindow();
            if (stage != null) {
                stage.setResizable(true);
                stage.setMaximized(true);
            }
        });

        // Configuración de scroll y tabla
        if (tblProveedores != null) {
            tblProveedores.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        }
        if (scrollTabla != null) {
            scrollTabla.setFitToWidth(false);
            scrollTabla.setFitToHeight(true);
            scrollTabla.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            scrollTabla.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        }
        if (lblTotalProveedores != null) lblTotalProveedores.setText("#");

        setupColumns();
        refreshTable();
    }

    private void setupColumns() {
        // Columna ID (no editable)
        if (colID != null) {
            colID.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getIdProveedor()));
        }

        // Todas las demás columnas son solo de lectura - sin edición in-line
        if (colNombre != null) {
            colNombre.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNombre()));
        }

        if (colApellido != null) {
            colApellido.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getApellido()));
        }

        if (colTipoDocumento != null) {
            colTipoDocumento.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTipoDocumento()));
        }

        if (colNIT != null) {
            colNIT.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNit_rut()));
        }

        if (colCorreo != null) {
            colCorreo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmail()));
        }

        if (colTelefono != null) {
            colTelefono.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTelefono()));
        }

        if (colDireccion != null) {
            colDireccion.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDireccion()));
        }

        if (colCiudad != null) {
            colCiudad.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCiudad()));
        }

        if (colTipoPersona != null) {
            colTipoPersona.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTipoPersona()));
        }
    }

    /**
     * Actualiza un proveedor en la base de datos después de editar.
     */
    private void actualizarProveedor(Proveedor proveedor) {
        try {
            boolean actualizado = proveedorService.actualizarProveedor(proveedor);
            if (actualizado) {
                new Alert(Alert.AlertType.INFORMATION, "Proveedor actualizado correctamente.", ButtonType.OK).show();
                refreshTable();
            } else {
                new Alert(Alert.AlertType.ERROR, "No se pudo actualizar el proveedor.").showAndWait();
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error al actualizar: " + e.getMessage()).showAndWait();
            refreshTable(); // Recargar para revertir cambio
        }
    }

    /** Recupera los proveedores del servicio y actualiza la tabla. */
    public void refreshTable() {
        try {
            List<Proveedor> lista = proveedorService.obtenerTodosLosProveedores();
            originalList = lista;
            masterData.setAll(lista);
            tblProveedores.setItems(masterData);
            lblTotalProveedores.setText(String.valueOf(masterData.size()));
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "No se pudo cargar proveedores: " + e.getMessage()).showAndWait();
        }
    }

    // ===== Navegación común =====
    private void goTo(String fxml) {
        try {
            var url = getClass().getResource(fxml);
            if (url == null) throw new IllegalStateException("NO existe el recurso (classpath): " + fxml);
            FXMLLoader loader = new FXMLLoader(url);
            Parent newRoot = loader.load();
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
    @FXML private void onRegistrarProductos()   { goTo(REGISTER_PRODUCT_FXML); }
    @FXML private void onInventario()           { goTo(INVENTARY_FXML); }
    @FXML private void onGestionarUsuarios()    { goTo(CREATE_USER_FXML); }
    @FXML private void onVisualizarRegistros()  { goTo(VISUALIZE_REGISTER_FXML); }
    @FXML private void onVentas()               { goTo(SECTION_SALES_FXML); }
    @FXML private void onAgregarClientes()      { goTo(ADD_CLIENT_FXML); }

    // ===== Acciones implementadas =====

    @FXML private void onBuscar() {
        String query = tfBuscarProveedor.getText();
        if (query == null || query.trim().isEmpty()) {
            tblProveedores.setItems(masterData);
            lblTotalProveedores.setText(String.valueOf(masterData.size()));
            return;
        }
        String q = query.trim().toLowerCase();
        List<Proveedor> filtered = originalList.stream()
                .filter(p -> containsIgnoreCase(p.getNombre(), q)
                        || containsIgnoreCase(p.getApellido(), q)
                        || containsIgnoreCase(p.getNit_rut(), q)
                        || containsIgnoreCase(p.getEmail(), q)
                        || containsIgnoreCase(p.getTelefono(), q)
                        || containsIgnoreCase(p.getCiudad(), q))
                .collect(Collectors.toList());
        tblProveedores.setItems(FXCollections.observableArrayList(filtered));
        lblTotalProveedores.setText(String.valueOf(filtered.size()));
    }

    @FXML private void onLimpiar() {
        tfBuscarProveedor.clear();
        refreshTable();
    }

    @FXML private void onFiltrar() {
        // Crear diálogo para seleccionar columna y valor a filtrar
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Filtrar Proveedores");
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
        cbColumna.getItems().addAll(
            "ID",
            "Nombre",
            "Apellido",
            "Tipo Documento",
            "NIT/RUT",
            "Correo",
            "Teléfono",
            "Dirección",
            "Ciudad",
            "Tipo Persona"
        );
        cbColumna.setValue("Nombre");

        // Campo de texto para el valor a buscar
        TextField tfValor = new TextField();
        tfValor.setPromptText("Ingrese el valor a buscar...");

        grid.add(new Label("Filtrar por:"), 0, 0);
        grid.add(cbColumna, 1, 0);
        grid.add(new Label("Valor:"), 0, 1);
        grid.add(tfValor, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Enfocar el campo de valor
        Platform.runLater(() -> tfValor.requestFocus());

        // Procesar el resultado
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnFiltrar) {
                return true;
            }
            return false;
        });

        Optional<Boolean> resultado = dialog.showAndWait();

        if (resultado.isPresent() && resultado.get()) {
            String columna = cbColumna.getValue();
            String valor = tfValor.getText().trim();

            if (valor.isEmpty()) {
                new Alert(Alert.AlertType.WARNING,
                    "Debe ingresar un valor para filtrar.",
                    ButtonType.OK).showAndWait();
                return;
            }

            // Filtrar según la columna seleccionada
            filtrarPorColumna(columna, valor);
        }
    }

    /**
     * Filtra los proveedores según la columna y valor especificados.
     */
    private void filtrarPorColumna(String columna, String valor) {
        String valorLower = valor.toLowerCase();
        List<Proveedor> filtrados = originalList.stream()
            .filter(p -> {
                switch (columna) {
                    case "ID":
                        return String.valueOf(p.getIdProveedor()).contains(valorLower);
                    case "Nombre":
                        return containsIgnoreCase(p.getNombre(), valorLower);
                    case "Apellido":
                        return containsIgnoreCase(p.getApellido(), valorLower);
                    case "Tipo Documento":
                        return containsIgnoreCase(p.getTipoDocumento(), valorLower);
                    case "NIT/RUT":
                        return containsIgnoreCase(p.getNit_rut(), valorLower);
                    case "Correo":
                        return containsIgnoreCase(p.getEmail(), valorLower);
                    case "Teléfono":
                        return containsIgnoreCase(p.getTelefono(), valorLower);
                    case "Dirección":
                        return containsIgnoreCase(p.getDireccion(), valorLower);
                    case "Ciudad":
                        return containsIgnoreCase(p.getCiudad(), valorLower);
                    case "Tipo Persona":
                        return containsIgnoreCase(p.getTipoPersona(), valorLower);
                    default:
                        return false;
                }
            })
            .collect(Collectors.toList());

        if (filtrados.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION,
                "No se encontraron proveedores que coincidan con: '" + valor + "' en " + columna,
                ButtonType.OK).showAndWait();
        }

        tblProveedores.setItems(FXCollections.observableArrayList(filtrados));
        lblTotalProveedores.setText(String.valueOf(filtrados.size()));
    }

    @FXML private void onEditarSeleccion() {
        Proveedor seleccionado = tblProveedores.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            new Alert(Alert.AlertType.WARNING, "Debe seleccionar un proveedor para editar.").showAndWait();
            return;
        }

        // Guardar el estado actual del proveedor ANTES de editar (para poder deshacer)
        proveedorAntesDeEditar = clonarProveedor(seleccionado);

        // Crear diálogo de edición
        Dialog<Proveedor> dialog = crearDialogoEdicion(seleccionado);
        Optional<Proveedor> resultado = dialog.showAndWait();

        resultado.ifPresent(proveedorEditado -> {
            try {
                boolean actualizado = proveedorService.actualizarProveedor(proveedorEditado);
                if (actualizado) {
                    new Alert(Alert.AlertType.INFORMATION, "Proveedor actualizado correctamente.").showAndWait();
                    refreshTable();
                } else {
                    new Alert(Alert.AlertType.ERROR, "No se pudo actualizar el proveedor.").showAndWait();
                    proveedorAntesDeEditar = null; // Limpiar si no se pudo actualizar
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Error al actualizar: " + e.getMessage()).showAndWait();
                proveedorAntesDeEditar = null; // Limpiar si hubo error
            }
        });

        // Si el usuario canceló, limpiar el estado anterior
        if (!resultado.isPresent()) {
            proveedorAntesDeEditar = null;
        }
    }

    /**
     * Método auxiliar para clonar un proveedor y guardar su estado.
     */
    private Proveedor clonarProveedor(Proveedor original) {
        Proveedor clon = new Proveedor();
        clon.setIdProveedor(original.getIdProveedor());
        clon.setTipoPersona(original.getTipoPersona());
        clon.setTipoDocumento(original.getTipoDocumento());
        clon.setNombre(original.getNombre());
        clon.setApellido(original.getApellido());
        clon.setNit_rut(original.getNit_rut());
        clon.setTelefono(original.getTelefono());
        clon.setEmail(original.getEmail());
        clon.setDireccion(original.getDireccion());
        clon.setCiudad(original.getCiudad());
        return clon;
    }

    /**
     * Crea un diálogo personalizado para editar un proveedor.
     */
    private Dialog<Proveedor> crearDialogoEdicion(Proveedor proveedor) {
        Dialog<Proveedor> dialog = new Dialog<>();
        dialog.setTitle("Editar Proveedor");
        dialog.setHeaderText("Editar información del proveedor: " + proveedor.getNombre() + " " + proveedor.getApellido());

        // Botones
        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, btnCancelar);

        // Crear formulario
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Campos del formulario
        ComboBox<String> cbTipoPersona = new ComboBox<>();
        cbTipoPersona.getItems().addAll("Natural", "Jurídica");
        cbTipoPersona.setValue(proveedor.getTipoPersona());

        ComboBox<String> cbTipoDocumento = new ComboBox<>();
        cbTipoDocumento.getItems().addAll("CC", "NIT", "RUT", "Cédula", "Pasaporte");
        cbTipoDocumento.setValue(proveedor.getTipoDocumento() != null ? proveedor.getTipoDocumento() : "NIT");

        TextField tfNombre = new TextField(proveedor.getNombre());
        TextField tfApellido = new TextField(proveedor.getApellido());
        TextField tfNIT = new TextField(proveedor.getNit_rut());
        TextField tfTelefono = new TextField(proveedor.getTelefono());
        TextField tfEmail = new TextField(proveedor.getEmail());
        TextField tfDireccion = new TextField(proveedor.getDireccion());
        TextField tfCiudad = new TextField(proveedor.getCiudad());

        // Agregar campos al grid
        int row = 0;
        grid.add(new Label("Tipo Persona:"), 0, row);
        grid.add(cbTipoPersona, 1, row++);

        grid.add(new Label("Tipo Documento:"), 0, row);
        grid.add(cbTipoDocumento, 1, row++);

        grid.add(new Label("Nombre:"), 0, row);
        grid.add(tfNombre, 1, row++);

        grid.add(new Label("Apellido:"), 0, row);
        grid.add(tfApellido, 1, row++);

        grid.add(new Label("NIT/RUT:"), 0, row);
        grid.add(tfNIT, 1, row++);

        grid.add(new Label("Teléfono:"), 0, row);
        grid.add(tfTelefono, 1, row++);

        grid.add(new Label("Email:"), 0, row);
        grid.add(tfEmail, 1, row++);

        grid.add(new Label("Dirección:"), 0, row);
        grid.add(tfDireccion, 1, row++);

        grid.add(new Label("Ciudad:"), 0, row);
        grid.add(tfCiudad, 1, row++);

        dialog.getDialogPane().setContent(grid);

        // Convertir resultado cuando se presiona Guardar
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnGuardar) {
                Proveedor editado = new Proveedor();
                editado.setIdProveedor(proveedor.getIdProveedor());
                editado.setTipoPersona(cbTipoPersona.getValue());
                editado.setTipoDocumento(cbTipoDocumento.getValue()); // Se guarda en el modelo pero no en BD
                editado.setNombre(tfNombre.getText().trim());
                editado.setApellido(tfApellido.getText().trim());
                editado.setNit_rut(tfNIT.getText().trim());
                editado.setTelefono(tfTelefono.getText().trim());
                editado.setEmail(tfEmail.getText().trim());
                editado.setDireccion(tfDireccion.getText().trim());
                editado.setCiudad(tfCiudad.getText().trim());
                return editado;
            }
            return null;
        });

        // Enfocar el primer campo
        Platform.runLater(() -> tfNombre.requestFocus());

        return dialog;
    }

    @FXML private void onEliminarSeleccion() {
        Proveedor seleccionado = tblProveedores.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            new Alert(Alert.AlertType.WARNING, "Seleccione un proveedor para eliminar.").showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Está seguro de eliminar al proveedor " + seleccionado.getNombre() + " " +
                seleccionado.getApellido() + "?\n\nEsta acción es permanente y no se puede deshacer.",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmar Eliminación");
        Optional<ButtonType> resultado = confirm.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.YES) {
            try {
                boolean eliminado = proveedorService.eliminarProveedor(seleccionado.getIdProveedor());

                if (eliminado) {
                    refreshTable();
                    new Alert(Alert.AlertType.INFORMATION,
                        "Proveedor eliminado exitosamente.",
                        ButtonType.OK).showAndWait();
                } else {
                    new Alert(Alert.AlertType.ERROR,
                        "No se pudo eliminar el proveedor.",
                        ButtonType.OK).showAndWait();
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR,
                    "Error al eliminar el proveedor: " + e.getMessage(),
                    ButtonType.OK).showAndWait();
            }
        }
    }

    /**
     * Deshace la última edición realizada a un proveedor.
     * Restaura el proveedor a su estado anterior.
     */
    @FXML private void onDeshacer() {
        if (proveedorAntesDeEditar == null) {
            new Alert(Alert.AlertType.INFORMATION,
                "No hay cambios de edición para deshacer.",
                ButtonType.OK).showAndWait();
            return;
        }

        // Mostrar diálogo de confirmación
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deshacer Edición");
        alert.setHeaderText("Restaurar proveedor editado");
        alert.setContentText("¿Desea deshacer los cambios del proveedor:\n\n" +
                "Nombre: " + proveedorAntesDeEditar.getNombre() + " " + proveedorAntesDeEditar.getApellido() + "\n" +
                "NIT/RUT: " + proveedorAntesDeEditar.getNit_rut() + "\n\n" +
                "Se restaurará al estado anterior de la edición.");

        ButtonType btnRestaurar = new ButtonType("Deshacer", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btnRestaurar, btnCancelar);

        Optional<ButtonType> resultado = alert.showAndWait();

        if (resultado.isPresent() && resultado.get() == btnRestaurar) {
            try {
                boolean actualizado = proveedorService.actualizarProveedor(proveedorAntesDeEditar);

                if (actualizado) {
                    String nombreCompleto = proveedorAntesDeEditar.getNombre() + " " + proveedorAntesDeEditar.getApellido();

                    // Limpiar la variable de estado anterior
                    proveedorAntesDeEditar = null;

                    // Recargar la tabla
                    refreshTable();

                    new Alert(Alert.AlertType.INFORMATION,
                        "Cambios deshechos. El proveedor " + nombreCompleto + " ha sido restaurado.",
                        ButtonType.OK).showAndWait();
                } else {
                    new Alert(Alert.AlertType.ERROR,
                        "No se pudo deshacer los cambios del proveedor.",
                        ButtonType.OK).showAndWait();
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR,
                    "Error al deshacer los cambios: " + e.getMessage(),
                    ButtonType.OK).showAndWait();
            }
        }
    }

    private boolean containsIgnoreCase(String source, String q) {
        return source != null && source.toLowerCase().contains(q);
    }
}
