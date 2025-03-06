package org.main.unimapapi.utils;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.*;
import javax.mail.Session;
import javax.mail.Transport;
import org.main.unimapapi.configs.AppConfig;

public class EmailSender {

    public static void sendEmail(String recipient, String code) {
        Runnable emailTask = () -> {

            String sender = AppConfig.getSender();
            String host = AppConfig.getHost();
            String port = AppConfig.getPort();

            Properties properties = System.getProperties();
            properties.setProperty("mail.smtp.host", host);
            properties.setProperty("mail.smtp.port", port);
            properties.setProperty("mail.smtp.auth", "true");
            properties.setProperty("mail.smtp.starttls.enable", "true");

            Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
                protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new javax.mail.PasswordAuthentication(sender, AppConfig.getPassword());
                }
            });

            try {
                // MimeMessage object.
                MimeMessage message = new MimeMessage(session);

                // Set From Field: adding senders email to from field.
                message.setFrom(new InternetAddress(sender));

                // Set To Field: adding recipient's email to from field.
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));

                // Set Subject: subject of the email
                message.setSubject("UniMap Confirmation Code");

                // set body of the email.
                message.setText("Hi, your confirmation code is: " + code + "\n\n" +
                        "This code will expire in 5 minute!" + "\n\n" + "Best regards, UniMap Team");

                // Send email.
                Transport.send(message);
                System.out.println("Mail successfully sent");
            } catch (MessagingException mex) {
                ServerLogger.logServer(ServerLogger.Level.ERROR, "Failed to send email to " + recipient + ": " + mex.getMessage());
            }
        };
        Thread emailThread = new Thread(emailTask);
        emailThread.start();
    }
}