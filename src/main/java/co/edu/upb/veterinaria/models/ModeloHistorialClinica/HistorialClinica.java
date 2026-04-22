package co.edu.upb.veterinaria.models.ModeloHistorialClinica;

import co.edu.upb.veterinaria.models.ModeloMascota.Mascota;
import co.edu.upb.veterinaria.models.ModeloServicio.Servicio;

import java.util.List;

public class HistorialClinica {
    private int idHistorialClinica;
    private String observaciones;
    private List<Servicio> servicios;
    private Mascota mascota;

    public HistorialClinica() {}

    public HistorialClinica(int idHistorialClinica,
                            String observaciones, List<Servicio> servicios, Mascota mascota) {
        this.idHistorialClinica = idHistorialClinica;
        this.observaciones = observaciones;
        this.servicios = servicios;
        this.mascota = mascota;
    }

    public int getIdHistoriaClinica() { return idHistorialClinica; }
    public void setIdHistoriaClinica(int idHistoriaClinica) { this.idHistorialClinica = idHistoriaClinica; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public List<Servicio> getServicios() { return servicios; }
    public void setServicios(List<Servicio> servicios) { this.servicios = servicios; }

    public Mascota getMascota() { return mascota; }
    public void setMascota(Mascota mascota) { this.mascota = mascota;}
}
