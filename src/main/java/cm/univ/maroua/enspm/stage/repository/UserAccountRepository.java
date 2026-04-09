package cm.univ.maroua.enspm.stage.repository;

import cm.univ.maroua.enspm.stage.domain.UserAccount;
import cm.univ.maroua.enspm.stage.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
/**
 * Repository JPA UserAccountRepository.
 */
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    long countByRole(UserRole role);
}
