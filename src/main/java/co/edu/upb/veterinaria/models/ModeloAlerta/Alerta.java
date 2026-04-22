package co.edu.upb.veterinaria.models.ModeloAlerta;

import co.edu.upb.veterinaria.models.ModeloProducto.Producto;

import java.util.Date;

public class Alerta {
    private int idAlerta;
    private boolean pendiente;
    private String motivo;
    private Date fechaObjetivo;
    private Date fechaCreacion;
    private Producto producto;

    public Alerta() { }

    public Alerta(int idAlerta, boolean pendiente, String motivo,
                  Date fechaObjetivo, Date fechaCreacion, Producto producto) {
        this.idAlerta = idAlerta;
        this.pendiente = pendiente;
        this.motivo = motivo;
        this.fechaObjetivo = fechaObjetivo;
        this.fechaCreacion = fechaCreacion;
        this.producto = producto;
    }

    public int getIdAlerta() { return idAlerta; }
    public void setIdAlerta(int idAlerta) { this.idAlerta = idAlerta; }

    public boolean isPendiente() { return pendiente; }
    public void setPendiente(boolean pendiente) { this.pendiente = pendiente; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Date getFechaObjetivo() { return fechaObjetivo; }
    public void setFechaObjetivo(Date fechaObjetivo) { this.fechaObjetivo = fechaObjetivo; }

    public Date getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

}
