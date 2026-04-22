package co.edu.upb.veterinaria.repositories.RepositorioMascota;

import co.edu.upb.veterinaria.config.DatabaseConfig;
import co.edu.upb.veterinaria.models.ModeloMascota.Mascota;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MascotaRepositoryImpl implements MascotaRepository {

    private final DataSource ds;

    public MascotaRepositoryImpl() {
        this.ds = DatabaseConfig.getDataSource();
    }

    // ========================= CREATE =========================
    @Override
    public int create(Mascota mascota) {
        final String sql = """
            INSERT INTO veterinaria.mascota(
              nombre, raza, especie, sexo, numerochip, edad, cliente_idcliente
            )
            VALUES (?,?,?,?,?,?,?)
            RETURNING idmascota
        """;

        try (Connection cn = ds.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nvl(mascota.getNombre()));
            ps.setString(2, nvl(mascota.getRaza()));
            ps.setString(3, nvl(mascota.getEspecie()));
            ps.setString(4, String.valueOf(mascota.getSexo()));
            ps.setString(5, nvl(mascota.getNumeroChip()));
            ps.setInt(6, mascota.getEdad());

            // Si hay responsable asignado, usar su ID, de lo contrario NULL
            if (mascota.getResponsable() != null && mascota.getResponsable().getIdCliente() > 0) {
                ps.setInt(7, mascota.getResponsable().getIdCliente());
            } else {
                ps.setNull(7, Types.INTEGER);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("No se devolvió idmascota");
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error creando mascota: " + e.getMessage(), e);
        }
    }

    // ========================= READ =========================
    @Override
    public Optional<Mascota> findById(int id) {
        final String sql = """
            SELECT idmascota, nombre, raza, especie, sexo, numerochip, edad, cliente_idcliente
            FROM veterinaria.mascota
            WHERE idmascota = ?
        """;

        try (Connection cn = ds.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(mapMascota(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error consultando mascota: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Mascota> findByClienteId(int clienteId) {
        final String sql = """
            SELECT idmascota, nombre, raza, especie, sexo, numerochip, edad, cliente_idcliente
            FROM veterinaria.mascota
            WHERE cliente_idcliente = ?
            ORDER BY nombre
        """;

        List<Mascota> mascotas = new ArrayList<>();
        try (Connection cn = ds.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, clienteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    mascotas.add(mapMascota(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error consultando mascotas por cliente: " + e.getMessage(), e);
        }

        return mascotas;
    }

    @Override
    public List<Mascota> findSinCliente() {
        final String sql = """
            SELECT idmascota, nombre, raza, especie, sexo, numerochip, edad, cliente_idcliente
            FROM veterinaria.mascota
            WHERE cliente_idcliente IS NULL
            ORDER BY nombre
        """;

        List<Mascota> mascotas = new ArrayList<>();
        try (Connection cn = ds.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    mascotas.add(mapMascota(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error consultando mascotas sin cliente: " + e.getMessage(), e);
        }

        return mascotas;
    }

    @Override
    public List<Mascota> findAll(int limit, int offset) {
        final String sql = """
            SELECT idmascota, nombre, raza, especie, sexo, numerochip, edad, cliente_idcliente
            FROM veterinaria.mascota
            ORDER BY idmascota DESC
            LIMIT ? OFFSET ?
        """;

        List<Mascota> mascotas = new ArrayList<>();
        try (Connection cn = ds.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, Math.max(1, limit));
            ps.setInt(2, Math.max(0, offset));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    mascotas.add(mapMascota(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error listando mascotas: " + e.getMessage(), e);
        }

        return mascotas;
    }

    // ========================= UPDATE =========================
    @Override
    public boolean update(Mascota mascota) {
        final String sql = """
            UPDATE veterinaria.mascota SET
              nombre=?, raza=?, especie=?, sexo=?, numerochip=?, edad=?, cliente_idcliente=?
            WHERE idmascota=?
        """;

        try (Connection cn = ds.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nvl(mascota.getNombre()));
            ps.setString(2, nvl(mascota.getRaza()));
            ps.setString(3, nvl(mascota.getEspecie()));
            ps.setString(4, String.valueOf(mascota.getSexo()));
            ps.setString(5, nvl(mascota.getNumeroChip()));
            ps.setInt(6, mascota.getEdad());

            if (mascota.getResponsable() != null && mascota.getResponsable().getIdCliente() > 0) {
                ps.setInt(7, mascota.getResponsable().getIdCliente());
            } else {
                ps.setNull(7, Types.INTEGER);
            }

            ps.setInt(8, mascota.getIdMascota());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando mascota: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean asignarCliente(int idMascota, int idCliente) {
        final String sql = """
            UPDATE veterinaria.mascota 
            SET cliente_idcliente = ?
            WHERE idmascota = ?
        """;

        try (Connection cn = ds.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            ps.setInt(2, idMascota);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error asignando cliente a mascota: " + e.getMessage(), e);
        }
    }

    // ========================= DELETE =========================
    @Override
    public boolean delete(int id) {
        final String sql = "DELETE FROM veterinaria.mascota WHERE idmascota=?";

        try (Connection cn = ds.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error eliminando mascota: " + e.getMessage(), e);
        }
    }

    // ========================= UTILIDADES =========================
    @Override
    public int count() {
        final String sql = "SELECT COUNT(*) FROM veterinaria.mascota";

        try (Connection cn = ds.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error contando mascotas: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByNumeroChip(String numeroChip) {
        final String sql = "SELECT 1 FROM veterinaria.mascota WHERE numerochip=? LIMIT 1";

        try (Connection cn = ds.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, numeroChip);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error verificando chip: " + e.getMessage(), e);
        }
    }

    // ========================= MAPEO =========================
    private Mascota mapMascota(ResultSet rs) throws SQLException {
        Mascota m = new Mascota();
        m.setIdMascota(rs.getInt("idmascota"));
        m.setNombre(rs.getString("nombre"));
        m.setRaza(rs.getString("raza"));
        m.setEspecie(rs.getString("especie"));

        String sexoStr = rs.getString("sexo");
        m.setSexo((sexoStr != null && !sexoStr.isEmpty()) ? sexoStr.charAt(0) : ' ');

        m.setNumeroChip(rs.getString("numerochip"));
        m.setEdad(rs.getInt("edad"));

        // No cargamos el cliente completo aquí para evitar ciclos
        // Si se necesita, se puede hacer una carga lazy

        return m;
    }

    // ========================= HELPERS =========================
    private String nvl(String val) {
        return (val == null || val.trim().isEmpty()) ? null : val.trim();
    }
}
