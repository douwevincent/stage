package cm.univ.maroua.enspm.stage.service.dto;

import cm.univ.maroua.enspm.stage.domain.UserRole;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO UserAccountDTO.
 */
public record UserAccountDTO(
        Long id,
        String email,
        UserRole role,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) implements Serializable {
}
