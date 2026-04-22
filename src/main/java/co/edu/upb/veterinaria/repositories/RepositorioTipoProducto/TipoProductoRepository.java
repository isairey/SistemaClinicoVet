package co.edu.upb.veterinaria.repositories.RepositorioTipoProducto;

import co.edu.upb.veterinaria.config.DatabaseConfig;
import co.edu.upb.veterinaria.models.ModeloTipoProducto.TipoProducto;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * TipoProductoRepository
 * ----------------------
 * Repositorio para gestionar los tipos de producto:
 * - Medicamento
 * - Alimento
 * - Material Quirúrgico
 * - Accesorio/Juguete
 */
public class TipoProductoRepository {

    private final DataSource dataSource;

    public TipoProductoRepository() {
        this.dataSource = DatabaseConfig.getDataSource();
    }

    /**
     * Crea un nuevo tipo de producto.
     * @return id generado
     */
    public int create(TipoProducto tipo) throws SQLException {
        final String sql = "INSERT INTO veterinaria.tipoproducto (\"nombreTipo\") VALUES (?) RETURNING idtipoproducto";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, tipo.getNombreTipo());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            throw new SQLException("No se devolvió idtipoproducto en INSERT");
        }
    }

    /**
     * Busca un tipo de producto por ID.
     */
    public Optional<TipoProducto> findById(int id) throws SQLException {
        final String sql = "SELECT idtipoproducto, \"nombreTipo\" FROM veterinaria.tipoproducto WHERE idtipoproducto = ?";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new TipoProducto(
                            rs.getInt("idtipoproducto"),
                            rs.getString("nombreTipo")
                    ));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Busca un tipo de producto por nombre exacto (case-insensitive).
     */
    public Optional<TipoProducto> findByNombre(String nombreTipo) throws SQLException {
        final String sql = "SELECT idtipoproducto, \"nombreTipo\" FROM veterinaria.tipoproducto WHERE LOWER(\"nombreTipo\") = LOWER(?)";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombreTipo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new TipoProducto(
                            rs.getInt("idtipoproducto"),
                            rs.getString("nombreTipo")
                    ));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Lista todos los tipos de producto disponibles.
     */
    public List<TipoProducto> findAll() throws SQLException {
        final String sql = "SELECT idtipoproducto, \"nombreTipo\" FROM veterinaria.tipoproducto ORDER BY idtipoproducto";
        List<TipoProducto> out = new ArrayList<>();
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new TipoProducto(
                        rs.getInt("idtipoproducto"),
                        rs.getString("nombreTipo")
                ));
            }
        }
        return out;
    }

    /**
     * Cuenta cuántos tipos de producto hay en la BD.
     */
    public long count() throws SQLException {
        final String sql = "SELECT COUNT(*) AS c FROM veterinaria.tipoproducto";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong("c") : 0L;
        }
    }
}

