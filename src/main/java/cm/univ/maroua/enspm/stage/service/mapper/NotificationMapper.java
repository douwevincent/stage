package cm.univ.maroua.enspm.stage.service.mapper;

import cm.univ.maroua.enspm.stage.domain.Notification;
import cm.univ.maroua.enspm.stage.service.dto.NotificationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
/**
 * Mapper MapStruct NotificationMapper.
 */
public interface NotificationMapper extends EntityMapper<NotificationDTO, Notification> {
    @Mapping(source = "typeStage.id", target = "typeStageId")
    NotificationDTO toDto(Notification entity);

    @Mapping(source = "typeStageId", target = "typeStage.id")
    Notification toEntity(NotificationDTO dto);
}
