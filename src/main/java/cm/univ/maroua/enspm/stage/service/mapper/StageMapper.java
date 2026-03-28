package cm.univ.maroua.enspm.stage.service.mapper;

import cm.univ.maroua.enspm.stage.domain.Stage;
import cm.univ.maroua.enspm.stage.service.dto.StageDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StageMapper extends EntityMapper<StageDTO, Stage> {
    @Mapping(source = "etudiant.id", target = "etudiantId")
    @Mapping(source = "etudiant.matricule", target = "etudiantMatricule")
    @Mapping(source = "etudiant.nom", target = "etudiantNom")
    @Mapping(source = "entreprise.id", target = "entrepriseId")
    @Mapping(source = "entreprise.nom", target = "entrepriseNom")
    @Mapping(source = "encadreur.id", target = "encadreurId")
    @Mapping(source = "anneeAcademique.id", target = "anneeAcademiqueId")
    @Mapping(source = "sessionEvaluation.id", target = "sessionEvaluationId")
    StageDTO toDto(Stage entity);

    @Mapping(source = "etudiantId", target = "etudiant.id")
    @Mapping(source = "entrepriseId", target = "entreprise.id")
    @Mapping(source = "encadreurId", target = "encadreur.id")
    @Mapping(source = "anneeAcademiqueId", target = "anneeAcademique.id")
    @Mapping(source = "sessionEvaluationId", target = "sessionEvaluation.id")
    Stage toEntity(StageDTO dto);
}
