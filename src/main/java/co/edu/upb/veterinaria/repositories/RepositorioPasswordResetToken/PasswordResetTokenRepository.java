package co.edu.upb.veterinaria.repositories.RepositorioPasswordResetToken;

import co.edu.upb.veterinaria.config.DatabaseConfig;
import co.edu.upb.veterinaria.models.ModeloPasswordResetToken.PasswordResetToken;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repositorio para la gestión de tokens de recuperación de contraseña
 */
public class PasswordResetTokenRepository {

    /**
     * Guarda un nuevo token de recuperación
     */
    public int save(PasswordResetToken token) throws SQLException {
        String sql = "INSERT INTO veterinaria.password_reset_token " +
                     "(usuario_idusuario, email, codigo_otp, fecha_creacion, fecha_expiracion, usado) " +
                     "VALUES (?, ?, ?, ?, ?, ?) RETURNING idtoken";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, token.getUsuarioId());
            stmt.setString(2, token.getEmail());
            stmt.setString(3, token.getCodigoOtp());
            stmt.setTimestamp(4, Timestamp.valueOf(token.getFechaCreacion()));
            stmt.setTimestamp(5, Timestamp.valueOf(token.getFechaExpiracion()));
            stmt.setBoolean(6, token.isUsado());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idtoken");
                }
            }
        }
        
        throw new SQLException("No se pudo crear el token de recuperación");
    }

    /**
     * Busca un token válido por email y código OTP
     */
    public Optional<PasswordResetToken> findByEmailAndCodigo(String email, String codigoOtp) throws SQLException {
        String sql = "SELECT idtoken, usuario_idusuario, email, codigo_otp, " +
                     "fecha_creacion, fecha_expiracion, usado " +
                     "FROM veterinaria.password_reset_token " +
                     "WHERE email = ? AND codigo_otp = ? AND usado = FALSE " +
                     "ORDER BY fecha_creacion DESC LIMIT 1";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setString(2, codigoOtp);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToToken(rs));
                }
            }
        }
        
        return Optional.empty();
    }

    /**
     * Busca el último token generado para un email (sin importar si está usado)
     */
    public Optional<PasswordResetToken> findLastByEmail(String email) throws SQLException {
        String sql = "SELECT idtoken, usuario_idusuario, email, codigo_otp, " +
                     "fecha_creacion, fecha_expiracion, usado " +
                     "FROM veterinaria.password_reset_token " +
                     "WHERE email = ? " +
                     "ORDER BY fecha_creacion DESC LIMIT 1";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToToken(rs));
                }
            }
        }
        
        return Optional.empty();
    }

    /**
     * Marca un token como usado
     */
    public void markAsUsed(int idToken) throws SQLException {
        String sql = "UPDATE veterinaria.password_reset_token SET usado = TRUE WHERE idtoken = ?";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idToken);
            stmt.executeUpdate();
        }
    }

    /**
     * Elimina tokens expirados (limpieza)
     */
    public int deleteExpiredTokens() throws SQLException {
        String sql = "DELETE FROM veterinaria.password_reset_token WHERE fecha_expiracion < ?";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            return stmt.executeUpdate();
        }
    }

    /**
     * Elimina todos los tokens de un usuario
     */
    public void deleteByUsuarioId(int usuarioId) throws SQLException {
        String sql = "DELETE FROM veterinaria.password_reset_token WHERE usuario_idusuario = ?";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            stmt.executeUpdate();
        }
    }

    /**
     * Mapea un ResultSet a un objeto PasswordResetToken
     */
    private PasswordResetToken mapResultSetToToken(ResultSet rs) throws SQLException {
        PasswordResetToken token = new PasswordResetToken();
        token.setIdToken(rs.getInt("idtoken"));
        token.setUsuarioId(rs.getInt("usuario_idusuario"));
        token.setEmail(rs.getString("email"));
        token.setCodigoOtp(rs.getString("codigo_otp"));
        token.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());
        token.setFechaExpiracion(rs.getTimestamp("fecha_expiracion").toLocalDateTime());
        token.setUsado(rs.getBoolean("usado"));
        return token;
    }
}
