package co.edu.upb.veterinaria.repositories.RepositorioModulo;

import co.edu.upb.veterinaria.config.DatabaseConfig;
import co.edu.upb.veterinaria.models.ModeloModulo.Modulo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de módulos del sistema
 */
public class ModuloRepository {

    /**
     * Obtiene todos los módulos del sistema ordenados por 'orden'
     */
    public List<Modulo> findAll() throws SQLException {
        String sql = "SELECT idmodulo, nombremodulo, descripcion, icono, orden " +
                     "FROM veterinaria.modulo ORDER BY orden ASC";
        
        List<Modulo> modulos = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                modulos.add(mapResultSetToModulo(rs));
            }
        }
        
        return modulos;
    }

    /**
     * Busca un módulo por ID
     */
    public Optional<Modulo> findById(int idModulo) throws SQLException {
        String sql = "SELECT idmodulo, nombremodulo, descripcion, icono, orden " +
                     "FROM veterinaria.modulo WHERE idmodulo = ?";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idModulo);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToModulo(rs));
                }
            }
        }
        
        return Optional.empty();
    }

    /**
     * Busca un módulo por nombre
     */
    public Optional<Modulo> findByNombre(String nombreModulo) throws SQLException {
        String sql = "SELECT idmodulo, nombremodulo, descripcion, icono, orden " +
                     "FROM veterinaria.modulo WHERE nombremodulo = ?";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nombreModulo);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToModulo(rs));
                }
            }
        }
        
        return Optional.empty();
    }

    /**
     * Obtiene los módulos asignados a un usuario específico
     */
    public List<Modulo> findByUsuarioId(int usuarioId) throws SQLException {
        String sql = "SELECT m.idmodulo, m.nombremodulo, m.descripcion, m.icono, m.orden " +
                     "FROM veterinaria.modulo m " +
                     "INNER JOIN veterinaria.usuario_modulo um ON m.idmodulo = um.modulo_idmodulo " +
                     "WHERE um.usuario_idusuario = ? " +
                     "ORDER BY m.orden ASC";
        
        List<Modulo> modulos = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    modulos.add(mapResultSetToModulo(rs));
                }
            }
        }
        
        return modulos;
    }

    /**
     * Mapea un ResultSet a un objeto Modulo
     */
    private Modulo mapResultSetToModulo(ResultSet rs) throws SQLException {
        Modulo modulo = new Modulo();
        modulo.setIdModulo(rs.getInt("idmodulo"));
        modulo.setNombreModulo(rs.getString("nombremodulo"));
        modulo.setDescripcion(rs.getString("descripcion"));
        modulo.setIcono(rs.getString("icono"));
        
        int orden = rs.getInt("orden");
        modulo.setOrden(rs.wasNull() ? null : orden);
        
        return modulo;
    }
}
