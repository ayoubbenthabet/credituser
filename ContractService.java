package com.example.BACK.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
@Service

public class ContractService {
    public byte[] generateEmptyPdf() {
        try {
            // Créer un flux de sortie en mémoire
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            // Créer un PdfWriter
            PdfWriter writer = new PdfWriter(byteArrayOutputStream);

            // Créer un PdfDocument avec le writer
            PdfDocument pdf = new PdfDocument(writer);

            // Créer un Document et ajouter une page vide
            Document document = new Document(pdf);
            document.add(new Paragraph(""));  // Ajoute un paragraphe vide

            // Fermer le document
            document.close();

            // Retourner le contenu PDF sous forme de tableau de bytes
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
