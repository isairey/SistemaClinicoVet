package co.edu.upb.veterinaria.models.ModeloUnidadMedida;

public class UnidadMedida {
    private int idUnidadMedida;
    private String nombre;


    public UnidadMedida() { }

    public UnidadMedida(int idUnidadMedida, String nombre) {
        this.idUnidadMedida = idUnidadMedida;
        this.nombre = nombre;

    }

    public int getIdUnidadMedida() {
        return idUnidadMedida;
    }
    public void setIdUnidadMedida(int idUnidadMedida) {
        this.idUnidadMedida = idUnidadMedida;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}
