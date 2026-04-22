package co.edu.upb.veterinaria.repositories.RepositorioUnidadMedida;

import co.edu.upb.veterinaria.config.DatabaseConfig;
import co.edu.upb.veterinaria.models.ModeloUnidadMedida.UnidadMedida;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * UnidadMedidaRepository
 * ----------------------
 * - Encapsula todo el acceso a BD (PostgreSQL / Supabase).
 * - CRUD con delete físico (eliminación permanente).
 * - TODAS las consultas usan PreparedStatement con '?'.
 * - ORDER BY se arma con whitelist (columna/dirección seguras).
 *
 * Tabla: veterinaria.unidadmedida
 *  - idunidadmedida (PK, integer, AUTO INCREMENT)
 *  - nombre (varchar, NOT NULL)
 *
 * Modelo: UnidadMedida(idUnidadMedida:int, nombre:String)
 */
public class UnidadMedidaRepository {

    // ---------- Infra ----------
    private final DataSource dataSource;

    public UnidadMedidaRepository() {
        this.dataSource = DatabaseConfig.getDataSource();
    }

    // ---------- Helpers de seguridad/util ----------

    /** Forma el patrón para ILIKE '%q%'. */
    private String likeParam(String q) {
        return (q == null || q.isBlank()) ? "%" : "%" + q.trim() + "%";
    }

    /** Whitelist de columnas permitidas para ORDER BY. */
    private String validateSortColumn(String sort) {
        if (sort == null) return "idunidadmedida";
        switch (sort.toLowerCase()) {
            case "nombre":
                return "nombre";
            case "id":
            case "idunidadmedida":
            default:
                return "idunidadmedida";
        }
    }

    /** Dirección de orden segura. */
    private String sortDir(boolean asc) {
        return asc ? "ASC" : "DESC";
    }

    /** Mapea una fila del ResultSet al modelo. */
    private UnidadMedida mapRow(ResultSet rs) throws SQLException {
        return new UnidadMedida(
                rs.getInt("idunidadmedida"),
                rs.getString("nombre")
        );
    }

    // ---------- CRUD ----------

    /**
     * INSERT con auto-increment (no se pasa el id).
     * @return id generado automáticamente por la BD.
     */
    public int create(UnidadMedida u) throws SQLException {
        final String sql =
                "INSERT INTO veterinaria.unidadmedida (nombre) VALUES (?) RETURNING idunidadmedida";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, u.getNombre());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            throw new SQLException("No se devolvió idunidadmedida en INSERT");
        }
    }

    /**
     * UPDATE por id.
     * @return true si afectó filas.
     */
    public boolean update(UnidadMedida u) throws SQLException {
        final String sql =
                "UPDATE veterinaria.unidadmedida SET nombre = ? WHERE idunidadmedida = ?";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, u.getNombre());
            ps.setInt(2, u.getIdUnidadMedida());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * DELETE físico: elimina permanentemente la unidad de medida de la BD.
     * ⚠️ ADVERTENCIA: Esta operación NO se puede deshacer.
     *
     * IMPORTANTE: Si hay productos usando esta unidad de medida, la BD lanzará error
     * de violación de FK (a menos que esté configurado ON DELETE CASCADE).
     *
     * @param id ID de la unidad de medida a eliminar
     * @return true si se eliminó, false si no existía
     * @throws SQLException si hay error de BD (ej. violación de FK)
     */
    public boolean delete(int id) throws SQLException {
        final String sql = "DELETE FROM veterinaria.unidadmedida WHERE idunidadmedida = ?";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * SELECT por PK.
     */
    public Optional<UnidadMedida> findById(int id) throws SQLException {
        final String sql =
                "SELECT idunidadmedida, nombre FROM veterinaria.unidadmedida WHERE idunidadmedida = ?";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    /**
     * SELECT por nombre exacto (case-insensitive).
     */
    public Optional<UnidadMedida> findByNombre(String nombre) throws SQLException {
        final String sql =
                "SELECT idunidadmedida, nombre " +
                        "FROM veterinaria.unidadmedida " +
                        "WHERE LOWER(nombre) = LOWER(?)";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    /**
     * Listado con filtro (ILIKE), orden y paginación.
     * - Filtro y paginación con '?'.
     * - ORDER BY protegido con whitelist.
     */
    public List<UnidadMedida> findAll(String q, String sort, boolean asc, int limit, int offset) throws SQLException {
        final String sortCol = validateSortColumn(sort);
        final String dir = sortDir(asc);

        StringBuilder sql = new StringBuilder(
                "SELECT idunidadmedida, nombre " +
                        "FROM veterinaria.unidadmedida " +
                        "WHERE nombre ILIKE ? " +
                        "ORDER BY " + sortCol + " " + dir
        );
        if (limit > 0) {
            sql.append(" LIMIT ? OFFSET ?");
        }

        List<UnidadMedida> out = new ArrayList<>();
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            int i = 1;
            ps.setString(i++, likeParam(q));
            if (limit > 0) {
                ps.setInt(i++, limit);
                ps.setInt(i, Math.max(0, offset));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRow(rs));
            }
        }
        return out;
    }

    /**
     * Conteo para paginación (mismo filtro que findAll).
     */
    public long count(String q) throws SQLException {
        final String sql =
                "SELECT COUNT(*) AS c FROM veterinaria.unidadmedida WHERE nombre ILIKE ?";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, likeParam(q));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong("c") : 0L;
            }
        }
    }

    /**
     * ¿Existe por id?
     */
    public boolean existsById(int id) throws SQLException {
        final String sql =
                "SELECT 1 FROM veterinaria.unidadmedida WHERE idunidadmedida = ? LIMIT 1";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * ¿Existe por nombre exacto (case-insensitive)?
     */
    public boolean existsByNombre(String nombre) throws SQLException {
        final String sql =
                "SELECT 1 FROM veterinaria.unidadmedida WHERE LOWER(nombre) = LOWER(?) LIMIT 1";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // -------------------- UTILIDADES ADICIONALES --------------------

    /**
     * Lista TODAS las unidades de medida (sin paginación).
     * Útil para poblar combos/listas en vistas.
     */
    public List<UnidadMedida> findAll() throws SQLException {
        final String sql =
                "SELECT idunidadmedida, nombre FROM veterinaria.unidadmedida ORDER BY nombre ASC";
        List<UnidadMedida> out = new ArrayList<>();
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapRow(rs));
        }
        return out;
    }
}

