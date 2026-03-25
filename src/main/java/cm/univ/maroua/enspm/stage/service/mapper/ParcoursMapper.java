package cm.univ.maroua.enspm.stage.service.mapper;

import cm.univ.maroua.enspm.stage.domain.Parcours;
import cm.univ.maroua.enspm.stage.service.dto.ParcoursDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ParcoursMapper extends EntityMapper<ParcoursDTO, Parcours> {
    @Mapping(source = "specialite.id", target = "specialiteId")
    @Mapping(source = "specialite.code", target = "specialiteCode")
    @Mapping(source = "specialite.intitule", target = "specialiteIntitule")
    @Mapping(source = "niveau.id", target = "niveauId")
    @Mapping(source = "niveau.libelle", target = "niveauLibelle")
    @Mapping(target = "libelle", expression = "java(buildParcoursLibelle(entity))")
    ParcoursDTO toDto(Parcours entity);

    @Mapping(source = "specialiteId", target = "specialite.id")
    @Mapping(source = "niveauId", target = "niveau.id")
    @Mapping(target = "inscriptions", ignore = true)
    @Mapping(target = "critereParcours", ignore = true)
    Parcours toEntity(ParcoursDTO dto);

    default String buildParcoursLibelle(Parcours entity) {
        if (entity == null) {
            return null;
        }

        String specialiteCode = entity.getSpecialite() != null ? entity.getSpecialite().getCode() : null;
        String niveauLibelle = entity.getNiveau() != null ? entity.getNiveau().getLibelle() : null;

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
