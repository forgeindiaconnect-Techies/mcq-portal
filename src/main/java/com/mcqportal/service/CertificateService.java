package com.mcqportal.service;
import java.util.Optional;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.mcqportal.entity.Certificate;
import com.mcqportal.entity.Result;
import com.mcqportal.entity.ResultStatus;
import com.mcqportal.repository.CertificateRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;

@Service
public class CertificateService {
    private final CertificateRepository certificateRepository;

    public CertificateService(CertificateRepository certificateRepository) {
        this.certificateRepository = certificateRepository;
    }

  @Transactional
public Certificate generateForResult(Result result) {

    if (result.getStatus() != ResultStatus.PASS) {
        throw new IllegalArgumentException("You are not eligible for certificate generation.");
    }

    Optional<Certificate> existingCertificate =
            certificateRepository.findByResult(result);

    if (existingCertificate.isPresent()) {
        return existingCertificate.get();
    }

    Certificate certificate = new Certificate();
    certificate.setCertificateId(nextCertificateId());
    certificate.setUser(result.getUser());
    certificate.setResult(result);
    certificate.setStudentName(result.getUser().getFullname());
    certificate.setPercentage(result.getPercentage());
    certificate.setIssueDate(LocalDate.now());
    certificate.setStatus(ResultStatus.PASS);

    return certificateRepository.save(certificate);
}

    private String nextCertificateId() {
        long next = certificateRepository.count() + 1;
        return "FIC-" + LocalDate.now().getYear() + "-" + String.format("%04d", next);
    }

    public byte[] createPdf(Certificate certificate) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate(), 54, 54, 45, 45);
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();

            // Colors
            Color blueColor = new Color(23, 70, 162);       // #1746a2
            Color darkBlueColor = new Color(16, 46, 104);   // #102e68
            Color greenColor = new Color(22, 131, 91);      // #16835b
            Color goldColor = new Color(216, 199, 134);     // #d8c786
            Color darkColor = new Color(31, 41, 55);        // #1f2937
            Color ribbonColor = new Color(100, 116, 139);   // #64748b

            // Fonts
            Font ribbonFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, ribbonColor);
            Font logoTextFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, blueColor);
            Font h1Font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 26, darkBlueColor);
            Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 14, darkColor);
            Font studentFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 32, greenColor);
            Font scoreFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, darkBlueColor);
            Font statusFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, greenColor);
            Font metaFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, darkColor);
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, darkColor);

            // Canvas drawing for background and concentric border outline
            float pageWidth = PageSize.A4.rotate().getWidth();
            float pageHeight = PageSize.A4.rotate().getHeight();
            PdfContentByte canvas = writer.getDirectContentUnder();
            
            canvas.saveState();
            // Fill background with warm parchment tint #fffdf8 (255, 253, 248)
            canvas.setColorFill(new Color(255, 253, 248));
            canvas.rectangle(0, 0, pageWidth, pageHeight);
            canvas.fill();

            // Draw concentric borders to replicate the HTML CSS gradient/border design:
            // 1. Outer blue outline (2px)
            canvas.setColorStroke(blueColor);
            canvas.setLineWidth(2f);
            canvas.rectangle(15, 15, pageWidth - 30, pageHeight - 30);
            canvas.stroke();

            // 2. Outer blue border (5px)
            canvas.setColorStroke(blueColor);
            canvas.setLineWidth(5f);
            canvas.rectangle(23, 23, pageWidth - 46, pageHeight - 46);
            canvas.stroke();

            // 3. Middle gold border (3px)
            canvas.setColorStroke(new Color(255, 193, 7)); // #ffc107
            canvas.setLineWidth(3f);
            canvas.rectangle(27, 27, pageWidth - 54, pageHeight - 54);
            canvas.stroke();

            // 4. Inner green border (5px)
            canvas.setColorStroke(greenColor);
            canvas.setLineWidth(5f);
            canvas.rectangle(31, 31, pageWidth - 62, pageHeight - 62);
            canvas.stroke();
            canvas.restoreState();

            // Center watermark
            addCertificateWatermark(writer);

            // 1. Top ribbon header
            Paragraph ribbon = new Paragraph("FORGE INDIA CONNECT", ribbonFont);
            ribbon.setAlignment(Element.ALIGN_CENTER);
            ribbon.setSpacingBefore(18);
            document.add(ribbon);

            // 2. Centered Logo and Brand Name Table
            PdfPTable logoTable = new PdfPTable(2);
            logoTable.setWidthPercentage(40);
            logoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
            logoTable.setSpacingBefore(8);
            logoTable.setSpacingAfter(8);
            logoTable.setWidths(new float[]{1.2f, 2.8f});

            Image logoImg = null;
            try {
                var resource = new ClassPathResource("static/image/logo-circle.png");
                if (resource.exists()) {
                    logoImg = Image.getInstance(resource.getInputStream().readAllBytes());
                    logoImg.scaleToFit(50, 50);
                }
            } catch (Exception ignored) {}

            PdfPCell cellImg = new PdfPCell();
            if (logoImg != null) {
                cellImg.addElement(logoImg);
            }
            cellImg.setBorder(Rectangle.NO_BORDER);
            cellImg.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellImg.setHorizontalAlignment(Element.ALIGN_RIGHT);
            logoTable.addCell(cellImg);

            Paragraph logoText = new Paragraph("FIC TALENT EXAM", logoTextFont);
            PdfPCell cellText = new PdfPCell(logoText);
            cellText.setBorder(Rectangle.NO_BORDER);
            cellText.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellText.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellText.setPaddingLeft(10f);
            logoTable.addCell(cellText);

            document.add(logoTable);

            // 3. Certificate Title
            Paragraph h1 = new Paragraph("CERTIFICATE OF ACHIEVEMENT", h1Font);
            h1.setAlignment(Element.ALIGN_CENTER);
            h1.setSpacingBefore(10);
            document.add(h1);

            // 4. Certification text
            Paragraph p1 = new Paragraph("This is to certify that", textFont);
            p1.setAlignment(Element.ALIGN_CENTER);
            p1.setSpacingBefore(12);
            document.add(p1);

            // 5. Student Name
            Paragraph pName = new Paragraph(certificate.getStudentName(), studentFont);
            pName.setAlignment(Element.ALIGN_CENTER);
            pName.setSpacingBefore(8);
            pName.setSpacingAfter(8);
            document.add(pName);

            // 6. Exam Completion
            Paragraph p2 = new Paragraph("has successfully completed the MCQ Examination", textFont);
            p2.setAlignment(Element.ALIGN_CENTER);
            p2.setSpacingBefore(4);
            document.add(p2);

            // 7. Score
            Paragraph p3 = new Paragraph("with a score of", textFont);
            p3.setAlignment(Element.ALIGN_CENTER);
            p3.setSpacingBefore(4);
            document.add(p3);

            Paragraph pScore = new Paragraph(certificate.getPercentage() + " %", scoreFont);
            pScore.setAlignment(Element.ALIGN_CENTER);
            pScore.setSpacingBefore(6);
            document.add(pScore);

            // 8. Status
            Paragraph pStatus = new Paragraph("Status: PASS", statusFont);
            pStatus.setAlignment(Element.ALIGN_CENTER);
            pStatus.setSpacingBefore(6);
            document.add(pStatus);

            // 9. Metadata (Date & Certificate ID) with top border line
            PdfPTable metaTable = new PdfPTable(2);
            metaTable.setWidthPercentage(75);
            metaTable.setHorizontalAlignment(Element.ALIGN_CENTER);
            metaTable.setSpacingBefore(15);

            PdfPCell cellDate = new PdfPCell(new Paragraph("Date: " + certificate.getIssueDate(), metaFont));
            cellDate.setBorder(Rectangle.TOP);
            cellDate.setBorderColorTop(goldColor);
            cellDate.setBorderWidthTop(1f);
            cellDate.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellDate.setPaddingTop(8f);
            metaTable.addCell(cellDate);

            PdfPCell cellId = new PdfPCell(new Paragraph("Certificate ID: " + certificate.getCertificateId(), metaFont));
            cellId.setBorder(Rectangle.TOP);
            cellId.setBorderColorTop(goldColor);
            cellId.setBorderWidthTop(1f);
            cellId.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellId.setPaddingTop(8f);
            metaTable.addCell(cellId);

            document.add(metaTable);

            // 10. Footer (Authorized Signature & Official Seal) with top border line
            PdfPTable footerTable = new PdfPTable(2);
            footerTable.setWidthPercentage(75);
            footerTable.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.setSpacingBefore(20);

            PdfPCell cellSig = new PdfPCell(new Paragraph("Authorized Signature", footerFont));
            cellSig.setBorder(Rectangle.TOP);
            cellSig.setBorderColorTop(goldColor);
            cellSig.setBorderWidthTop(1f);
            cellSig.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellSig.setPaddingTop(8f);
            footerTable.addCell(cellSig);

            PdfPCell cellSeal = new PdfPCell(new Paragraph("Official Seal", footerFont));
            cellSeal.setBorder(Rectangle.TOP);
            cellSeal.setBorderColorTop(goldColor);
            cellSeal.setBorderWidthTop(1f);
            cellSeal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellSeal.setPaddingTop(8f);
            footerTable.addCell(cellSeal);

            document.add(footerTable);

            document.close();
            return outputStream.toByteArray();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to generate certificate PDF", ex);
        }
    }

    private void addCertificateImage(Document document, float width, float height, float x, float y) {
        try {
            var resource = new ClassPathResource("static/image/logo-circle.png");
            if (!resource.exists()) {
                return;
            }
            Image image = Image.getInstance(resource.getInputStream().readAllBytes());
            image.scaleToFit(width, height);
            image.setAbsolutePosition(x, y);
            document.add(image);
        } catch (Exception ignored) {
            // Certificate text still downloads even if the optional logo resource is unavailable.
        }
    }

    private void addCertificateWatermark(PdfWriter writer) {
        try {
            var resource = new ClassPathResource("static/image/logo-circle.png");
            if (!resource.exists()) {
                return;
            }
            Image image = Image.getInstance(resource.getInputStream().readAllBytes());
            image.scaleToFit(360, 360);
            float pageWidth = PageSize.A4.rotate().getWidth();
            float pageHeight = PageSize.A4.rotate().getHeight();
            float x = (pageWidth - image.getScaledWidth()) / 2;
            float y = (pageHeight - image.getScaledHeight()) / 2;
            image.setAbsolutePosition(x, y);

            PdfGState state = new PdfGState();
            state.setFillOpacity(0.08f);
            PdfContentByte under = writer.getDirectContentUnder();
            under.saveState();
            under.setGState(state);
            under.addImage(image);
            under.restoreState();
        } catch (Exception ignored) {
            // The branded text certificate remains available if the logo cannot be read.
        }
    }
}
