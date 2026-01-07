package com.example.collegeranker.service.extract;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.InputStream;
import java.net.URL;

public class PdfTextExtractor {

    public String extract(String pdfUrl) {

        try (InputStream is = new URL(pdfUrl).openStream();
             PDDocument doc = PDDocument.load(is)) {

            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(doc);

        } catch (Exception e) {
            return "";
        }
    }
}
