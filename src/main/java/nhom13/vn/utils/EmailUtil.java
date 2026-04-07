package nhom13.vn.utils;

import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;

public class EmailUtil {

    private static final String DEFAULT_FROM_EMAIL = "levanphong511@gmail.com";
    private static final String DEFAULT_APP_PASSWORD = "mejxzqzjhjpwrnmx";
    private static final String DEFAULT_SMTP_HOST = "smtp.gmail.com";
    private static final String DEFAULT_SMTP_PORT = "587";
    private static final String DEFAULT_SMTP_AUTH = "true";
    private static final String DEFAULT_SMTP_STARTTLS = "true";

    public static boolean sendEmail(String toEmail, String newPassword) {

        final String fromEmail = getEnv("MAIL_FROM_EMAIL", DEFAULT_FROM_EMAIL);
        final String appPassword = getEnv("MAIL_APP_PASSWORD", DEFAULT_APP_PASSWORD);

        Properties props = new Properties();

        props.put("mail.smtp.host", getEnv("MAIL_SMTP_HOST", DEFAULT_SMTP_HOST));
        props.put("mail.smtp.port", getEnv("MAIL_SMTP_PORT", DEFAULT_SMTP_PORT));
        props.put("mail.smtp.auth", getEnv("MAIL_SMTP_AUTH", DEFAULT_SMTP_AUTH));
        props.put("mail.smtp.starttls.enable", getEnv("MAIL_SMTP_STARTTLS_ENABLE", DEFAULT_SMTP_STARTTLS));

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(fromEmail, appPassword);
                    }
                });

        try {

            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(fromEmail));

            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(toEmail));

            message.setSubject("Reset Password");

            message.setText("Your new password is: " + newPassword);

            Transport.send(message);

            return true;

        } catch (Exception e) {

            e.printStackTrace();
            return false;

        }
    }

    private static String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }
}
