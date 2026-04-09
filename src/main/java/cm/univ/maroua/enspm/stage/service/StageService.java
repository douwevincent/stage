package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.*;
import cm.univ.maroua.enspm.stage.repository.*;
import cm.univ.maroua.enspm.stage.service.dto.StageDTO;
import cm.univ.maroua.enspm.stage.service.mapper.StageMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
public class StageService {

    private final StageRepository stageRepository;
    private final StageMapper stageMapper;
    private final AnneeAcademiqueRepository anneeAcademiqueRepository;
    private final EtudiantRepository etudiantRepository;
    private final EncadreurRepository encadreurRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final InscriptionRepository inscriptionRepository;
    private final TypeStageRepository typeStageRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public StageService(StageRepository stageRepository, StageMapper stageMapper,
            AnneeAcademiqueRepository anneeAcademiqueRepository,
            EtudiantRepository etudiantRepository,
            EncadreurRepository encadreurRepository,
            EntrepriseRepository entrepriseRepository,
            InscriptionRepository inscriptionRepository,
            TypeStageRepository typeStageRepository) {
        this.stageRepository = stageRepository;
        this.stageMapper = stageMapper;
        this.anneeAcademiqueRepository = anneeAcademiqueRepository;
        this.etudiantRepository = etudiantRepository;
        this.encadreurRepository = encadreurRepository;
        this.entrepriseRepository = entrepriseRepository;
        this.inscriptionRepository = inscriptionRepository;
        this.typeStageRepository = typeStageRepository;
    }

    public Page<StageDTO> findAll(Pageable pageable) {
        return stageRepository.findAll(pageable).map(stageMapper::toDto);
    }

    public Page<StageDTO> findByStatut(Statut statut, Pageable pageable) {
        return stageRepository.findByStatut(statut, pageable).map(stageMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<StageDTO> findOne(Long id) {
        return stageRepository.findById(id).map(stageMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<Stage> findEntityById(Long id) {
        return stageRepository.findById(id);
    }

    public StageDTO save(StageDTO stageDTO) {
        Stage stage = stageMapper.toEntity(stageDTO);
        sanitizeTransientRelations(stage);

        if (stage.getEntreprise() == null || stage.getEntreprise().getId() == null) {
            throw new IllegalArgumentException("L'entreprise est obligatoire");
        }

        if (stage.getAnneeAcademique() == null || stage.getAnneeAcademique().getId() == null) {
            AnneeAcademique active = anneeAcademiqueRepository.findByActifTrue()
                    .orElseThrow(() -> new IllegalStateException("Aucune année académique active"));
            stage.setAnneeAcademique(active);
        }
        resolveAndAssignTypeStage(stage, stageDTO.typeStageId());
        if (stage.getSource() == null) {
            stage.setSource(Source.OPERATEUR);
        }
        if (stage.getStatut() == null) {
            stage.setStatut(Statut.VALIDE);
        }
        stage = stageRepository.save(stage);
        return stageMapper.toDto(stage);
    }

    public StageDTO update(Long id, StageDTO stageDTO) {
        StageDTO existing = findOne(id)
                .orElseThrow(() -> new IllegalArgumentException("Stage non trouvé: " + id));

        StageDTO merged = new StageDTO(
                id,
                stageDTO.etudiantId(),
                stageDTO.etudiantMatricule(),
                stageDTO.etudiantNom(),
            stageDTO.typeStageId() != null ? stageDTO.typeStageId() : existing.typeStageId(),
            stageDTO.typeStageLibelle() != null ? stageDTO.typeStageLibelle() : existing.typeStageLibelle(),
                stageDTO.entrepriseId() != null ? stageDTO.entrepriseId() : existing.entrepriseId(),
                stageDTO.entrepriseNom(),
                stageDTO.ville(),
                stageDTO.adresse(),
                stageDTO.encadreurId() != null ? stageDTO.encadreurId() : existing.encadreurId(),
                stageDTO.encadreurNom(),
                stageDTO.dateDebut() != null ? stageDTO.dateDebut() : existing.dateDebut(),
                stageDTO.dateFin() != null ? stageDTO.dateFin() : existing.dateFin(),
                stageDTO.anneeAcademiqueId() != null ? stageDTO.anneeAcademiqueId() : existing.anneeAcademiqueId(),
                stageDTO.sessionEvaluationId() != null ? stageDTO.sessionEvaluationId() : existing.sessionEvaluationId(),
                stageDTO.source() != null ? stageDTO.source() : existing.source(),
                stageDTO.statut() != null ? stageDTO.statut() : existing.statut(),
                stageDTO.cheminAutorisation() != null ? stageDTO.cheminAutorisation() : existing.cheminAutorisation());

        return save(merged);
    }

    // MapStruct may instantiate relation objects with null IDs (e.g. Encadreur{id=null}).
    // Hibernate interprets them as transient entities and fails on flush.
    private void sanitizeTransientRelations(Stage stage) {
        if (stage.getEtudiant() != null && stage.getEtudiant().getId() == null) {
            stage.setEtudiant(null);
        }
        if (stage.getEntreprise() != null && stage.getEntreprise().getId() == null) {
            stage.setEntreprise(null);
        }
        if (stage.getTypeStage() != null && stage.getTypeStage().getId() == null) {
            stage.setTypeStage(null);
        }
        if (stage.getEncadreur() != null && stage.getEncadreur().getId() == null) {
            stage.setEncadreur(null);
        }
        if (stage.getAnneeAcademique() != null && stage.getAnneeAcademique().getId() == null) {
            stage.setAnneeAcademique(null);
        }
        if (stage.getSessionEvaluation() != null && stage.getSessionEvaluation().getId() == null) {
            stage.setSessionEvaluation(null);
        }
    }

    public StageDTO declarer(String etudiantMatricule,
                              Long entrepriseId, String entrepriseNom, String entrepriseSecteur,
                              String ville, String adresse,
                              LocalDate dateDebut, LocalDate dateFin,
                              MultipartFile autorisation) throws IOException {

        Etudiant etudiant = etudiantRepository.findByMatricule(etudiantMatricule)
                .orElseThrow(() -> new IllegalArgumentException("Étudiant non trouvé: " + etudiantMatricule));

        Entreprise entreprise;
        if (entrepriseId != null) {
            entreprise = entrepriseRepository.findById(entrepriseId)
                    .orElseThrow(() -> new IllegalArgumentException("Entreprise non trouvée: " + entrepriseId));
        } else {
            String nomRecherche = entrepriseNom != null ? entrepriseNom.trim() : "";
            entreprise = entrepriseRepository.findByNomIgnoreCase(nomRecherche)
                    .orElseGet(() -> {
                        Entreprise e = new Entreprise();
                        e.setNom(nomRecherche);
                        e.setSecteur(entrepriseSecteur);
                        return entrepriseRepository.save(e);
                    });
        }

        String cheminAutorisation = null;
        if (autorisation != null && !autorisation.isEmpty()) {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
            String originalName = StringUtils.cleanPath(
                    autorisation.getOriginalFilename() != null ? autorisation.getOriginalFilename() : "file");
            String fileName = System.currentTimeMillis() + "_" + originalName;
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(autorisation.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            cheminAutorisation = fileName;
        }

        AnneeAcademique anneeActive = anneeAcademiqueRepository.findByActifTrue()
                .orElseThrow(() -> new IllegalStateException("Aucune année académique active"));

        Stage stage = new Stage();
        stage.setEtudiant(etudiant);
        stage.setEntreprise(entreprise);
        stage.setVille(ville);
        stage.setAdresse(adresse);
        stage.setDateDebut(dateDebut);
        stage.setDateFin(dateFin);
        stage.setAnneeAcademique(anneeActive);
        resolveAndAssignTypeStage(stage, null);
        stage.setSource(Source.ETUDIANT);
        stage.setStatut(Statut.EN_ATTENTE_VALIDATION);
        stage.setCheminAutorisation(cheminAutorisation);

        return stageMapper.toDto(stageRepository.save(stage));
    }

    public StageDTO valider(Long id) {
        Stage stage = stageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Stage non trouvé: " + id));
        stage.setStatut(Statut.VALIDE);
        return stageMapper.toDto(stageRepository.save(stage));
    }

    public StageDTO rejeter(Long id) {
        Stage stage = stageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Stage non trouvé: " + id));
        stage.setStatut(Statut.REJETE);
        return stageMapper.toDto(stageRepository.save(stage));
    }

    public StageDTO assignerEtudiant(Long stageId, Long etudiantId) {
        Stage stage = stageRepository.findById(stageId)
            .orElseThrow(() -> new IllegalArgumentException("Stage non trouvé: " + stageId));
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
            .orElseThrow(() -> new IllegalArgumentException("Étudiant non trouvé: " + etudiantId));
        stage.setEtudiant(etudiant);
        resolveAndAssignTypeStage(stage, null);
        return stageMapper.toDto(stageRepository.save(stage));
    }

    public StageDTO assignerEncadreur(Long stageId, Long encadreurId) {
        Stage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new IllegalArgumentException("Stage non trouvé: " + stageId));
        Encadreur encadreur = encadreurRepository.findById(encadreurId)
                .orElseThrow(() -> new IllegalArgumentException("Encadreur non trouvé: " + encadreurId));

        if (stage.getEntreprise() == null || stage.getEntreprise().getId() == null) {
            throw new IllegalStateException("Le stage n'est rattaché à aucune entreprise");
        }
        if (encadreur.getEntreprise() == null || encadreur.getEntreprise().getId() == null) {
            throw new IllegalArgumentException("L'encadreur n'est rattaché à aucune entreprise");
        }
        if (!stage.getEntreprise().getId().equals(encadreur.getEntreprise().getId())) {
            throw new IllegalArgumentException("L'encadreur doit appartenir à l'entreprise du stage");
        }

        stage.setEncadreur(encadreur);
        return stageMapper.toDto(stageRepository.save(stage));
    }

    private void resolveAndAssignTypeStage(Stage stage, Long requestedTypeStageId) {
        TypeStage derivedTypeStage = resolveTypeStageFromInscription(stage);

        if (requestedTypeStageId != null) {
            TypeStage requestedTypeStage = typeStageRepository.findById(requestedTypeStageId)
                    .orElseThrow(() -> new IllegalArgumentException("Type de stage non trouvé: " + requestedTypeStageId));

            if (derivedTypeStage != null && !requestedTypeStage.getId().equals(derivedTypeStage.getId())) {
                throw new IllegalArgumentException("Le type de stage fourni ne correspond pas à l'inscription de l'étudiant");
            }

            stage.setTypeStage(requestedTypeStage);
            return;
        }

        if (derivedTypeStage != null) {
            if (stage.getTypeStage() != null
                    && stage.getTypeStage().getId() != null
                    && !stage.getTypeStage().getId().equals(derivedTypeStage.getId())) {
                throw new IllegalArgumentException("L'étudiant n'est pas inscrit dans le niveau correspondant au type de stage sélectionné");
            }
            stage.setTypeStage(derivedTypeStage);
            return;
        }

        if (stage.getEtudiant() == null || stage.getEtudiant().getId() == null) {
            throw new IllegalArgumentException("Le type de stage est requis si aucun étudiant n'est associé au stage");
        }
        if (stage.getAnneeAcademique() == null || stage.getAnneeAcademique().getId() == null) {
            throw new IllegalArgumentException("L'année académique est requise pour déterminer le type de stage");
        }

        throw new IllegalArgumentException(
                "Aucune inscription ne permet de déterminer le type de stage pour cet étudiant");
    }

    private TypeStage resolveTypeStageFromInscription(Stage stage) {
        if (stage.getEtudiant() == null || stage.getEtudiant().getId() == null) {
            return null;
        }
        if (stage.getAnneeAcademique() == null || stage.getAnneeAcademique().getId() == null) {
            return null;
        }

        Inscription inscription = inscriptionRepository
                .findFirstByEtudiantIdAndAnneeAcademiqueIdOrderByIdDesc(
                        stage.getEtudiant().getId(),
                        stage.getAnneeAcademique().getId())
                .orElse(null);

        if (inscription == null) {
            return null;
        }

        if (inscription.getParcours() == null
                || inscription.getParcours().getNiveau() == null
                || inscription.getParcours().getNiveau().getTypeStage() == null) {
            throw new IllegalArgumentException("Le parcours de l'étudiant ne permet pas de déterminer le type de stage");
        }

        return inscription.getParcours().getNiveau().getTypeStage();
    }

    @Transactional(readOnly = true)
    public Resource loadAutorisation(Long id) throws IOException {
        Stage stage = stageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Stage non trouvé: " + id));
        if (stage.getCheminAutorisation() == null) {
            throw new IllegalStateException("Aucun fichier d'autorisation pour ce stage");
        }
        Path filePath = Paths.get(uploadDir).toAbsolutePath().normalize()
                .resolve(stage.getCheminAutorisation());
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists()) {
            throw new IllegalStateException("Fichier introuvable: " + stage.getCheminAutorisation());
        }
        return resource;
    }

    public void delete(Long id) {
        stageRepository.deleteById(id);
    }
}

