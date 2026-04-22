package co.edu.upb.veterinaria.controllers.ControllerSectionSales;

import co.edu.upb.veterinaria.config.DatabaseConfig;
import co.edu.upb.veterinaria.models.ModeloCliente.Cliente;
import co.edu.upb.veterinaria.models.ModeloLineaVenta.LineaVenta;
import co.edu.upb.veterinaria.models.ModeloMascota.Mascota;
import co.edu.upb.veterinaria.models.ModeloProducto.Producto;
import co.edu.upb.veterinaria.models.ModeloServicio.Servicio;
import co.edu.upb.veterinaria.models.ModeloVenta.Venta;
import co.edu.upb.veterinaria.models.ModeloAlimento.Alimento;
import co.edu.upb.veterinaria.models.ModeloMedicamento.Medicamento;
import co.edu.upb.veterinaria.models.ModeloMaterialQuirurgico.MaterialQuirurgico;
import co.edu.upb.veterinaria.models.ModeloAccesorio.Accesorio;
import co.edu.upb.veterinaria.repositories.RepositorioCliente.ClienteRepository;
import co.edu.upb.veterinaria.repositories.RepositorioCliente.ClienteRepositoryImpl;
import co.edu.upb.veterinaria.repositories.RepositorioMascota.MascotaRepository;
import co.edu.upb.veterinaria.repositories.RepositorioMascota.MascotaRepositoryImpl;
import co.edu.upb.veterinaria.repositories.RepositorioProducto.ProductoRepository;
import co.edu.upb.veterinaria.repositories.RepositorioServicio.ServicioRepository;
import co.edu.upb.veterinaria.repositories.RepositorioAlimento.AlimentoRepository;
import co.edu.upb.veterinaria.repositories.RepositorioMedicamento.MedicamentoRepository;
import co.edu.upb.veterinaria.repositories.RepositorioMaterialQuirurgico.MaterialQuirurgicoRepository;
import co.edu.upb.veterinaria.repositories.RepositorioAccesorio.AccesorioRepository;
import co.edu.upb.veterinaria.services.ServicioVenta.VentaService;
import co.edu.upb.veterinaria.controllers.ClienteTemporalData;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.*;

public class SalesSection {

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
    private static final String ADD_CLIENT_FXML =
            "/co/edu/upb/veterinaria/views/AddClient-view/AddClient-view.fxml";
    private static final String ADD_SUPPLIERS_FXML =
            "/co/edu/upb/veterinaria/views/AddSuppliers-view/AddSuppliers-view.fxml";
    private static final String ADD_SURGICAL_PROCEDURES_FXML =
            "/co/edu/upb/veterinaria/views/AddSurgicalProcedure-view/AddSurgicalProcedure-view.fxml";
    private static final String ADD_SERVICES_FXML =
            "/co/edu/upb/veterinaria/views/AddServices-view/AddServices-view.fxml";

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
            btnVisualizarRegistros, btnAgregarClientes, btnAdministrarProveedores;

    // ===== Búsqueda =====
    @FXML private TextField tfBuscar;
    @FXML private Button btnFiltrar, btnBuscar, btnLimpiar;

    // ===== Tabla y columnas =====
    @FXML private ScrollPane scrollTabla;
    @FXML private TableView<ItemVenta> tblProductos;
    @FXML private TableColumn<ItemVenta, String> colId, colNombre, colTipoProducto, colCodigoBarras,
            colReferencia, colLote, colCantidad, colValor, colSubTotal;

    // ===== Pie =====
    @FXML private Label lbCliente, lbMascota, lbTotal;
    @FXML private Button btnSeleccionarCliente, btnSeleccionarMascota, btnAgregarProcedimiento, btnVender;

    // ===== Acciones generales =====
    @FXML private Button btnAumentarSeleccion, btnDisminuirSeleccion, btnEliminarSeleccion;

    // ===== Botón superior derecho =====
    @FXML private Button btnAgregarServicioTop;

    // ===== SERVICIOS Y REPOSITORIOS =====
    private final ClienteRepository clienteRepository = new ClienteRepositoryImpl();
    private final MascotaRepository mascotaRepository = new MascotaRepositoryImpl();
    private final ProductoRepository productoRepository = new ProductoRepository();
    private final ServicioRepository servicioRepository = new ServicioRepository(DatabaseConfig.getDataSource());
    private final AlimentoRepository alimentoRepository = new AlimentoRepository();
    private final MedicamentoRepository medicamentoRepository = new MedicamentoRepository();
    private final MaterialQuirurgicoRepository materialQuirurgicoRepository = new MaterialQuirurgicoRepository();
    private final AccesorioRepository accesorioRepository = new AccesorioRepository();
    private final VentaService ventaService = new VentaService();

    // ===== ESTADO DE LA VENTA =====
    private Cliente clienteSeleccionado = null;
    private Mascota mascotaSeleccionada = null;
    private final ObservableList<ItemVenta> itemsVenta = FXCollections.observableArrayList();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("es", "CO"));

    // ===== Clase interna para items de venta =====
    public static class ItemVenta {
        private final String id;
        private final String nombre;
        private final String tipo;
        private final String codigoBarras;
        private final String referencia;
        private final String lote;
        private int cantidad;
        private final double valorUnitario;
        private Producto producto;
        private Servicio servicio;

        public ItemVenta(Producto p) {
            this.id = String.valueOf(p.getIdProducto());
            this.nombre = p.getNombre();
            // Determinar el tipo específico del producto
            this.tipo = determinarTipoProducto(p);
            this.codigoBarras = p.getCodigoBarras() != null ? p.getCodigoBarras() : "";
            this.referencia = p.getReferencia() != null ? p.getReferencia() : "";
            this.lote = ""; // Implementar si tienes lotes
            this.cantidad = 1;
            this.valorUnitario = p.getPrecio();
            this.producto = p;
        }

        private static String determinarTipoProducto(Producto p) {
            if (p instanceof Alimento) {
                return "Alimento";
            } else if (p instanceof Medicamento) {
                return "Medicamento";
            } else if (p instanceof MaterialQuirurgico) {
                return "Material Quirúrgico";
            } else if (p instanceof Accesorio) {
                return "Accesorio/Juguete";
            } else {
                return "Producto";
            }
        }

        public ItemVenta(Servicio s) {
            this.id = String.valueOf(s.getIdServicio());
            this.nombre = s.getNombreServicio();
            this.tipo = "Servicio";
            this.codigoBarras = "";
            this.referencia = "";
            this.lote = "";
            this.cantidad = 1;
            this.valorUnitario = s.getPrecio();
            this.servicio = s;
        }

        // Getters
        public String getId() { return id; }
        public String getNombre() { return nombre; }
        public String getTipo() { return tipo; }
        public String getCodigoBarras() { return codigoBarras; }
        public String getReferencia() { return referencia; }
        public String getLote() { return lote; }
        public int getCantidad() { return cantidad; }
        public double getValorUnitario() { return valorUnitario; }
        public double getSubTotal() { return cantidad * valorUnitario; }
        public Producto getProducto() { return producto; }
        public Servicio getServicio() { return servicio; }

        public void setCantidad(int cantidad) { this.cantidad = cantidad; }
        public void aumentarCantidad() { this.cantidad++; }
        public void disminuirCantidad() { if (this.cantidad > 1) this.cantidad--; }
    }

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
        configurarTabla();

        if (scrollTabla != null) {
            scrollTabla.setFitToWidth(false);
            scrollTabla.setFitToHeight(true);
            scrollTabla.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            scrollTabla.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        }

        // Restaurar cliente y mascota previamente seleccionados
        restaurarClienteYMascota();

        // Restaurar items de venta previos (productos ya agregados)
        restaurarItemsVenta();

        if (lbCliente != null && clienteSeleccionado == null) lbCliente.setText("N/A");
        if (lbMascota != null && mascotaSeleccionada == null) lbMascota.setText("N/A");
        if (lbTotal != null) lbTotal.setText(currencyFormat.format(0));
        if (btnAgregarProcedimiento != null) btnAgregarProcedimiento.setDisable(false);

        // Cargar servicios pendientes si hay alguno
        cargarServiciosPendientes();

        // Actualizar el total después de restaurar todo
        actualizarTotal();
    }

    /**
     * Restaura el cliente y mascota previamente seleccionados desde ClienteTemporalData
     */
    private void restaurarClienteYMascota() {
        // Restaurar cliente
        Cliente clienteGuardado = ClienteTemporalData.getCliente();
        if (clienteGuardado != null) {
            clienteSeleccionado = clienteGuardado;
            if (lbCliente != null) {
                lbCliente.setText(clienteSeleccionado.getNombre() + " " +
                    clienteSeleccionado.getApellidos() + " - " + clienteSeleccionado.getCc());
            }
        }

        // Restaurar mascota
        Mascota mascotaGuardada = ClienteTemporalData.getMascota();
        if (mascotaGuardada != null) {
            mascotaSeleccionada = mascotaGuardada;
            if (lbMascota != null) {
                lbMascota.setText(mascotaSeleccionada.getNombre() + " - " + mascotaSeleccionada.getEspecie());
            }
        }
    }

    /**
     * Restaura los items de venta guardados previamente (productos y servicios ya agregados)
     */
    private void restaurarItemsVenta() {
        List<Object> itemsGuardados = ClienteTemporalData.getItemsVentaTemporales();

        if (!itemsGuardados.isEmpty()) {
            // Agregar cada item guardado a la lista actual
            for (Object obj : itemsGuardados) {
                if (obj instanceof ItemVenta) {
                    itemsVenta.add((ItemVenta) obj);
                }
            }

            // Limpiar los items temporales después de restaurarlos
            ClienteTemporalData.limpiarItemsVentaTemporales();

            // Refrescar la tabla
            tblProductos.refresh();
        }
    }

    private void cargarServiciosPendientes() {
        // Verificar si hay servicios que agregar desde AddSurgicalProcedure
        List<Servicio> serviciosPendientes = ClienteTemporalData.getServiciosParaVenta();

        if (!serviciosPendientes.isEmpty()) {
            // Agregar cada servicio a la venta
            for (Servicio servicio : serviciosPendientes) {
                agregarServicioAVenta(servicio);
            }

            // Limpiar los servicios pendientes
            ClienteTemporalData.limpiarServiciosParaVenta();

            // Mostrar mensaje de confirmación
            mostrarAlerta("Servicios agregados",
                "Se han agregado " + serviciosPendientes.size() + " servicio(s) a la venta.",
                Alert.AlertType.INFORMATION);
        }
    }

    private void agregarServicioAVenta(Servicio servicio) {
        // Verificar si ya existe en la lista
        for (ItemVenta item : itemsVenta) {
            if (item.getServicio() != null && item.getServicio().getIdServicio() == servicio.getIdServicio()) {
                item.aumentarCantidad();
                tblProductos.refresh();
                actualizarTotal();
                return;
            }
        }

        // Agregar nuevo item
        itemsVenta.add(new ItemVenta(servicio));
        tblProductos.refresh();
        actualizarTotal();
    }

    private void configurarTabla() {
        if (tblProductos == null) return;

        tblProductos.setItems(itemsVenta);
        tblProductos.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Configurar columnas
        colId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombre()));
        colTipoProducto.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTipo()));
        colCodigoBarras.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCodigoBarras()));
        colReferencia.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getReferencia()));
        colLote.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLote()));
        colCantidad.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getCantidad())));
        colValor.setCellValueFactory(data -> new SimpleStringProperty(currencyFormat.format(data.getValue().getValorUnitario())));
        colSubTotal.setCellValueFactory(data -> new SimpleStringProperty(currencyFormat.format(data.getValue().getSubTotal())));
    }

    private void actualizarTotal() {
        double total = itemsVenta.stream()
                .mapToDouble(ItemVenta::getSubTotal)
                .sum();
        if (lbTotal != null) {
            lbTotal.setText(currencyFormat.format(total));
        }
    }

    // ===== Utilidades =====
    private Stage safeStage() {
        if (root != null && root.getScene() != null) {
            return (Stage) root.getScene().getWindow();
        }
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

    // ===== Header (logo + menús) =====
    @FXML private void onLogoClick()         { goTo(MAINMENU_FXML); }
    @FXML private void onVerNotificaciones() { goTo(SEENOTIFICATIONS_FXML); }
    @FXML private void onDatosPersonales()   { goTo(PERSONALDATA_FXML); }
    @FXML private void onCerrarSesion()      { goTo(LOGIN_FXML); }

    // ===== Menú superior (módulos) =====
    @FXML private void onRegistrarProductos()     { goTo(REGISTER_PRODUCT_FXML); }
    @FXML private void onInventario()             { goTo(INVENTARY_FXML); }
    @FXML private void onGestionarUsuario()       { goTo(CREATE_USER_FXML); }
    @FXML private void onVisualizarRegistros()    { goTo(VISUALIZE_REGISTER_FXML); }
    @FXML private void onAgregarClientes()        { goTo(ADD_CLIENT_FXML); }
    @FXML private void onAdministrarProveedores() { goTo(ADD_SUPPLIERS_FXML); }

    // ===== SELECCIONAR CLIENTE =====
    @FXML
    private void onSeleccionarCliente() {
        try {
            List<Cliente> clientes = clienteRepository.findAll(1000, 0);

            if (clientes.isEmpty()) {
                mostrarAlerta("Sin clientes", "No hay clientes registrados en el sistema.", Alert.AlertType.WARNING);
                return;
            }

            Cliente seleccionado = mostrarDialogoSeleccionCliente(clientes);
            if (seleccionado != null) {
                clienteSeleccionado = seleccionado;
                lbCliente.setText(seleccionado.getNombre() + " " + seleccionado.getApellidos() + " - " + seleccionado.getCc());

                // GUARDAR el cliente en ClienteTemporalData para persistir la selección
                ClienteTemporalData.setCliente(clienteSeleccionado);

                // Resetear mascota
                mascotaSeleccionada = null;
                lbMascota.setText("N/A");
                ClienteTemporalData.setMascota(null);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar clientes: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private Cliente mostrarDialogoSeleccionCliente(List<Cliente> clientes) {
        Dialog<Cliente> dialog = new Dialog<>();
        dialog.setTitle("Seleccionar Cliente");
        dialog.setHeaderText("Seleccione un cliente de la lista");

        // Crear tabla
        TableView<Cliente> table = new TableView<>();
        table.setItems(FXCollections.observableArrayList(clientes));

        TableColumn<Cliente, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombre() + " " + data.getValue().getApellidos()));
        colNombre.setPrefWidth(300);

        TableColumn<Cliente, String> colCedula = new TableColumn<>("Cédula");
        colCedula.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCc()));
        colCedula.setPrefWidth(150);

        TableColumn<Cliente, String> colTelefono = new TableColumn<>("Teléfono");
        colTelefono.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTelefono()));
        colTelefono.setPrefWidth(150);

        table.getColumns().addAll(colNombre, colCedula, colTelefono);
        table.setPrefHeight(400);

        VBox content = new VBox(10, new Label("Doble clic para seleccionar:"), table);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        ButtonType btnSeleccionar = new ButtonType("Seleccionar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = ButtonType.CANCEL;
        dialog.getDialogPane().getButtonTypes().addAll(btnSeleccionar, btnCancelar);

        final Cliente[] seleccionado = {null};

        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && table.getSelectionModel().getSelectedItem() != null) {
                seleccionado[0] = table.getSelectionModel().getSelectedItem();
                dialog.setResult(seleccionado[0]);
                dialog.close();
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnSeleccionar) {
                return table.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }

    // ===== SELECCIONAR MASCOTA =====
    @FXML
    private void onSeleccionarMascota() {
        if (clienteSeleccionado == null) {
            mostrarAlerta("Cliente requerido", "Primero debe seleccionar un cliente.", Alert.AlertType.WARNING);
            return;
        }

        try {
            List<Mascota> mascotas = mascotaRepository.findByClienteId(clienteSeleccionado.getIdCliente());

            if (mascotas.isEmpty()) {
                mostrarAlerta("Sin mascotas", "Este cliente no tiene mascotas registradas.", Alert.AlertType.INFORMATION);
                return;
            }

            Mascota seleccionada = mostrarDialogoSeleccionMascota(mascotas);
            if (seleccionada != null) {
                mascotaSeleccionada = seleccionada;
                lbMascota.setText(seleccionada.getNombre() + " - " + seleccionada.getEspecie());

                // GUARDAR la mascota en ClienteTemporalData para persistir la selección
                ClienteTemporalData.setMascota(mascotaSeleccionada);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar mascotas: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private Mascota mostrarDialogoSeleccionMascota(List<Mascota> mascotas) {
        Dialog<Mascota> dialog = new Dialog<>();
        dialog.setTitle("Seleccionar Mascota");
        dialog.setHeaderText("Seleccione una mascota del cliente");

        TableView<Mascota> table = new TableView<>();
        table.setItems(FXCollections.observableArrayList(mascotas));

        TableColumn<Mascota, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombre()));
        colNombre.setPrefWidth(150);

        TableColumn<Mascota, String> colEspecie = new TableColumn<>("Especie");
        colEspecie.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEspecie()));
        colEspecie.setPrefWidth(120);

        TableColumn<Mascota, String> colRaza = new TableColumn<>("Raza");
        colRaza.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRaza()));
        colRaza.setPrefWidth(150);

        TableColumn<Mascota, String> colEdad = new TableColumn<>("Edad");
        colEdad.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getEdad())));
        colEdad.setPrefWidth(80);

        table.getColumns().addAll(colNombre, colEspecie, colRaza, colEdad);
        table.setPrefHeight(300);

        VBox content = new VBox(10, new Label("Doble clic para seleccionar:"), table);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        ButtonType btnSeleccionar = new ButtonType("Seleccionar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = ButtonType.CANCEL;
        dialog.getDialogPane().getButtonTypes().addAll(btnSeleccionar, btnCancelar);

        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && table.getSelectionModel().getSelectedItem() != null) {
                dialog.setResult(table.getSelectionModel().getSelectedItem());
                dialog.close();
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnSeleccionar) {
                return table.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }

    // ===== BUSCAR PRODUCTOS =====
    @FXML
    private void onBuscar() {
        String busqueda = tfBuscar.getText();
        if (busqueda == null || busqueda.trim().isEmpty()) {
            mostrarAlerta("Búsqueda vacía", "Por favor ingrese un término de búsqueda.", Alert.AlertType.WARNING);
            return;
        }

        try {
            // Llamar al método search con el orden correcto de parámetros
            // search(String q, String estadoFilter, String sort, boolean asc, int limit, int offset)
            List<Producto> productos = productoRepository.search(busqueda.trim(), null, "nombre", true, 100, 0);

            if (productos.isEmpty()) {
                mostrarAlerta("Sin resultados", "No se encontraron productos que coincidan con la búsqueda.", Alert.AlertType.INFORMATION);
                return;
            }

            Producto seleccionado = mostrarDialogoSeleccionProducto(productos);
            if (seleccionado != null) {
                agregarProductoAVenta(seleccionado);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al buscar productos: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private Producto mostrarDialogoSeleccionProducto(List<Producto> productos) {
        Dialog<Producto> dialog = new Dialog<>();
        dialog.setTitle("Seleccionar Producto");
        dialog.setHeaderText("Seleccione un producto de los resultados");

        TableView<Producto> table = new TableView<>();
        table.setItems(FXCollections.observableArrayList(productos));

        TableColumn<Producto, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombre()));
        colNombre.setPrefWidth(250);

        TableColumn<Producto, String> colRef = new TableColumn<>("Referencia");
        colRef.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getReferencia()));
        colRef.setPrefWidth(120);

        TableColumn<Producto, String> colCodigo = new TableColumn<>("Código Barras");
        colCodigo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCodigoBarras()));
        colCodigo.setPrefWidth(130);

        TableColumn<Producto, String> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getStock())));
        colStock.setPrefWidth(80);

        TableColumn<Producto, String> colPrecio = new TableColumn<>("Precio");
        colPrecio.setCellValueFactory(data -> new SimpleStringProperty(currencyFormat.format(data.getValue().getPrecio())));
        colPrecio.setPrefWidth(120);

        table.getColumns().addAll(colNombre, colRef, colCodigo, colStock, colPrecio);
        table.setPrefHeight(400);

        VBox content = new VBox(10, new Label("Doble clic para agregar:"), table);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        ButtonType btnSeleccionar = new ButtonType("Agregar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = ButtonType.CANCEL;
        dialog.getDialogPane().getButtonTypes().addAll(btnSeleccionar, btnCancelar);

        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && table.getSelectionModel().getSelectedItem() != null) {
                dialog.setResult(table.getSelectionModel().getSelectedItem());
                dialog.close();
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnSeleccionar) {
                return table.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }

    private void agregarProductoAVenta(Producto producto) {
        // Verificar si ya existe en la lista
        for (ItemVenta item : itemsVenta) {
            if (item.getProducto() != null && item.getProducto().getIdProducto() == producto.getIdProducto()) {
                item.aumentarCantidad();
                tblProductos.refresh();
                actualizarTotal();
                return;
            }
        }

        // Agregar nuevo item
        itemsVenta.add(new ItemVenta(producto));
        tblProductos.refresh();
        actualizarTotal();
    }

    // ===== AGREGAR SERVICIO =====
    @FXML
    private void onAgregarProcedimiento() {
        if (clienteSeleccionado == null) {
            mostrarAlerta("Cliente requerido", "Primero debe seleccionar un cliente para agregar un servicio.", Alert.AlertType.WARNING);
            return;
        }

        // Guardar los items de venta actuales ANTES de navegar
        ClienteTemporalData.setItemsVentaTemporales(new ArrayList<>(itemsVenta));

        // Guardar datos temporales del cliente y mascota
        ClienteTemporalData.setCliente(clienteSeleccionado);
        ClienteTemporalData.setMascota(mascotaSeleccionada);

        // Navegar a la vista de añadir servicios
        goTo(ADD_SURGICAL_PROCEDURES_FXML);
    }

    @FXML private void onAgregarServicio() {
        if (clienteSeleccionado == null) {
            mostrarAlerta("Cliente requerido", "Primero debe seleccionar un cliente para agregar un servicio.", Alert.AlertType.WARNING);
            return;
        }

        // Guardar los items de venta actuales ANTES de navegar
        ClienteTemporalData.setItemsVentaTemporales(new ArrayList<>(itemsVenta));

        // Guardar datos temporales del cliente y mascota
        ClienteTemporalData.setCliente(clienteSeleccionado);
        ClienteTemporalData.setMascota(mascotaSeleccionada);

        goTo(ADD_SERVICES_FXML);
    }

    // ===== FILTRAR =====
    @FXML
    private void onFiltrar() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Filtrar por tipo de producto");
        dialog.setHeaderText("Seleccione el tipo de producto a mostrar");

        ComboBox<String> cbTipo = new ComboBox<>();
        cbTipo.getItems().addAll("Todos", "Servicio", "Alimento", "Medicamento", "Material Quirúrgico", "Accesorio/Juguete");
        cbTipo.setValue("Todos");
        cbTipo.setPrefWidth(300);

        VBox content = new VBox(10, new Label("Tipo de producto:"), cbTipo);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        ButtonType btnFiltrar = new ButtonType("Filtrar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = ButtonType.CANCEL;
        dialog.getDialogPane().getButtonTypes().addAll(btnFiltrar, btnCancelar);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnFiltrar) {
                return cbTipo.getValue();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(tipo -> {
            if ("Todos".equals(tipo)) {
                tblProductos.setItems(itemsVenta);
            } else if ("Servicio".equals(tipo)) {
                // Mostrar solo servicios
                ObservableList<ItemVenta> filtrados = FXCollections.observableArrayList();
                for (ItemVenta item : itemsVenta) {
                    if ("Servicio".equals(item.getTipo())) {
                        filtrados.add(item);
                    }
                }
                tblProductos.setItems(filtrados);
            } else {
                // Filtrar por tipo de producto específico y buscar en BD
                filtrarPorTipoProducto(tipo);
            }
        });
    }

    private void filtrarPorTipoProducto(String nombreTipo) {
        try {
            // Buscar productos DIRECTAMENTE en la base de datos usando repositorios especializados
            List<Producto> productosDelTipo = buscarProductosPorTipoEnBD(nombreTipo);

            if (productosDelTipo.isEmpty()) {
                mostrarAlerta("Sin resultados",
                    "No hay productos del tipo '" + nombreTipo + "' en la base de datos.",
                    Alert.AlertType.INFORMATION);
                return;
            }

            // Mostrar diálogo con los productos encontrados
            mostrarDialogoProductosPorTipo(productosDelTipo, nombreTipo);

        } catch (Exception e) {
            mostrarAlerta("Error", "Error al buscar productos por tipo: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private List<Producto> buscarProductosPorTipoEnBD(String nombreTipo) throws SQLException {
        List<Producto> productos = new ArrayList<>();

        switch (nombreTipo) {
            case "Alimento":
                // AlimentoRepository.search tiene 12 parámetros: q, estadoFilter, lote, fechaVencimientoDesde, fechaVencimientoHasta, fraccionable, stockMinimo, stockMaximo, sort, asc, limit, offset
                List<Alimento> alimentos = alimentoRepository.search(
                    "", null, null, null, null, null, null, null, "nombre", true, 1000, 0
                );
                productos.addAll(alimentos);
                break;
            case "Medicamento":
                // MedicamentoRepository.search tiene 12 parámetros: q, estadoFilter, lote, fechaVencimientoDesde, fechaVencimientoHasta, fraccionable, stockMinimo, stockMaximo, sort, asc, limit, offset
                List<Medicamento> medicamentos = medicamentoRepository.search(
                    "", null, null, null, null, null, null, null, "nombre", true, 1000, 0
                );
                productos.addAll(medicamentos);
                break;
            case "Material Quirúrgico":
                // MaterialQuirurgicoRepository.search tiene 12 parámetros: q, estadoFilter, lote, fechaVencimientoDesde, fechaVencimientoHasta, fraccionable, stockMinimo, stockMaximo, sort, asc, limit, offset
                List<MaterialQuirurgico> materiales = materialQuirurgicoRepository.search(
                    "", null, null, null, null, null, null, null, "nombre", true, 1000, 0
                );
                productos.addAll(materiales);
                break;
            case "Accesorio/Juguete":
                // AccesorioRepository.search tiene 8 parámetros: q, estadoFilter, stockMinimo, stockMaximo, sort, asc, limit, offset
                List<Accesorio> accesorios = accesorioRepository.search(
                    "", null, null, null, "nombre", true, 1000, 0
                );
                productos.addAll(accesorios);
                break;
        }

        return productos;
    }

    private void mostrarDialogoProductosPorTipo(List<Producto> productos, String nombreTipo) {
        Dialog<Producto> dialog = new Dialog<>();
        dialog.setTitle("Productos tipo: " + nombreTipo);
        dialog.setHeaderText("Seleccione un producto para agregar a la venta (" + productos.size() + " encontrados)");

        TableView<Producto> table = new TableView<>();
        table.setItems(FXCollections.observableArrayList(productos));

        TableColumn<Producto, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombre()));
        colNombre.setPrefWidth(250);

        TableColumn<Producto, String> colRef = new TableColumn<>("Referencia");
        colRef.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getReferencia()));
        colRef.setPrefWidth(120);

        TableColumn<Producto, String> colCodigo = new TableColumn<>("Código Barras");
        colCodigo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCodigoBarras()));
        colCodigo.setPrefWidth(130);

        TableColumn<Producto, String> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getStock())));
        colStock.setPrefWidth(80);

        TableColumn<Producto, String> colPrecio = new TableColumn<>("Precio");
        colPrecio.setCellValueFactory(data -> new SimpleStringProperty(currencyFormat.format(data.getValue().getPrecio())));
        colPrecio.setPrefWidth(120);

        table.getColumns().addAll(colNombre, colRef, colCodigo, colStock, colPrecio);
        table.setPrefHeight(400);

        VBox contentBox = new VBox(10, new Label("Doble clic para agregar:"), table);
        contentBox.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(contentBox);

        ButtonType btnAgregar = new ButtonType("Agregar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = ButtonType.CANCEL;
        dialog.getDialogPane().getButtonTypes().addAll(btnAgregar, btnCancelar);

        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && table.getSelectionModel().getSelectedItem() != null) {
                Producto seleccionado = table.getSelectionModel().getSelectedItem();
                agregarProductoAVenta(seleccionado);
                dialog.close();
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnAgregar) {
                return table.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(this::agregarProductoAVenta);
    }

    // ===== LIMPIAR =====
    @FXML
    private void onLimpiar() {
        tfBuscar.clear();
        tblProductos.setItems(itemsVenta);
    }

    // ===== AUMENTAR CANTIDAD =====
    @FXML
    private void onAumentarSeleccion() {
        ItemVenta seleccionado = tblProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Sin selección", "Por favor seleccione un item de la tabla.", Alert.AlertType.WARNING);
            return;
        }

        seleccionado.aumentarCantidad();
        tblProductos.refresh();
        actualizarTotal();
    }

    // ===== DISMINUIR CANTIDAD =====
    @FXML
    private void onDisminuirSeleccion() {
        ItemVenta seleccionado = tblProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Sin selección", "Por favor seleccione un item de la tabla.", Alert.AlertType.WARNING);
            return;
        }

        seleccionado.disminuirCantidad();
        tblProductos.refresh();
        actualizarTotal();
    }

    // ===== ELIMINAR ITEM =====
    @FXML
    private void onEliminarSeleccion() {
        ItemVenta seleccionado = tblProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Sin selección", "Por favor seleccione un item de la tabla.", Alert.AlertType.WARNING);
            return;
        }

        itemsVenta.remove(seleccionado);
        tblProductos.refresh();
        actualizarTotal();
    }

    // ===== VENDER =====
    @FXML
    private void onVender() {
        // Validaciones
        if (clienteSeleccionado == null) {
            mostrarAlerta("Cliente requerido", "Debe seleccionar un cliente antes de realizar la venta.", Alert.AlertType.WARNING);
            return;
        }

        if (itemsVenta.isEmpty()) {
            mostrarAlerta("Venta vacía", "Debe agregar al menos un producto o servicio a la venta.", Alert.AlertType.WARNING);
            return;
        }

        // Confirmar venta
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Venta");
        confirmacion.setHeaderText("¿Desea realizar esta venta?");
        confirmacion.setContentText("Cliente: " + clienteSeleccionado.getNombre() + " " + clienteSeleccionado.getApellidos() +
                "\nTotal: " + lbTotal.getText());

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                realizarVenta();
            }
        });
    }

    private void realizarVenta() {
        try {
            // Crear venta
            Venta venta = new Venta();
            venta.setComprador(clienteSeleccionado);
            venta.setFecha(new Date());

            // Calcular total
            double total = itemsVenta.stream()
                    .mapToDouble(ItemVenta::getSubTotal)
                    .sum();
            venta.setTotalVenta(total);

            // Crear líneas de venta
            List<LineaVenta> lineas = new ArrayList<>();
            for (ItemVenta item : itemsVenta) {
                LineaVenta linea = new LineaVenta();
                linea.setProducto(item.getProducto());
                linea.setServicio(item.getServicio());
                linea.setCantidad(item.getCantidad());
                linea.setValor(item.getValorUnitario());
                linea.setsubTotal(item.getSubTotal());
                lineas.add(linea);
            }
            venta.setLineasVenta(lineas);

            // Guardar venta
            int idVenta = ventaService.crearVenta(venta);

            if (idVenta > 0) {
                mostrarAlerta("Venta realizada",
                        "La venta se ha registrado exitosamente.\nID de venta: " + idVenta,
                        Alert.AlertType.INFORMATION);

                // Limpiar formulario
                limpiarFormulario();
            } else {
                mostrarAlerta("Error", "No se pudo registrar la venta.", Alert.AlertType.ERROR);
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al realizar la venta: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void limpiarFormulario() {
        // NO limpiar clienteSeleccionado ni mascotaSeleccionada para mantenerlos después de la venta
        // clienteSeleccionado = null;
        // mascotaSeleccionada = null;

        // Solo limpiar los items de la venta
        itemsVenta.clear();

        // NO limpiar los labels del cliente y mascota, mantenerlos visibles
        // lbCliente.setText("N/A");
        // lbMascota.setText("N/A");

        lbTotal.setText(currencyFormat.format(0));
        tfBuscar.clear();
        tblProductos.setItems(itemsVenta);
        tblProductos.refresh();

        // Los datos de cliente y mascota permanecen en ClienteTemporalData
        // para la siguiente venta del mismo cliente
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

