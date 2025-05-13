package rh.Service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailService {

    // IMPORTANT: Replace with your actual Gmail credentials (preferably an App Password)
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SENDER_EMAIL = "dabyain@gmail.com";
    private static final String SENDER_PASSWORD = "tkqo whur qylg umrw";

    public boolean sendPasswordResetEmail(String toEmail, String userName, String token) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", "587"); // Or 465 for SSL

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Password Reset Request");

            // Create a beautiful HTML email template here
            String htmlContent = "<html><body style=\"font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4;\">"
                + "<div style=\"max-width: 600px; margin: auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);\">"
                + "<h2 style=\"color: #333333; text-align: center;\">Password Reset</h2>"
                + "<p style=\"font-size: 16px; color: #555555; line-height: 1.6;\">Hello " + userName + ",</p>"
                + "<p style=\"font-size: 16px; color: #555555; line-height: 1.6;\">You requested a password reset. Please use the following token to reset your password. This token is valid for 1 hour:</p>"
                + "<div style=\"text-align: center; margin: 20px 0;\">"
                + "<span style=\"display: inline-block; padding: 12px 25px; font-size: 20px; color: #ffffff; background-color: #007bff; border-radius: 5px; letter-spacing: 2px;\">"
                + token
                + "</span>"
                + "</div>"
                + "<p style=\"font-size: 16px; color: #555555; line-height: 1.6;\">If you did not request a password reset, please ignore this email.</p>"
                + "<hr style=\"border: 0; border-top: 1px solid #eeeeee; margin: 20px 0;\">"
                + "<p style=\"font-size: 12px; color: #999999; text-align: center;\">This is an automated message. Please do not reply.</p>"
                + "</div></body></html>";
            
            message.setContent(htmlContent, "text/html");

            Transport.send(message);
            System.out.println("Password reset email sent successfully to " + toEmail);
            return true;
        } catch (MessagingException e) {
            System.err.println("Error sending password reset email: " + e.getMessage());
            e.printStackTrace(); // For detailed error logging
            return false;
        }
    }
}
