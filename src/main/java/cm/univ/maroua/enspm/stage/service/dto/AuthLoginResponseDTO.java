package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;

/**
 * DTO AuthLoginResponseDTO.
 */
public record AuthLoginResponseDTO(
        String tokenType,
        String accessToken,
        UserAccountDTO user
) implements Serializable {
}
