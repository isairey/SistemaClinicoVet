package co.edu.upb.veterinaria.services.ServicioClienteMascota;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Gestor temporal de clientes "eliminados" (Soft Delete).
 * Mantiene un registro en memoria y archivo de los IDs de clientes marcados como eliminados.
 * NOTA: Esta es una solución temporal. Lo ideal es agregar una columna 'estado' en la BD.
 */
public class ClienteEliminadoManager {

    private static final String ARCHIVO_ELIMINADOS = "clientes_eliminados.dat";
    private static ClienteEliminadoManager instance;
    private final Set<Integer> clientesEliminados;

    private ClienteEliminadoManager() {
        this.clientesEliminados = new HashSet<>();
        cargarDesdeArchivo();
    }

    /**
     * Singleton para mantener una única instancia.
     */
    public static synchronized ClienteEliminadoManager getInstance() {
        if (instance == null) {
            instance = new ClienteEliminadoManager();
        }
        return instance;
    }

    /**
     * Marca un cliente como eliminado (soft delete).
     */
    public void marcarComoEliminado(int idCliente) {
        clientesEliminados.add(idCliente);
        guardarEnArchivo();
        System.out.println("✓ Cliente ID=" + idCliente + " marcado como ELIMINADO (soft delete)");
    }

    /**
     * Restaura un cliente eliminado.
     */
    public void restaurar(int idCliente) {
        boolean removido = clientesEliminados.remove(idCliente);
        if (removido) {
            guardarEnArchivo();
            System.out.println("✓ Cliente ID=" + idCliente + " RESTAURADO");
        }
    }

    /**
     * Verifica si un cliente está marcado como eliminado.
     */
    public boolean estaEliminado(int idCliente) {
        return clientesEliminados.contains(idCliente);
    }

    /**
     * Obtiene todos los IDs de clientes eliminados.
     */
    public Set<Integer> getClientesEliminados() {
        return new HashSet<>(clientesEliminados);
    }

    /**
     * Genera la condición SQL para filtrar clientes eliminados.
     * Ejemplo: "c.idcliente NOT IN (1, 5, 10)"
     */
    public String getSqlCondicionExcluirEliminados(String aliasTabla) {
        if (clientesEliminados.isEmpty()) {
            return "1=1"; // No hay eliminados, retornar condición siempre verdadera
        }

        StringBuilder sb = new StringBuilder();
        sb.append(aliasTabla).append(".idcliente NOT IN (");

        int count = 0;
        for (Integer id : clientesEliminados) {
            if (count > 0) sb.append(", ");
            sb.append(id);
            count++;
        }

        sb.append(")");
        return sb.toString();
    }

    /**
     * Limpia todos los registros (útil para testing o resetear).
     */
    public void limpiarTodo() {
        clientesEliminados.clear();
        guardarEnArchivo();
        System.out.println("✓ Todos los registros de eliminación limpiados");
    }

    /**
     * Guarda el estado en un archivo para persistencia entre ejecuciones.
     */
    private void guardarEnArchivo() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(ARCHIVO_ELIMINADOS))) {
            oos.writeObject(clientesEliminados);
            System.out.println("→ Estado guardado en archivo (" + clientesEliminados.size() + " eliminados)");
        } catch (IOException e) {
            System.err.println("⚠ Error al guardar archivo de eliminados: " + e.getMessage());
        }
    }

    /**
     * Carga el estado desde el archivo al iniciar.
     */
    @SuppressWarnings("unchecked")
    private void cargarDesdeArchivo() {
        File archivo = new File(ARCHIVO_ELIMINADOS);
        if (!archivo.exists()) {
            System.out.println("→ No hay archivo previo de eliminados");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(archivo))) {
            Set<Integer> cargados = (Set<Integer>) ois.readObject();
            clientesEliminados.addAll(cargados);
            System.out.println("✓ Cargados " + clientesEliminados.size() + " clientes eliminados desde archivo");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("⚠ Error al cargar archivo de eliminados: " + e.getMessage());
        }
    }

    /**
     * Obtiene estadísticas del sistema.
     */
    public String getEstadisticas() {
        return "Clientes marcados como eliminados: " + clientesEliminados.size();
    }
}

