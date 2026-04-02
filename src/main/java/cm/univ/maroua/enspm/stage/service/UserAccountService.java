package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.UserAccount;
import cm.univ.maroua.enspm.stage.domain.UserRole;
import cm.univ.maroua.enspm.stage.repository.UserAccountRepository;
import cm.univ.maroua.enspm.stage.service.dto.UserAccountDTO;
import cm.univ.maroua.enspm.stage.service.dto.UserCreateRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAccountService(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Page<UserAccountDTO> findAll(Pageable pageable) {
        return userAccountRepository.findAll(pageable).map(this::toDto);
    }

    public UserAccountDTO create(UserCreateRequestDTO request) {
        if (userAccountRepository.existsByEmailIgnoreCase(request.email())) {
            throw new IllegalArgumentException("Un compte existe deja avec cet email");
        }

        UserAccount user = new UserAccount();
        user.setEmail(request.email().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        user.setActive(request.active() == null || request.active());

        return toDto(userAccountRepository.save(user));
    }

    public UserAccountDTO activate(Long id) {
        UserAccount user = findEntity(id);
        user.setActive(true);
        return toDto(userAccountRepository.save(user));
    }

    public UserAccountDTO deactivate(Long id) {
        UserAccount user = findEntity(id);
        if (user.getRole() == UserRole.SUPER_ADMIN && userAccountRepository.countByRole(UserRole.SUPER_ADMIN) <= 1) {
            throw new IllegalStateException("Impossible de desactiver le dernier SUPER_ADMIN");
        }
        user.setActive(false);
        return toDto(userAccountRepository.save(user));
    }

    public UserAccountDTO resetPassword(Long id, String newPassword) {
        UserAccount user = findEntity(id);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        return toDto(userAccountRepository.save(user));
    }

    public void delete(Long id) {
        UserAccount user = findEntity(id);
        if (user.getRole() == UserRole.SUPER_ADMIN && userAccountRepository.countByRole(UserRole.SUPER_ADMIN) <= 1) {
            throw new IllegalStateException("Impossible de supprimer le dernier SUPER_ADMIN");
        }
        userAccountRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public boolean hasSuperAdmin() {
        return userAccountRepository.countByRole(UserRole.SUPER_ADMIN) > 0;
    }

    public UserAccountDTO createSuperAdminIfMissing(String email, String rawPassword) {
        if (hasSuperAdmin()) {
            return null;
        }
        if (userAccountRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalStateException("Un utilisateur avec cet email existe deja");
        }

        UserAccount superAdmin = new UserAccount();
        superAdmin.setEmail(email.trim().toLowerCase());
        superAdmin.setPasswordHash(passwordEncoder.encode(rawPassword));
        superAdmin.setRole(UserRole.SUPER_ADMIN);
        superAdmin.setActive(true);

        return toDto(userAccountRepository.save(superAdmin));
    }

    private UserAccount findEntity(Long id) {
        return userAccountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
    }

    private UserAccountDTO toDto(UserAccount user) {
        return new UserAccountDTO(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.isActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
