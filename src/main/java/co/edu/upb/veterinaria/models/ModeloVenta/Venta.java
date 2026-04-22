package co.edu.upb.veterinaria.models.ModeloVenta;

import co.edu.upb.veterinaria.models.ModeloCliente.Cliente;
import co.edu.upb.veterinaria.models.ModeloLineaVenta.LineaVenta;
import co.edu.upb.veterinaria.models.ModeloUsuario.Usuario;

import java.util.Date;
import java.util.List;

public class Venta {
    private int idVenta;
    private Cliente comprador;
    private Date fecha;
    private double totalVenta;
    private List<LineaVenta> lineasVenta;
    private Usuario usuario;

    public Venta() { }

    public Venta(int idVenta, Cliente comprador, Date fecha,
                 double totalVenta, List<LineaVenta> lineasVenta, Usuario usuario) {
        this.idVenta = idVenta;
        this.comprador = comprador;
        this.fecha = fecha;
        this.totalVenta = totalVenta;
        this.lineasVenta = lineasVenta;
        this.usuario = usuario;
    }

    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }

    public Cliente getComprador() { return comprador; }
    public void setComprador(Cliente comprador) { this.comprador = comprador; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public double getTotalVenta() { return totalVenta; }
    public void setTotalVenta(double totalVenta) { this.totalVenta = totalVenta; }

    public List<LineaVenta> getLineasVenta() { return lineasVenta; }
    public void setLineasVenta(List<LineaVenta> lineasVenta) { this.lineasVenta = lineasVenta; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
