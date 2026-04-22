package co.edu.upb.veterinaria.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DatabaseConfig {

    private static HikariDataSource ds;

    static {
        HikariConfig config = new HikariConfig();

        // Conexión a PostgreSQL — credenciales desde variables de entorno (ver .env.example)
        String jdbcUrl = System.getenv().getOrDefault("DB_JDBC_URL",
                "jdbc:postgresql://localhost:5432/postgres?currentSchema=veterinaria");
        String dbUser = System.getenv().getOrDefault("DB_USER", "");
        String dbPass = System.getenv().getOrDefault("DB_PASS", "");

        config.setJdbcUrl(jdbcUrl);
        config.setUsername(dbUser);
        config.setPassword(dbPass);

        // Configuración del pool de conexiones HikariCP
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000); // 30 minutos
        config.setPoolName("HikariPool-Veterinaria");

        // Propiedades adicionales para PostgreSQL
        // DESACTIVAR caché de PreparedStatements para evitar conflictos
        config.addDataSourceProperty("cachePrepStmts", "false");
        config.addDataSourceProperty("prepStmtCacheSize", "0");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "0");

        ds = new HikariDataSource(config);
    }

    private DatabaseConfig() {}

    public static DataSource getDataSource() {
        return ds;
    }
}
