package com.grootan.storeflow.service.impl;

import com.grootan.storeflow.dto.response.OrderResponseDto;
import com.grootan.storeflow.exceptions.AppException;
import com.grootan.storeflow.service.PdfGenerationService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfGenerationServiceImpl implements PdfGenerationService {

    @Override
    public byte[] generateOrderSummaryPdf(OrderResponseDto order) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Order Summary - " + order.getReferenceNumber());
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contentStream.newLineAtOffset(50, 700);
                contentStream.setLeading(15f);

                contentStream.showText("Status: " + order.getStatus());
                contentStream.newLine();
                contentStream.showText("Date: " + order.getCreatedAt());
                contentStream.newLine();
                contentStream.showText("Total Amount: $" + order.getTotalAmount());
                contentStream.newLine();
                contentStream.showText("Shipping Address:");
                contentStream.newLine();
                contentStream.showText(order.getShippingStreet() + ", " + order.getShippingCity() + ", " + order.getShippingCountry() + " " + order.getShippingPostalCode());
                contentStream.newLine();
                contentStream.newLine();
                contentStream.showText("Items:");
                contentStream.newLine();

                if (order.getItems() != null) {
                    for (var item : order.getItems()) {
                        contentStream.showText("- " + item.getProduct().getName() + " x" + item.getQuantity() + " ($" + item.getSubtotal() + ")");
                        contentStream.newLine();
                    }
                }

                contentStream.endText();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new AppException("Failed to generate PDF document", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
