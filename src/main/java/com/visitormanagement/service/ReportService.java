package com.visitormanagement.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.opencsv.CSVWriter;
import com.visitormanagement.entity.Visitor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportService {
    
    public String generateCSVReport(List<Visitor> visitors) {
        StringWriter stringWriter = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(stringWriter);
        
        // Header
        String[] header = {"ID", "Name", "Phone", "Purpose", "Appointment Date", "Created By", "Created At"};
        csvWriter.writeNext(header);
        
        // Data
        for (Visitor visitor : visitors) {
            String[] data = {
                visitor.getId().toString(),
                visitor.getName(),
                visitor.getPhoneNumber(),
                visitor.getPurpose() != null ? visitor.getPurpose() : "",
                visitor.getAppointmentDate().toString(),
                visitor.getCreatedBy().getFullName(),
                visitor.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            };
            csvWriter.writeNext(data);
        }
        
        try {
            csvWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return stringWriter.toString();
    }
    
    public byte[] generatePDFReport(List<Visitor> visitors) {
        try {
            Document document = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            
            document.open();
            
            // Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph title = new Paragraph("Visitor Management Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));
            
            // Table
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            
            // Header
            String[] headers = {"ID", "Name", "Phone", "Purpose", "Date", "Created By", "Created At"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);
            }
            
            // Data
            for (Visitor visitor : visitors) {
                table.addCell(visitor.getId().toString());
                table.addCell(visitor.getName());
                table.addCell(visitor.getPhoneNumber());
                table.addCell(visitor.getPurpose() != null ? visitor.getPurpose() : "");
                table.addCell(visitor.getAppointmentDate().toString());
                table.addCell(visitor.getCreatedBy().getFullName());
                table.addCell(visitor.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            }
            
            document.add(table);
            document.close();
            
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}