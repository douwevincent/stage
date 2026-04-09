package cm.univ.maroua.enspm.stage.config;

import cm.univ.maroua.enspm.stage.service.UserAccountService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Initialise automatiquement le compte super administrateur au demarrage.
 *
 * <p>Si l'email et le mot de passe de bootstrap sont fournis dans la
 * configuration, le composant garantit la presence d'un compte SUPER_ADMIN.</p>
 */
@Component
public class SuperAdminBootstrap implements ApplicationRunner {

    private final UserAccountService userAccountService;
    private final String superAdminEmail;
    private final String superAdminPassword;

    public SuperAdminBootstrap(
            UserAccountService userAccountService,
            @Value("${app.security.super-admin.email}") String superAdminEmail,
            @Value("${app.security.super-admin.password}") String superAdminPassword
    ) {
        this.userAccountService = userAccountService;
        this.superAdminEmail = superAdminEmail;
        this.superAdminPassword = superAdminPassword;
    }

    /**
     * Execute l'initialisation du super administrateur apres le demarrage.
     */
    @Override
    public void run(ApplicationArguments args) {
        if (superAdminEmail == null || superAdminEmail.isBlank()) {
            return;
        }
        if (superAdminPassword == null || superAdminPassword.isBlank()) {
            return;
        }
        userAccountService.createSuperAdminIfMissing(superAdminEmail, superAdminPassword);
    }
}
