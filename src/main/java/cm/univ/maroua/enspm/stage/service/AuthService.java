package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.config.security.JwtTokenService;
import cm.univ.maroua.enspm.stage.domain.UserAccount;
import cm.univ.maroua.enspm.stage.repository.UserAccountRepository;
import cm.univ.maroua.enspm.stage.service.dto.AuthLoginRequestDTO;
import cm.univ.maroua.enspm.stage.service.dto.AuthLoginResponseDTO;
import cm.univ.maroua.enspm.stage.service.dto.UserAccountDTO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserAccountRepository userAccountRepository;
    private final JwtTokenService jwtTokenService;

    public AuthService(
            AuthenticationManager authenticationManager,
            UserAccountRepository userAccountRepository,
            JwtTokenService jwtTokenService
    ) {
        this.authenticationManager = authenticationManager;
        this.userAccountRepository = userAccountRepository;
        this.jwtTokenService = jwtTokenService;
    }

    public AuthLoginResponseDTO login(AuthLoginRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (DisabledException ex) {
            throw new IllegalStateException("Ce compte est desactive");
        } catch (BadCredentialsException ex) {
            throw new IllegalArgumentException("Email ou mot de passe invalide");
        }

        UserAccount user = userAccountRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        String token = jwtTokenService.generateToken(user);
        return new AuthLoginResponseDTO("Bearer", token, toDto(user));
    }

    @Transactional(readOnly = true)
    public UserAccountDTO me(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalArgumentException("Utilisateur non authentifie");
        }
        UserAccount user = userAccountRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        return toDto(user);
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
