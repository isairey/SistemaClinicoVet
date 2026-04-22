package co.edu.upb.veterinaria.models.ModeloModulo;

/**
 * Representa un módulo del sistema (Inventario, Ventas, etc.)
 * Corresponde a la tabla veterinaria.modulo
 */
public class Modulo {
    private int idModulo;
    private String nombreModulo;
    private String descripcion;
    private String icono;
    private Integer orden;

    public Modulo() {}

    public Modulo(int idModulo, String nombreModulo, String descripcion, String icono, Integer orden) {
        this.idModulo = idModulo;
        this.nombreModulo = nombreModulo;
        this.descripcion = descripcion;
        this.icono = icono;
        this.orden = orden;
    }

    // Getters y Setters
    public int getIdModulo() {
        return idModulo;
    }

    public void setIdModulo(int idModulo) {
        this.idModulo = idModulo;
    }

    public String getNombreModulo() {
        return nombreModulo;
    }

    public void setNombreModulo(String nombreModulo) {
        this.nombreModulo = nombreModulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    @Override
    public String toString() {
        return nombreModulo; // Para mostrar en ComboBox/ListView
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Modulo modulo = (Modulo) o;
        return idModulo == modulo.idModulo;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idModulo);
    }
}
