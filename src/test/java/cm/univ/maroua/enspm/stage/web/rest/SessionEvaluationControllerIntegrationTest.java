package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.domain.SessionEvaluation;
import cm.univ.maroua.enspm.stage.domain.SessionEvaluationStatut;
import cm.univ.maroua.enspm.stage.repository.SessionEvaluationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class SessionEvaluationControllerIntegrationTest {

    private MockMvc mockMvc;

        @Autowired
        private WebApplicationContext webApplicationContext;

    @Autowired
    private SessionEvaluationRepository sessionEvaluationRepository;

    @BeforeEach
    void setUp() {
                mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        sessionEvaluationRepository.deleteAll();
    }

    @Test
    void createSessionEvaluationShouldAcceptEnCoursStatus() throws Exception {
        String payload = """
                {
                  "codeAcces": "ACC-001",
                  "statut": "EN_COURS",
                  "dateLimite": "2030-01-15"
                }
                """;

        mockMvc.perform(post("/api/v1/session-evaluations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.codeAcces").value("ACC-001"))
                .andExpect(jsonPath("$.statut").value("EN_COURS"))
                .andExpect(jsonPath("$.dateLimite").value("2030-01-15"));

        assertThat(sessionEvaluationRepository.findAll())
                .hasSize(1)
                .first()
                .extracting(SessionEvaluation::getStatut, SessionEvaluation::getCodeAcces, SessionEvaluation::getDateLimite)
                .containsExactly(SessionEvaluationStatut.EN_COURS, "ACC-001", LocalDate.of(2030, 1, 15));
    }

    @Test
    void createSessionEvaluationShouldRejectInvalidStatusWithBadRequest() throws Exception {
        String payload = """
                {
                  "codeAcces": "ACC-002",
                  "statut": "STATUT_INVALIDE",
                  "dateLimite": "2030-01-16"
                }
                """;

        mockMvc.perform(post("/api/v1/session-evaluations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());

        assertThat(sessionEvaluationRepository.count()).isZero();
    }
}