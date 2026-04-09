package cm.univ.maroua.enspm.stage.service.dto;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

/**
 * DTO AppSettingUpdateDTO.
 */
public record AppSettingUpdateDTO(@NotBlank String valeur) implements Serializable {
}
