package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.Bareme;
import cm.univ.maroua.enspm.stage.domain.Departement;
import cm.univ.maroua.enspm.stage.domain.Niveau;
import cm.univ.maroua.enspm.stage.domain.Parcours;
import cm.univ.maroua.enspm.stage.domain.Specialite;
import cm.univ.maroua.enspm.stage.repository.BaremeRepository;
import cm.univ.maroua.enspm.stage.repository.BaremeCritereRepository;
import cm.univ.maroua.enspm.stage.repository.DepartementRepository;
import cm.univ.maroua.enspm.stage.repository.InscriptionRepository;
import cm.univ.maroua.enspm.stage.repository.NiveauRepository;
import cm.univ.maroua.enspm.stage.repository.ParcoursRepository;
import cm.univ.maroua.enspm.stage.repository.SpecialiteRepository;
import cm.univ.maroua.enspm.stage.service.dto.ParcoursDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class ParcoursServiceIntegrationTest {

    @Autowired
    private ParcoursService parcoursService;

    @Autowired
    private ParcoursRepository parcoursRepository;

    @Autowired
    private BaremeRepository baremeRepository;

    @Autowired
    private BaremeCritereRepository baremeCritereRepository;

    @Autowired
    private DepartementRepository departementRepository;

    @Autowired
    private SpecialiteRepository specialiteRepository;

    @Autowired
    private NiveauRepository niveauRepository;

    @Autowired
    private InscriptionRepository inscriptionRepository;

    @BeforeEach
    void setUp() {
        inscriptionRepository.deleteAllInBatch();
        parcoursRepository.deleteAllInBatch();
        baremeCritereRepository.deleteAllInBatch();
        baremeRepository.deleteAllInBatch();
        specialiteRepository.deleteAllInBatch();
        niveauRepository.deleteAllInBatch();
        departementRepository.deleteAllInBatch();
    }

    @Test
    void saveShouldAssignDefaultBaremeWhenBaremeIsMissing() {
        Bareme baremeParDefaut = baremeRepository.save(new Bareme(null, "B-DEF", "Barème par défaut", true, true, null));

        Departement departement = departementRepository.save(new Departement(null, "INFO", "Informatique"));
        Specialite specialite = specialiteRepository.save(new Specialite(null, "GL", "Génie Logiciel", departement));
        Niveau niveau = niveauRepository.save(new Niveau(null, "Licence 3", null));

        ParcoursDTO saved = parcoursService.save(new ParcoursDTO(
                null,
                null,
                null,
                null,
                specialite.getId(),
                niveau.getId(),
                null,
                null,
                null,
                null,
                null,
                null));

        Parcours parcours = parcoursRepository.findById(saved.id()).orElseThrow();

        assertThat(saved.baremeId()).isEqualTo(baremeParDefaut.getId());
        assertThat(parcours.getBareme()).isNotNull();
        assertThat(parcours.getBareme().getId()).isEqualTo(baremeParDefaut.getId());
    }

    @Test
    void saveShouldRejectInactiveBareme() {
        Bareme baremeInactif = baremeRepository.save(new Bareme(null, "B-INACTIF", "Barème inactif", false, false, null));

        Departement departement = departementRepository.save(new Departement(null, "INFO", "Informatique"));
        Specialite specialite = specialiteRepository.save(new Specialite(null, "GL", "Génie Logiciel", departement));
        Niveau niveau = niveauRepository.save(new Niveau(null, "Licence 3", null));

        assertThatThrownBy(() -> parcoursService.save(new ParcoursDTO(
                null,
                null,
                null,
                null,
                specialite.getId(),
                niveau.getId(),
                baremeInactif.getId(),
                null,
                null,
                null,
                null,
                null)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Le barème doit être actif");
    }
}