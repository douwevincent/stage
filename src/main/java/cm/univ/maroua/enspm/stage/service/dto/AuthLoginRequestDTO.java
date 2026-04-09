package cm.univ.maroua.enspm.stage.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO AuthLoginRequestDTO.
 */
public record AuthLoginRequestDTO(
        @NotBlank @Email String email,
        @NotBlank String password
) {
}
