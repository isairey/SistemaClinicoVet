package co.edu.upb.veterinaria.models.ModeloAccesorio;

import co.edu.upb.veterinaria.models.ModeloMarca.Marca;
import co.edu.upb.veterinaria.models.ModeloProducto.Producto;
import co.edu.upb.veterinaria.models.ModeloProveedor.Proveedor;
import co.edu.upb.veterinaria.models.ModeloUsuario.Usuario;

public class Accesorio extends Producto {

    public Accesorio() { }

    public Accesorio(int idProducto, String nombre, String referencia, String codigoBarras, double precio, double costo,
                     Marca marca, String descripcion, int stock, byte[] imagenProducto, String estado, Proveedor proveedor, Usuario usuario) {
        super(idProducto, nombre, referencia, codigoBarras, precio, costo,
        marca, descripcion, stock, imagenProducto, estado, proveedor, usuario);
    }
}


