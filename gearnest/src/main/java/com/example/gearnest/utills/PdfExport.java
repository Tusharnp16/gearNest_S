package com.example.gearnest.utills;


import com.example.gearnest.model.ContactMessage;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

public class PdfExport {

    public static void export(List<ContactMessage> messages, HttpServletResponse response) throws IOException {
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, response.getOutputStream());

            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph title = new Paragraph("Contact Messages", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable table = new PdfPTable(7); // 7 columns
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            table.setWidths(new float[]{1f, 2f, 3f, 3f, 4f, 2f, 3f});

            addTableHeader(table);
            addRows(table, messages);

            document.add(table);

        } catch (DocumentException e) {
            throw new IOException("Error creating PDF", e);
        } finally {
            document.close();
        }
    }

    private static void addTableHeader(PdfPTable table) {
        Stream.of("ID", "Name", "Email", "Subject", "Message", "Responded", "Created At")
            .forEach(header -> {
                PdfPCell cell = new PdfPCell();
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setPadding(5);
                Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
                cell.setPhrase(new Phrase(header, font));
                table.addCell(cell);
            });
    }

    private static void addRows(PdfPTable table, List<ContactMessage> messages) {
        for (ContactMessage msg : messages) {
            table.addCell(String.valueOf(msg.getId()));
            table.addCell(msg.getName());
            table.addCell(msg.getEmail());
            table.addCell(msg.getSubject());
            table.addCell(msg.getMessage());
            table.addCell(msg.isResponded() ? "Yes" : "No");
            table.addCell(msg.getCreatedAt().toString());
        }
    }
}
