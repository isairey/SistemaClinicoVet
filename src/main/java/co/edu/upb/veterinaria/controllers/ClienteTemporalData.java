package co.edu.upb.veterinaria.controllers;

import co.edu.upb.veterinaria.models.ModeloCliente.Cliente;
import co.edu.upb.veterinaria.models.ModeloMascota.Mascota;
import co.edu.upb.veterinaria.models.ModeloServicio.Servicio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase para almacenar temporalmente los datos del cliente mientras se navega entre vistas.
 */
public class ClienteTemporalData {

    private static String tipoPersona;
    private static String tipoDocumento;
    private static String documento;
    private static String nombre;
    private static String apellidos;
    private static LocalDate fechaNacimiento;
    private static String ciudad;
    private static String email;
    private static String direccion;
    private static String telefono;
    private static String contactoNombre;
    private static String contactoTelefono;

    // Lista de mascotas añadidas temporalmente (solo las recién creadas)
    private static final List<Mascota> mascotasSeleccionadas = new ArrayList<>();

    // Para funcionalidad de ventas - almacenar cliente y mascota completos
    private static Cliente clienteActual = null;
    private static Mascota mascotaActual = null;

    // Para almacenar servicios seleccionados desde AddSurgicalProcedure
    private static final List<Servicio> serviciosParaVenta = new ArrayList<>();
    private static String observacionesServicio = null;

    // Para almacenar items de venta temporales (productos y servicios ya agregados)
    private static final List<Object> itemsVentaTemporales = new ArrayList<>();

    public static void guardarDatos(String tipoPersona, String tipoDocumento, String documento,
                                     String nombre, String apellidos, LocalDate fechaNacimiento,
                                     String ciudad, String email, String direccion, String telefono,
                                     String contactoNombre, String contactoTelefono) {
        ClienteTemporalData.tipoPersona = tipoPersona;
        ClienteTemporalData.tipoDocumento = tipoDocumento;
        ClienteTemporalData.documento = documento;
        ClienteTemporalData.nombre = nombre;
        ClienteTemporalData.apellidos = apellidos;
        ClienteTemporalData.fechaNacimiento = fechaNacimiento;
        ClienteTemporalData.ciudad = ciudad;
        ClienteTemporalData.email = email;
        ClienteTemporalData.direccion = direccion;
        ClienteTemporalData.telefono = telefono;
        ClienteTemporalData.contactoNombre = contactoNombre;
        ClienteTemporalData.contactoTelefono = contactoTelefono;
    }

    public static void limpiar() {
        tipoPersona = null;
        tipoDocumento = null;
        documento = null;
        nombre = null;
        apellidos = null;
        fechaNacimiento = null;
        ciudad = null;
        email = null;
        direccion = null;
        telefono = null;
        contactoNombre = null;
        contactoTelefono = null;
        mascotasSeleccionadas.clear();
        clienteActual = null;
        mascotaActual = null;
        serviciosParaVenta.clear();
        observacionesServicio = null;
        itemsVentaTemporales.clear();
    }

    public static boolean tieneDatos() {
        return nombre != null || documento != null;
    }

    // Getters para datos de cliente
    public static String getTipoPersona() { return tipoPersona; }
    public static String getTipoDocumento() { return tipoDocumento; }
    public static String getDocumento() { return documento; }
    public static String getNombre() { return nombre; }
    public static String getApellidos() { return apellidos; }
    public static LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public static String getCiudad() { return ciudad; }
    public static String getEmail() { return email; }
    public static String getDireccion() { return direccion; }
    public static String getTelefono() { return telefono; }
    public static String getContactoNombre() { return contactoNombre; }
    public static String getContactoTelefono() { return contactoTelefono; }

    // Métodos para manejar mascotas temporales
    public static List<Mascota> getMascotasSeleccionadas() {
        return new ArrayList<>(mascotasSeleccionadas);
    }

    public static void setMascotasSeleccionadas(List<Mascota> mascotas) {
        mascotasSeleccionadas.clear();
        if (mascotas != null) {
            mascotasSeleccionadas.addAll(mascotas);
        }
    }

    public static void agregarMascota(Mascota mascota) {
        if (mascota != null && !mascotasSeleccionadas.contains(mascota)) {
            mascotasSeleccionadas.add(mascota);
        }
    }

    public static void removerMascotaPorId(int idMascota) {
        mascotasSeleccionadas.removeIf(m -> m.getIdMascota() == idMascota);
    }

    // ===== MÉTODOS PARA VENTAS =====

    /**
     * Establece el cliente actual para operaciones de venta/servicios
     */
    public static void setCliente(Cliente cliente) {
        clienteActual = cliente;
    }

    /**
     * Obtiene el cliente actual
     */
    public static Cliente getCliente() {
        return clienteActual;
    }

    /**
     * Establece la mascota actual para operaciones de venta/servicios
     */
    public static void setMascota(Mascota mascota) {
        mascotaActual = mascota;
    }

    /**
     * Obtiene la mascota actual
     */
    public static Mascota getMascota() {
        return mascotaActual;
    }

    /**
     * Establece los servicios seleccionados para agregar a la venta
     */
    public static void setServiciosSeleccionados(List<Servicio> servicios) {
        serviciosParaVenta.clear();
        if (servicios != null) {
            serviciosParaVenta.addAll(servicios);
        }
    }

    /**
     * Obtiene los servicios seleccionados para la venta
     */
    public static List<Servicio> getServiciosParaVenta() {
        return new ArrayList<>(serviciosParaVenta);
    }

    /**
     * Limpia los servicios para venta
     */
    public static void limpiarServiciosParaVenta() {
        serviciosParaVenta.clear();
        observacionesServicio = null;
    }

    /**
     * Establece las observaciones del servicio
     */
    public static void setObservacionesServicio(String observaciones) {
        observacionesServicio = observaciones;
    }

    /**
     * Obtiene las observaciones del servicio
     */
    public static String getObservacionesServicio() {
        return observacionesServicio;
    }

    /**
     * Guarda los items de venta temporalmente (productos y servicios ya agregados)
     */
    public static void setItemsVentaTemporales(List<?> items) {
        itemsVentaTemporales.clear();
        if (items != null) {
            itemsVentaTemporales.addAll(items);
        }
    }

    /**
     * Obtiene los items de venta guardados temporalmente
     */
    public static List<Object> getItemsVentaTemporales() {
        return new ArrayList<>(itemsVentaTemporales);
    }

    /**
     * Limpia los items de venta temporales
     */
    public static void limpiarItemsVentaTemporales() {
        itemsVentaTemporales.clear();
    }
}
