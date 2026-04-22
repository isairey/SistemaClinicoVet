package co.edu.upb.veterinaria.repositories.RepositorioUsuarioModulo;

import co.edu.upb.veterinaria.config.DatabaseConfig;
import co.edu.upb.veterinaria.models.ModeloUsuarioModulo.UsuarioModulo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio para la gestión de la relación Usuario-Módulo
 */
public class UsuarioModuloRepository {

    /**
     * Asigna un módulo a un usuario
     */
    public void save(UsuarioModulo usuarioModulo) throws SQLException {
        String sql = "INSERT INTO veterinaria.usuario_modulo " +
                     "(usuario_idusuario, modulo_idmodulo) " +
                     "VALUES (?, ?) " +
                     "ON CONFLICT (usuario_idusuario, modulo_idmodulo) DO NOTHING";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioModulo.getUsuarioIdUsuario());
            stmt.setInt(2, usuarioModulo.getModuloIdModulo());
            stmt.executeUpdate();
        }
    }

    /**
     * Asigna múltiples módulos a un usuario en una transacción
     */
    public void saveAll(int usuarioId, List<Integer> moduloIds) throws SQLException {
        String sql = "INSERT INTO veterinaria.usuario_modulo " +
                     "(usuario_idusuario, modulo_idmodulo) " +
                     "VALUES (?, ?) " +
                     "ON CONFLICT (usuario_idusuario, modulo_idmodulo) DO NOTHING";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConfig.getDataSource().getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement(sql);
            
            for (Integer moduloId : moduloIds) {
                stmt.setInt(1, usuarioId);
                stmt.setInt(2, moduloId);
                stmt.addBatch();
            }
            
            stmt.executeBatch();
            stmt.clearBatch(); // Limpiar el batch después de ejecutar
            conn.commit();
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Elimina todos los módulos asignados a un usuario
     */
    public void deleteByUsuarioId(int usuarioId) throws SQLException {
        String sql = "DELETE FROM veterinaria.usuario_modulo WHERE usuario_idusuario = ?";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            stmt.executeUpdate();
        }
    }

    /**
     * Elimina un módulo específico de un usuario
     */
    public void deleteByUsuarioIdAndModuloId(int usuarioId, int moduloId) throws SQLException {
        String sql = "DELETE FROM veterinaria.usuario_modulo " +
                     "WHERE usuario_idusuario = ? AND modulo_idmodulo = ?";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, moduloId);
            stmt.executeUpdate();
        }
    }

    /**
     * Obtiene todos los IDs de módulos asignados a un usuario
     */
    public List<Integer> findModuloIdsByUsuarioId(int usuarioId) throws SQLException {
        String sql = "SELECT modulo_idmodulo FROM veterinaria.usuario_modulo " +
                     "WHERE usuario_idusuario = ?";
        
        List<Integer> moduloIds = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    moduloIds.add(rs.getInt("modulo_idmodulo"));
                }
            }
        }
        
        return moduloIds;
    }

    /**
     * Actualiza los módulos de un usuario (elimina los anteriores y asigna los nuevos)
     */
    public void updateModulosForUsuario(int usuarioId, List<Integer> nuevosModuloIds) throws SQLException {
        Connection conn = null;
        PreparedStatement deleteStmt = null;
        PreparedStatement insertStmt = null;
        
        try {
            conn = DatabaseConfig.getDataSource().getConnection();
            conn.setAutoCommit(false);
            
            // 1. Eliminar asignaciones anteriores
            String deleteSql = "DELETE FROM veterinaria.usuario_modulo WHERE usuario_idusuario = ?";
            deleteStmt = conn.prepareStatement(deleteSql);
            deleteStmt.setInt(1, usuarioId);
            deleteStmt.executeUpdate();
            
            // 2. Insertar nuevas asignaciones
            if (!nuevosModuloIds.isEmpty()) {
                String insertSql = "INSERT INTO veterinaria.usuario_modulo " +
                                   "(usuario_idusuario, modulo_idmodulo) VALUES (?, ?)";
                insertStmt = conn.prepareStatement(insertSql);
                
                for (Integer moduloId : nuevosModuloIds) {
                    insertStmt.setInt(1, usuarioId);
                    insertStmt.setInt(2, moduloId);
                    insertStmt.addBatch();
                }
                
                insertStmt.executeBatch();
                insertStmt.clearBatch(); // Limpiar el batch después de ejecutar
            }
            
            conn.commit();
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            // Cerrar statements explícitamente
            if (deleteStmt != null) {
                try {
                    deleteStmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (insertStmt != null) {
                try {
                    insertStmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Verifica si un usuario tiene acceso a un módulo específico
     */
    public boolean hasAccess(int usuarioId, int moduloId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM veterinaria.usuario_modulo " +
                     "WHERE usuario_idusuario = ? AND modulo_idmodulo = ?";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, moduloId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
}
