package co.edu.upb.veterinaria.models.ModeloMedicamento;

import co.edu.upb.veterinaria.models.ModeloMarca.Marca;
import co.edu.upb.veterinaria.models.ModeloUnidadMedida.UnidadMedida;
import co.edu.upb.veterinaria.models.ModeloProducto.Producto;
import co.edu.upb.veterinaria.models.ModeloProveedor.Proveedor;
import co.edu.upb.veterinaria.models.ModeloUsuario.Usuario;

import java.util.Date;

public class Medicamento extends Producto {

    private String lote;
    private Date fechaVencimiento;
    private int semanasParaAlerta;
    private boolean fraccionable;
    private boolean fraccionado;
    private double contenido;
    private UnidadMedida unidadMedida;
    private double dosisPorUnidad;

    public Medicamento() { }

    public Medicamento(
            int idProducto, String nombre, String referencia, String codigoBarras, double precio, double costo,
            Marca marca, String descripcion, int stock, byte[] imagenProducto, String estado, Proveedor proveedor, Usuario usuario,
            String lote, Date fechaVencimiento, int semanasParaAlerta,
            boolean fraccionable, boolean fraccionado, double contenido,
            UnidadMedida unidadMedida, double dosisPorUnidad) {

        super(idProducto, nombre, referencia, codigoBarras, precio, costo,
                marca, descripcion, stock, imagenProducto, estado, proveedor, usuario);

        this.lote = lote;
        this.fechaVencimiento = fechaVencimiento;
        this.semanasParaAlerta = semanasParaAlerta;
        this.fraccionable = fraccionable;
        this.fraccionado = fraccionado;
        this.contenido = contenido;
        this.unidadMedida = unidadMedida;
        this.dosisPorUnidad = dosisPorUnidad;
    }

    public String getLote() {
        return lote;
    }
    public void setLote(String lote) {
        this.lote = lote;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }
    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public int getSemanasParaAlerta() {
        return semanasParaAlerta;
    }
    public void setSemanasParaAlerta(int semanasParaAlerta) {
        this.semanasParaAlerta = semanasParaAlerta;
    }

    public boolean isFraccionable() {
        return fraccionable;
    }
    public void setFraccionable(boolean fraccionable) {
        this.fraccionable = fraccionable;
    }

    public boolean isFraccionado() {
        return fraccionado;
    }
    public void setFraccionado(boolean fraccionado) {
        this.fraccionado = fraccionado;
    }

    public double getContenido() {
        return contenido;
    }
    public void setContenido(double contenido) {
        this.contenido = contenido;
    }

    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }
    public void setUnidadmedida(UnidadMedida unidadMedida){
        this.unidadMedida = unidadMedida;
    }

    public double getDosisPorUnidad() {
        return dosisPorUnidad;
    }
    public void setDosisPorUnidad(double dosisPorUnidad) {
        this.dosisPorUnidad = dosisPorUnidad;
    }

}
