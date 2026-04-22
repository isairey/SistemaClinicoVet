package co.edu.upb.veterinaria.repositories.RepositorioUsuario;

import co.edu.upb.veterinaria.config.DatabaseConfig;
import co.edu.upb.veterinaria.models.ModeloUsuario.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de usuarios
 */
public class UsuarioRepository {

    /**
     * Crea un nuevo usuario en la base de datos
     * @return El ID del usuario creado
     */
    public int save(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO veterinaria.usuario " +
                     "(cc, nombre, apellidos, usuario, email, contrasena, telefono, direccion) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING idusuario";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario.getCc());
            stmt.setString(2, usuario.getNombre());
            stmt.setString(3, usuario.getApellidos());
            stmt.setString(4, usuario.getUsuario());
            stmt.setString(5, usuario.getEmail());
            stmt.setString(6, usuario.getContrasena()); // Ya debe venir encriptada
            stmt.setString(7, usuario.getTelefono());
            stmt.setString(8, usuario.getDireccion());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idusuario");
                }
            }
        }
        
        throw new SQLException("No se pudo crear el usuario");
    }

    /**
     * Actualiza un usuario existente
     */
    public void update(Usuario usuario) throws SQLException {
        String sql = "UPDATE veterinaria.usuario SET " +
                     "cc = ?, nombre = ?, apellidos = ?, usuario = ?, email = ?, " +
                     "contrasena = ?, telefono = ?, direccion = ? " +
                     "WHERE idusuario = ?";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario.getCc());
            stmt.setString(2, usuario.getNombre());
            stmt.setString(3, usuario.getApellidos());
            stmt.setString(4, usuario.getUsuario());
            stmt.setString(5, usuario.getEmail());
            stmt.setString(6, usuario.getContrasena());
            stmt.setString(7, usuario.getTelefono());
            stmt.setString(8, usuario.getDireccion());
            stmt.setInt(9, usuario.getIdUsuario());
            
            stmt.executeUpdate();
        }
    }

    /**
     * Elimina un usuario por ID
     */
    public void deleteById(int idUsuario) throws SQLException {
        String sql = "DELETE FROM veterinaria.usuario WHERE idusuario = ?";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            stmt.executeUpdate();
        }
    }

    /**
     * Busca un usuario por ID
     */
    public Optional<Usuario> findById(int idUsuario) throws SQLException {
        String sql = "SELECT idusuario, cc, nombre, apellidos, usuario, email, " +
                     "contrasena, telefono, direccion " +
                     "FROM veterinaria.usuario WHERE idusuario = ?";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUsuario(rs));
                }
            }
        }
        
        return Optional.empty();
    }

    /**
     * Busca un usuario por nombre de usuario (username)
     */
    public Optional<Usuario> findByUsuario(String usuario) throws SQLException {
        String sql = "SELECT idusuario, cc, nombre, apellidos, usuario, email, " +
                     "contrasena, telefono, direccion " +
                     "FROM veterinaria.usuario WHERE usuario = ?";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUsuario(rs));
                }
            }
        }
        
        return Optional.empty();
    }

    /**
     * Busca un usuario por email
     */
    public Optional<Usuario> findByEmail(String email) throws SQLException {
        String sql = "SELECT idusuario, cc, nombre, apellidos, usuario, email, " +
                     "contrasena, telefono, direccion " +
                     "FROM veterinaria.usuario WHERE email = ?";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUsuario(rs));
                }
            }
        }
        
        return Optional.empty();
    }

    /**
     * Obtiene todos los usuarios del sistema
     */
    public List<Usuario> findAll() throws SQLException {
        String sql = "SELECT idusuario, cc, nombre, apellidos, usuario, email, " +
                     "contrasena, telefono, direccion " +
                     "FROM veterinaria.usuario ORDER BY nombre, apellidos";
        
        List<Usuario> usuarios = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
        }
        
        return usuarios;
    }

    /**
     * Busca usuarios por filtro (nombre, apellido, usuario o email)
     */
    public List<Usuario> findByFilter(String filtro) throws SQLException {
        String sql = "SELECT idusuario, cc, nombre, apellidos, usuario, email, " +
                     "contrasena, telefono, direccion " +
                     "FROM veterinaria.usuario " +
                     "WHERE LOWER(nombre) LIKE ? OR LOWER(apellidos) LIKE ? " +
                     "   OR LOWER(usuario) LIKE ? OR LOWER(email) LIKE ? " +
                     "ORDER BY nombre, apellidos";
        
        List<Usuario> usuarios = new ArrayList<>();
        String filtroLike = "%" + filtro.toLowerCase() + "%";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, filtroLike);
            stmt.setString(2, filtroLike);
            stmt.setString(3, filtroLike);
            stmt.setString(4, filtroLike);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(mapResultSetToUsuario(rs));
                }
            }
        }
        
        return usuarios;
    }

    /**
     * Verifica si un nombre de usuario ya existe
     */
    public boolean existsByUsuario(String usuario) throws SQLException {
        String sql = "SELECT COUNT(*) FROM veterinaria.usuario WHERE usuario = ?";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }

    /**
     * Verifica si un email ya existe
     */
    public boolean existsByEmail(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM veterinaria.usuario WHERE email = ?";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }

    /**
     * Mapea un ResultSet a un objeto Usuario
     */
    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("idusuario"));
        usuario.setCc(rs.getString("cc"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setApellidos(rs.getString("apellidos"));
        usuario.setUsuario(rs.getString("usuario"));
        usuario.setEmail(rs.getString("email"));
        usuario.setContrasena(rs.getString("contrasena"));
        usuario.setTelefono(rs.getString("telefono"));
        usuario.setDireccion(rs.getString("direccion"));
        return usuario;
    }
}
