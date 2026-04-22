package co.edu.upb.veterinaria.models.ModeloReporteVenta;

import co.edu.upb.veterinaria.models.ModeloVenta.Venta;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReporteVenta {
    private int idReporteVenta;
    private List<Venta> ventas;
    private Date fechaInicio;
    private Date fechaFin;
    private double precioVentas;

    public ReporteVenta() { }

    public ReporteVenta(int idReporteVenta, List<Venta> ventas,
                        Date fechaInicio, Date fechaFin, double precioVentas) {
        this.idReporteVenta = idReporteVenta;
        this.ventas = (ventas != null) ? ventas : new ArrayList<>();
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.precioVentas = precioVentas;
    }

    public int getIdReporteVenta() { return idReporteVenta; }
    public void setIdReporteVenta(int idReporteVenta) { this.idReporteVenta = idReporteVenta; }

    public List<Venta> getVentas() { return ventas; }
    public void setVentas(List<Venta> ventas) { this.ventas = ventas; }

    public Date getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(Date fechaInicio) { this.fechaInicio = fechaInicio; }

    public Date getFechaFin() { return fechaFin; }
    public void setFechaFin(Date fechaFin) { this.fechaFin = fechaFin; }

    public double getPrecioVentas() { return precioVentas; }
    public void setPrecioVentas(double precioVentas) { this.precioVentas = precioVentas; }
}
