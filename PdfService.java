package tn.esprit.projet1.Service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;

@Service
public class PdfService {

    public void generateEmptyPdf(String filePath) {
        try {
            // Créer un PdfWriter pour le fichier de sortie
            PdfWriter writer = new PdfWriter(filePath);

            // Créer un PdfDocument en utilisant le PdfWriter
            PdfDocument pdf = new PdfDocument(writer);

            // Créer un Document, qui est le contenu principal du PDF
            Document document = new Document(pdf);

            // Aucun contenu n'est ajouté pour un PDF vide
            document.close();

            System.out.println("PDF vide généré avec succès !");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la génération du PDF.");
        }
    }
}
