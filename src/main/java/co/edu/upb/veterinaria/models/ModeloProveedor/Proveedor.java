package co.edu.upb.veterinaria.models.ModeloProveedor;

public class Proveedor {
    private int idProveedor;
    private String tipoPersona;
    private String nit_rut;
    private String nombre;
    private String telefono;
    private String direccion;
    private String email;
    private String ciudad;
    private String tipoDocumento;
    private String apellido;

    public Proveedor() { }

    public Proveedor(int idProveedor, String tipoPersona, String nit_rut, String nombre,
                     String telefono, String direccion, String email,
                     String ciudad, String tipoDocumento, String apellido) {
        this.idProveedor = idProveedor;
        this.tipoPersona = tipoPersona;
        this.nit_rut = nit_rut;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
        this.email = email;
        this.ciudad = ciudad;
        this.tipoDocumento = tipoDocumento;
        this.apellido = apellido;
    }

    public int getIdProveedor() {
        return idProveedor;
    }
    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }
    public String getTipoPersona() {
        return tipoPersona;
    }
    public void setTipoPersona(String tipoPersona) {
        this.tipoPersona = tipoPersona;
    }
    public String getNit_rut() {
        return nit_rut;
    }
    public void setNit_rut(String nit_rut) {
        this.nit_rut = nit_rut;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getCiudad() {
        return ciudad;
    }
    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
    public String getTipoDocumento() {
        return tipoDocumento;
    }
    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }
    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
}