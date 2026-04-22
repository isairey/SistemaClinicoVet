package co.edu.upb.veterinaria.services.ServicioClienteMascota;

import co.edu.upb.veterinaria.config.DatabaseConfig;
import co.edu.upb.veterinaria.models.ModeloClienteMascota.ClienteMascotaDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para obtener datos combinados de Cliente y Mascota desde la BD.
 * Implementa Soft Delete temporal usando ClienteEliminadoManager.
 */
public class ClienteMascotaService {

    private final ClienteEliminadoManager eliminadoManager = ClienteEliminadoManager.getInstance();

    /**
     * Obtiene todos los registros de clientes ACTIVOS con sus mascotas.
     * Filtra automáticamente los clientes marcados como eliminados.
     */
    public List<ClienteMascotaDTO> obtenerTodosLosRegistros() throws SQLException {
        List<ClienteMascotaDTO> registros = new ArrayList<>();

        // Agregar condición para excluir eliminados
        String condicionEliminados = eliminadoManager.getSqlCondicionExcluirEliminados("c");

        String sql = """
            SELECT 
                c.idcliente, c.tipopersona, c.tipodocumento, c.cc, c.nombre, c.apellidos,
                c.fechanacimiento, c.ciudad, c.email, c.direccion, c.telefono,
                c.nombrecontactoemergencia, c.telefonocontactoemergencia,
                m.idmascota, m.nombre as mascota_nombre, m.raza, m.especie, m.sexo, m.edad, m.numerochip
            FROM veterinaria.cliente c
            LEFT JOIN veterinaria.mascota m ON c.idcliente = m.cliente_idcliente
            WHERE %s
            ORDER BY c.idcliente, m.idmascota
        """.formatted(condicionEliminados);

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ClienteMascotaDTO dto = new ClienteMascotaDTO();

                // Datos del cliente
                dto.setIdCliente(rs.getInt("idcliente"));
                dto.setTipoPersona(rs.getString("tipopersona"));
                dto.setTipoDocumento(rs.getString("tipodocumento"));
                dto.setNumeroDocumento(rs.getString("cc"));
                dto.setNombre(rs.getString("nombre"));
                dto.setApellidos(rs.getString("apellidos"));
                dto.setFechaNacimiento(rs.getDate("fechanacimiento"));
                dto.setCiudad(rs.getString("ciudad"));
                dto.setEmail(rs.getString("email"));
                dto.setDireccion(rs.getString("direccion"));
                dto.setTelefono(rs.getString("telefono"));
                dto.setContactoNombre(rs.getString("nombrecontactoemergencia"));
                dto.setContactoTelefono(rs.getString("telefonocontactoemergencia"));

                // Datos de la mascota (pueden ser null)
                Integer idMascota = rs.getObject("idmascota", Integer.class);
                dto.setIdMascota(idMascota);
                dto.setMascotaNombre(rs.getString("mascota_nombre"));
                dto.setRaza(rs.getString("raza"));
                dto.setEspecie(rs.getString("especie"));

                // Convertir char a String para sexo
                String sexoStr = rs.getString("sexo");
                dto.setSexo(sexoStr);

                dto.setEdad(rs.getObject("edad", Integer.class));
                dto.setNumeroChip(rs.getString("numerochip"));

                registros.add(dto);
            }
        }

        System.out.println("✓ Cargados " + registros.size() + " registros (excluyendo " +
                         eliminadoManager.getClientesEliminados().size() + " eliminados)");
        return registros;
    }

    /**
     * Busca registros por texto (busca en nombre, apellidos, documento, ciudad, email, nombre de mascota).
     * Filtra automáticamente los clientes eliminados.
     */
    public List<ClienteMascotaDTO> buscarRegistros(String textoBusqueda) throws SQLException {
        List<ClienteMascotaDTO> registros = new ArrayList<>();

        String condicionEliminados = eliminadoManager.getSqlCondicionExcluirEliminados("c");

        String sql = """
            SELECT 
                c.idcliente, c.tipopersona, c.tipodocumento, c.cc, c.nombre, c.apellidos,
                c.fechanacimiento, c.ciudad, c.email, c.direccion, c.telefono,
                c.nombrecontactoemergencia, c.telefonocontactoemergencia,
                m.idmascota, m.nombre as mascota_nombre, m.raza, m.especie, m.sexo, m.edad, m.numerochip
            FROM veterinaria.cliente c
            LEFT JOIN veterinaria.mascota m ON c.idcliente = m.cliente_idcliente
            WHERE %s
            AND (LOWER(c.nombre) LIKE LOWER(?) 
               OR LOWER(c.apellidos) LIKE LOWER(?)
               OR LOWER(c.cc) LIKE LOWER(?)
               OR LOWER(c.ciudad) LIKE LOWER(?)
               OR LOWER(c.email) LIKE LOWER(?)
               OR LOWER(m.nombre) LIKE LOWER(?)
               OR LOWER(m.raza) LIKE LOWER(?)
               OR LOWER(m.especie) LIKE LOWER(?))
            ORDER BY c.idcliente, m.idmascota
        """.formatted(condicionEliminados);

        String parametro = "%" + textoBusqueda + "%";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 1; i <= 8; i++) {
                stmt.setString(i, parametro);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ClienteMascotaDTO dto = new ClienteMascotaDTO();

                    dto.setIdCliente(rs.getInt("idcliente"));
                    dto.setTipoPersona(rs.getString("tipopersona"));
                    dto.setTipoDocumento(rs.getString("tipodocumento"));
                    dto.setNumeroDocumento(rs.getString("cc"));
                    dto.setNombre(rs.getString("nombre"));
                    dto.setApellidos(rs.getString("apellidos"));
                    dto.setFechaNacimiento(rs.getDate("fechanacimiento"));
                    dto.setCiudad(rs.getString("ciudad"));
                    dto.setEmail(rs.getString("email"));
                    dto.setDireccion(rs.getString("direccion"));
                    dto.setTelefono(rs.getString("telefono"));
                    dto.setContactoNombre(rs.getString("nombrecontactoemergencia"));
                    dto.setContactoTelefono(rs.getString("telefonocontactoemergencia"));

                    Integer idMascota = rs.getObject("idmascota", Integer.class);
                    dto.setIdMascota(idMascota);
                    dto.setMascotaNombre(rs.getString("mascota_nombre"));
                    dto.setRaza(rs.getString("raza"));
                    dto.setEspecie(rs.getString("especie"));
                    dto.setSexo(rs.getString("sexo"));
                    dto.setEdad(rs.getObject("edad", Integer.class));
                    dto.setNumeroChip(rs.getString("numerochip"));

                    registros.add(dto);
                }
            }
        }

        return registros;
    }

    /**
     * Filtra registros por tipo de persona o ciudad.
     * Excluye automáticamente los clientes eliminados.
     */
    public List<ClienteMascotaDTO> filtrarRegistros(String tipoPersona, String ciudad) throws SQLException {
        List<ClienteMascotaDTO> registros = new ArrayList<>();

        String condicionEliminados = eliminadoManager.getSqlCondicionExcluirEliminados("c");

        StringBuilder sql = new StringBuilder("""
            SELECT 
                c.idcliente, c.tipopersona, c.tipodocumento, c.cc, c.nombre, c.apellidos,
                c.fechanacimiento, c.ciudad, c.email, c.direccion, c.telefono,
                c.nombrecontactoemergencia, c.telefonocontactoemergencia,
                m.idmascota, m.nombre as mascota_nombre, m.raza, m.especie, m.sexo, m.edad, m.numerochip
            FROM veterinaria.cliente c
            LEFT JOIN veterinaria.mascota m ON c.idcliente = m.cliente_idcliente
            WHERE %s
        """.formatted(condicionEliminados));

        List<String> parametros = new ArrayList<>();

        if (tipoPersona != null && !tipoPersona.isEmpty() && !tipoPersona.equals("Todos")) {
            sql.append(" AND LOWER(c.tipopersona) = LOWER(?)");
            parametros.add(tipoPersona);
        }

        if (ciudad != null && !ciudad.isEmpty() && !ciudad.equals("Todas")) {
            sql.append(" AND LOWER(c.ciudad) = LOWER(?)");
            parametros.add(ciudad);
        }

        sql.append(" ORDER BY c.idcliente, m.idmascota");

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {
                stmt.setString(i + 1, parametros.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ClienteMascotaDTO dto = new ClienteMascotaDTO();

                    dto.setIdCliente(rs.getInt("idcliente"));
                    dto.setTipoPersona(rs.getString("tipopersona"));
                    dto.setTipoDocumento(rs.getString("tipodocumento"));
                    dto.setNumeroDocumento(rs.getString("cc"));
                    dto.setNombre(rs.getString("nombre"));
                    dto.setApellidos(rs.getString("apellidos"));
                    dto.setFechaNacimiento(rs.getDate("fechanacimiento"));
                    dto.setCiudad(rs.getString("ciudad"));
                    dto.setEmail(rs.getString("email"));
                    dto.setDireccion(rs.getString("direccion"));
                    dto.setTelefono(rs.getString("telefono"));
                    dto.setContactoNombre(rs.getString("nombrecontactoemergencia"));
                    dto.setContactoTelefono(rs.getString("telefonocontactoemergencia"));

                    Integer idMascota = rs.getObject("idmascota", Integer.class);
                    dto.setIdMascota(idMascota);
                    dto.setMascotaNombre(rs.getString("mascota_nombre"));
                    dto.setRaza(rs.getString("raza"));
                    dto.setEspecie(rs.getString("especie"));
                    dto.setSexo(rs.getString("sexo"));
                    dto.setEdad(rs.getObject("edad", Integer.class));
                    dto.setNumeroChip(rs.getString("numerochip"));

                    registros.add(dto);
                }
            }
        }

        return registros;
    }

    /**
     * SOFT DELETE: Marca un cliente como eliminado sin borrarlo físicamente de la BD.
     * Mantiene toda la trazabilidad (ventas, mascotas, historiales).
     *
     * @param idCliente ID del cliente a marcar como eliminado
     */
    public void eliminarCliente(int idCliente) throws SQLException {
        // Verificar que el cliente existe en la BD
        if (!clienteExiste(idCliente)) {
            throw new SQLException("No se encontró el cliente con ID: " + idCliente);
        }

        // Verificar si ya está eliminado
        if (eliminadoManager.estaEliminado(idCliente)) {
            throw new SQLException("El cliente ya está marcado como eliminado");
        }

        // Marcar como eliminado (SOFT DELETE)
        eliminadoManager.marcarComoEliminado(idCliente);

        System.out.println("✓ Cliente ID=" + idCliente + " marcado como ELIMINADO (soft delete)");
        System.out.println("  → Los datos permanecen en la BD para auditoría");
        System.out.println("  → No se afectan ventas, mascotas ni historiales");
    }

    /**
     * RESTAURAR: Revierte la eliminación lógica de un cliente.
     *
     * @param idCliente ID del cliente a restaurar
     */
    public void restaurarCliente(int idCliente) throws SQLException {
        if (!eliminadoManager.estaEliminado(idCliente)) {
            throw new SQLException("El cliente no está marcado como eliminado");
        }

        eliminadoManager.restaurar(idCliente);
        System.out.println("✓ Cliente ID=" + idCliente + " RESTAURADO");
    }

    /**
     * Verifica si un cliente existe en la base de datos.
     */
    private boolean clienteExiste(int idCliente) throws SQLException {
        String sql = "SELECT COUNT(*) FROM veterinaria.cliente WHERE idcliente = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCliente);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }

    /**
     * Verifica si un cliente tiene ventas asociadas.
     *
     * @param idCliente ID del cliente
     * @return true si tiene ventas, false si no
     */
    public boolean clienteTieneVentas(int idCliente) throws SQLException {
        String sql = "SELECT COUNT(*) FROM veterinaria.venta WHERE cliente_idcliente = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCliente);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }

    /**
     * Obtiene información sobre las dependencias de un cliente.
     *
     * @param idCliente ID del cliente
     * @return String con información sobre dependencias
     */
    public String obtenerInfoDependencias(int idCliente) throws SQLException {
        StringBuilder info = new StringBuilder();

        try (Connection conn = DatabaseConfig.getDataSource().getConnection()) {
            // Contar ventas
            String sqlVentas = "SELECT COUNT(*) FROM veterinaria.venta WHERE cliente_idcliente = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlVentas)) {
                ps.setInt(1, idCliente);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int numVentas = rs.getInt(1);
                    info.append("• Ventas: ").append(numVentas).append("\n");
                }
            }

            // Contar mascotas
            String sqlMascotas = "SELECT COUNT(*) FROM veterinaria.mascota WHERE cliente_idcliente = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlMascotas)) {
                ps.setInt(1, idCliente);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int numMascotas = rs.getInt(1);
                    info.append("• Mascotas: ").append(numMascotas).append("\n");
                }
            }

            // Contar historiales clínicos
            String sqlHistoriales = """
                SELECT COUNT(*) FROM veterinaria.historialclinica hc
                INNER JOIN veterinaria.mascota m ON hc.mascota_idmascota = m.idmascota
                WHERE m.cliente_idcliente = ?
            """;
            try (PreparedStatement ps = conn.prepareStatement(sqlHistoriales)) {
                ps.setInt(1, idCliente);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int numHistoriales = rs.getInt(1);
                    info.append("• Historiales Clínicos: ").append(numHistoriales).append("\n");
                }
            }
        }

        return info.toString();
    }

    /**
     * Filtra registros por campo específico y valor de búsqueda.
     * Excluye automáticamente clientes eliminados.
     */
    public List<ClienteMascotaDTO> filtrarPorCampo(String campo, String valor) throws SQLException {
        List<ClienteMascotaDTO> registros = new ArrayList<>();

        String condicionEliminados = eliminadoManager.getSqlCondicionExcluirEliminados("c");

        // Mapeo de campos amigables a nombres de columnas en BD
        String columna = switch (campo.toLowerCase()) {
            case "nombre" -> "c.nombre";
            case "apellidos" -> "c.apellidos";
            case "tipo persona" -> "c.tipopersona";
            case "tipo documento" -> "c.tipodocumento";
            case "número documento", "documento" -> "c.cc";
            case "ciudad" -> "c.ciudad";
            case "email" -> "c.email";
            case "dirección" -> "c.direccion";
            case "teléfono" -> "c.telefono";
            case "fecha nacimiento" -> "c.fechanacimiento";
            case "contacto emergencia" -> "c.nombrecontactoemergencia";
            case "mascota" -> "m.nombre";
            case "raza" -> "m.raza";
            case "especie" -> "m.especie";
            default -> throw new SQLException("Campo no válido: " + campo);
        };

        String sql = """
            SELECT 
                c.idcliente, c.tipopersona, c.tipodocumento, c.cc, c.nombre, c.apellidos,
                c.fechanacimiento, c.ciudad, c.email, c.direccion, c.telefono,
                c.nombrecontactoemergencia, c.telefonocontactoemergencia,
                m.idmascota, m.nombre as mascota_nombre, m.raza, m.especie, m.sexo, m.edad, m.numerochip
            FROM veterinaria.cliente c
            LEFT JOIN veterinaria.mascota m ON c.idcliente = m.cliente_idcliente
            WHERE %s AND LOWER(%s) LIKE LOWER(?)
            ORDER BY c.idcliente, m.idmascota
        """.formatted(condicionEliminados, columna);

        String parametro = "%" + valor + "%";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, parametro);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ClienteMascotaDTO dto = new ClienteMascotaDTO();

                    dto.setIdCliente(rs.getInt("idcliente"));
                    dto.setTipoPersona(rs.getString("tipopersona"));
                    dto.setTipoDocumento(rs.getString("tipodocumento"));
                    dto.setNumeroDocumento(rs.getString("cc"));
                    dto.setNombre(rs.getString("nombre"));
                    dto.setApellidos(rs.getString("apellidos"));
                    dto.setFechaNacimiento(rs.getDate("fechanacimiento"));
                    dto.setCiudad(rs.getString("ciudad"));
                    dto.setEmail(rs.getString("email"));
                    dto.setDireccion(rs.getString("direccion"));
                    dto.setTelefono(rs.getString("telefono"));
                    dto.setContactoNombre(rs.getString("nombrecontactoemergencia"));
                    dto.setContactoTelefono(rs.getString("telefonocontactoemergencia"));

                    Integer idMascota = rs.getObject("idmascota", Integer.class);
                    dto.setIdMascota(idMascota);
                    dto.setMascotaNombre(rs.getString("mascota_nombre"));
                    dto.setRaza(rs.getString("raza"));
                    dto.setEspecie(rs.getString("especie"));
                    dto.setSexo(rs.getString("sexo"));
                    dto.setEdad(rs.getObject("edad", Integer.class));
                    dto.setNumeroChip(rs.getString("numerochip"));

                    registros.add(dto);
                }
            }
        }

        return registros;
    }

    /**
     * Obtiene todos los tipos de persona únicos.
     */
    public List<String> obtenerTiposPersona() throws SQLException {
        List<String> tipos = new ArrayList<>();
        String sql = "SELECT DISTINCT tipopersona FROM veterinaria.cliente ORDER BY tipopersona";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String tipo = rs.getString("tipopersona");
                if (tipo != null && !tipo.trim().isEmpty()) {
                    tipos.add(tipo);
                }
            }
        }

        return tipos;
    }

    /**
     * Obtiene todos los tipos de documento únicos.
     */
    public List<String> obtenerTiposDocumento() throws SQLException {
        List<String> tipos = new ArrayList<>();
        String sql = "SELECT DISTINCT tipodocumento FROM veterinaria.cliente ORDER BY tipodocumento";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String tipo = rs.getString("tipodocumento");
                if (tipo != null && !tipo.trim().isEmpty()) {
                    tipos.add(tipo);
                }
            }
        }

        return tipos;
    }
}
