package com.example.BACK.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Random;

@Service
public class SmsService {
    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    // Remplacez ces valeurs par vos informations Twilio
    private static final String ACCOUNT_SID = "ACd012070b704a6e9173d0affe9e6e31e3";
    private static final String AUTH_TOKEN = "c5e1e9033e1031153458ad0f062b099c";
    private static final String FROM_PHONE_NUMBER = "+14063067364";

    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    // Méthode pour générer un code aléatoire à 6 chiffres
    private String generateResetCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Génère un nombre entre 100000 et 999999
        return String.valueOf(code);
    }

    public String sendPasswordResetSms(String toPhoneNumber) {
        // Vérification et formatage du numéro de téléphone
        if (!toPhoneNumber.startsWith("+")) {
            toPhoneNumber = "+216" + toPhoneNumber; // Ajout du code pays si manquant
        }

        String resetCode = generateResetCode(); // Génération du code de réinitialisation
        String body = "Hello,\n\n" +
                "You have requested to reset your password.\n" +
                "Here is your reset code: " + resetCode + "\n\n" +
                "If you did not request this, please ignore this message.\n\n" +
                "Best regards, AMENA's Team";

        try {
            // Envoi du SMS via Twilio
            Message message = Message.creator(
                    new PhoneNumber(toPhoneNumber), // Numéro du destinataire
                    new PhoneNumber(FROM_PHONE_NUMBER), // Votre numéro Twilio
                    body // Le corps du message
            ).create();

            System.out.println("Message sent: " + message.getSid());
            return resetCode; // Retourne le code pour une éventuelle sauvegarde
        } catch (Exception e) {
            System.out.println("Erreur lors de l'envoi du SMS : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean sendSms(String phoneNumber, String message) {



        // Ensure proper phone number format
        if (!phoneNumber.startsWith("+")) {
            phoneNumber = "+216" + phoneNumber; // Add country code if missing
        }

        logger.info("Sending SMS to {}: {}", phoneNumber, message);

        try {
            // Send SMS via Twilio
            Message twilioMessage = Message.creator(
                    new PhoneNumber(phoneNumber),
                    new PhoneNumber(FROM_PHONE_NUMBER),
                    message
            ).create();

            logger.info("SMS sent successfully, message ID: {}", twilioMessage.getSid());
            return true;
        } catch (Exception e) {
            logger.error("Failed to send SMS to {}: {}", phoneNumber, e.getMessage(), e);
            return false;
        }
    }

    public boolean sendPaymentReminderSms(String phoneNumber, String clientName, Double amount, LocalDate dueDate) {
        // Ensure proper phone number format
        if (!phoneNumber.startsWith("+")) {
            phoneNumber = "+216" + phoneNumber; // Add country code if missing
        }

        // Create the reminder message
        String body = String.format(
                "Hello %s,\n\n" +
                        "This is a reminder that your loan payment of %.2f TND is due on %s.\n" +
                        "Please ensure your account has sufficient funds.\n" +
                        "If you have any questions, please contact us at +216-xx-xxx-xxx.\n\n" +
                        "Best regards,\n" +
                        "AMENA's Team",
                clientName,
                amount,
                dueDate.toString()
        );

        try {
            // Send SMS via Twilio
            Message message = Message.creator(
                    new PhoneNumber(phoneNumber),
                    new PhoneNumber(FROM_PHONE_NUMBER),
                    body
            ).create();

            logger.info("Payment reminder SMS sent to {}: Message ID {}", phoneNumber, message.getSid());
            return true;
        } catch (Exception e) {
            logger.error("Error sending payment reminder SMS to {}: {}", phoneNumber, e.getMessage(), e);
            return false;
        }
    }

}
