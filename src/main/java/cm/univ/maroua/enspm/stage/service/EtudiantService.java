package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.AnneeAcademique;
import cm.univ.maroua.enspm.stage.domain.Departement;
import cm.univ.maroua.enspm.stage.domain.Etudiant;
import cm.univ.maroua.enspm.stage.domain.Inscription;
import cm.univ.maroua.enspm.stage.domain.Niveau;
import cm.univ.maroua.enspm.stage.domain.Parcours;
import cm.univ.maroua.enspm.stage.domain.Specialite;
import cm.univ.maroua.enspm.stage.domain.TypeStage;
import cm.univ.maroua.enspm.stage.domain.PeriodeStage;
import cm.univ.maroua.enspm.stage.repository.AnneeAcademiqueRepository;
import cm.univ.maroua.enspm.stage.repository.DepartementRepository;
import cm.univ.maroua.enspm.stage.repository.EtudiantRepository;
import cm.univ.maroua.enspm.stage.repository.InscriptionRepository;
import cm.univ.maroua.enspm.stage.repository.NiveauRepository;
import cm.univ.maroua.enspm.stage.repository.ParcoursRepository;
import cm.univ.maroua.enspm.stage.repository.PeriodeStageRepository;
import cm.univ.maroua.enspm.stage.repository.SpecialiteRepository;
import cm.univ.maroua.enspm.stage.service.dto.EtudiantImportResultDTO;
import cm.univ.maroua.enspm.stage.service.dto.EtudiantImportRowDTO;
import cm.univ.maroua.enspm.stage.service.dto.EtudiantDTO;
import cm.univ.maroua.enspm.stage.service.dto.ImportRowMessageDTO;
import cm.univ.maroua.enspm.stage.service.dto.StageDeclarationContextDTO;
import cm.univ.maroua.enspm.stage.service.mapper.EtudiantMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;
    private final EtudiantMapper etudiantMapper;
    private final AnneeAcademiqueRepository anneeAcademiqueRepository;
    private final DepartementRepository departementRepository;
    private final NiveauRepository niveauRepository;
    private final SpecialiteRepository specialiteRepository;
    private final ParcoursRepository parcoursRepository;
    private final InscriptionRepository inscriptionRepository;
    private final PeriodeStageRepository periodeStageRepository;

    public EtudiantService(
            EtudiantRepository etudiantRepository,
            EtudiantMapper etudiantMapper,
            AnneeAcademiqueRepository anneeAcademiqueRepository,
            DepartementRepository departementRepository,
            NiveauRepository niveauRepository,
            SpecialiteRepository specialiteRepository,
            ParcoursRepository parcoursRepository,
            InscriptionRepository inscriptionRepository,
            PeriodeStageRepository periodeStageRepository) {
        this.etudiantRepository = etudiantRepository;
        this.etudiantMapper = etudiantMapper;
        this.anneeAcademiqueRepository = anneeAcademiqueRepository;
        this.departementRepository = departementRepository;
        this.niveauRepository = niveauRepository;
        this.specialiteRepository = specialiteRepository;
        this.parcoursRepository = parcoursRepository;
        this.inscriptionRepository = inscriptionRepository;
        this.periodeStageRepository = periodeStageRepository;
    }

    public Page<EtudiantDTO> findAll(Pageable pageable) {
        return etudiantRepository.findAll(pageable).map(etudiantMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<EtudiantDTO> search(String q, Pageable pageable) {
        if (q == null || q.isBlank()) {
            return findAll(pageable);
        }
        String query = q.trim();
        return etudiantRepository
                .findByMatriculeContainingIgnoreCaseOrNomContainingIgnoreCase(
                        query, query, pageable)
                .map(etudiantMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<EtudiantDTO> findOne(Long id) {
        return etudiantRepository.findById(id).map(etudiantMapper::toDto);
    }

    public EtudiantDTO save(EtudiantDTO etudiantDTO) {
        Etudiant etudiant = etudiantMapper.toEntity(etudiantDTO);
        etudiant = etudiantRepository.save(etudiant);
        return etudiantMapper.toDto(etudiant);
    }

    public EtudiantImportResultDTO importRows(List<EtudiantImportRowDTO> rows) {
        if (rows == null || rows.isEmpty()) {
            return new EtudiantImportResultDTO(0, 0, 0, 0, 0, 0, List.of(), List.of());
        }

        AnneeAcademique anneeActive = anneeAcademiqueRepository.findByActifTrue()
                .orElseThrow(() -> new IllegalStateException("Aucune année académique active"));

        List<ImportRowMessageDTO> erreurs = new ArrayList<>();
        List<ImportRowMessageDTO> avertissements = new ArrayList<>();

        int etudiantsCrees = 0;
        int etudiantsExistants = 0;
        int inscriptionsCreees = 0;

        for (int i = 0; i < rows.size(); i++) {
            EtudiantImportRowDTO row = rows.get(i);
            int rowNumber = resolveRowNumber(row, i);
            String matricule = normalize(row.matricule());
            String nom = normalize(row.nom());
            String email = normalize(row.email());
            String telephone = normalize(row.telephone());
            String libelleNiveau = normalize(row.libelleNiveau());
            String codeDepartement = normalize(row.codeDepartement());
            String codeSpecialite = normalize(row.codeSpecialite());

            String missing = firstMissingRequired(matricule, nom, libelleNiveau, codeDepartement, codeSpecialite);
            if (missing != null) {
                erreurs.add(new ImportRowMessageDTO(rowNumber, matricule, "Champ requis manquant: " + missing));
                continue;
            }

            if (email != null && !email.isBlank() && !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                erreurs.add(new ImportRowMessageDTO(rowNumber, matricule, "Email invalide"));
                continue;
            }

            Optional<Departement> departement = departementRepository.findByCodeIgnoreCase(codeDepartement);
            if (departement.isEmpty()) {
                erreurs.add(new ImportRowMessageDTO(rowNumber, matricule,
                        "Département introuvable pour code: " + codeDepartement));
                continue;
            }

            Optional<Specialite> specialite = specialiteRepository
                    .findByCodeIgnoreCaseAndDepartementId(codeSpecialite, departement.get().getId());
            if (specialite.isEmpty()) {
                erreurs.add(new ImportRowMessageDTO(rowNumber, matricule,
                        "Spécialité introuvable pour code: " + codeSpecialite
                                + " dans le département " + codeDepartement));
                continue;
            }

            Optional<Niveau> niveau = niveauRepository.findByLibelleIgnoreCase(libelleNiveau);
            if (niveau.isEmpty()) {
                erreurs.add(new ImportRowMessageDTO(rowNumber, matricule,
                        "Niveau introuvable pour libellé: " + libelleNiveau));
                continue;
            }

            Optional<Parcours> parcours = parcoursRepository
                    .findBySpecialiteIdAndNiveauId(specialite.get().getId(), niveau.get().getId());
            if (parcours.isEmpty()) {
                erreurs.add(new ImportRowMessageDTO(rowNumber, matricule,
                        "Parcours introuvable pour spécialité " + codeSpecialite
                                + " et niveau " + libelleNiveau));
                continue;
            }

            Etudiant etudiant;
            Optional<Etudiant> existingEtudiant = etudiantRepository.findByMatricule(matricule);
            if (existingEtudiant.isPresent()) {
                etudiant = existingEtudiant.get();
                boolean updated = false;

                if (isBlank(etudiant.getNom()) && !isBlank(nom)) {
                    etudiant.setNom(nom);
                    updated = true;
                }

                if (isBlank(etudiant.getEmail()) && !isBlank(email)) {
                    etudiant.setEmail(email);
                    updated = true;
                }

                if (isBlank(etudiant.getTelephone()) && !isBlank(telephone)) {
                    etudiant.setTelephone(telephone);
                    updated = true;
                }

                if (updated) {
                    etudiant = etudiantRepository.save(etudiant);
                }

                etudiantsExistants++;
            } else {
                etudiant = new Etudiant();
                etudiant.setMatricule(matricule);
                etudiant.setNom(nom);
                etudiant.setEmail(email);
                etudiant.setTelephone(telephone);
                etudiant = etudiantRepository.save(etudiant);
                etudiantsCrees++;
            }

            boolean inscriptionExists = inscriptionRepository.existsByAnneeAcademiqueIdAndEtudiantIdAndParcoursId(
                    anneeActive.getId(), etudiant.getId(), parcours.get().getId());

            if (inscriptionExists) {
                avertissements.add(new ImportRowMessageDTO(rowNumber, matricule,
                        "Inscription déjà existante pour l'année active"));
                continue;
            }

            Inscription inscription = new Inscription();
            inscription.setAnneeAcademique(anneeActive);
            inscription.setEtudiant(etudiant);
            inscription.setParcours(parcours.get());
            inscriptionRepository.save(inscription);
            inscriptionsCreees++;
        }

        return new EtudiantImportResultDTO(
                rows.size(),
                etudiantsCrees,
                etudiantsExistants,
                inscriptionsCreees,
                avertissements.size(),
                erreurs.size(),
                erreurs,
                avertissements);
    }

    private int resolveRowNumber(EtudiantImportRowDTO row, int index) {
        if (row.no() != null && row.no() > 0) {
            return row.no();
        }
        return index + 2;
    }

    private String firstMissingRequired(
            String matricule,
            String nom,
            String libelleNiveau,
            String codeDepartement,
            String codeSpecialite) {
        if (matricule == null || matricule.isBlank()) {
            return "Matricule";
        }
        if (nom == null || nom.isBlank()) {
            return "Nom";
        }
        if (libelleNiveau == null || libelleNiveau.isBlank()) {
            return "Libellé niveau";
        }
        if (codeDepartement == null || codeDepartement.isBlank()) {
            return "code département";
        }
        if (codeSpecialite == null || codeSpecialite.isBlank()) {
            return "code spécialité";
        }
        return null;
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public void delete(Long id) {
        etudiantRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<EtudiantDTO> findByMatricule(String matricule) {
        return etudiantRepository.findByMatricule(matricule).map(etudiantMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<StageDeclarationContextDTO> findStageDeclarationContext(String matricule) {
        Optional<Etudiant> etudiant = etudiantRepository.findByMatricule(matricule);
        if (etudiant.isEmpty()) {
            return Optional.empty();
        }

        AnneeAcademique anneeActive = anneeAcademiqueRepository.findByActifTrue()
                .orElseThrow(() -> new IllegalStateException("Aucune année académique active"));

        Inscription inscription = inscriptionRepository
                .findFirstByEtudiantIdAndAnneeAcademiqueIdOrderByIdDesc(etudiant.get().getId(), anneeActive.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aucune inscription active ne permet de déterminer le type de stage pour cet étudiant"));

        if (inscription.getParcours() == null
                || inscription.getParcours().getNiveau() == null
                || inscription.getParcours().getNiveau().getTypeStage() == null) {
            throw new IllegalArgumentException("Le niveau d'inscription de l'étudiant ne permet pas de déterminer le type de stage");
        }

        TypeStage typeStage = inscription.getParcours().getNiveau().getTypeStage();
        PeriodeStage periodeStage = periodeStageRepository
                .findByTypeStageIdAndAnneeAcademiqueId(typeStage.getId(), anneeActive.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aucune période de stage active n'est configurée pour le type de stage de cet étudiant"));

        return Optional.of(new StageDeclarationContextDTO(
                etudiantMapper.toDto(etudiant.get()),
                typeStage.getId(),
                typeStage.getLibelle(),
                periodeStage.getDateDebut(),
                periodeStage.getDateFin()));
    }
}
