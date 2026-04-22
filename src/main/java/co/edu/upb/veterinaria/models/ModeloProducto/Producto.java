package co.edu.upb.veterinaria.models.ModeloProducto;

import co.edu.upb.veterinaria.models.ModeloMarca.Marca;
import co.edu.upb.veterinaria.models.ModeloProveedor.Proveedor;
import co.edu.upb.veterinaria.models.ModeloUsuario.Usuario;

public class Producto {

    private int idProducto;
    private String nombre;
    private String referencia;
    private String codigoBarras;
    private double precio;
    private double costo;
    private Marca marca;
    private String descripcion;
    private int stock;
    private byte[] imagenProducto;
    private String estado;
    private Proveedor proveedor;
    private Usuario usuario;


    public Producto(){ }

    public Producto(int idProducto, String nombre, String referencia, String codigoBarras, double precio, double costo,
                    Marca marca, String descripcion, int stock, byte[] imagenProducto, String estado, Proveedor proveedor, Usuario usuario) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.referencia = referencia;
        this.codigoBarras = codigoBarras;
        this.precio = precio;
        this.costo = costo;
        this.marca = marca;
        this.descripcion = descripcion;
        this.stock = stock;
        this.imagenProducto = imagenProducto;
        this.estado = estado;
        this.proveedor = proveedor;
        this.usuario = usuario;

    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        if (precio < 0) throw new IllegalArgumentException("El precio no puede ser negativo");
        this.precio = precio;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        if (costo < 0) throw new IllegalArgumentException("El costo no puede ser negativo");
        this.costo = costo;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setImagenProducto(byte[] imagenProducto) {
        this.imagenProducto = imagenProducto;
    }
    public byte[] getImagenProducto() {
        return imagenProducto;
    }

    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

}
