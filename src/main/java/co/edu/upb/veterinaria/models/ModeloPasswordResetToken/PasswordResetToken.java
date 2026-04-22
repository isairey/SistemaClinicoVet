package co.edu.upb.veterinaria.models.ModeloPasswordResetToken;

import java.time.LocalDateTime;

/**
 * Representa un token de recuperación de contraseña
 * Corresponde a la tabla veterinaria.password_reset_token
 */
public class PasswordResetToken {
    private int idToken;
    private int usuarioId;
    private String email;
    private String codigoOtp;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaExpiracion;
    private boolean usado;

    public PasswordResetToken() {}

    public PasswordResetToken(int usuarioId, String email, String codigoOtp, 
                             LocalDateTime fechaCreacion, LocalDateTime fechaExpiracion) {
        this.usuarioId = usuarioId;
        this.email = email;
        this.codigoOtp = codigoOtp;
        this.fechaCreacion = fechaCreacion;
        this.fechaExpiracion = fechaExpiracion;
        this.usado = false;
    }

    // Getters y Setters
    public int getIdToken() {
        return idToken;
    }

    public void setIdToken(int idToken) {
        this.idToken = idToken;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCodigoOtp() {
        return codigoOtp;
    }

    public void setCodigoOtp(String codigoOtp) {
        this.codigoOtp = codigoOtp;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public boolean isUsado() {
        return usado;
    }

    public void setUsado(boolean usado) {
        this.usado = usado;
    }

    /**
     * Verifica si el token es válido (no usado y no expirado)
     */
    public boolean isValido() {
        return !usado && LocalDateTime.now().isBefore(fechaExpiracion);
    }

    @Override
    public String toString() {
        return "PasswordResetToken{" +
                "idToken=" + idToken +
                ", usuarioId=" + usuarioId +
                ", email='" + email + '\'' +
                ", usado=" + usado +
                ", expira=" + fechaExpiracion +
                '}';
    }
}
