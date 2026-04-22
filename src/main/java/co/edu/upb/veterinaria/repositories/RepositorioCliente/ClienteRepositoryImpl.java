package co.edu.upb.veterinaria.repositories.RepositorioCliente;

import co.edu.upb.veterinaria.config.DatabaseConfig;
import co.edu.upb.veterinaria.models.ModeloCliente.Cliente;
import co.edu.upb.veterinaria.models.ModeloMascota.Mascota;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class ClienteRepositoryImpl implements ClienteRepository {

    private final DataSource ds;

    public ClienteRepositoryImpl() {
        this.ds = DatabaseConfig.getDataSource();
    }

    // ========================= CREATE =========================
    @Override
    public int create(Cliente c, boolean incluirMascotas) {
        final String sqlCliente = """
            INSERT INTO veterinaria.cliente(
              nombre, apellidos, tipopersona, cc, fechanacimiento,
              email, direccion, telefono, nombrecontactoemergencia,
              telefonocontactoemergencia, ciudad, tipodocumento
            )
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?)
            RETURNING idcliente
        """;

        final String sqlMascota = """
            INSERT INTO veterinaria.mascota(
              nombre, raza, especie, sexo, numerochip, edad, cliente_idcliente
            ) VALUES (?,?,?,?,?,?,?)
        """;

        try (Connection cn = ds.getConnection()) {
            cn.setAutoCommit(false);

            int nuevoId;
            try (PreparedStatement ps = cn.prepareStatement(sqlCliente)) {
                ps.setString(1,  nvl(c.getNombre()));
                ps.setString(2,  nvl(c.getApellidos())); // requiere el campo en el modelo
                ps.setString(3,  nvl(c.getTipoPersona()));
                ps.setString(4,  nvl(c.getCc()));
                ps.setDate  (5,  toSqlDate(c.getFechaNacimiento()));
                ps.setString(6,  nvl(c.getEmail()));
                ps.setString(7,  nvl(c.getDireccion()));
                ps.setString(8,  nvl(c.getTelefono()));
                ps.setString(9,  nvl(c.getNombreContacto()));
                ps.setString(10, nvl(c.getTelefonoContacto()));
                ps.setString(11, nvl(c.getCiudad()));
                ps.setString(12, nvl(c.getTipoDocumento()));

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException("No se devolvió idcliente");
                    nuevoId = rs.getInt(1);
                }
            }

            if (incluirMascotas && c.getMascotas() != null) {
                try (PreparedStatement psM = cn.prepareStatement(sqlMascota)) {
                    for (Mascota m : c.getMascotas()) {
                        psM.setString(1, nvl(m.getNombre()));
                        psM.setString(2, nvl(m.getRaza()));
                        psM.setString(3, nvl(m.getEspecie()));
                        psM.setString(4, String.valueOf(m.getSexo())); // BD es varchar
                        psM.setString(5, nvl(m.getNumeroChip()));
                        psM.setInt   (6, m.getEdad());
                        psM.setInt   (7, nuevoId);
                        psM.addBatch();
                    }
                    psM.executeBatch();
                }
            }

            cn.commit();
            return nuevoId;

        } catch (SQLException e) {
            throw new RuntimeException("Error creando cliente: " + e.getMessage(), e);
        }
    }

    // ========================= READ =========================
    @Override
    public Optional<Cliente> findById(int id, boolean incluirMascotas) {
        final String sql = baseSelect() + " WHERE c.idcliente = ?";
        try (Connection cn = ds.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                Cliente c = mapCliente(rs);
                if (incluirMascotas) c.setMascotas(loadMascotas(cn, c.getIdCliente()));
                return Optional.of(c);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error consultando cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Cliente> findByCc(String cc, boolean incluirMascotas) {
        final String sql = baseSelect() + " WHERE c.cc = ?";
        try (Connection cn = ds.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, cc);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                Cliente c = mapCliente(rs);
                if (incluirMascotas) c.setMascotas(loadMascotas(cn, c.getIdCliente()));
                return Optional.of(c);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error consultando cliente por CC: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Cliente> findAll(int limit, int offset) {
        String sql = baseSelect() + " ORDER BY c.idcliente DESC LIMIT ? OFFSET ?";
        List<Cliente> out = new ArrayList<>();
        try (Connection cn = ds.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, Math.max(1, limit));
            ps.setInt(2, Math.max(0, offset));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapCliente(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error listando clientes: " + e.getMessage(), e);
        }
        return out;
    }

    // ========================= UPDATE =========================
    @Override
    public boolean update(Cliente c) {
        final String sql = """
            UPDATE veterinaria.cliente SET
              nombre=?, apellidos=?, tipopersona=?, cc=?, fechanacimiento=?,
              email=?, direccion=?, telefono=?, nombrecontactoemergencia=?,
              telefonocontactoemergencia=?, ciudad=?, tipodocumento=?
            WHERE idcliente=?
        """;
        try (Connection cn = ds.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1,  nvl(c.getNombre()));
            ps.setString(2,  nvl(c.getApellidos()));
            ps.setString(3,  nvl(c.getTipoPersona()));
            ps.setString(4,  nvl(c.getCc()));
            ps.setDate  (5,  toSqlDate(c.getFechaNacimiento()));
            ps.setString(6,  nvl(c.getEmail()));
            ps.setString(7,  nvl(c.getDireccion()));
            ps.setString(8,  nvl(c.getTelefono()));
            ps.setString(9,  nvl(c.getNombreContacto()));
            ps.setString(10, nvl(c.getTelefonoContacto()));
            ps.setString(11, nvl(c.getCiudad()));
            ps.setString(12, nvl(c.getTipoDocumento()));
            ps.setInt   (13, c.getIdCliente());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando cliente: " + e.getMessage(), e);
        }
    }

    // ========================= UTILIDADES =========================
    @Override
    public boolean existsByCc(String cc) {
        final String sql = "SELECT 1 FROM veterinaria.cliente WHERE cc=? LIMIT 1";
        try (Connection cn = ds.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, cc);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error validando CC: " + e.getMessage(), e);
        }
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM veterinaria.cliente";
        try (Connection cn = ds.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Error contando clientes: " + e.getMessage(), e);
        }
    }

    // ========================= Helpers =========================
    private String baseSelect() {
        return """
            SELECT c.idcliente, c.nombre, c.apellidos, c.tipopersona, c.cc, c.fechanacimiento,
                   c.email, c.direccion, c.telefono, c.nombrecontactoemergencia,
                   c.telefonocontactoemergencia, c.ciudad, c.tipodocumento
            FROM veterinaria.cliente c
        """;
    }

    private Cliente mapCliente(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setIdCliente(rs.getInt("idcliente"));
        c.setNombre(rs.getString("nombre"));
        c.setApellidos(rs.getString("apellidos"));
        c.setTipoPersona(rs.getString("tipopersona"));
        c.setCc(rs.getString("cc"));
        c.setFechaNacimiento(rs.getDate("fechanacimiento"));
        c.setEmail(rs.getString("email"));
        c.setDireccion(rs.getString("direccion"));
        c.setTelefono(rs.getString("telefono"));
        c.setNombreContacto(rs.getString("nombrecontactoemergencia"));
        c.setTelefonoContacto(rs.getString("telefonocontactoemergencia"));
        c.setCiudad(rs.getString("ciudad"));
        c.setTipoDocumento(rs.getString("tipodocumento"));
        c.setMascotas(new ArrayList<>()); // se llena aparte si se pide
        return c;
    }

    private List<Mascota> loadMascotas(Connection cn, int idCliente) throws SQLException {
        final String sql = """
            SELECT idmascota, nombre, raza, especie, sexo, numerochip, edad
            FROM veterinaria.mascota WHERE cliente_idcliente = ?
            ORDER BY idmascota
        """;
        List<Mascota> list = new ArrayList<>();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Mascota m = new Mascota();
                    m.setIdMascota(rs.getInt("idmascota"));
                    m.setNombre(rs.getString("nombre"));
                    m.setRaza(rs.getString("raza"));
                    m.setEspecie(rs.getString("especie"));

                    // Manejo seguro de sexo
                    String sexoStr = rs.getString("sexo");
                    if (sexoStr != null && !sexoStr.isEmpty()) {
                        m.setSexo(sexoStr.charAt(0));
                    } else {
                        m.setSexo('N'); // 'N' = No especificado por defecto
                    }

                    m.setNumeroChip(rs.getString("numerochip"));
                    m.setEdad(rs.getInt("edad"));
                    list.add(m);
                }
            }
        }
        return list;
    }

    private static java.sql.Date toSqlDate(java.util.Date d) {
        if (d == null) return null;
        return new java.sql.Date(d.getTime());
    }

    private static String nvl(String s) {
        return s == null ? "" : s.trim();
    }
}
