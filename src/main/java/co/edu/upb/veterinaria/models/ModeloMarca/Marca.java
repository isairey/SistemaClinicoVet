package co.edu.upb.veterinaria.models.ModeloMarca;

public class Marca {
    private int idMarca;
    private String nombreMarca;
    private String descripcion;

    public Marca(){}

    public Marca(int idMarca, String nombreMarca) {
        this.idMarca = idMarca;
        this.nombreMarca = nombreMarca;
    }

    public Marca(int idMarca, String nombreMarca, String descripcion) {
        this.idMarca = idMarca;
        this.nombreMarca = nombreMarca;
        this.descripcion = descripcion;
    }

    public int getIdMarca() {
        return idMarca;
    }
    public void setIdMarca(int idMarca) {
        this.idMarca = idMarca;
    }
    public String getNombreMarca() {
        return nombreMarca;
    }
    public void setNombreMarca(String nombreMarca) {
        this.nombreMarca = nombreMarca;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}
