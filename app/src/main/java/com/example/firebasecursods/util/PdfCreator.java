package com.example.firebasecursods.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.events.PdfPageEventForwarder;

public class PdfCreator extends PdfPageEventForwarder { // class que vai criar pagina do pdf

    Phrase[] phrases = new Phrase[2];
    int pageNumber;

    @Override
    public void onOpenDocument(PdfWriter writer, Document document) {
        phrases[0] = new Phrase("pdf");
    }

    @Override
    public void onChapter(PdfWriter writer, Document document, float paragraphPosition, Paragraph title) {
        phrases[1] = new Phrase(title.getContent());
        pageNumber = 1;
    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        pageNumber++;
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {

        Rectangle rectangle = writer.getBoxSize("box_a");

        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_RIGHT, new Phrase(""),
                rectangle.getRight(), rectangle.getTop(), 0);

        // escolher qual e lado que vai ficar numero que pagina
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_RIGHT, new Phrase(String.format("%d", pageNumber)),
                (rectangle.getRight() + rectangle.getRight()) / 2, rectangle.getBorder() - 18, 0);
    }
}