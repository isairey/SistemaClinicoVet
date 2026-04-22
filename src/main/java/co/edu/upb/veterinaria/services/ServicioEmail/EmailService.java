package co.edu.upb.veterinaria.services.ServicioEmail;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

/**
 * Servicio para envío de emails
 * Configurado para Gmail, pero puede adaptarse a otros proveedores
 */
public class EmailService {

    // Credenciales leídas desde variables de entorno (ver .env.example)
    private static final String SMTP_HOST = System.getenv().getOrDefault("SMTP_HOST", "smtp.gmail.com");
    private static final String SMTP_PORT = System.getenv().getOrDefault("SMTP_PORT", "587");
    private static final String EMAIL_FROM = System.getenv().getOrDefault("EMAIL_FROM", "");
    private static final String EMAIL_PASSWORD = System.getenv().getOrDefault("EMAIL_PASS", "");
    private static final String EMAIL_FROM_NAME = System.getenv().getOrDefault("EMAIL_FROM_NAME", "SOS Veterinaria");

    /**
     * Envía un email con código OTP para recuperación de contraseña
     * 
     * @param emailDestino Email del destinatario
     * @param codigoOtp Código OTP de 6 dígitos
     * @throws MessagingException Si hay error al enviar el email
     */
    public void enviarCodigoRecuperacion(String emailDestino, String codigoOtp) throws MessagingException {
        String asunto = "Código de Recuperación de Contraseña - SOS Veterinaria";
        String cuerpo = construirHtmlRecuperacion(codigoOtp);
        
        enviarEmail(emailDestino, asunto, cuerpo, true);
    }

    /**
     * Envía un email genérico
     * 
     * @param emailDestino Email del destinatario
     * @param asunto Asunto del email
     * @param cuerpo Cuerpo del email (puede ser HTML si isHtml = true)
     * @param isHtml Si el cuerpo es HTML
     * @throws MessagingException Si hay error al enviar el email
     */
    public void enviarEmail(String emailDestino, String asunto, String cuerpo, boolean isHtml) 
            throws MessagingException {
        
        // Configurar propiedades SMTP
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", SMTP_HOST);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        // Crear sesión con autenticación
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
            }
        });

        // Crear mensaje
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM, EMAIL_FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailDestino));
            message.setSubject(asunto);

            if (isHtml) {
                message.setContent(cuerpo, "text/html; charset=utf-8");
            } else {
                message.setText(cuerpo);
            }

            // Enviar
            Transport.send(message);
            
        } catch (java.io.UnsupportedEncodingException e) {
            throw new MessagingException("Error de codificación al crear el mensaje", e);
        } catch (MessagingException e) {
            throw e;
        }
    }

    /**
     * Construye el HTML para el email de recuperación de contraseña
     */
    private String construirHtmlRecuperacion(String codigoOtp) {
        return "<!DOCTYPE html>" +
                "<html lang='es'>" +
                "<head>" +
                "  <meta charset='UTF-8'>" +
                "  <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "  <style>" +
                "    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }" +
                "    .container { max-width: 600px; margin: 0 auto; background-color: white; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
                "    .header { background: linear-gradient(135deg, #113051 0%, #2E3F52 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }" +
                "    .content { padding: 40px 30px; }" +
                "    .code-box { background-color: #f8f9fa; border: 2px dashed #113051; border-radius: 8px; padding: 20px; text-align: center; margin: 30px 0; }" +
                "    .code { font-size: 36px; font-weight: bold; color: #113051; letter-spacing: 8px; font-family: 'Courier New', monospace; }" +
                "    .warning { background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; border-radius: 4px; }" +
                "    .footer { background-color: #f8f9fa; padding: 20px; text-align: center; color: #6c757d; font-size: 12px; border-radius: 0 0 10px 10px; }" +
                "    .btn { display: inline-block; padding: 12px 30px; background-color: #113051; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }" +
                "  </style>" +
                "</head>" +
                "<body>" +
                "  <div class='container'>" +
                "    <div class='header'>" +
                "      <h1>🔒 Recuperación de Contraseña</h1>" +
                "      <p>SOS Veterinaria</p>" +
                "    </div>" +
                "    <div class='content'>" +
                "      <h2>Hola,</h2>" +
                "      <p>Recibimos una solicitud para restablecer tu contraseña. Usa el siguiente código de verificación:</p>" +
                "      <div class='code-box'>" +
                "        <p style='margin: 0; color: #6c757d; font-size: 14px;'>Tu código de verificación es:</p>" +
                "        <div class='code'>" + codigoOtp + "</div>" +
                "      </div>" +
                "      <div class='warning'>" +
                "        <strong>⏰ Importante:</strong> Este código expirará en <strong>15 minutos</strong>." +
                "      </div>" +
                "      <p>Si no solicitaste este cambio, puedes ignorar este mensaje de forma segura.</p>" +
                "      <p style='margin-top: 30px;'>Saludos,<br><strong>Equipo de SOS Veterinaria</strong></p>" +
                "    </div>" +
                "    <div class='footer'>" +
                "      <p>Este es un mensaje automático, por favor no respondas a este email.</p>" +
                "      <p>&copy; 2025 SOS Veterinaria. Todos los derechos reservados.</p>" +
                "    </div>" +
                "  </div>" +
                "</body>" +
                "</html>";
    }

    /**
     * Valida la configuración del servicio de email
     */
    public boolean validarConfiguracion() {
        return !EMAIL_FROM.isEmpty() && !EMAIL_PASSWORD.isEmpty();
    }
}
