package co.edu.upb.veterinaria.services.ServicioUsuario;

import at.favre.lib.crypto.bcrypt.BCrypt;
import co.edu.upb.veterinaria.models.ModeloModulo.Modulo;
import co.edu.upb.veterinaria.models.ModeloUsuario.Usuario;
import co.edu.upb.veterinaria.repositories.RepositorioModulo.ModuloRepository;
import co.edu.upb.veterinaria.repositories.RepositorioUsuario.UsuarioRepository;
import co.edu.upb.veterinaria.repositories.RepositorioUsuarioModulo.UsuarioModuloRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de usuarios y sus permisos (módulos)
 */
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ModuloRepository moduloRepository;
    private final UsuarioModuloRepository usuarioModuloRepository;

    public UsuarioService() {
        this.usuarioRepository = new UsuarioRepository();
        this.moduloRepository = new ModuloRepository();
        this.usuarioModuloRepository = new UsuarioModuloRepository();
    }

    /**
     * Crea un nuevo usuario con módulos asignados
     * @param usuario El usuario a crear (la contraseña será encriptada automáticamente)
     * @param moduloIds Lista de IDs de módulos a asignar
     * @return El ID del usuario creado
     * @throws IllegalArgumentException Si las validaciones fallan
     * @throws SQLException Si hay error en la BD
     */
    public int crearUsuario(Usuario usuario, List<Integer> moduloIds) throws SQLException {
        // Validaciones
        validarUsuario(usuario);
        
        if (moduloIds == null || moduloIds.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un módulo para el usuario");
        }
        
        // Verificar que el username no exista
        if (usuarioRepository.existsByUsuario(usuario.getUsuario())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso");
        }
        
        // Verificar que el email no exista
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        
        // Encriptar la contraseña
        String hashedPassword = encriptarContrasena(usuario.getContrasena());
        usuario.setContrasena(hashedPassword);
        
        // Guardar el usuario
        int usuarioId = usuarioRepository.save(usuario);
        
        // Asignar los módulos
        usuarioModuloRepository.saveAll(usuarioId, moduloIds);
        
        return usuarioId;
    }

    /**
     * Actualiza un usuario existente y sus módulos
     */
    public void actualizarUsuario(Usuario usuario, List<Integer> moduloIds) throws SQLException {
        validarUsuario(usuario);
        
        if (moduloIds == null || moduloIds.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un módulo para el usuario");
        }
        
        // Verificar que el usuario exista
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(usuario.getIdUsuario());
        if (usuarioExistente.isEmpty()) {
            throw new IllegalArgumentException("El usuario no existe");
        }
        
        // Si la contraseña cambió, encriptarla
        if (!usuario.getContrasena().startsWith("$2a$")) { // No es un hash BCrypt
            String hashedPassword = encriptarContrasena(usuario.getContrasena());
            usuario.setContrasena(hashedPassword);
        }
        
        // Actualizar usuario
        usuarioRepository.update(usuario);
        
        // Actualizar módulos
        usuarioModuloRepository.updateModulosForUsuario(usuario.getIdUsuario(), moduloIds);
    }

    /**
     * Actualiza solo los datos del usuario (sin cambiar contraseña ni módulos)
     */
    public void actualizarDatosUsuario(Usuario usuario) throws SQLException {
        validarUsuario(usuario);
        
        // Verificar que el usuario exista y mantener su contraseña actual
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(usuario.getIdUsuario());
        if (usuarioExistente.isEmpty()) {
            throw new IllegalArgumentException("El usuario no existe");
        }
        
        // Mantener la contraseña actual
        usuario.setContrasena(usuarioExistente.get().getContrasena());
        
        usuarioRepository.update(usuario);
    }

    /**
     * Cambia la contraseña de un usuario
     */
    public void cambiarContrasena(int usuarioId, String nuevaContrasena, String confirmarContrasena) throws SQLException {
        if (nuevaContrasena == null || nuevaContrasena.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        
        if (!nuevaContrasena.equals(confirmarContrasena)) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }
        
        if (nuevaContrasena.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("El usuario no existe");
        }
        
        Usuario usuario = usuarioOpt.get();
        usuario.setContrasena(encriptarContrasena(nuevaContrasena));
        usuarioRepository.update(usuario);
    }

    /**
     * Elimina un usuario y sus asignaciones de módulos
     */
    public void eliminarUsuario(int usuarioId) throws SQLException {
        // Las asignaciones de módulos se eliminan automáticamente por CASCADE
        usuarioRepository.deleteById(usuarioId);
    }

    /**
     * Obtiene todos los usuarios
     */
    public List<Usuario> obtenerTodosLosUsuarios() throws SQLException {
        return usuarioRepository.findAll();
    }

    /**
     * Busca usuarios por filtro (nombre, apellido, usuario o email)
     */
    public List<Usuario> buscarUsuarios(String filtro) throws SQLException {
        if (filtro == null || filtro.trim().isEmpty()) {
            return obtenerTodosLosUsuarios();
        }
        return usuarioRepository.findByFilter(filtro.trim());
    }

    /**
     * Obtiene un usuario por ID
     */
    public Optional<Usuario> obtenerUsuarioPorId(int usuarioId) throws SQLException {
        return usuarioRepository.findById(usuarioId);
    }

    /**
     * Obtiene un usuario por nombre de usuario
     */
    public Optional<Usuario> obtenerUsuarioPorNombreUsuario(String usuario) throws SQLException {
        return usuarioRepository.findByUsuario(usuario);
    }

    /**
     * Obtiene todos los módulos del sistema
     */
    public List<Modulo> obtenerTodosLosModulos() throws SQLException {
        return moduloRepository.findAll();
    }

    /**
     * Obtiene los módulos asignados a un usuario
     */
    public List<Modulo> obtenerModulosDeUsuario(int usuarioId) throws SQLException {
        return moduloRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Obtiene los IDs de los módulos asignados a un usuario
     */
    public List<Integer> obtenerIdsModulosDeUsuario(int usuarioId) throws SQLException {
        return usuarioModuloRepository.findModuloIdsByUsuarioId(usuarioId);
    }

    /**
     * Verifica si un usuario tiene acceso a un módulo específico
     */
    public boolean tieneAccesoAModulo(int usuarioId, int moduloId) throws SQLException {
        return usuarioModuloRepository.hasAccess(usuarioId, moduloId);
    }

    /**
     * Verifica si un usuario tiene acceso a un módulo por nombre
     */
    public boolean tieneAccesoAModulo(int usuarioId, String nombreModulo) throws SQLException {
        Optional<Modulo> modulo = moduloRepository.findByNombre(nombreModulo);
        if (modulo.isEmpty()) {
            return false;
        }
        return usuarioModuloRepository.hasAccess(usuarioId, modulo.get().getIdModulo());
    }

    /**
     * Valida las credenciales de un usuario (para login)
     */
    public Optional<Usuario> validarCredenciales(String usuario, String contrasena) throws SQLException {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsuario(usuario);
        
        if (usuarioOpt.isEmpty()) {
            return Optional.empty();
        }
        
        Usuario usuarioEncontrado = usuarioOpt.get();
        
        // Verificar la contraseña
        BCrypt.Result result = BCrypt.verifyer().verify(contrasena.toCharArray(), usuarioEncontrado.getContrasena());
        
        if (result.verified) {
            return Optional.of(usuarioEncontrado);
        }
        
        return Optional.empty();
    }

    /**
     * Encripta una contraseña usando BCrypt
     */
    private String encriptarContrasena(String contrasena) {
        return BCrypt.withDefaults().hashToString(12, contrasena.toCharArray());
    }

    /**
     * Valida los datos básicos de un usuario
     */
    private void validarUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        
        if (usuario.getCc() == null || usuario.getCc().trim().isEmpty()) {
            throw new IllegalArgumentException("La cédula es obligatoria");
        }
        
        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        
        if (usuario.getApellidos() == null || usuario.getApellidos().trim().isEmpty()) {
            throw new IllegalArgumentException("Los apellidos son obligatorios");
        }
        
        if (usuario.getUsuario() == null || usuario.getUsuario().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio");
        }
        
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        
        if (!usuario.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("El email no es válido");
        }
        
        if (usuario.getTelefono() == null || usuario.getTelefono().trim().isEmpty()) {
            throw new IllegalArgumentException("El teléfono es obligatorio");
        }
        
        if (usuario.getDireccion() == null || usuario.getDireccion().trim().isEmpty()) {
            throw new IllegalArgumentException("La dirección es obligatoria");
        }
    }

    // ==================== MÉTODOS DE RECUPERACIÓN DE CONTRASEÑA ====================

    /**
     * Inicia el proceso de recuperación de contraseña generando un código OTP
     * @param email Email del usuario que solicita la recuperación
     * @return El código OTP generado (para pruebas, en producción no debería retornarse)
     * @throws SQLException Si hay error en la BD
     * @throws IllegalArgumentException Si el email no existe
     */
    public String iniciarRecuperacionContrasena(String email) throws SQLException {
        // Buscar usuario por email
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("No existe un usuario registrado con ese email");
        }
        
        Usuario usuario = usuarioOpt.get();
        
        // Generar código OTP de 6 dígitos
        String codigoOtp = generarCodigoOTP();
        
        // Crear token con expiración de 15 minutos
        java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
        java.time.LocalDateTime expiracion = ahora.plusMinutes(15);
        
        co.edu.upb.veterinaria.models.ModeloPasswordResetToken.PasswordResetToken token = 
                new co.edu.upb.veterinaria.models.ModeloPasswordResetToken.PasswordResetToken(
                        usuario.getIdUsuario(),
                        email,
                        codigoOtp,
                        ahora,
                        expiracion
                );
        
        // Guardar token en BD
        co.edu.upb.veterinaria.repositories.RepositorioPasswordResetToken.PasswordResetTokenRepository tokenRepo = 
                new co.edu.upb.veterinaria.repositories.RepositorioPasswordResetToken.PasswordResetTokenRepository();
        tokenRepo.save(token);
        
        // Enviar email con el código
        try {
            co.edu.upb.veterinaria.services.ServicioEmail.EmailService emailService = 
                    new co.edu.upb.veterinaria.services.ServicioEmail.EmailService();
            emailService.enviarCodigoRecuperacion(email, codigoOtp);
        } catch (javax.mail.MessagingException e) {
            // Si falla el envío del email, log pero no fallar el proceso
            System.err.println("⚠️ Error al enviar email: " + e.getMessage());
            // En desarrollo, mostrar el código en consola
            System.out.println("📧 Código OTP para " + email + ": " + codigoOtp);
        }
        
        return codigoOtp; // Solo para pruebas
    }

    /**
     * Valida el código OTP ingresado por el usuario
     * @param email Email del usuario
     * @param codigoOtp Código ingresado por el usuario
     * @return true si el código es válido, false en caso contrario
     * @throws SQLException Si hay error en la BD
     */
    public boolean validarCodigoOTP(String email, String codigoOtp) throws SQLException {
        co.edu.upb.veterinaria.repositories.RepositorioPasswordResetToken.PasswordResetTokenRepository tokenRepo = 
                new co.edu.upb.veterinaria.repositories.RepositorioPasswordResetToken.PasswordResetTokenRepository();
        
        Optional<co.edu.upb.veterinaria.models.ModeloPasswordResetToken.PasswordResetToken> tokenOpt = 
                tokenRepo.findByEmailAndCodigo(email, codigoOtp);
        
        if (tokenOpt.isEmpty()) {
            return false;
        }
        
        co.edu.upb.veterinaria.models.ModeloPasswordResetToken.PasswordResetToken token = tokenOpt.get();
        
        // Verificar que el token sea válido (no usado y no expirado)
        return token.isValido();
    }

    /**
     * Restablece la contraseña de un usuario usando un código OTP válido
     * @param email Email del usuario
     * @param codigoOtp Código OTP previamente validado
     * @param nuevaContrasena Nueva contraseña
     * @param confirmarContrasena Confirmación de la nueva contraseña
     * @throws SQLException Si hay error en la BD
     * @throws IllegalArgumentException Si las validaciones fallan
     */
    public void restablecerContrasenaConOTP(String email, String codigoOtp, 
                                             String nuevaContrasena, String confirmarContrasena) throws SQLException {
        // Validar contraseñas
        if (nuevaContrasena == null || nuevaContrasena.trim().isEmpty()) {
            throw new IllegalArgumentException("La nueva contraseña no puede estar vacía");
        }
        
        if (nuevaContrasena.length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }
        
        if (!nuevaContrasena.equals(confirmarContrasena)) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }
        
        // Validar token OTP
        co.edu.upb.veterinaria.repositories.RepositorioPasswordResetToken.PasswordResetTokenRepository tokenRepo = 
                new co.edu.upb.veterinaria.repositories.RepositorioPasswordResetToken.PasswordResetTokenRepository();
        
        Optional<co.edu.upb.veterinaria.models.ModeloPasswordResetToken.PasswordResetToken> tokenOpt = 
                tokenRepo.findByEmailAndCodigo(email, codigoOtp);
        
        if (tokenOpt.isEmpty()) {
            throw new IllegalArgumentException("Código OTP inválido");
        }
        
        co.edu.upb.veterinaria.models.ModeloPasswordResetToken.PasswordResetToken token = tokenOpt.get();
        
        if (!token.isValido()) {
            throw new IllegalArgumentException("El código OTP ha expirado o ya fue usado");
        }
        
        // Buscar usuario por email
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        
        Usuario usuario = usuarioOpt.get();
        
        // Encriptar nueva contraseña
        String contrasenaEncriptada = encriptarContrasena(nuevaContrasena);
        usuario.setContrasena(contrasenaEncriptada);
        
        // Actualizar usuario en BD
        usuarioRepository.update(usuario);
        
        // Marcar token como usado
        tokenRepo.markAsUsed(token.getIdToken());
    }

    /**
     * Restablece la contraseña de un usuario directamente usando solo el email
     * (Para usar después de validar OTP en pantalla anterior)
     * @param email Email del usuario
     * @param nuevaContrasena Nueva contraseña
     * @param confirmarContrasena Confirmación de la nueva contraseña
     * @throws SQLException Si hay error en la BD
     * @throws IllegalArgumentException Si las validaciones fallan
     */
    public void restablecerContrasenaDirecta(String email, String nuevaContrasena, String confirmarContrasena) throws SQLException {
        // Validar contraseñas
        if (nuevaContrasena == null || nuevaContrasena.trim().isEmpty()) {
            throw new IllegalArgumentException("La nueva contraseña no puede estar vacía");
        }
        
        if (nuevaContrasena.length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }
        
        if (!nuevaContrasena.equals(confirmarContrasena)) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }
        
        // Buscar usuario por email
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        
        Usuario usuario = usuarioOpt.get();
        
        // Encriptar nueva contraseña
        String contrasenaEncriptada = encriptarContrasena(nuevaContrasena);
        usuario.setContrasena(contrasenaEncriptada);
        
        // Actualizar usuario en BD
        usuarioRepository.update(usuario);
    }

    /**
     * Genera un código OTP aleatorio de 6 dígitos
     */
    private String generarCodigoOTP() {
        int codigo = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(codigo);
    }

    /**
     * Limpia tokens expirados de la base de datos
     * @return Cantidad de tokens eliminados
     * @throws SQLException Si hay error en la BD
     */
    public int limpiarTokensExpirados() throws SQLException {
        co.edu.upb.veterinaria.repositories.RepositorioPasswordResetToken.PasswordResetTokenRepository tokenRepo = 
                new co.edu.upb.veterinaria.repositories.RepositorioPasswordResetToken.PasswordResetTokenRepository();
        return tokenRepo.deleteExpiredTokens();
    }
}
