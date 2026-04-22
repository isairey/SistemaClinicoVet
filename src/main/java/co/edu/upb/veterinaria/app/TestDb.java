package co.edu.upb.veterinaria.app;

import co.edu.upb.veterinaria.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestDb {
    public static void main(String[] args) {
        System.out.println("=== Test de Conexión a Base de Datos PostgreSQL (Supabase) ===\n");
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            
            // Información de la conexión
            System.out.println("✓ Conexión exitosa!");
            System.out.println("URL: " + metaData.getURL());
            System.out.println("Usuario: " + metaData.getUserName());
            System.out.println("Driver: " + metaData.getDriverName() + " " + metaData.getDriverVersion());
            System.out.println("Base de datos: " + metaData.getDatabaseProductName() + " " + metaData.getDatabaseProductVersion());
            System.out.println();
            
            // Listar schemas disponibles
            System.out.println("=== Schemas disponibles ===");
            try (ResultSet schemas = metaData.getSchemas()) {
                while (schemas.next()) {
                    String schema = schemas.getString("TABLE_SCHEM");
                    if (!schema.startsWith("pg_") && !schema.equals("information_schema")) {
                        System.out.println(" - " + schema);
                    }
                }
            }
            System.out.println();
            
            // Listar tablas del schema 'veterinaria' (si existe)
            System.out.println("=== Tablas en schema 'veterinaria' ===");
            try (Statement st = conn.createStatement()) {
                ResultSet rs = st.executeQuery(
                        "SELECT table_schema, table_name FROM information_schema.tables " +
                        "WHERE table_schema='veterinaria' ORDER BY table_name"
                );
                int count = 0;
                while (rs.next()) {
                    System.out.println(" - " + rs.getString("table_schema") + "." + rs.getString("table_name"));
                    count++;
                }
                if (count == 0) {
                    System.out.println(" (No hay tablas en el schema 'veterinaria' o el schema no existe)");
                }
            }
            System.out.println();
            
            // Listar todas las tablas del schema público
            System.out.println("=== Tablas en schema 'public' ===");
            try (Statement st = conn.createStatement()) {
                ResultSet rs = st.executeQuery(
                        "SELECT table_schema, table_name FROM information_schema.tables " +
                        "WHERE table_schema='public' AND table_type='BASE TABLE' ORDER BY table_name"
                );
                int count = 0;
                while (rs.next()) {
                    System.out.println(" - " + rs.getString("table_schema") + "." + rs.getString("table_name"));
                    count++;
                }
                if (count == 0) {
                    System.out.println(" (No hay tablas en el schema 'public')");
                }
            }
            System.out.println();
            
            System.out.println("✓ Test completado exitosamente!");
            
        } catch (Exception e) {
            System.err.println("✗ Error durante la prueba de conexión:");
            System.err.println("Mensaje: " + e.getMessage());
            System.err.println("\nStack trace:");
            e.printStackTrace();
        }
    }
}
