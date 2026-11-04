package com.prestaclub.prestaclub_backend.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import jakarta.mail.*;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.prestaclub.prestaclub_backend.dto.EmailValues;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    TemplateEngine templateEngine;

    @Value("${mail.urlFront}")
    private String urlFront;

    @Value("${spring.mail.username}")
    private String mailFrom;

    public void sendEmail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("eduardiitop123@gmail.com");
        message.setTo("kalux_1505@hotmail.com");
        message.setSubject("Prueba envio");
        message.setText("Contenido de envio");
        javaMailSender.send(message);
    }

    public void sendEmailTemplateForgotPassword(EmailValues emailValues) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            Context context = new Context();
            Map<String, Object> model = new HashMap<>();
            model.put("username", emailValues.getUsername());
            model.put("url", urlFront + "reset-password?token=" + emailValues.getJwt());
            model.put("jwt", emailValues.getJwt());
            context.setVariables(model);
            String htmlText = templateEngine.process("email-template-forgot-password", context);
            helper.setFrom(mailFrom);
            helper.setTo(emailValues.getEmail());
            helper.setSubject(emailValues.getSubject());
            helper.setText(htmlText, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendEmailNewNotificacion(EmailValues emailValues, String descripcion) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            Context context = new Context();
            Map<String, Object> model = new HashMap<>();
            model.put("username", emailValues.getUsername());
            model.put("contenido", descripcion);
            context.setVariables(model);
            String htmlText = templateEngine.process("email-template-new-notification", context);
            helper.setFrom(mailFrom);
            helper.setTo(emailValues.getEmail());
            helper.setSubject(emailValues.getSubject());
            helper.setText(htmlText, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendEmailMovimientoDeSolicitud(String subject, String titulo, String contenido,
            List<String> emailList) {
        Map<String, Object> model = new HashMap<>();
        model.put("titulo", titulo);
        model.put("contenido", contenido);
        enviarEmail(model, subject, emailList, "email-template-lcr-solicitud-movimiento-etapa");
    }

    public void enviarEmail(Map<String, Object> model, String subject, List<String> emails, String nombreTemplate) {

        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            Context context = new Context();
            context.setVariables(model);
            String htmlText = templateEngine.process(nombreTemplate, context);
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(mailFrom);
            helper.setTo(emails.toArray(new String[0]));
            helper.setSubject(subject);
            helper.setText(htmlText, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendFromGMail(String from, String pass, String[] to, String subject, String body) {
        Properties props = System.getProperties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", "587");
        Session session = Session.getDefaultInstance(props);
        MimeMessage message = new MimeMessage(session);
        System.out.println("ddd" + to.length);

        try {
            message.setFrom(new InternetAddress(from));
            InternetAddress[] toAddress = new InternetAddress[to.length];

            int i;
            for (i = 0; i < to.length; ++i) {
                toAddress[i] = new InternetAddress(to[i]);
                System.out.println("toAddress[i]" + String.valueOf(toAddress[i]));
            }

            for (i = 0; i < toAddress.length; ++i) {
                message.addRecipient(Message.RecipientType.TO, toAddress[i]);
            }

            message.setHeader("Content-Type", "text/html; charset=UTF-8");
            message.setSubject(subject);
            message.setContent(body, "text/html; charset=UTF-8");
            System.out.println("from"+from);
            System.out.println("pass"+pass);
            Transport.send(message, from, pass);
        } catch (AddressException var11) {
            System.out.println("error"+var11.getMessage());
        } catch (MessagingException var12) {
            System.out.println("error"+var12.getMessage());
        }


    }


    public void sendFromGMail2(String from, String pass, String[] to, String subject, String body) {

            Properties props = System.getProperties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.port", 587);
            props.put("mail.smtp.password", pass);
            props.put("mail.smtp.user", from);
            props.put("mail.smtp.auth", "true");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(from, pass);
                }
            });
            MimeMessage message = new MimeMessage(session);

            try {
                // Crear el mensaje
                message.setFrom(new InternetAddress(from));
                InternetAddress[] toAddress = new InternetAddress[to.length];
                for (int i = 0; i < to.length; i++) {
                    toAddress[i] = new InternetAddress(to[i]);
                }

                for (int i = 0; i < toAddress.length; i++) {
                    message.addRecipient(Message.RecipientType.TO, toAddress[i]);
                }

                //	message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
                message.setSubject(subject);
                // message.setContent(multipart);
                message.setText(body);

                // Enviar el correo
                Transport.send(message);
                // Thread.sleep(500);
                Properties props1 = new Properties();
                props1.setProperty("mail.store.protocol", "imaps");
            } catch (AuthenticationFailedException e) {
                System.out.println("dddd" + e.getMessage());
            } catch (MessagingException e) {
                System.out.println("deeee" + e.getMessage());
            } catch (Exception e) {
                System.out.println("tttt" + e.getMessage());
            }

        }

        public void sendEmailMovimientoDeSolicitudHtml(String subject, String titulo, String contenidoHtml,
            List<String> emails) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailFrom);
            helper.setTo(emails.toArray(new String[0]));
            helper.setSubject(subject);

            // Construir el HTML completo con estilos
            String htmlContent = "<!DOCTYPE html>" +
                    "<html>" +
                    "<head>" +
                    "<meta charset='UTF-8'>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; padding: 20px; }" +
                    "h2 { color: #2c3e50; }" +
                    "table { border-collapse: collapse; width: 100%; margin: 20px 0; }" +
                    "th { background-color: #4CAF50; color: white; padding: 12px; text-align: left; border: 1px solid #ddd; }"
                    +
                    "td { padding: 8px; border: 1px solid #ddd; text-align: center; }" +
                    "tr:nth-child(even) { background-color: #f2f2f2; }" +
                    ".footer { margin-top: 30px; padding-top: 20px; border-top: 1px solid #ddd; color: #666; font-size: 12px; }"
                    +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<h2>" + titulo + "</h2>" +
                    "<div>" + contenidoHtml + "</div>" +
                    "<div class='footer'>" +
                    "<p>Este es un correo automático, por favor no responder.</p>" +
                    "<p>PrestaClub - Sistema de Gestión</p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlContent, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            System.out.println("Error al enviar email HTML: " + e.getMessage());
            e.printStackTrace();
        }
    }

}