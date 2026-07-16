package com.jasper.documentmatcher.document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Component;

@Component
class MockExternalDocumentProvider implements ExternalDocumentProvider {

    private static final String SOURCE_SYSTEM = "DATEV Mock";

    private static final List<MockExternalDocument> DOCUMENTS = List.of(
            new MockExternalDocument(
                    "datev-salary-laura",
                    "datev-gehaltsabrechnung-laura-hoffmann.pdf",
                    "Gehaltsabrechnung aus DATEV",
                    "Auto-Zuordnung zu Laura Hoffmann und Kategorie Gehalt",
                    List.of("Gehaltsabrechnung", "Mitarbeiterin: Laura Hoffmann", "Import aus DATEV Lohn und Gehalt.")),
            new MockExternalDocument(
                    "datev-certificate-miriam",
                    "datev-bescheinigung-miriam-weber.pdf",
                    "Bescheinigung aus DATEV",
                    "Auto-Zuordnung zu Miriam Weber und Kategorie Bescheinigungen",
                    List.of("Bescheinigung", "Mitarbeiterin: Miriam Weber", "Import aus DATEV Dokumentenablage.")),
            new MockExternalDocument(
                    "datev-review-no-employee",
                    "datev-gehalt-ohne-mitarbeiter.pdf",
                    "Gehaltsdokument ohne eindeutigen Mitarbeitenden",
                    "Prueffall: Kategorie-Signal, aber kein Mitarbeiter-Match",
                    List.of("Gehaltsabrechnung", "Dieses externe Dokument nennt keinen bekannten Mitarbeitenden.")),
            new MockExternalDocument(
                    "datev-review-ambiguous",
                    "datev-vertrag-mehrere-personen.pdf",
                    "Vertragsdokument mit mehreren Personen",
                    "Prueffall: mehrere bekannte Namen",
                    List.of("Arbeitsvertrag", "Beteiligte Personen: David Schneider und Laura Hoffmann")),
            new MockExternalDocument(
                    "datev-zip-payroll-month",
                    "datev-export-lohn-januar.zip",
                    "ZIP-Export mit mehreren DATEV-Dokumenten",
                    "Importiert mehrere PDFs aus einem gemockten ZIP-Paket",
                    List.of()));

    @Override
    public List<ExternalDocumentResponse> findAvailableDocuments() {
        return DOCUMENTS.stream()
                .map(document -> new ExternalDocumentResponse(
                        document.id(),
                        SOURCE_SYSTEM,
                        document.filename(),
                        document.description(),
                        document.expectedOutcome()))
                .toList();
    }

    @Override
    public ExternalDocumentContent fetch(String externalDocumentId) {
        var document = DOCUMENTS.stream()
                .filter(candidate -> candidate.id().equals(externalDocumentId))
                .findFirst()
                .orElseThrow(() -> new ExternalDocumentNotFoundException(externalDocumentId));

        return new ExternalDocumentContent(document.filename(), createPdf(document.lines()));
    }

    @Override
    public List<ExternalDocumentContent> fetchAll(String externalDocumentId) {
        if (!"datev-zip-payroll-month".equals(externalDocumentId)) {
            return ExternalDocumentProvider.super.fetchAll(externalDocumentId);
        }

        return List.of(
                new ExternalDocumentContent(
                        "datev-zip-gehaltsabrechnung-laura-hoffmann.pdf",
                        createPdf(List.of("Gehaltsabrechnung", "Mitarbeiterin: Laura Hoffmann", "Aus ZIP-Export importiert."))),
                new ExternalDocumentContent(
                        "datev-zip-bescheinigung-miriam-weber.pdf",
                        createPdf(List.of("Bescheinigung", "Mitarbeiterin: Miriam Weber", "Aus ZIP-Export importiert."))),
                new ExternalDocumentContent(
                        "datev-zip-gehalt-ohne-mitarbeiter.pdf",
                        createPdf(List.of("Gehaltsabrechnung", "Dieses externe Dokument nennt keinen bekannten Mitarbeitenden."))));
    }

    private byte[] createPdf(List<String> lines) {
        try (var document = new PDDocument(); var output = new ByteArrayOutputStream()) {
            var page = new PDPage();
            document.addPage(page);

            try (var contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contentStream.newLineAtOffset(72, 760);
                for (var line : lines) {
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(0, -18);
                }
                contentStream.endText();
            }

            document.save(output);
            return output.toByteArray();
        } catch (IOException e) {
            throw new InvalidUploadException("Externes Dokument konnte nicht gelesen werden.");
        }
    }

    private record MockExternalDocument(
            String id, String filename, String description, String expectedOutcome, List<String> lines) {
    }
}
