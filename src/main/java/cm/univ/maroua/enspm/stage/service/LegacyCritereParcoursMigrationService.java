package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.Bareme;
import cm.univ.maroua.enspm.stage.domain.BaremeCritere;
import cm.univ.maroua.enspm.stage.domain.CritereParcours;
import cm.univ.maroua.enspm.stage.domain.Parcours;
import cm.univ.maroua.enspm.stage.repository.BaremeCritereRepository;
import cm.univ.maroua.enspm.stage.repository.BaremeRepository;
import cm.univ.maroua.enspm.stage.repository.CritereParcoursRepository;
import cm.univ.maroua.enspm.stage.repository.ParcoursRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
/**
 * Service metier LegacyCritereParcoursMigrationService.
 */
public class LegacyCritereParcoursMigrationService {

    private final CritereParcoursRepository critereParcoursRepository;
    private final BaremeRepository baremeRepository;
    private final BaremeCritereRepository baremeCritereRepository;
    private final ParcoursRepository parcoursRepository;

    public LegacyCritereParcoursMigrationService(
            CritereParcoursRepository critereParcoursRepository,
            BaremeRepository baremeRepository,
            BaremeCritereRepository baremeCritereRepository,
            ParcoursRepository parcoursRepository) {
        this.critereParcoursRepository = critereParcoursRepository;
        this.baremeRepository = baremeRepository;
        this.baremeCritereRepository = baremeCritereRepository;
        this.parcoursRepository = parcoursRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void migrateIfNeeded() {
        List<CritereParcours> legacyRows = critereParcoursRepository.findAll();
        if (legacyRows.isEmpty()) {
            return;
        }

        Map<Long, List<CritereParcours>> byParcours = legacyRows.stream()
                .filter(row -> row.getParcours() != null && row.getParcours().getId() != null)
                .collect(Collectors.groupingBy(row -> row.getParcours().getId()));

        if (byParcours.isEmpty()) {
            return;
        }

        Map<String, Bareme> signatureToBareme = new HashMap<>();
        int createdBaremes = 0;
        String runSuffix = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());

        for (Map.Entry<Long, List<CritereParcours>> entry : byParcours.entrySet()) {
            Long parcoursId = entry.getKey();
            Parcours parcours = parcoursRepository.findById(parcoursId).orElse(null);
            if (parcours == null || parcours.getBareme() != null) {
                continue;
            }

            List<CritereParcours> sortedRows = new ArrayList<>(entry.getValue());
            sortedRows.sort(Comparator.comparing(row -> row.getCritere().getId()));

            String signature = buildSignature(sortedRows);
            Bareme bareme = signatureToBareme.get(signature);

            if (bareme == null) {
                bareme = new Bareme();
                bareme.setCode("AUTO_" + runSuffix + "_" + (createdBaremes + 1));
                bareme.setLibelle("Barème migré automatiquement");
                bareme.setActif(Boolean.TRUE);
                bareme = baremeRepository.save(bareme);

                List<BaremeCritere> lignes = new ArrayList<>();
                for (CritereParcours legacyRow : sortedRows) {
                    if (legacyRow.getCritere() == null || legacyRow.getCritere().getId() == null || legacyRow.getCoefficient() == null) {
                        continue;
                    }
                    BaremeCritere ligne = new BaremeCritere();
                    ligne.setBareme(bareme);
                    ligne.setCritere(legacyRow.getCritere());
                    ligne.setCoefficient(legacyRow.getCoefficient());
                    lignes.add(ligne);
                }
                baremeCritereRepository.saveAll(lignes);
                signatureToBareme.put(signature, bareme);
                createdBaremes++;
            }

            parcours.setBareme(bareme);
            parcoursRepository.save(parcours);
        }

        // Legacy flow is decommissioned: remove only rows that are safely migrated.
        critereParcoursRepository.deleteForMigratedParcours();
    }

    private String buildSignature(List<CritereParcours> rows) {
        return rows.stream()
                .map(row -> {
                    Long critereId = row.getCritere() != null ? row.getCritere().getId() : null;
                    Float coefficient = row.getCoefficient();
                    return critereId + ":" + normalizeCoefficient(coefficient);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.joining("|"));
    }

    private String normalizeCoefficient(Float coefficient) {
        if (coefficient == null) {
            return "null";
        }
        return String.format(java.util.Locale.ROOT, "%.4f", coefficient);
    }
}
