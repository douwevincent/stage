package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.Bareme;
import cm.univ.maroua.enspm.stage.repository.BaremeCritereRepository;
import cm.univ.maroua.enspm.stage.repository.BaremeRepository;
import cm.univ.maroua.enspm.stage.repository.InscriptionRepository;
import cm.univ.maroua.enspm.stage.repository.ParcoursRepository;
import cm.univ.maroua.enspm.stage.service.dto.BaremeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class BaremeServiceIntegrationTest {

    @Autowired
    private BaremeService baremeService;

    @Autowired
    private BaremeRepository baremeRepository;

    @Autowired
    private BaremeCritereRepository baremeCritereRepository;

    @Autowired
    private ParcoursRepository parcoursRepository;

    @Autowired
    private InscriptionRepository inscriptionRepository;

    @BeforeEach
    void setUp() {
        inscriptionRepository.deleteAllInBatch();
        parcoursRepository.deleteAllInBatch();
        baremeCritereRepository.deleteAllInBatch();
        baremeRepository.deleteAllInBatch();
    }

    @Test
    void saveShouldKeepOnlyOneDefaultBareme() {
        BaremeDTO premier = baremeService.save(new BaremeDTO(null, "B1", "Barème 1", true, true));
        BaremeDTO second = baremeService.save(new BaremeDTO(null, "B2", "Barème 2", true, true));

        List<Bareme> baremes = baremeRepository.findAll();

        assertThat(baremes).hasSize(2);
        assertThat(baremes).filteredOn(Bareme::getParDefaut).hasSize(1);
        assertThat(baremeRepository.findById(premier.id())).get().extracting(Bareme::getParDefaut).isEqualTo(false);
        assertThat(baremeRepository.findById(second.id())).get().extracting(Bareme::getParDefaut).isEqualTo(true);
    }
}