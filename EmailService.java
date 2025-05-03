    package com.example.BACK.service;

    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.mail.MailSendException;
    import org.springframework.mail.SimpleMailMessage;
    import org.springframework.mail.javamail.JavaMailSender;
    import org.springframework.mail.javamail.JavaMailSenderImpl;
    import org.springframework.stereotype.Service;
    import java.util.HashMap;
    import java.util.Map;
    import java.util.Properties;
    import java.util.Random;

    @Service
    public class EmailService {

        @Autowired
        private JavaMailSender javaMailSender;
        private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

        private Map<String, String> codes = new HashMap<>(); // Stocker temporairement les codes

        public String envoyerCode(String email) {
            String code = genererCode(); // Génère un code aléatoire
            String sujet = "Password Reset Code";
            String message = "Hello,\n\n" +
                    "You have requested to reset your password. " +
                    "Here is your reset code: " + code + "\n\n" +
                    "If you did not request this, please ignore this email.\n\n" +
                    "Best regards, AMENA's Team";

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(email);
            mailMessage.setSubject(sujet);
            mailMessage.setText(message);

            javaMailSender.send(mailMessage);

            return "Reset code sent successfully to " + email;
        }


        public boolean verifierCode(String email, String code) {
            return codes.containsKey(email) && codes.get(email).equals(code);
        }

        private String genererCode() {
            Random random = new Random();
            int code = 100000 + random.nextInt(900000); // Code à 6 chiffres
            return String.valueOf(code);
        }




        private SimpleMailMessage createEmailMessage(String subject, String body, String recipient) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(recipient);
            message.setSubject(subject);
            message.setText(body);
            return message;
        }

        public EmailService() {
            // Configurer manuellement le serveur SMTP si nécessaire
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

            // Configurez l'hôte SMTP et le port
            mailSender.setHost("smtp.gmail.com"); // Cette ligne s'assure que le bon serveur est utilisé
            mailSender.setPort(465); // Ou 587 selon votre configuration (465 pour SSL)

            // Authentification avec votre compte Gmail
            mailSender.setUsername("azzahamdi0417@gmail.com");  // Utilisez votre email
            mailSender.setPassword("fhse hmbi xwlm onro");  // Votre mot de passe d'application Gmail (PAS votre mot de passe principal)

            // Configuration des propriétés supplémentaires
            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true"); // Activez STARTTLS pour le port 587
            props.put("mail.smtp.ssl.enable", "true");      // Activez SSL pour le port 465

            this.javaMailSender = mailSender;
        }

        public void sendSimpleMessage(String to, String subject, String text) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            javaMailSender.send(message);
        }


    }
