package cm.univ.maroua.enspm.stage.service.mapper;

import cm.univ.maroua.enspm.stage.domain.Inscription;
import cm.univ.maroua.enspm.stage.service.dto.InscriptionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
/**
 * Mapper MapStruct InscriptionMapper.
 */
public interface InscriptionMapper extends EntityMapper<InscriptionDTO, Inscription> {
    @Mapping(source = "anneeAcademique.id", target = "anneeAcademiqueId")
    @Mapping(source = "anneeAcademique.libelle", target = "anneeAcademiqueLibelle")
    @Mapping(source = "etudiant.id", target = "etudiantId")
    @Mapping(source = "etudiant.matricule", target = "etudiantMatricule")
    @Mapping(source = "etudiant.nom", target = "etudiantNom")
    @Mapping(source = "parcours.id", target = "parcoursId")
    @Mapping(source = "parcours.specialite.id", target = "parcoursSpecialiteId")
    @Mapping(source = "parcours.specialite.code", target = "parcoursSpecialiteCode")
    @Mapping(source = "parcours.specialite.intitule", target = "parcoursSpecialiteIntitule")
    @Mapping(source = "parcours.niveau.id", target = "parcoursNiveauId")
    @Mapping(source = "parcours.niveau.libelle", target = "parcoursNiveauLibelle")
    @Mapping(target = "parcoursLibelle", expression = "java(buildParcoursLibelle(entity))")
    InscriptionDTO toDto(Inscription entity);

    @Mapping(source = "anneeAcademiqueId", target = "anneeAcademique.id")
    @Mapping(source = "etudiantId", target = "etudiant.id")
    @Mapping(source = "parcoursId", target = "parcours.id")
    Inscription toEntity(InscriptionDTO dto);

    default String buildParcoursLibelle(Inscription entity) {
        if (entity == null || entity.getParcours() == null) {
            return null;
        }

        String specialiteCode = entity.getParcours().getSpecialite() != null
                        ? entity.getParcours().getSpecialite().getCode()
                        : null;
        String niveauLibelle = entity.getParcours().getNiveau() != null
                        ? entity.getParcours().getNiveau().getLibelle()
                        : null;

        if (specialiteCode == null && niveauLibelle == null) {
            return null;
        }

        if (specialiteCode == null) {
            return niveauLibelle;
        }

        if (niveauLibelle == null) {
            return specialiteCode;
        }

        return specialiteCode + " - " + niveauLibelle;
    }
}
