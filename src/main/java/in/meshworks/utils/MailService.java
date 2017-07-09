package in.meshworks.utils;

import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


/**
 * Created by dhruv.suri on 14/05/17.
 */
@Service
public class MailService {


     public void sendMail(String text, boolean onlyMe) {
        String to;
        String subject;
        if (onlyMe) {
            subject = "TV : Keyword";
            to = "sdhruv93@gmail.com";
        } else {
            subject = "Ad Identified";
            to = "aanand@nextpixar.com,dhruv.suri@oyorooms.com";
        }

        final String username = "worksmesh@gmail.com";
        final String password = "proxymesh93";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("sdhruv93@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(text);

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    public void sendProxyMail(String text) {
        String to = "sdhruv93@gmail.com,skk4326@gmail.com";
        final String username = "sdhruv93@gmail.com";
        final String password = "charu*6567";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {


            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("sdhruv93@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));
            message.setSubject("Proxy");
            message.setText(text);


            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        System.out.println("Done");
    }
}
