package co.edu.upb.veterinaria.services.ServicioLineaVenta;

import co.edu.upb.veterinaria.models.ModeloLineaVenta.LineaVenta;
import co.edu.upb.veterinaria.models.ModeloProducto.Producto;
import co.edu.upb.veterinaria.models.ModeloServicio.Servicio;
import co.edu.upb.veterinaria.repositories.RepositorioLineaVenta.LineaVentaRepository;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * Servicio para gestionar las líneas de venta.
 * Una LineaVenta es como un "nodo" dentro de la estructura de una Venta (lista enlazada).
 * Cada línea puede contener un producto o un servicio, con su cantidad y subtotal.
 */
public class LineaVentaService {

    private final LineaVentaRepository repository;

    /**
     * Constructor que inicializa el servicio con el repositorio.
     * @param dataSource Fuente de datos para la conexión a la base de datos
     */
    public LineaVentaService(DataSource dataSource) {
        this.repository = new LineaVentaRepository(dataSource);
    }

    /**
     * Crea una nueva línea de venta para un producto.
     * @param producto Producto a agregar
     * @param cantidad Cantidad del producto
     * @param idVenta ID de la venta a la que pertenece
     * @return ID de la línea de venta creada
     * @throws SQLException Si hay error en la base de datos
     */
    public int crearLineaVentaProducto(Producto producto, int cantidad, int idVenta) throws SQLException {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        LineaVenta linea = new LineaVenta();
        linea.setProducto(producto);
        linea.setCantidad(cantidad);
        linea.setValor(producto.getPrecio());

        // Calcular el subtotal: precio unitario * cantidad
        double subtotal = producto.getPrecio() * cantidad;
        linea.setsubTotal(subtotal);

        return repository.create(linea, idVenta);
    }

    /**
     * Crea una nueva línea de venta para un servicio.
     * @param servicio Servicio a agregar
     * @param cantidad Cantidad del servicio (normalmente 1)
     * @param idVenta ID de la venta a la que pertenece
     * @return ID de la línea de venta creada
     * @throws SQLException Si hay error en la base de datos
     */
    public int crearLineaVentaServicio(Servicio servicio, int cantidad, int idVenta) throws SQLException {
        if (servicio == null) {
            throw new IllegalArgumentException("El servicio no puede ser nulo");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        LineaVenta linea = new LineaVenta();
        linea.setServicio(servicio);
        linea.setCantidad(cantidad);
        linea.setValor(servicio.getPrecio());

        // Calcular el subtotal: precio del servicio * cantidad
        double subtotal = servicio.getPrecio() * cantidad;
        linea.setsubTotal(subtotal);

        return repository.create(linea, idVenta);
    }

    /**
     * Obtiene todas las líneas de venta de una venta específica.
     * @param idVenta ID de la venta
     * @return Lista de líneas de venta
     * @throws SQLException Si hay error en la base de datos
     */
    public List<LineaVenta> obtenerLineasPorVenta(int idVenta) throws SQLException {
        return repository.findByVenta(idVenta);
    }

    /**
     * Calcula el total de una venta sumando todos los subtotales de sus líneas.
     * @param idVenta ID de la venta
     * @return Total de la venta
     * @throws SQLException Si hay error en la base de datos
     */
    public double calcularTotalVenta(int idVenta) throws SQLException {
        List<LineaVenta> lineas = repository.findByVenta(idVenta);
        return lineas.stream()
                .mapToDouble(LineaVenta::getsubTotal)
                .sum();
    }

    /**
     * Valida que una línea de venta tenga datos consistentes.
     * @param linea Línea de venta a validar
     * @return true si es válida, false en caso contrario
     */
    public boolean validarLineaVenta(LineaVenta linea) {
        if (linea == null) {
            return false;
        }

        // Una línea debe tener producto O servicio, no ambos ni ninguno
        boolean tieneProducto = linea.getProducto() != null;
        boolean tieneServicio = linea.getServicio() != null;

        if (tieneProducto && tieneServicio) {
            return false; // No puede tener ambos
        }

        if (!tieneProducto && !tieneServicio) {
            return false; // Debe tener al menos uno
        }

        // Validar cantidad y valores
        if (linea.getCantidad() <= 0) {
            return false;
        }

        if (linea.getValor() < 0 || linea.getsubTotal() < 0) {
            return false;
        }

        return true;
    }

    /**
     * Calcula el subtotal de una línea de venta.
     * @param valorUnitario Precio unitario del producto/servicio
     * @param cantidad Cantidad
     * @return Subtotal calculado
     */
    public double calcularSubtotal(double valorUnitario, int cantidad) {
        if (valorUnitario < 0 || cantidad <= 0) {
            throw new IllegalArgumentException("Valores inválidos para calcular subtotal");
        }
        return valorUnitario * cantidad;
    }

    /**
     * Crea una línea de venta genérica (para usar en interfaz antes de guardar).
     * @param producto Producto (puede ser null si es servicio)
     * @param servicio Servicio (puede ser null si es producto)
     * @param cantidad Cantidad
     * @return LineaVenta creada (sin guardar en BD)
     */
    public LineaVenta crearLineaVentaTemporal(Producto producto, Servicio servicio, int cantidad) {
        LineaVenta linea = new LineaVenta();

        if (producto != null) {
            linea.setProducto(producto);
            linea.setValor(producto.getPrecio());
            linea.setsubTotal(producto.getPrecio() * cantidad);
        } else if (servicio != null) {
            linea.setServicio(servicio);
            linea.setValor(servicio.getPrecio());
            linea.setsubTotal(servicio.getPrecio() * cantidad);
        }

        linea.setCantidad(cantidad);

        return linea;
    }

    /**
     * Guarda una línea de venta ya creada.
     * @param linea Línea de venta a guardar
     * @param idVenta ID de la venta a la que pertenece
     * @return ID de la línea de venta creada
     * @throws SQLException Si hay error en la base de datos
     */
    public int guardarLineaVenta(LineaVenta linea, int idVenta) throws SQLException {
        if (!validarLineaVenta(linea)) {
            throw new IllegalArgumentException("La línea de venta no es válida");
        }
        return repository.create(linea, idVenta);
    }

    /**
     * Actualiza la cantidad de una línea de venta y recalcula el subtotal.
     * @param linea Línea de venta a actualizar
     * @param nuevaCantidad Nueva cantidad
     */
    public void actualizarCantidad(LineaVenta linea, int nuevaCantidad) {
        if (nuevaCantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        linea.setCantidad(nuevaCantidad);
        double nuevoSubtotal = linea.getValor() * nuevaCantidad;
        linea.setsubTotal(nuevoSubtotal);
    }

    /**
     * Verifica si una línea de venta es de tipo producto.
     * @param linea Línea de venta
     * @return true si es producto, false si es servicio
     */
    public boolean esProducto(LineaVenta linea) {
        return linea != null && linea.getProducto() != null;
    }

    /**
     * Verifica si una línea de venta es de tipo servicio.
     * @param linea Línea de venta
     * @return true si es servicio, false si es producto
     */
    public boolean esServicio(LineaVenta linea) {
        return linea != null && linea.getServicio() != null;
    }

    /**
     * Obtiene el nombre del item (producto o servicio) de una línea de venta.
     * @param linea Línea de venta
     * @return Nombre del item
     */
    public String obtenerNombreItem(LineaVenta linea) {
        if (linea == null) {
            return "";
        }

        if (linea.getProducto() != null) {
            return linea.getProducto().getNombre();
        } else if (linea.getServicio() != null) {
            return linea.getServicio().getNombreServicio();
        }

        return "";
    }

    /**
     * Obtiene el tipo de item (Producto o Servicio).
     * @param linea Línea de venta
     * @return "Producto" o "Servicio"
     */
    public String obtenerTipoItem(LineaVenta linea) {
        if (linea == null) {
            return "";
        }

        return linea.getProducto() != null ? "Producto" : "Servicio";
    }
}
