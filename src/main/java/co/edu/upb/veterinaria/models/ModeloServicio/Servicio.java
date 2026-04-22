package co.edu.upb.veterinaria.models.ModeloServicio;

public class Servicio {
    private int idServicio;
    private String nombreServicio;
    private double precio;
    private String descripcion;

    public Servicio() { }

    public Servicio(int idServicio, String nombreServicio, double precio,
                    String descripcion) {
        this.idServicio = idServicio;
        this.nombreServicio = nombreServicio;
        this.precio = precio;
        this.descripcion = descripcion;
    }

    public int getIdServicio() { return idServicio; }
    public void setIdServicio(int idServicio) { this.idServicio = idServicio; }

    public String getNombreServicio() { return nombreServicio; }
    public void setNombreServicio(String nombreServicio) { this.nombreServicio = nombreServicio; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
