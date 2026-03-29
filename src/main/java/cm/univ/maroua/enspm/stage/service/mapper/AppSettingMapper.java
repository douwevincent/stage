package cm.univ.maroua.enspm.stage.service.mapper;

import cm.univ.maroua.enspm.stage.domain.AppSetting;
import cm.univ.maroua.enspm.stage.service.dto.AppSettingDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AppSettingMapper extends EntityMapper<AppSettingDTO, AppSetting> {
}