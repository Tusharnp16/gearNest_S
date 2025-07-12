package com.example.gearnest.utills;

import com.example.gearnest.model.ContactMessage;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;

public class ExcelExporter {

    public static void export(List<ContactMessage> messages, HttpServletResponse response) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Contact Messages");

        // Create Header
        Row headerRow = sheet.createRow(0);
        String[] columns = { "ID", "Name", "Email", "Subject", "Message", "Responded", "Created At" };

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        // Fill Data
        int rowIdx = 1;
        for (ContactMessage msg : messages) {
            Row row = sheet.createRow(rowIdx++);

            row.createCell(0).setCellValue(msg.getId());
            row.createCell(1).setCellValue(msg.getName());
            row.createCell(2).setCellValue(msg.getEmail());
            row.createCell(3).setCellValue(msg.getSubject());
            row.createCell(4).setCellValue(msg.getMessage());
            row.createCell(5).setCellValue(msg.isResponded() ? "Yes" : "No");
            row.createCell(6).setCellValue(msg.getCreatedAt().toString());
        }

        // Auto-size columns
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write to response
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
