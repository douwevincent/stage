package cm.univ.maroua.enspm.stage.service.dto;

import cm.univ.maroua.enspm.stage.domain.AppSettingType;

import java.io.Serializable;
import java.time.LocalDateTime;

public record AppSettingDTO(
        Long id,
        String cle,
        String valeur,
        AppSettingType type,
        String description,
        boolean secret,
        boolean modifiable,
        LocalDateTime updatedAt) implements Serializable {
}