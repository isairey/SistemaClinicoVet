package co.edu.upb.veterinaria.models.ModeloPermiso;

import java.util.Set;

public class Permiso {
    private int idPermiso;
    private Set<String> modulos;
    private String descripcion;

    public Permiso() { }

    public Permiso(int idPermiso, Set<String> modulos, String descripcion) {
        this.idPermiso = idPermiso;
        this.modulos = modulos;
        this.descripcion = descripcion;
    }

    public int getIdPermiso() { return idPermiso; }
    public void setIdPermiso(int idPermiso) { this.idPermiso = idPermiso; }

    public Set<String> getModulos() { return modulos; }
    public void setModulos(Set<String> modulos) { this.modulos = modulos; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

}
