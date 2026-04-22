package co.edu.upb.veterinaria.models.ModeloUsuarioModulo;

import java.time.LocalDateTime;

/**
 * Representa la relación entre Usuario y Módulo
 * Corresponde a la tabla veterinaria.usuario_modulo
 */
public class UsuarioModulo {
    private int idUsuarioModulo;
    private int usuarioIdUsuario;
    private int moduloIdModulo;
    private LocalDateTime fechaAsignacion;

    public UsuarioModulo() {}

    public UsuarioModulo(int idUsuarioModulo, int usuarioIdUsuario, int moduloIdModulo, LocalDateTime fechaAsignacion) {
        this.idUsuarioModulo = idUsuarioModulo;
        this.usuarioIdUsuario = usuarioIdUsuario;
        this.moduloIdModulo = moduloIdModulo;
        this.fechaAsignacion = fechaAsignacion;
    }

    // Constructor sin ID (para inserts)
    public UsuarioModulo(int usuarioIdUsuario, int moduloIdModulo) {
        this.usuarioIdUsuario = usuarioIdUsuario;
        this.moduloIdModulo = moduloIdModulo;
        this.fechaAsignacion = LocalDateTime.now();
    }

    // Getters y Setters
    public int getIdUsuarioModulo() {
        return idUsuarioModulo;
    }

    public void setIdUsuarioModulo(int idUsuarioModulo) {
        this.idUsuarioModulo = idUsuarioModulo;
    }

    public int getUsuarioIdUsuario() {
        return usuarioIdUsuario;
    }

    public void setUsuarioIdUsuario(int usuarioIdUsuario) {
        this.usuarioIdUsuario = usuarioIdUsuario;
    }

    public int getModuloIdModulo() {
        return moduloIdModulo;
    }

    public void setModuloIdModulo(int moduloIdModulo) {
        this.moduloIdModulo = moduloIdModulo;
    }

    public LocalDateTime getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDateTime fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }
}
