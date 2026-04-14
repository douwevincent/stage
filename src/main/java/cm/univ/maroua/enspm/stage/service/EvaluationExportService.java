package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.service.dto.SessionEvaluationExportDTO;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class EvaluationExportService {

    private final EvaluationPdfService evaluationPdfService;

    public EvaluationExportService(EvaluationPdfService evaluationPdfService) {
        this.evaluationPdfService = evaluationPdfService;
    }

    // ========== PDF Export Methods ==========

    public byte[] generatePdfByNiveau(Long niveauId, String niveauLabel, String anneeAcademique, 
                                      Map<Long, List<SessionEvaluationExportDTO>> data) {
        return buildPdfDocument(data, "Résultats - Niveau " + niveauLabel, anneeAcademique);
    }

    public byte[] generatePdfByParcours(Long parcoursId, String parcoursLabel, String anneeAcademique,
                                        List<SessionEvaluationExportDTO> data) {
        return buildPdfDocumentSingleParcours(data, "Résultats - Parcours", parcoursLabel, anneeAcademique);
    }

    public byte[] generatePdfByTypeStage(Long typeStageId, String typeStageLabel, String anneeAcademique,
                                         Map<Long, List<SessionEvaluationExportDTO>> data) {
        return buildPdfDocument(data, "Résultats - Type de Stage " + typeStageLabel, anneeAcademique);
    }

    // ========== Excel Export Methods ==========

    public byte[] generateExcelByNiveau(Long niveauId, String niveauLabel, String anneeAcademique,
                                        Map<Long, List<SessionEvaluationExportDTO>> data) {
        return buildExcelWorkbook(data, "Résultats - Niveau " + niveauLabel);
    }

    public byte[] generateExcelByParcours(Long parcoursId, String parcoursLabel, String anneeAcademique,
                                          List<SessionEvaluationExportDTO> data) {
        return buildExcelWorkbookSingleParcours(data, "Résultats - Parcours", parcoursLabel);
    }

    public byte[] generateExcelByTypeStage(Long typeStageId, String typeStageLabel, String anneeAcademique,
                                           Map<Long, List<SessionEvaluationExportDTO>> data) {
        return buildExcelWorkbook(data, "Résultats - Type de Stage " + typeStageLabel);
    }

    // ========== Private PDF Methods ==========

    private byte[] buildPdfDocument(Map<Long, List<SessionEvaluationExportDTO>> groupedData, 
                                     String titre, String anneeAcademique) {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, output);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Font sectionFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 10, Font.NORMAL);
            Font smallFont = new Font(Font.HELVETICA, 8, Font.NORMAL);

            boolean firstPage = true;
            for (List<SessionEvaluationExportDTO> parcoursData : groupedData.values()) {
                if (parcoursData.isEmpty()) continue;

                if (!firstPage) {
                    document.newPage();
                }
                firstPage = false;

                // Ajouter l'en-tête officiel pour chaque parcours
                evaluationPdfService.buildOfficialHeaderPublic(document, smallFont);

                document.add(new Paragraph(titre, titleFont));
                document.add(new Paragraph(" "));

                String parcoursLabel = parcoursData.get(0).parcoursLabel();
                document.add(new Paragraph("Parcours: " + parcoursLabel, sectionFont));
                document.add(new Paragraph("Année académique: " + anneeAcademique, normalFont));
                document.add(new Paragraph(" "));

                // Tableau des résultats
                addResultsTable(document, parcoursData, sectionFont, normalFont);

                document.add(new Paragraph(" "));
            }

            document.close();
            return output.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Impossible de générer le PDF d'export", e);
        }
    }

    private byte[] buildPdfDocumentSingleParcours(List<SessionEvaluationExportDTO> data, String titre,
                                                   String parcoursLabel, String anneeAcademique) {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, output);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Font sectionFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 10, Font.NORMAL);
            Font smallFont = new Font(Font.HELVETICA, 8, Font.NORMAL);

            // Ajouter l'en-tête officiel
            evaluationPdfService.buildOfficialHeaderPublic(document, smallFont);

            document.add(new Paragraph(titre + " - " + parcoursLabel, titleFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Année académique: " + anneeAcademique, normalFont));
            document.add(new Paragraph(" "));

            // Tableau des résultats triés par nom
            List<SessionEvaluationExportDTO> sortedData = data.stream()
                .sorted((d1, d2) -> d1.etudiantNom().compareTo(d2.etudiantNom()))
                .toList();
            addResultsTable(document, sortedData, sectionFont, normalFont);

            document.close();
            return output.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Impossible de générer le PDF d'export", e);
        }
    }

    private void addResultsTable(Document document, List<SessionEvaluationExportDTO> data, 
                                 Font headerFont, Font normalFont) throws Exception {
        PdfPTable table = new PdfPTable(new float[]{0.8f, 1.5f, 2f, 1.2f});
        table.setWidthPercentage(100f);

        // Headers
        addTableHeader(table, "#", headerFont);
        addTableHeader(table, "Matricule", headerFont);
        addTableHeader(table, "Nom", headerFont);
        addTableHeader(table, "Note Finale", headerFont);

        // Données triées par nom
        List<SessionEvaluationExportDTO> sortedData = data.stream()
            .sorted((d1, d2) -> d1.etudiantNom().compareTo(d2.etudiantNom()))
            .toList();

        AtomicInteger count = new AtomicInteger(1);
        for (SessionEvaluationExportDTO dto : sortedData) {
            table.addCell(createCell(String.valueOf(count.getAndIncrement()), normalFont));
            table.addCell(createCell(dto.matricule() != null ? dto.matricule() : "-", normalFont));
            table.addCell(createCell(dto.etudiantNom() != null ? dto.etudiantNom() : "-", normalFont));
            String noteFinale = dto.totalScore() != null ? String.format("%.2f / %.2f", dto.totalScore(), dto.maxScore()) : "-";
            table.addCell(createCell(noteFinale, normalFont));
        }

        document.add(table);
    }

    private void addTableHeader(PdfPTable table, String title, Font font) {
        PdfPCell cell = new PdfPCell(new Paragraph(title, font));
        cell.setPadding(6f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private PdfPCell createCell(String value, Font font) {
        PdfPCell cell = new PdfPCell(new Paragraph(value, font));
        cell.setPadding(6f);
        return cell;
    }

    // ========== Private Excel Methods ==========

    private byte[] buildExcelWorkbook(Map<Long, List<SessionEvaluationExportDTO>> groupedData, String titre) {
        try (Workbook workbook = new XSSFWorkbook()) {
            for (List<SessionEvaluationExportDTO> parcoursData : groupedData.values()) {
                if (parcoursData.isEmpty()) continue;

                String parcoursLabel = sanitizeSheetName(parcoursData.get(0).parcoursLabel());
                Sheet sheet = workbook.createSheet(parcoursLabel);

                addExcelHeaders(sheet);
                addExcelRows(sheet, parcoursData);
            }

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            workbook.write(output);
            return output.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Impossible de générer le fichier Excel d'export", e);
        }
    }

    private byte[] buildExcelWorkbookSingleParcours(List<SessionEvaluationExportDTO> data, String titre, String parcoursLabel) {
        try (Workbook workbook = new XSSFWorkbook()) {
            String sheetName = sanitizeSheetName(parcoursLabel);
            Sheet sheet = workbook.createSheet(sheetName);

            addExcelHeaders(sheet);
            
            // Trier par nom avant d'ajouter au fichier
            List<SessionEvaluationExportDTO> sortedData = data.stream()
                .sorted((d1, d2) -> d1.etudiantNom().compareTo(d2.etudiantNom()))
                .toList();
            addExcelRows(sheet, sortedData);

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            workbook.write(output);
            return output.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Impossible de générer le fichier Excel d'export", e);
        }
    }

    private void addExcelHeaders(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("#");
        headerRow.createCell(1).setCellValue("Matricule");
        headerRow.createCell(2).setCellValue("Nom");
        headerRow.createCell(3).setCellValue("Note Finale");

        // Auto-size columns
        sheet.setColumnWidth(0, 2000);
        sheet.setColumnWidth(1, 4000);
        sheet.setColumnWidth(2, 6000);
        sheet.setColumnWidth(3, 5000);
    }

    private void addExcelRows(Sheet sheet, List<SessionEvaluationExportDTO> data) {
        // Trier les données par nom
        List<SessionEvaluationExportDTO> sortedData = data.stream()
            .sorted((d1, d2) -> d1.etudiantNom().compareTo(d2.etudiantNom()))
            .toList();

        AtomicInteger rowNum = new AtomicInteger(1);
        for (SessionEvaluationExportDTO dto : sortedData) {
            Row row = sheet.createRow(rowNum.getAndIncrement());
            row.createCell(0).setCellValue(rowNum.get() - 1); // Numéro auto-incrémenté
            row.createCell(1).setCellValue(dto.matricule() != null ? dto.matricule() : "");
            row.createCell(2).setCellValue(dto.etudiantNom() != null ? dto.etudiantNom() : "");
            
            if (dto.totalScore() != null) {
                String noteFinale = String.format("%.2f / %.2f", dto.totalScore(), dto.maxScore());
                row.createCell(3).setCellValue(noteFinale);
            } else {
                row.createCell(3).setCellValue("");
            }
        }
    }

    private String sanitizeSheetName(String name) {
        // Les noms de feuilles Excel ne peuvent pas contenir certains caractères
        return name.replace("/", "-").replace("\\", "-").replace("?", "").replace("*", "").substring(0, Math.min(31, name.length()));
    }
}
