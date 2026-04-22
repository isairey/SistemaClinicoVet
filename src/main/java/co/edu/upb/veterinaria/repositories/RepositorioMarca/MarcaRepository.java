package co.edu.upb.veterinaria.repositories.RepositorioMarca;

import co.edu.upb.veterinaria.config.DatabaseConfig;
import co.edu.upb.veterinaria.models.ModeloMarca.Marca;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * MarcaRepository
 * ----------------
 * - Acceso a BD encapsulado (caja negra).
 * - CRUD con delete físico (eliminación permanente).
 * - Consultas SIEMPRE parametrizadas (PreparedStatement con '?').
 * - ORDER BY protegido con whitelist de columnas/dirección.
 *
 * Tabla: veterinaria.marca
 *  - idmarca (PK, integer, AUTO INCREMENT)
 *  - nombremarca (varchar, NOT NULL)
 *  - descripcion (varchar, NOT NULL) ⚠️ IMPORTANTE: Es NOT NULL en BD
 *
 * Modelo: Marca(idMarca:int, nombreMarca:String, descripcion:String)
 */
public class MarcaRepository {

    // ---------- Infra ----------
    private final DataSource dataSource;

    public MarcaRepository() {
        this.dataSource = DatabaseConfig.getDataSource();
    }

    // ---------- Helpers de seguridad/util ----------

    /** Patrón para ILIKE '%q%'. */
    private String likeParam(String q) {
        return (q == null || q.isBlank()) ? "%" : "%" + q.trim() + "%";
    }

    /** Whitelist de columnas permitidas para ORDER BY. */
    private String validateSortColumn(String sort) {
        if (sort == null) return "idmarca";
        switch (sort.toLowerCase()) {
            case "nombremarca":
            case "nombre":
                return "nombremarca";
            case "descripcion":
                return "descripcion";
            case "id":
            case "idmarca":
            default:
                return "idmarca";
        }
    }

    /** Dirección de orden segura. */
    private String sortDir(boolean asc) {
        return asc ? "ASC" : "DESC";
    }

    /** Mapeo de una fila al modelo Marca. */
    private Marca mapRow(ResultSet rs) throws SQLException {
        return new Marca(
                rs.getInt("idmarca"),
                rs.getString("nombremarca"),
                rs.getString("descripcion")
        );
    }

    // ---------- CRUD ----------

    /**
     * INSERT con auto-increment (no se pasa el id).
     * @return id generado automáticamente por la BD.
     */
    public int create(Marca marca) throws SQLException {
        final String sql =
                "INSERT INTO veterinaria.marca (nombremarca, descripcion) VALUES (?, ?) RETURNING idmarca";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, marca.getNombreMarca());

            // ⚠️ IMPORTANTE: descripcion es NOT NULL en BD
            // Si no se proporciona, usamos cadena vacía en lugar de NULL
            if (marca.getDescripcion() != null && !marca.getDescripcion().isBlank()) {
                ps.setString(2, marca.getDescripcion());
            } else {
                ps.setString(2, ""); // Cadena vacía en lugar de NULL
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            throw new SQLException("No se devolvió idmarca en INSERT");
        }
    }

    /**
     * UPDATE por id.
     * @return true si afectó filas.
     */
    public boolean update(Marca marca) throws SQLException {
        final String sql =
                "UPDATE veterinaria.marca SET nombremarca = ?, descripcion = ? WHERE idmarca = ?";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, marca.getNombreMarca());

            // ⚠️ IMPORTANTE: descripcion es NOT NULL en BD
            // Si no se proporciona, usamos cadena vacía
            if (marca.getDescripcion() != null && !marca.getDescripcion().isBlank()) {
                ps.setString(2, marca.getDescripcion());
            } else {
                ps.setString(2, ""); // Cadena vacía en lugar de NULL
            }

            ps.setInt(3, marca.getIdMarca());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * DELETE físico: elimina permanentemente la marca de la BD.
     * ⚠️ ADVERTENCIA: Esta operación NO se puede deshacer.
     *
     * IMPORTANTE: Si hay productos usando esta marca, la BD lanzará error
     * de violación de FK (a menos que esté configurado ON DELETE CASCADE).
     *
     * @param id ID de la marca a eliminar
     * @return true si se eliminó, false si no existía
     * @throws SQLException si hay error de BD (ej. violación de FK)
     */
    public boolean delete(int id) throws SQLException {
        final String sql = "DELETE FROM veterinaria.marca WHERE idmarca = ?";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * SELECT por PK.
     */
    public Optional<Marca> findById(int id) throws SQLException {
        final String sql =
                "SELECT idmarca, nombremarca, descripcion FROM veterinaria.marca WHERE idmarca = ?";
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
    public Optional<Marca> findByNombre(String nombreMarca) throws SQLException {
        final String sql =
                "SELECT idmarca, nombremarca, descripcion " +
                        "FROM veterinaria.marca " +
                        "WHERE LOWER(nombremarca) = LOWER(?)";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nombreMarca);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    /**
     * Listado con filtro (ILIKE), orden y paginación.
     * - Filtro busca en nombre y descripción.
     * - ORDER BY con columnas whitelisted: idmarca | nombremarca | descripcion.
     */
    public List<Marca> findAll(String q, String sort, boolean asc, int limit, int offset) throws SQLException {
        final String sortCol = validateSortColumn(sort);
        final String dir = sortDir(asc);

        StringBuilder sql = new StringBuilder(
                "SELECT idmarca, nombremarca, descripcion " +
                        "FROM veterinaria.marca " +
                        "WHERE (nombremarca ILIKE ? OR COALESCE(descripcion, '') ILIKE ?) " +
                        "ORDER BY " + sortCol + " " + dir
        );
        if (limit > 0) {
            sql.append(" LIMIT ? OFFSET ?");
        }

        List<Marca> out = new ArrayList<>();
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            int i = 1;
            String like = likeParam(q);
            ps.setString(i++, like);
            ps.setString(i++, like);
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
                "SELECT COUNT(*) AS c FROM veterinaria.marca " +
                "WHERE (nombremarca ILIKE ? OR COALESCE(descripcion, '') ILIKE ?)";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            String like = likeParam(q);
            ps.setString(1, like);
            ps.setString(2, like);
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
                "SELECT 1 FROM veterinaria.marca WHERE idmarca = ? LIMIT 1";
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
    public boolean existsByNombre(String nombreMarca) throws SQLException {
        final String sql =
                "SELECT 1 FROM veterinaria.marca WHERE LOWER(nombremarca) = LOWER(?) LIMIT 1";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nombreMarca);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // -------------------- UTILIDADES ADICIONALES --------------------

    /**
     * Lista TODAS las marcas (sin paginación).
     * Útil para poblar combos/listas en vistas de registro de productos.
     */
    public List<Marca> findAll() throws SQLException {
        final String sql =
                "SELECT idmarca, nombremarca, descripcion FROM veterinaria.marca ORDER BY nombremarca ASC";
        List<Marca> out = new ArrayList<>();
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapRow(rs));
        }
        return out;
    }
}

