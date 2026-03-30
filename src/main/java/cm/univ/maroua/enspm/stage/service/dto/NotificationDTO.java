package cm.univ.maroua.enspm.stage.service.dto;

import cm.univ.maroua.enspm.stage.domain.NotificationReferenceDateType;
import java.io.Serializable;

public record NotificationDTO(
    Long id,
    Long typeStageId,
    NotificationReferenceDateType referenceDateType,
    Integer offsetDays,
    String objet,
    String contenuTemplate,
    Boolean actif
) implements Serializable {
}
