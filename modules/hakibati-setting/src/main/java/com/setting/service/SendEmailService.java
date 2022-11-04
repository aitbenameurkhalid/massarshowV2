package com.setting.service;

import com.axelor.app.AppSettings;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmailService {
  public static void sendEmail(String to, String EmailSubject, String EmailText) {
    //        SendMail.main();

    // Recipient's email ID needs to be mentioned.
    //  String to = "tablelycee@gmail.com";

    // Sender's email ID needs to be mentioned
    String from = AppSettings.get().get("mail.smtp.user", "myhakibati@gmail.com");

    // Assuming you are sending email from through gmails smtp
    String host = AppSettings.get().get("mail.smtp.host", "smtp.gmail.com");

    String port = AppSettings.get().get("mail.smtp.port", "465");

    String ssl_enable = AppSettings.get().get("mail.smtp.ssl.enable", "true");

    String auth = AppSettings.get().get("mail.smtp.auth", "true");

    String user = AppSettings.get().get("mail.smtp.user", "myhakibati@gmail.com");

    String password = AppSettings.get().get("mail.smtp.pass", "k123456789@");

    // Get system properties
    Properties properties = System.getProperties();

    // Setup mail server
    properties.put("mail.smtp.host", host);
    properties.put("mail.smtp.port", port);
    //   properties.put("mail.smtp.ssl.enable", ssl_enable);
    properties.put("mail.smtp.auth", auth);
    properties.put("mail.smtp.socketFactory.port", "465");
    properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

    // Get the Session object.// and pass username and password
    Session session =
        Session.getInstance(
            properties,
            new javax.mail.Authenticator() {

              protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication(user, password);
              }
            });

    // Used to debug SMTP issues
    session.setDebug(true);

    try {
      // Create a default MimeMessage object.
      MimeMessage message = new MimeMessage(session);

      // Set From: header field of the header.
      message.setFrom(new InternetAddress(from));

      // Set To: header field of the header.
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

      // Set Subject: header field
      message.setSubject(EmailSubject);

      // Now set the actual message
      message.setText(EmailText, "UTF-8", "html");
      // Send message
      Transport.send(message);

    } catch (MessagingException mex) {
      mex.printStackTrace();
    }
  }
}
