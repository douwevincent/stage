package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.service.dto.EvaluationCategoryDetailDTO;
import cm.univ.maroua.enspm.stage.service.dto.EvaluationCriterionDetailDTO;
import cm.univ.maroua.enspm.stage.service.dto.EvaluationResultDetailDTO;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

@Service
public class EvaluationPdfService {

    public byte[] buildEditableEvaluationSheet(EvaluationResultDetailDTO detail) {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, output);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Font sectionFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 10, Font.NORMAL);
            Font smallFont = new Font(Font.HELVETICA, 8, Font.NORMAL);
            Font signatureFont = new Font(Font.TIMES_ROMAN, 16, Font.ITALIC);

            buildOfficialHeader(document, smallFont);

            document.add(new Paragraph("Fiche d'evaluation de stage", titleFont));
            document.add(new Paragraph(" "));

            PdfPTable infos = new PdfPTable(2);
            infos.setWidthPercentage(100f);
            infos.setSpacingAfter(10f);

            addInfoCell(infos, "Matricule", detail.matricule(), normalFont);
            addInfoCell(infos, "Etudiant", detail.etudiantNom(), normalFont);
            addInfoCell(infos, "Departement", detail.departement(), normalFont);
            addInfoCell(infos, "Niveau", detail.niveau(), normalFont);
            addInfoCell(infos, "Specialite", detail.specialite(), normalFont);
            addInfoCell(infos, "Entreprise", detail.entrepriseNom(), normalFont);
            addInfoCell(infos, "Encadreur", detail.encadreurNom(), normalFont);
            addInfoCell(infos, "Periode", formatPeriod(detail), normalFont);
            addInfoCell(infos, "Note totale", formatScore(detail.totalScore()) + " / " + formatScore(detail.maxScore()), normalFont);
            addInfoCell(infos, "Annee academique", detail.anneeAcademique(), normalFont);
            document.add(infos);

            document.add(new Paragraph("Detail des notes", sectionFont));
            document.add(new Paragraph(" "));

            PdfPTable notes = new PdfPTable(new float[]{2.2f, 3.2f, 1.2f, 1.2f, 2.2f});
            notes.setWidthPercentage(100f);
            addHeader(notes, "Categorie", sectionFont);
            addHeader(notes, "Critere", sectionFont);
            addHeader(notes, "Coeff", sectionFont);
            addHeader(notes, "Note", sectionFont);
            addHeader(notes, "Commentaire", sectionFont);

            for (EvaluationCategoryDetailDTO category : detail.categories()) {
                List<EvaluationCriterionDetailDTO> criteres = category.criteres();
                if (criteres == null || criteres.isEmpty()) {
                    continue;
                }
                for (int i = 0; i < criteres.size(); i++) {
                    EvaluationCriterionDetailDTO critere = criteres.get(i);
                    if (i == 0) {
                        PdfPCell categoryCell = valueCell(category.categorie(), normalFont);
                        categoryCell.setRowspan(criteres.size());
                        notes.addCell(categoryCell);
                    }
                    notes.addCell(valueCell(critere.critere(), normalFont));
                    notes.addCell(valueCell(formatScore(critere.coefficient()), normalFont));
                    notes.addCell(valueCell(critere.note() != null ? String.valueOf(critere.note()) : "-", normalFont));
                    notes.addCell(valueCell(critere.commentaire(), normalFont));
                }
            }

            document.add(notes);

            document.add(new Paragraph("\n"));
            PdfPTable signatureTable = new PdfPTable(new float[]{1f, 1f});
            signatureTable.setWidthPercentage(100f);

            PdfPCell leftSpacer = new PdfPCell(new Paragraph(" "));
            leftSpacer.setBorder(PdfPCell.NO_BORDER);
            signatureTable.addCell(leftSpacer);

            PdfPCell signatureCell = new PdfPCell();
            signatureCell.setBorder(PdfPCell.NO_BORDER);

            Paragraph encadreurLabel = new Paragraph("Nom de l'encadreur :", normalFont);
            encadreurLabel.setAlignment(Element.ALIGN_RIGHT);
            signatureCell.addElement(encadreurLabel);

            Paragraph signatureName = new Paragraph(safe(detail.encadreurNom()), signatureFont);
            signatureName.setAlignment(Element.ALIGN_RIGHT);
            signatureName.setSpacingBefore(6f);
            signatureCell.addElement(signatureName);

            Paragraph signatureRule = new Paragraph("______________________________", normalFont);
            signatureRule.setAlignment(Element.ALIGN_RIGHT);
            signatureRule.setSpacingBefore(4f);
            signatureCell.addElement(signatureRule);

            Paragraph signatureWord = new Paragraph("Signature", normalFont);
            signatureWord.setAlignment(Element.ALIGN_RIGHT);
            signatureCell.addElement(signatureWord);

            signatureTable.addCell(signatureCell);
            document.add(signatureTable);

            document.close();
            return output.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Impossible de generer la fiche PDF", e);
        }
    }

    private void buildOfficialHeader(Document document, Font smallFont) throws Exception {
        // Table 3 colonnes : FR | Logos+Contact | EN
        PdfPTable header = new PdfPTable(new float[]{1f, 0.75f, 1f});
        header.setWidthPercentage(100f);
        header.setSpacingAfter(4f);

        // ---- Colonne gauche (Français) ----
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(PdfPCell.NO_BORDER);
        leftCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        leftCell.setVerticalAlignment(Element.ALIGN_TOP);

        Font boldSmall = new Font(Font.HELVETICA, 8, Font.BOLD);
        String[] leftLines = {
            "République du Cameroun",
            "Paix – Travail – Patrie",
            "****",
            "Ministère de l'Enseignement Supérieur",
            "****",
            "Université de Maroua",
            "****",
            "Ecole Nationale Supérieure Polytechnique",
            "B.P 58 Maroua",
            "enspm@univ-maroua.cm",
            "http://www.enspm.univ-maroua.cm"
        };
        for (String line : leftLines) {
            Paragraph p = new Paragraph(line, smallFont);
            p.setAlignment(Element.ALIGN_CENTER);
            p.setSpacingBefore(1f);
            leftCell.addElement(p);
        }
        header.addCell(leftCell);

        // ---- Colonne centre (Logos + Contact université) ----
        PdfPCell centerCell = new PdfPCell();
        centerCell.setBorder(PdfPCell.NO_BORDER);
        centerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        centerCell.setVerticalAlignment(Element.ALIGN_TOP);

        // Logo université de Maroua
        try (InputStream univLogoStream = getClass().getResourceAsStream("/images/logo-univ-maroua.png")) {
            if (univLogoStream != null) {
                Image univLogo = Image.getInstance(univLogoStream.readAllBytes());
                univLogo.scaleToFit(65, 65);
                univLogo.setAlignment(Element.ALIGN_CENTER);
                centerCell.addElement(univLogo);
            }
        } catch (Exception ignored) { }

        String[] centerLines = {
            "B.P 46 Maroua",
            "Tel 222 29 50 46/Fax 222 29 50 12",
            "www.univ-maroua.cm",
            "rectorat@univ-maroua.cm"
        };
        for (String line : centerLines) {
            Paragraph p = new Paragraph(line, smallFont);
            p.setAlignment(Element.ALIGN_CENTER);
            p.setSpacingBefore(1f);
            centerCell.addElement(p);
        }

        // Logo ENSPM
        try (InputStream enspmLogoStream = getClass().getResourceAsStream("/images/logo-enspm.png")) {
            if (enspmLogoStream != null) {
                Image enspmLogo = Image.getInstance(enspmLogoStream.readAllBytes());
                enspmLogo.scaleToFit(65, 65);
                enspmLogo.setAlignment(Element.ALIGN_CENTER);
                centerCell.addElement(enspmLogo);
            }
        } catch (Exception ignored) { }

        header.addCell(centerCell);

        // ---- Colonne droite (Anglais) ----
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(PdfPCell.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        rightCell.setVerticalAlignment(Element.ALIGN_TOP);

        String[] rightLines = {
            "Republic of Cameroon",
            "Peace – Work – Fatherland",
            "****",
            "Ministry of Higher Education",
            "****",
            "The University of Maroua",
            "****",
            "The National Advanced School of Engineering",
            "PO Box 58 Maroua",
            "enspm@univ-maroua.cm",
            "http://www.enspm.univ-maroua.cm"
        };
        for (String line : rightLines) {
            Paragraph p = new Paragraph(line, smallFont);
            p.setAlignment(Element.ALIGN_CENTER);
            p.setSpacingBefore(1f);
            rightCell.addElement(p);
        }
        header.addCell(rightCell);

        document.add(header);

        // Filet de séparation
        LineSeparator separator = new LineSeparator(1f, 100f, null, Element.ALIGN_CENTER, -2f);
        document.add(separator);
        document.add(new Paragraph(" "));
    }

    private static void addInfoCell(PdfPTable table, String label, String value, Font font) {
        Font boldFont = new Font(font.getFamily(), font.getSize(), Font.BOLD);
        com.lowagie.text.Phrase phrase = new com.lowagie.text.Phrase();
        phrase.add(new com.lowagie.text.Chunk(label + " : ", boldFont));
        phrase.add(new com.lowagie.text.Chunk(value == null || value.isBlank() ? "-" : value, font));
        PdfPCell cell = new PdfPCell(phrase);
        cell.setPadding(6f);
        table.addCell(cell);
    }

    private static PdfPCell valueCell(String value, Font font) {
        PdfPCell cell = new PdfPCell(new Paragraph(value == null || value.isBlank() ? "-" : value, font));
        cell.setPadding(6f);
        return cell;
    }

    private static void addHeader(PdfPTable table, String title, Font font) {
        PdfPCell cell = new PdfPCell(new Paragraph(title, font));
        cell.setPadding(6f);
        table.addCell(cell);
    }

    private static String formatScore(Number score) {
        if (score == null) {
            return "0";
        }
        double value = score.doubleValue();
        if (value == Math.rint(value)) {
            return String.valueOf((long) value);
        }
        return String.format(java.util.Locale.ROOT, "%.2f", value);
    }

    private static String formatPeriod(EvaluationResultDetailDTO detail) {
        String start = detail.dateDebut() != null ? detail.dateDebut().toString() : "-";
        String end = detail.dateFin() != null ? detail.dateFin().toString() : "-";
        return start + " -> " + end;
    }

    private static String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
