package cm.univ.maroua.enspm.stage.service.dto;

public record SessionEvaluationExportDTO(
    Long sessionId,
    String matricule,
    String etudiantNom,
    Float totalScore,
    Float maxScore,
    Long parcoursId,
    String parcoursLabel,
    String niveauLabel
) {}
