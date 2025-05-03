package com.example.BACK.service;

import com.example.BACK.model.ChatMessage;
import com.example.BACK.model.User;
import com.example.BACK.repository.ChatMessageRepository;
import com.example.BACK.repository.UserRepository;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ChatMessageStatisticsService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;

    public String getMessageSentimentStatistics(Long agentId) {
        // Récupérer les messages envoyés à l'agent donné (par receiver_id)
        List<ChatMessage> messages = chatMessageRepository.findByReceiverId(agentId);

        if (messages.isEmpty()) {
            return "No messages found for the given agent.";
        }

        // Calculer le nombre total de messages
        int totalMessages = messages.size();
        int positiveMessages = 0;
        int negativeMessages = 0;
        int neutralMessages = 0;

        // Compter les messages en fonction du sentiment
        for (ChatMessage message : messages) {
            String sentiment = message.getSentiment();

            // Vérification si le sentiment est null avant de le manipuler
            if (sentiment != null) {
                sentiment = sentiment.trim().replaceAll("[^a-zA-Z]", "");
            }

            if ("Positive".equalsIgnoreCase(sentiment)) {
                positiveMessages++;
            } else if ("Negative".equalsIgnoreCase(sentiment)) {
                negativeMessages++;
            } else if ("Neutral".equalsIgnoreCase(sentiment)) {
                neutralMessages++;
            }
        }

        // Calculer les pourcentages
        double positivePercentage = (positiveMessages * 100.0) / totalMessages;
        double negativePercentage = (negativeMessages * 100.0) / totalMessages;
        double neutralPercentage = (neutralMessages * 100.0) / totalMessages;

        // Envoi d'email d'avertissement si le pourcentage des messages négatifs dépasse 70%
        if (negativePercentage >= 70) {
            sendWarningEmail(agentId);
        }

        // Générer le PDF avec les statistiques
        generatePdfStatistics(agentId, positivePercentage, negativePercentage, neutralPercentage);

        // Formater les résultats
        return String.format("Sentiment Statistics for Agent %d: \n" +
                "Positive: %.2f%% \n" +
                "Negative: %.2f%% \n" +
                "Neutral: %.2f%%", agentId, positivePercentage, negativePercentage, neutralPercentage);
    }


    // Générer le PDF avec un graphique en camembert
    private void generatePdfStatistics(Long agentId, double positivePercentage, double negativePercentage, double neutralPercentage) {
        // Nom du fichier PDF à générer
        String fileName = "Statistics_Agent_" + agentId + ".pdf";

        // Créer le dataset pour le camembert
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Positive", positivePercentage);
        dataset.setValue("Negative", negativePercentage);
        dataset.setValue("Neutral", neutralPercentage);

        // Créer le graphique camembert
        JFreeChart chart = ChartFactory.createPieChart(
                "Sentiment Statistics for Agent " + agentId,  // Titre
                dataset,                                  // Dataset
                true,                                     // Légende
                true,                                     // Tooltips
                false);                                   // URL

        // Personnaliser l'apparence du camembert
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Positive", new Color(0, 255, 0)); // Vert pour positif
        plot.setSectionPaint("Negative", new Color(255, 0, 0)); // Rouge pour négatif
        plot.setSectionPaint("Neutral", new Color(255, 255, 0)); // Jaune pour neutre

        // Enregistrer le graphique sous forme d'image PNG
        String chartImagePath = "chart_Agent_" + agentId + ".png";
        try {
            File chartFile = new File(chartImagePath);
            ImageIO.write(chart.createBufferedImage(500, 300), "png", chartFile); // Utilisation de ImageIO pour enregistrer le graphique
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // Créer le document PDF
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fileName));

            // Ouvrir le document pour y ajouter du contenu
            document.open();

            // Ajouter un titre
            document.add(new Paragraph("Sentiment Statistics for Agent " + agentId));

            // Ajouter l'image du graphique
            Image chartImage = Image.getInstance(chartImagePath);
            chartImage.scaleToFit(500, 300); // Redimensionner l'image
            document.add(chartImage); // Ajouter l'image dans le PDF

            // Ajouter un paragraphe avec les statistiques textuelles
            document.add(new Paragraph(String.format("Positive: %.2f%%", positivePercentage)));
            document.add(new Paragraph(String.format("Negative: %.2f%%", negativePercentage)));
            document.add(new Paragraph(String.format("Neutral: %.2f%%", neutralPercentage)));

            // Fermer le document
            document.close();

            // Supprimer l'image après avoir créé le PDF (facultatif)
            new File(chartImagePath).delete();

            // Log du chemin du fichier PDF généré
            System.out.println("PDF generated at: " + new File(fileName).getAbsolutePath());

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    // Envoi de l'email d'avertissement
    private void sendWarningEmail(Long agentId) {
        Optional<User> userOptional = userRepository.findById(agentId);

        if (userOptional.isPresent()) {
            User agent = userOptional.get();
            String agentEmail = agent.getEmail();

            String subject = "Warning: High Negative Feedback Percentage";
            String body = "Dear Agent,\n\n"
                    + "We have noticed that the percentage of negative feedback from investors is higher than 70%. "
                    + "Please review your interactions and improve the service.\n\n"
                    + "Best regards, AMENA's TEAM";

            emailService.sendSimpleMessage(agentEmail, subject, body);
        } else {
            System.out.println("Agent with ID " + agentId + " not found.");
        }
    }
}
