package com.mcqportal.service;
import java.util.Optional;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfWriter;
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

            Font title = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 28, new Color(23, 70, 162));
            Font subTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Color.DARK_GRAY);
            Font body = FontFactory.getFont(FontFactory.HELVETICA, 16, Color.DARK_GRAY);
            Font name = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 30, new Color(12, 125, 80));
            Font small = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.GRAY);

            PdfContentByte canvas = writer.getDirectContent();
            canvas.saveState();
            canvas.setColorStroke(new Color(23, 70, 162));
            canvas.setLineWidth(4f);
            canvas.rectangle(36, 30, PageSize.A4.rotate().getWidth() - 72, PageSize.A4.rotate().getHeight() - 60);
            canvas.stroke();
            canvas.setColorStroke(new Color(255, 193, 7));
            canvas.setLineWidth(1.5f);
            canvas.rectangle(48, 42, PageSize.A4.rotate().getWidth() - 96, PageSize.A4.rotate().getHeight() - 84);
            canvas.stroke();
            canvas.restoreState();

            addCertificateWatermark(writer);
            addCertificateImage(document, 62, 62, 90, PageSize.A4.rotate().getHeight() - 130);

            Paragraph logo = new Paragraph("FIC TALENT EXAM", title);
            logo.setAlignment(Element.ALIGN_CENTER);
            logo.setSpacingBefore(35);
            document.add(logo);
            Paragraph cert = new Paragraph("CERTIFICATE OF ACHIEVEMENT", subTitle);
            cert.setAlignment(Element.ALIGN_CENTER);
            cert.setSpacingBefore(20);
            document.add(cert);
            Paragraph text = new Paragraph("This is to certify that", body);
            text.setAlignment(Element.ALIGN_CENTER);
            text.setSpacingBefore(28);
            document.add(text);
            Paragraph student = new Paragraph(certificate.getStudentName(), name);
            student.setAlignment(Element.ALIGN_CENTER);
            student.setSpacingBefore(12);
            document.add(student);
            Paragraph completion = new Paragraph("has successfully completed the MCQ Examination", body);
            completion.setAlignment(Element.ALIGN_CENTER);
            completion.setSpacingBefore(14);
            document.add(completion);
            Paragraph score = new Paragraph("with a score of " + certificate.getPercentage() + " %", subTitle);
            score.setAlignment(Element.ALIGN_CENTER);
            score.setSpacingBefore(18);
            document.add(score);
            Paragraph status = new Paragraph("Status: PASS", body);
            status.setAlignment(Element.ALIGN_CENTER);
            status.setSpacingBefore(10);
            document.add(status);
            Paragraph details = new Paragraph("Date: " + certificate.getIssueDate() + "    Certificate ID: " + certificate.getCertificateId(), small);
            details.setAlignment(Element.ALIGN_CENTER);
            details.setSpacingBefore(24);
            document.add(details);
            Paragraph footer = new Paragraph("Authorized Signature: FIC Administrator        Official Seal: Forge India Connect", small);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(55);
            document.add(footer);
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
            image.setAbsolutePosition(240, 95);
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
