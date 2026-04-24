package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.*;
import cm.univ.maroua.enspm.stage.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class EvaluationNotificationPlannerServiceIntegrationTest {

    @Autowired
    private EvaluationNotificationPlannerService plannerService;

    @Autowired
    private MailQueueRepository mailQueueRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private PeriodeStageRepository periodeStageRepository;

    @Autowired
    private StageRepository stageRepository;

    @Autowired
    private EncadreurRepository encadreurRepository;

    @Autowired
    private TypeStageRepository typeStageRepository;

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Autowired
    private InscriptionRepository inscriptionRepository;

    @Autowired
    private NiveauRepository niveauRepository;

    @Autowired
    private AnneeAcademiqueRepository anneeAcademiqueRepository;

    @Autowired
    private EntrepriseRepository entrepriseRepository;

    @Autowired
    private SessionEvaluationRepository sessionEvaluationRepository;

        @Autowired
        private AppSettingRepository appSettingRepository;

        @Autowired
        private NoteRepository noteRepository;

        @Autowired
        private ParcoursRepository parcoursRepository;

        @Autowired
        private SpecialiteRepository specialiteRepository;

        @Autowired
        private DepartementRepository departementRepository;

        @Autowired
        private CritereRepository critereRepository;

    // Entités partagées entre les tests
    private TypeStage typeStage;
    private AnneeAcademique annee;
    private Encadreur encadreur;
    private Etudiant etudiant;
    private Entreprise entreprise;
    private Niveau niveau;

    @BeforeEach
    void setUp() {
                mailQueueRepository.deleteAllInBatch();
                noteRepository.deleteAllInBatch();
                sessionEvaluationRepository.deleteAllInBatch();
                notificationRepository.deleteAllInBatch();
                stageRepository.deleteAllInBatch();
                inscriptionRepository.deleteAllInBatch();
                periodeStageRepository.deleteAllInBatch();

        typeStage   = typeStageRepository.save(new TypeStage(null, "Stage Test"));
        annee       = anneeAcademiqueRepository.save(new AnneeAcademique(null, "2025-2026", true));
        entreprise  = entrepriseRepository.save(new Entreprise(null, "Entreprise Test", "IT"));
        encadreur   = encadreurRepository.save(new Encadreur(null, "Dupont", "0600000000", "dupont@test.com", entreprise));
        etudiant    = etudiantRepository.save(new Etudiant(null, "Etudiant 1", "etudiant1@test.com", null, "MAT001"));
        niveau      = niveauRepository.save(new Niveau(null, "L3", typeStage));
    }

    // -----------------------------------------------------------------------
    // Tests de planification
    // -----------------------------------------------------------------------

    @Test
    void planifier_doitEnfilerUnMessageQuandNotificationDueAujourdhui() {
        // Arrange : notification DEBUT_STAGE avec offset=0 → déclenche si dateDebut == today
        LocalDate today = LocalDate.now();
        Notification notification = notificationRepository.save(
                new Notification(null, typeStage, NotificationReferenceDateType.DEBUT_STAGE, 0, true));

        Stage stage = creerStageNonNote(encadreur, etudiant, entreprise, annee, today, today.plusDays(25));
        creerInscription(etudiant, annee, niveau);

        // Act
        int nbEnfiles = plannerService.planifier();

        // Assert
        assertThat(nbEnfiles).isEqualTo(1);
        List<MailQueue> queue = mailQueueRepository.findAll();
        assertThat(queue).hasSize(1);
        assertThat(queue.get(0).getDestinataireEmail()).isEqualTo("dupont@test.com");
        assertThat(queue.get(0).getStatut()).isEqualTo(MailQueueStatut.PENDING);
        assertThat(queue.get(0).getEncadreurId()).isEqualTo(encadreur.getId());
                assertThat(queue.get(0).getStageId()).isEqualTo(stage.getId());
                assertThat(queue.get(0).getPeriodeStageId()).isNull();
        assertThat(queue.get(0).getNotificationId()).isEqualTo(notification.getId());
    }

    @Test
    void planifier_notifieEncoreSiEvaluationPartielle() {
        // Arrange : session d'évaluation présente mais sans note -> considéré non noté
        LocalDate today = LocalDate.now();
        notificationRepository.save(
                new Notification(null, typeStage, NotificationReferenceDateType.FIN_STAGE, 0, true));

        Stage stage = creerStageNonNote(encadreur, etudiant, entreprise, annee, today.minusDays(5), today);
        creerInscription(etudiant, annee, niveau);

        // Session ouverte sans note saisie
        sessionEvaluationRepository.save(
                new SessionEvaluation(null, stage, "CODE123", SessionEvaluationStatut.EN_ATTENTE, null));

        // Act
        int nbEnfiles = plannerService.planifier();

        // Assert : session présente mais sans notes → doit quand même envoyer
        // (notes IS EMPTY → on notifie encore)
        assertThat(nbEnfiles).isEqualTo(1);
        assertThat(sessionEvaluationRepository.findByStageId(stage.getId()))
                .get()
                .extracting(SessionEvaluation::getDateLimite)
                                .isEqualTo(today.plusDays(14));
    }

        @Test
        void planifier_dateLimite_utiliseDelaiConfigure() {
                LocalDate today = LocalDate.now();
                notificationRepository.save(
                                new Notification(null, typeStage, NotificationReferenceDateType.FIN_STAGE, 0, true));

                Stage stage = creerStageNonNote(encadreur, etudiant, entreprise, annee, today.minusDays(8), today);
                creerInscription(etudiant, annee, niveau);

                appSettingRepository.findByCle("EVALUATION_DELAY_DAYS").ifPresent(setting -> {
                        setting.setValeur("7");
                        appSettingRepository.save(setting);
                });

                int nbEnfiles = plannerService.planifier();

                assertThat(nbEnfiles).isEqualTo(1);
                assertThat(sessionEvaluationRepository.findByStageId(stage.getId()))
                                .get()
                                .extracting(SessionEvaluation::getDateLimite)
                                .isEqualTo(today.plusDays(7));
        }

        @Test
        void planifier_dateLimite_fallbackQuandDelaiInvalide() {
                LocalDate today = LocalDate.now();
                notificationRepository.save(
                                new Notification(null, typeStage, NotificationReferenceDateType.FIN_STAGE, 0, true));

                Stage stage = creerStageNonNote(encadreur, etudiant, entreprise, annee, today.minusDays(8), today);
                creerInscription(etudiant, annee, niveau);

                appSettingRepository.findByCle("EVALUATION_DELAY_DAYS").ifPresent(setting -> {
                        setting.setValeur("abc");
                        appSettingRepository.save(setting);
                });

                int nbEnfiles = plannerService.planifier();

                assertThat(nbEnfiles).isEqualTo(1);
                assertThat(sessionEvaluationRepository.findByStageId(stage.getId()))
                                .get()
                                .extracting(SessionEvaluation::getDateLimite)
                                .isEqualTo(today.plusDays(14));
        }

    @Test
        void planifier_neDoitPasEnfilerSiNotificationNonDue() {
        // Arrange : offset = 5 → déclenche quand dateDebut = today - 5 → pas aujourd'hui si dateDebut = today
        LocalDate today = LocalDate.now();
        notificationRepository.save(
                new Notification(null, typeStage, NotificationReferenceDateType.DEBUT_STAGE, 5, true));

        creerStageNonNote(encadreur, etudiant, entreprise, annee, today, today.plusDays(25));
        creerInscription(etudiant, annee, niveau);

        // Act
        int nbEnfiles = plannerService.planifier();

        // Assert
        assertThat(nbEnfiles).isEqualTo(0);
    }

    @Test
        void planifier_neDoitPasEnfilerSiNotificationInactive() {
        LocalDate today = LocalDate.now();
        notificationRepository.save(
                        new Notification(null, typeStage, NotificationReferenceDateType.DEBUT_STAGE, 0, false));

                creerStageNonNote(encadreur, etudiant, entreprise, annee, today, today.plusDays(25));
        creerInscription(etudiant, annee, niveau);

        int nbEnfiles = plannerService.planifier();

        assertThat(nbEnfiles).isEqualTo(0);
    }

    @Test
    void planifier_antiDoublon_deuxiemeAppelNeCreeAucunMessageSupplementaire() {
        LocalDate today = LocalDate.now();
        notificationRepository.save(
                new Notification(null, typeStage, NotificationReferenceDateType.DEBUT_STAGE, 0, true));

        creerStageNonNote(encadreur, etudiant, entreprise, annee, today, today.plusDays(25));
        creerInscription(etudiant, annee, niveau);

        // Act : deux exécutions consécutives
        plannerService.planifier();
        int secondAppel = plannerService.planifier();

        // Assert : pas de doublon
        assertThat(secondAppel).isEqualTo(0);
        assertThat(mailQueueRepository.findAll()).hasSize(1);
    }

    @Test
        void planifier_finPeriode_doitEnfilerQuandDateFinEgaleReference() {
        // Arrange : FIN_STAGE offset=0 → déclenche quand dateFin == today
        LocalDate today = LocalDate.now();
        notificationRepository.save(
                new Notification(null, typeStage, NotificationReferenceDateType.FIN_STAGE, 0, true));

        creerStageNonNote(encadreur, etudiant, entreprise, annee, today.minusDays(30), today);
        creerInscription(etudiant, annee, niveau);

        int nbEnfiles = plannerService.planifier();

        assertThat(nbEnfiles).isEqualTo(1);
    }

    @Test
    void planifier_neDoitPasEnfilerSiTousLesStagesSontNotes() {
        LocalDate today = LocalDate.now();
        notificationRepository.save(
                new Notification(null, typeStage, NotificationReferenceDateType.DEBUT_STAGE, 0, true));

        Stage stage = creerStageNonNote(encadreur, etudiant, entreprise, annee, today, today.plusDays(25));
        creerInscription(etudiant, annee, niveau);

        SessionEvaluation session = sessionEvaluationRepository.save(
                new SessionEvaluation(null, stage, "CODE456", SessionEvaluationStatut.EN_ATTENTE, today.plusDays(30)));

        Critere critere = critereRepository.save(new Critere(null, "Assiduité", "Critère test"));
        noteRepository.save(new Note(null, session, critere, null, 15, "RAS", LocalDate.now()));

        int nbEnfiles = plannerService.planifier();

        assertThat(nbEnfiles).isEqualTo(0);
        assertThat(mailQueueRepository.findAll()).isEmpty();
    }

        @Test
        void planifier_joursAvantFinStage_doitEnfilerQuandReferenceCorrespond() {
                LocalDate today = LocalDate.now();
                notificationRepository.save(
                                new Notification(null, typeStage, NotificationReferenceDateType.JOURS_AVANT_FIN_STAGE, -7, true));

                creerStageNonNote(encadreur, etudiant, entreprise, annee, today.minusDays(14), today.plusDays(7));
                creerInscription(etudiant, annee, niveau);

                int nbEnfiles = plannerService.planifier();

                assertThat(nbEnfiles).isEqualTo(1);
        }

        @Test
        void planifier_joursApresFinStage_doitEnfilerQuandReferenceCorrespond() {
                LocalDate today = LocalDate.now();
                notificationRepository.save(
                                new Notification(null, typeStage, NotificationReferenceDateType.JOURS_APRES_FIN_STAGE, 2, true));

                creerStageNonNote(encadreur, etudiant, entreprise, annee, today.minusDays(20), today.minusDays(2));
                creerInscription(etudiant, annee, niveau);

                int nbEnfiles = plannerService.planifier();

                assertThat(nbEnfiles).isEqualTo(1);
        }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private Stage creerStageNonNote(Encadreur enc, Etudiant etu, Entreprise ent,
                                    AnneeAcademique aa, LocalDate debut, LocalDate fin) {
        Stage stage = new Stage();
        stage.setEtudiant(etu);
        stage.setEntreprise(ent);
        stage.setEncadreur(enc);
        stage.setAnneeAcademique(aa);
        stage.setTypeStage(typeStage);
        stage.setDateDebut(debut);
        stage.setDateFin(fin);
        stage.setStatut(Statut.VALIDE);
        stage.setSource(Source.OPERATEUR);
        return stageRepository.save(stage);
    }

        private void creerInscription(Etudiant etu, AnneeAcademique aa, Niveau niv) {
                Departement dept = departementRepository.save(new Departement(null, "INFO", "Informatique"));
                Specialite spec = specialiteRepository.save(new Specialite(null, "GL", "Génie Logiciel", dept));
                Parcours parcours = parcoursRepository.save(new Parcours(null, spec, niv, null));
        inscriptionRepository.save(new Inscription(null, aa, etu, parcours));
    }
}
