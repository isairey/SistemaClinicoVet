package co.edu.upb.veterinaria.controllers.ControllerSectionBrands;

import co.edu.upb.veterinaria.models.ModeloMarca.Marca;
import co.edu.upb.veterinaria.services.ServicioMarca.MarcaService;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

public class SectionBrandsController {

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

    // Volver a AddMarca
    private static final String ADD_MARCA_FXML =
            "/co/edu/upb/veterinaria/views/AddMarca-view/AddMarca-view.fxml";

    // ===== SERVICIO =====
    private final MarcaService marcaService;

    // ===== DATOS =====
    private ObservableList<Marca> listaMarcas;
    private Stack<Marca> pilaMarcasEliminadas; // Para función "Deshacer"

    // Filtros actuales
    private String filtroActual = "TODOS"; // TODOS, ID, NOMBRE, DESCRIPCION
    private String textoBusqueda = "";

    // ===== Constructor =====
    public SectionBrandsController() {
        this.marcaService = new MarcaService();
        this.listaMarcas = FXCollections.observableArrayList();
        this.pilaMarcasEliminadas = new Stack<>();
    }

    // ===== Root / header =====
    @FXML private AnchorPane root;
    @FXML private ImageView topLogo;

    @FXML private MenuButton mbNotificaciones;
    @FXML private MenuItem   miVerNotificaciones;
    @FXML private MenuButton mbPerfil;
    @FXML private MenuItem   miDatosPersonales, miCerrarSesion;

    // Menú superior
    @FXML private Button btnRegistrarProductos, btnInventario, btnGestionarUsuario,
            btnVisualizarRegistros, btnVentas, btnAgregarClientes, btnAdministrarProveedores;

    // Búsqueda
    @FXML private TextField tfBuscar;
    @FXML private Button btnFiltrar, btnBuscar, btnLimpiar;

    // Tabla
    @FXML private ScrollPane scrollTabla;
    @FXML private TableView<?> tblMarcas;
    @FXML private TableColumn<?, ?> colIdMarca, colNombreMarca, colDescripcion;

    // Acciones
    @FXML private Button btnDeshacer, btnEditarSeleccion, btnEliminarSeleccion, btnVolver;

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            Stage stage = safeStage();
            if (stage != null) {
                stage.setResizable(true);
                stage.setMaximized(true);
            }
        });

        // Configurar tabla
        if (tblMarcas != null) {
            tblMarcas.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            configurarTabla();
        }
        if (scrollTabla != null) {
            scrollTabla.setFitToWidth(false);
            scrollTabla.setFitToHeight(true);
            scrollTabla.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            scrollTabla.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        }

        // Cargar datos iniciales
        cargarMarcas();
    }

    // ===== CONFIGURACIÓN DE LA TABLA =====

    /**
     * Configura las columnas de la tabla para mostrar los datos de Marca.
     */
    @SuppressWarnings("unchecked")
    private void configurarTabla() {
        // Columna ID
        ((TableColumn<Marca, Number>) colIdMarca).setCellValueFactory(
            cellData -> new SimpleIntegerProperty(cellData.getValue().getIdMarca())
        );

        // Columna Nombre
        ((TableColumn<Marca, String>) colNombreMarca).setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getNombreMarca())
        );

        // Columna Descripción
        ((TableColumn<Marca, String>) colDescripcion).setCellValueFactory(
            cellData -> {
                String desc = cellData.getValue().getDescripcion();
                return new SimpleStringProperty(desc != null ? desc : "(Sin descripción)");
            }
        );

        // Vincular la lista observable a la tabla
        ((TableView<Marca>) tblMarcas).setItems(listaMarcas);
    }

    // ===== CARGAR DATOS =====

    /**
     * Carga todas las marcas desde la base de datos y las muestra en la tabla.
     */
    private void cargarMarcas() {
        try {
            List<Marca> marcas = marcaService.listarTodasLasMarcas();
            listaMarcas.setAll(marcas);
        } catch (SQLException e) {
            mostrarAlerta(
                Alert.AlertType.ERROR,
                "Error al cargar marcas",
                "No se pudieron cargar las marcas desde la base de datos.\n\n" +
                "Error: " + e.getMessage()
            );
            e.printStackTrace();
        }
    }

    // ===== BÚSQUEDA Y FILTROS =====

    /**
     * Abre una ventana emergente para seleccionar el tipo de filtro.
     *
     * Opciones de filtro:
     *  - Todos (busca en ID, nombre y descripción)
     *  - Solo ID
     *  - Solo Nombre
     *  - Solo Descripción
     */
    @FXML
    private void onFiltrar() {
        // Crear ventana emergente (Stage)
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(safeStage());
        dialogStage.setTitle("Seleccionar Filtro");
        dialogStage.setResizable(false);

        // Crear contenido del diálogo
        VBox dialogVBox = new VBox(20);
        dialogVBox.setAlignment(Pos.CENTER);
        dialogVBox.setPadding(new Insets(25));
        dialogVBox.setStyle("-fx-background-color: #F5F7FA;");

        // Título
        Label lblTitulo = new Label("Seleccione el campo por el cual filtrar:");
        lblTitulo.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        // Opciones de filtro (RadioButtons)
        ToggleGroup grupoFiltros = new ToggleGroup();

        RadioButton rbTodos = new RadioButton("Todos los campos (ID, Nombre, Descripción)");
        rbTodos.setToggleGroup(grupoFiltros);
        rbTodos.setSelected(filtroActual.equals("TODOS"));
        rbTodos.setStyle("-fx-font-size: 13px;");

        RadioButton rbId = new RadioButton("Solo ID");
        rbId.setToggleGroup(grupoFiltros);
        rbId.setSelected(filtroActual.equals("ID"));
        rbId.setStyle("-fx-font-size: 13px;");

        RadioButton rbNombre = new RadioButton("Solo Nombre de la marca");
        rbNombre.setToggleGroup(grupoFiltros);
        rbNombre.setSelected(filtroActual.equals("NOMBRE"));
        rbNombre.setStyle("-fx-font-size: 13px;");

        RadioButton rbDescripcion = new RadioButton("Solo Descripción");
        rbDescripcion.setToggleGroup(grupoFiltros);
        rbDescripcion.setSelected(filtroActual.equals("DESCRIPCION"));
        rbDescripcion.setStyle("-fx-font-size: 13px;");

        VBox opcionesVBox = new VBox(10);
        opcionesVBox.getChildren().addAll(rbTodos, rbId, rbNombre, rbDescripcion);
        opcionesVBox.setStyle("-fx-padding: 10; -fx-background-color: white; -fx-background-radius: 8;");

        // Espacio para búsqueda OPCIONAL (según el filtro seleccionado)
        Label lblBusqueda = new Label("Texto de búsqueda (opcional):");
        lblBusqueda.setStyle("-fx-font-size: 13px; -fx-text-fill: #34495E;");

        TextField tfBusquedaFiltro = new TextField(textoBusqueda);
        tfBusquedaFiltro.setPromptText("Escriba el texto a buscar...");
        tfBusquedaFiltro.setPrefWidth(350);
        tfBusquedaFiltro.setStyle("-fx-font-size: 13px; -fx-padding: 8;");

        // Botones
        HBox botonesHBox = new HBox(15);
        botonesHBox.setAlignment(Pos.CENTER);

        Button btnAplicar = new Button("Aplicar Filtro");
        btnAplicar.setStyle("-fx-background-color: #0FB9BA; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-font-size: 13px;");

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle("-fx-background-color: #95A5A6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-font-size: 13px;");

        botonesHBox.getChildren().addAll(btnAplicar, btnCancelar);

        // Agregar todo al VBox principal
        dialogVBox.getChildren().addAll(lblTitulo, opcionesVBox, lblBusqueda, tfBusquedaFiltro, botonesHBox);

        // Eventos de botones
        btnAplicar.setOnAction(e -> {
            // Guardar filtro seleccionado
            if (rbId.isSelected()) {
                filtroActual = "ID";
            } else if (rbNombre.isSelected()) {
                filtroActual = "NOMBRE";
            } else if (rbDescripcion.isSelected()) {
                filtroActual = "DESCRIPCION";
            } else {
                filtroActual = "TODOS";
            }

            // Guardar texto de búsqueda
            textoBusqueda = tfBusquedaFiltro.getText();

            // Aplicar filtro y cerrar
            aplicarFiltro();
            dialogStage.close();
        });

        btnCancelar.setOnAction(e -> dialogStage.close());

        // Mostrar diálogo
        Scene dialogScene = new Scene(dialogVBox);
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }

    /**
     * Aplica el filtro actual y el texto de búsqueda a la lista de marcas.
     */
    private void aplicarFiltro() {
        try {
            if (textoBusqueda == null || textoBusqueda.trim().isEmpty()) {
                // Si no hay texto de búsqueda, mostrar todas
                cargarMarcas();
                return;
            }

            String textoLower = textoBusqueda.trim().toLowerCase();
            List<Marca> todasLasMarcas = marcaService.listarTodasLasMarcas();

            // Filtrar según el criterio seleccionado
            List<Marca> marcasFiltradas = todasLasMarcas.stream()
                .filter(marca -> {
                    switch (filtroActual) {
                        case "ID":
                            return String.valueOf(marca.getIdMarca()).contains(textoLower);

                        case "NOMBRE":
                            return marca.getNombreMarca() != null &&
                                   marca.getNombreMarca().toLowerCase().contains(textoLower);

                        case "DESCRIPCION":
                            return marca.getDescripcion() != null &&
                                   marca.getDescripcion().toLowerCase().contains(textoLower);

                        case "TODOS":
                        default:
                            return String.valueOf(marca.getIdMarca()).contains(textoLower) ||
                                   (marca.getNombreMarca() != null && marca.getNombreMarca().toLowerCase().contains(textoLower)) ||
                                   (marca.getDescripcion() != null && marca.getDescripcion().toLowerCase().contains(textoLower));
                    }
                })
                .toList();

            listaMarcas.setAll(marcasFiltradas);

        } catch (SQLException e) {
            mostrarAlerta(
                Alert.AlertType.ERROR,
                "Error al filtrar",
                "No se pudo aplicar el filtro.\n\nError: " + e.getMessage()
            );
            e.printStackTrace();
        }
    }

    /**
     * Realiza la búsqueda según el texto ingresado en la barra de búsqueda.
     * Busca en todos los campos (ID, Nombre, Descripción).
     */
    @FXML
    private void onBuscar() {
        textoBusqueda = tfBuscar.getText();
        filtroActual = "TODOS"; // Buscar en todos los campos por defecto
        aplicarFiltro();
    }

    /**
     * Limpia el campo de búsqueda y muestra todas las marcas.
     */
    @FXML
    private void onLimpiar() {
        tfBuscar.clear();
        textoBusqueda = "";
        filtroActual = "TODOS";
        cargarMarcas();
    }

    // ===== ACCIONES DE GESTIÓN =====

    /**
     * Deshace la última eliminación realizada.
     */
    @FXML
    private void onDeshacer() {
        if (pilaMarcasEliminadas.isEmpty()) {
            mostrarAlerta(
                Alert.AlertType.INFORMATION,
                "No hay nada que deshacer",
                "No se han eliminado marcas recientemente."
            );
            return;
        }

        try {
            // Recuperar la última marca eliminada
            Marca marcaEliminada = pilaMarcasEliminadas.pop();

            // ⚠️ IMPORTANTE: descripcion es NOT NULL en BD
            // Asegurar que la descripción no sea null
            String descripcionParaRegistrar = (marcaEliminada.getDescripcion() != null && !marcaEliminada.getDescripcion().isBlank())
                ? marcaEliminada.getDescripcion()
                : "";

            // Volver a registrar en la BD
            int idRestaurado = marcaService.registrarMarca(
                marcaEliminada.getNombreMarca(),
                descripcionParaRegistrar
            );

            mostrarAlerta(
                Alert.AlertType.INFORMATION,
                "Marca restaurada",
                "La marca '" + marcaEliminada.getNombreMarca() + "' fue restaurada con ID: " + idRestaurado
            );

            // Recargar tabla
            cargarMarcas();

        } catch (IllegalArgumentException e) {
            mostrarAlerta(
                Alert.AlertType.WARNING,
                "No se pudo restaurar",
                "La marca ya existe en el sistema: " + e.getMessage()
            );
        } catch (SQLException e) {
            mostrarAlerta(
                Alert.AlertType.ERROR,
                "Error al restaurar",
                "No se pudo restaurar la marca.\n\nError: " + e.getMessage()
            );
            e.printStackTrace();
        }
    }

    /**
     * Edita la marca seleccionada en la tabla.
     * Abre una ventana emergente profesional con los datos de la marca.
     */
    @FXML
    @SuppressWarnings("unchecked")
    private void onEditarSeleccion() {
        Marca marcaSeleccionada = ((TableView<Marca>) tblMarcas).getSelectionModel().getSelectedItem();

        if (marcaSeleccionada == null) {
            mostrarAlerta(
                Alert.AlertType.WARNING,
                "Ninguna marca seleccionada",
                "Por favor, seleccione una marca de la tabla para editarla."
            );
            return;
        }

        // Crear ventana emergente (Stage)
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(safeStage());
        dialogStage.setTitle("Editar Marca");
        dialogStage.setResizable(false);

        // Contenedor principal
        VBox mainVBox = new VBox(25);
        mainVBox.setAlignment(Pos.TOP_CENTER);
        mainVBox.setPadding(new Insets(30, 40, 30, 40));
        mainVBox.setStyle("-fx-background-color: #F5F7FA;");

        // Título
        Label lblTitulo = new Label("Editar Marca");
        lblTitulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        Label lblSubtitulo = new Label("Modifique los datos de la marca seleccionada");
        lblSubtitulo.setStyle("-fx-font-size: 13px; -fx-text-fill: #7F8C8D;");

        VBox tituloVBox = new VBox(5);
        tituloVBox.setAlignment(Pos.CENTER);
        tituloVBox.getChildren().addAll(lblTitulo, lblSubtitulo);

        // Formulario con GridPane
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(20);
        grid.setPadding(new Insets(25));
        grid.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        // Campo ID (solo lectura)
        Label lblId = new Label("ID de la Marca:");
        lblId.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #34495E;");

        TextField tfId = new TextField(String.valueOf(marcaSeleccionada.getIdMarca()));
        tfId.setEditable(false);
        tfId.setStyle("-fx-background-color: #ECF0F1; -fx-font-size: 14px; -fx-padding: 10; -fx-opacity: 0.7;");
        tfId.setPrefWidth(400);

        // Campo Nombre
        Label lblNombre = new Label("Nombre de la Marca:");
        lblNombre.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #34495E;");

        TextField tfNombre = new TextField(marcaSeleccionada.getNombreMarca());
        tfNombre.setPromptText("Ingrese el nombre de la marca...");
        tfNombre.setStyle("-fx-background-color: white; -fx-border-color: #BDC3C7; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-size: 14px; -fx-padding: 10;");
        tfNombre.setPrefWidth(400);

        // Campo Descripción
        Label lblDescripcion = new Label("Descripción:");
        lblDescripcion.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #34495E;");

        TextArea taDescripcion = new TextArea(marcaSeleccionada.getDescripcion());
        taDescripcion.setPromptText("Ingrese una descripción (opcional)...");
        taDescripcion.setWrapText(true);
        taDescripcion.setPrefRowCount(5);
        taDescripcion.setPrefWidth(400);
        taDescripcion.setStyle("-fx-background-color: white; -fx-border-color: #BDC3C7; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-size: 14px; -fx-padding: 10;");

        // Agregar campos al grid
        grid.add(lblId, 0, 0);
        grid.add(tfId, 1, 0);
        grid.add(lblNombre, 0, 1);
        grid.add(tfNombre, 1, 1);
        grid.add(lblDescripcion, 0, 2);
        grid.add(taDescripcion, 1, 2);

        // Botones de acción
        HBox botonesHBox = new HBox(20);
        botonesHBox.setAlignment(Pos.CENTER);

        Button btnGuardar = new Button("Guardar Cambios");
        btnGuardar.setStyle("-fx-background-color: #0FB9BA; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12 30; -fx-background-radius: 8;");
        btnGuardar.setCursor(javafx.scene.Cursor.HAND);

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle("-fx-background-color: #95A5A6; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12 30; -fx-background-radius: 8;");
        btnCancelar.setCursor(javafx.scene.Cursor.HAND);

        botonesHBox.getChildren().addAll(btnGuardar, btnCancelar);

        // Agregar todo al contenedor principal
        mainVBox.getChildren().addAll(tituloVBox, grid, botonesHBox);

        // Eventos de los botones
        btnGuardar.setOnAction(e -> {
            String nuevoNombre = tfNombre.getText();
            String nuevaDescripcion = taDescripcion.getText();

            // Validación básica
            if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
                mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Campo obligatorio",
                    "El nombre de la marca es obligatorio."
                );
                tfNombre.requestFocus();
                return;
            }

            try {
                // Actualizar en la base de datos
                boolean actualizado = marcaService.actualizarMarca(
                    marcaSeleccionada.getIdMarca(),
                    nuevoNombre,
                    nuevaDescripcion
                );

                if (actualizado) {
                    mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Marca actualizada",
                        "La marca '" + nuevoNombre.trim() + "' fue actualizada exitosamente."
                    );

                    // Recargar tabla
                    cargarMarcas();

                    // Cerrar ventana
                    dialogStage.close();
                }

            } catch (IllegalArgumentException ex) {
                mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error de validación",
                    ex.getMessage()
                );
            } catch (SQLException ex) {
                mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error al actualizar",
                    "No se pudo actualizar la marca.\n\nError: " + ex.getMessage()
                );
                ex.printStackTrace();
            }
        });

        btnCancelar.setOnAction(e -> dialogStage.close());

        // Mostrar ventana
        Scene dialogScene = new Scene(mainVBox, 550, 500);
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }

    /**
     * Elimina la marca seleccionada (con confirmación).
     */
    @FXML
    @SuppressWarnings("unchecked")
    private void onEliminarSeleccion() {
        Marca marcaSeleccionada = ((TableView<Marca>) tblMarcas).getSelectionModel().getSelectedItem();

        if (marcaSeleccionada == null) {
            mostrarAlerta(
                Alert.AlertType.WARNING,
                "Ninguna marca seleccionada",
                "Por favor, seleccione una marca de la tabla para eliminarla."
            );
            return;
        }

        // Confirmación con ventana emergente profesional
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar esta marca?");
        confirmacion.setContentText(
            "ID: " + marcaSeleccionada.getIdMarca() + "\n" +
            "Nombre: " + marcaSeleccionada.getNombreMarca() + "\n\n" +
            "⚠️ ADVERTENCIA: Esta operación es PERMANENTE.\n" +
            "Si hay productos usando esta marca, la operación fallará.\n\n" +
            "Puede usar 'Deshacer' inmediatamente después para restaurarla."
        );

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                // Guardar en pila para deshacer (antes de eliminar)
                pilaMarcasEliminadas.push(new Marca(
                    marcaSeleccionada.getIdMarca(),
                    marcaSeleccionada.getNombreMarca(),
                    marcaSeleccionada.getDescripcion()
                ));

                // Eliminar de la base de datos
                boolean eliminado = marcaService.eliminarMarca(marcaSeleccionada.getIdMarca());

                if (eliminado) {
                    mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Marca eliminada exitosamente",
                        "La marca '" + marcaSeleccionada.getNombreMarca() + "' fue eliminada.\n\n" +
                        "💡 Consejo: Use el botón 'Deshacer' si desea restaurarla."
                    );

                    // Recargar tabla
                    cargarMarcas();
                } else {
                    // Si no se eliminó, quitar de la pila
                    pilaMarcasEliminadas.pop();

                    mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "No se pudo eliminar",
                        "La marca no existe o ya fue eliminada."
                    );
                }

            } catch (SQLException ex) {
                // Revertir pila si falló
                if (!pilaMarcasEliminadas.isEmpty()) {
                    pilaMarcasEliminadas.pop();
                }

                String mensaje = ex.getMessage();
                if (mensaje != null && (mensaje.contains("foreign key") || mensaje.contains("productos asociados"))) {
                    mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "No se puede eliminar",
                        "❌ Esta marca no puede eliminarse porque hay productos asociados a ella.\n\n" +
                        "📋 Acción requerida:\n" +
                        "Primero debe eliminar o reasignar todos los productos que usan esta marca.\n\n" +
                        "Marca: " + marcaSeleccionada.getNombreMarca()
                    );
                } else {
                    mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "Error al eliminar",
                        "No se pudo eliminar la marca de la base de datos.\n\n" +
                        "Error técnico: " + mensaje
                    );
                }
                ex.printStackTrace();
            }
        }
    }

    // ===== Volver =====
    @FXML
    private void onVolver() { goTo(ADD_MARCA_FXML); }

    // ===== MÉTODOS DE NAVEGACIÓN DEL HEADER =====

    @FXML
    private void onLogoClick() { goTo(MAINMENU_FXML); }

    @FXML
    private void onVerNotificaciones() { goTo(SEENOTIFICATIONS_FXML); }

    @FXML
    private void onDatosPersonales() { goTo(PERSONALDATA_FXML); }

    @FXML
    private void onCerrarSesion() { goTo(LOGIN_FXML); }

    // ===== MÉTODOS DE NAVEGACIÓN DEL MENÚ SUPERIOR =====

    @FXML
    private void onRegistrarProductos() { goTo(REGISTER_PRODUCT_FXML); }

    @FXML
    private void onInventario() { goTo(INVENTARY_FXML); }

    @FXML
    private void onGestionarUsuario() { goTo(CREATE_USER_FXML); }

    @FXML
    private void onVisualizarRegistros() { goTo(VISUALIZE_REGISTER_FXML); }

    @FXML
    private void onVentas() { goTo(SECTION_SALES_FXML); }

    @FXML
    private void onAgregarClientes() { goTo(ADD_CLIENT_FXML); }

    @FXML
    private void onAdministrarProveedores() { goTo(ADD_SUPPLIERS_FXML); }

    // ===== UTILIDADES =====
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

    /**
     * Muestra una alerta modal.
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

