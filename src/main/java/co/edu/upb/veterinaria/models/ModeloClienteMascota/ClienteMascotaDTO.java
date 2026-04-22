package co.edu.upb.veterinaria.models.ModeloClienteMascota;

import java.util.Date;

/**
 * DTO para mostrar en la tabla la combinación de datos de Cliente y Mascota.
 * Cada fila representa un cliente con UNA de sus mascotas.
 */
public class ClienteMascotaDTO {
    // Datos del Cliente
    private int idCliente;
    private String tipoPersona;
    private String tipoDocumento;
    private String numeroDocumento;
    private String nombre;
    private String apellidos;
    private Date fechaNacimiento;
    private String ciudad;
    private String email;
    private String direccion;
    private String telefono;
    private String contactoNombre;
    private String contactoTelefono;

    // Datos de la Mascota
    private Integer idMascota;  // Puede ser null si no tiene mascotas
    private String mascotaNombre;
    private String raza;
    private String especie;
    private String sexo;
    private Integer edad;
    private String numeroChip;

    // Constructor vacío
    public ClienteMascotaDTO() {}

    // Constructor completo
    public ClienteMascotaDTO(int idCliente, String tipoPersona, String tipoDocumento, String numeroDocumento,
                             String nombre, String apellidos, Date fechaNacimiento, String ciudad,
                             String email, String direccion, String telefono, String contactoNombre,
                             String contactoTelefono, Integer idMascota, String mascotaNombre, String raza,
                             String especie, String sexo, Integer edad, String numeroChip) {
        this.idCliente = idCliente;
        this.tipoPersona = tipoPersona;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.ciudad = ciudad;
        this.email = email;
        this.direccion = direccion;
        this.telefono = telefono;
        this.contactoNombre = contactoNombre;
        this.contactoTelefono = contactoTelefono;
        this.idMascota = idMascota;
        this.mascotaNombre = mascotaNombre;
        this.raza = raza;
        this.especie = especie;
        this.sexo = sexo;
        this.edad = edad;
        this.numeroChip = numeroChip;
    }

    // Getters y Setters
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getTipoPersona() { return tipoPersona; }
    public void setTipoPersona(String tipoPersona) { this.tipoPersona = tipoPersona; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public Date getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Date fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getContactoNombre() { return contactoNombre; }
    public void setContactoNombre(String contactoNombre) { this.contactoNombre = contactoNombre; }

    public String getContactoTelefono() { return contactoTelefono; }
    public void setContactoTelefono(String contactoTelefono) { this.contactoTelefono = contactoTelefono; }

    public Integer getIdMascota() { return idMascota; }
    public void setIdMascota(Integer idMascota) { this.idMascota = idMascota; }

    public String getMascotaNombre() { return mascotaNombre != null ? mascotaNombre : "Sin mascota"; }
    public void setMascotaNombre(String mascotaNombre) { this.mascotaNombre = mascotaNombre; }

    public String getRaza() { return raza != null ? raza : "-"; }
    public void setRaza(String raza) { this.raza = raza; }

    public String getEspecie() { return especie != null ? especie : "-"; }
    public void setEspecie(String especie) { this.especie = especie; }

    public String getSexo() { return sexo != null ? sexo : "-"; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }

    public String getNumeroChip() { return numeroChip != null ? numeroChip : "-"; }
    public void setNumeroChip(String numeroChip) { this.numeroChip = numeroChip; }

    // Método para formato de fecha
    public String getFechaNacimientoFormateada() {
        if (fechaNacimiento == null) return "-";
        return new java.text.SimpleDateFormat("dd/MM/yyyy").format(fechaNacimiento);
    }

    // Método para edad como String
    public String getEdadString() {
        return edad != null ? edad.toString() : "-";
    }
}

