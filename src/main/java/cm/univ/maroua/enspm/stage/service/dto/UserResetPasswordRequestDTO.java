package cm.univ.maroua.enspm.stage.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserResetPasswordRequestDTO(
        @NotBlank @Size(min = 8, max = 120) String newPassword
) {
}
