package co.edu.upb.veterinaria.models.ModeloLineaVenta;

import co.edu.upb.veterinaria.models.ModeloProducto.Producto;
import co.edu.upb.veterinaria.models.ModeloServicio.Servicio;

public class LineaVenta {
    private int idLineaVenta;
    private Producto producto;
    private int cantidad;
    private double subTotal;
    private Servicio servicio;
    private double valor;

    public LineaVenta() { }

    public LineaVenta(int idLineaVenta, Producto producto, int cantidad, double subTotal, Servicio servicio, double valor) {
        this.idLineaVenta = idLineaVenta;
        this.producto = producto;
        this.cantidad = cantidad;
        this.subTotal = subTotal;
        this.servicio = servicio;
        this.valor = valor;
    }

    public int getIdLineaVenta() { return idLineaVenta; }
    public void setIdLineaVenta(int idLinea) { this.idLineaVenta = idLineaVenta; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getsubTotal() { return subTotal; }
    public void setsubTotal(double subTotal) { this.subTotal = subTotal; }

    public Servicio getServicio() { return servicio; }
    public void setServicio(Servicio servicio) { this.servicio = servicio;}

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
}
