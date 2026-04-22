package co.edu.upb.veterinaria.models.ModeloUsuario;

import co.edu.upb.veterinaria.models.ModeloPermiso.Permiso;

import java.util.HashSet;
import java.util.Set;

public class Usuario {
    private int idUsuario;
    private String cc;
    private String nombre;
    private String apellidos;
    private String usuario; // username
    private String email;
    private String contrasena; // password encriptada
    private String telefono;
    private String direccion;
    private Set<Permiso> permisos; // Mantener compatibilidad (deprecado en favor de módulos)

    public Usuario() {}

    public Usuario(int idUsuario, String cc, String nombre, String apellidos, 
                   String usuario, String email, String contrasena, 
                   String telefono, String direccion) {
        this.idUsuario = idUsuario;
        this.cc = cc;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.usuario = usuario;
        this.email = email;
        this.contrasena = contrasena;
        this.telefono = telefono;
        this.direccion = direccion;
        this.permisos = new HashSet<>();
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getCc() { return cc; }
    public void setCc(String cc) { this.cc = cc; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public Set<Permiso> getPermisos() { return permisos; }
    public void setPermisos(Set<Permiso> permisos) { this.permisos = permisos; }

    // Métodos de compatibilidad con nombres anteriores (deprecados)
    @Deprecated
    public String getUser() { return usuario; }
    @Deprecated
    public void setUser(String user) { this.usuario = user; }
    @Deprecated
    public String getPassword() { return contrasena; }
    @Deprecated
    public void setPassword(String password) { this.contrasena = password; }
}