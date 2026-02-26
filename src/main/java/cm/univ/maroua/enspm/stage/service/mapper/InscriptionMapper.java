package cm.univ.maroua.enspm.stage.service.mapper;

import cm.univ.maroua.enspm.stage.domain.Inscription;
import cm.univ.maroua.enspm.stage.service.dto.InscriptionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InscriptionMapper extends EntityMapper<InscriptionDTO, Inscription> {
    @Mapping(source = "anneeAcademique.id", target = "anneeAcademiqueId")
    @Mapping(source = "etudiant.id", target = "etudiantId")
    @Mapping(source = "parcours.id", target = "parcoursId")
    InscriptionDTO toDto(Inscription entity);

    @Mapping(source = "anneeAcademiqueId", target = "anneeAcademique.id")
    @Mapping(source = "etudiantId", target = "etudiant.id")
    @Mapping(source = "parcoursId", target = "parcours.id")
    Inscription toEntity(InscriptionDTO dto);
}
