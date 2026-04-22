package co.edu.upb.veterinaria.models.ModeloCliente;

import co.edu.upb.veterinaria.models.ModeloMascota.Mascota;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Cliente {
    private int idCliente;
    private String nombre;
    private String apellidos;
    private String cc;
    private Date fechaNacimiento;
    private String telefono;
    private String email;
    private String direccion;
    private String tipoPersona;
    private List<Mascota> mascotas;
    private String tipoDocumento;
    private String ciudad;
    private String nombreContacto;
    private String telefonoContacto;

    public Cliente() {
        this.mascotas = new ArrayList<>();
    }

    public Cliente(int idCliente, String nombre, String apellidos, String cc, Date fechaNacimiento,
                   String telefono, String email, String direccion, String tipoPersona,
                   List<Mascota> mascotas, String tipoDocumento, String ciudad,
                   String nombreContacto, String telefonoContacto) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.cc = cc;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.tipoPersona = tipoPersona;
        this.mascotas = (mascotas != null) ? mascotas : new ArrayList<>();
        this.tipoDocumento = tipoDocumento;
        this.ciudad = ciudad;
        this.nombreContacto = nombreContacto;
        this.telefonoContacto = telefonoContacto;
    }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getCc() { return cc; }
    public void setCc(String cc) { this.cc = cc; }

    public Date getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Date fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTipoPersona() { return tipoPersona; }
    public void setTipoPersona(String tipoPersona) { this.tipoPersona = tipoPersona; }

    public List<Mascota> getMascotas() { return mascotas; }
    public void setMascotas(List<Mascota> mascotas) { this.mascotas = mascotas; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getNombreContacto() { return nombreContacto; }
    public void setNombreContacto(String nombreContacto) { this.nombreContacto = nombreContacto; }

    public String getTelefonoContacto() { return telefonoContacto; }
    public void setTelefonoContacto(String telefonoContacto) { this.telefonoContacto = telefonoContacto; }

}
