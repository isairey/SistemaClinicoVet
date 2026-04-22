package co.edu.upb.veterinaria.models.ModeloTipoProducto;

/**
 * Modelo TipoProducto
 * -------------------
 * Representa los tipos de productos en el sistema:
 * - Medicamento
 * - Alimento
 * - Material Quirúrgico
 * - Accesorio/Juguete
 */
public class TipoProducto {
    private int idTipoProducto;
    private String nombreTipo;

    public TipoProducto() { }

    public TipoProducto(int idTipoProducto, String nombreTipo) {
        this.idTipoProducto = idTipoProducto;
        this.nombreTipo = nombreTipo;
    }

    public int getIdTipoProducto() {
        return idTipoProducto;
    }

    public void setIdTipoProducto(int idTipoProducto) {
        this.idTipoProducto = idTipoProducto;
    }

    public String getNombreTipo() {
        return nombreTipo;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    @Override
    public String toString() {
        return nombreTipo;
    }
}

