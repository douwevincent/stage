package cm.univ.maroua.enspm.stage.config.security;

import cm.univ.maroua.enspm.stage.domain.UserAccount;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/**
 * Service de generation et de validation des tokens JWT.
 *
 * <p>Le token embarque l'email de l'utilisateur comme sujet et son role comme claim.
 * La cle de signature est derivee de la configuration applicative.</p>
 */
@Service
public class JwtTokenService {

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtTokenService(
            @Value("${app.security.jwt.secret}") String jwtSecret,
            @Value("${app.security.jwt.expiration-ms}") long expirationMs
    ) {
        this.signingKey = Keys.hmacShaKeyFor(normalizeSecret(jwtSecret).getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /**
     * Genere un token JWT signe pour un utilisateur applicatif.
     *
     * @param user utilisateur authentifie
     * @return token JWT compact
     */
    public String generateToken(UserAccount user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(signingKey)
                .compact();
    }

    /**
     * Extrait l'adresse e-mail (claim {@code sub}) du token.
     *
     * @param token token JWT brut
     * @return e-mail contenu dans le token
     */
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Verifie la validite cryptographique et temporelle du token.
     *
     * @param token token JWT brut
     * @return {@code true} si le token est valide, sinon {@code false}
     */
    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Parse et valide le token afin d'en extraire ses claims.
     */
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Normalise la cle secrete pour atteindre la longueur minimale requise par HS256.
     */
    private String normalizeSecret(String secret) {
        if (secret == null) {
            throw new IllegalStateException("JWT secret cannot be null");
        }
        if (secret.length() >= 32) {
            return secret;
        }
        StringBuilder sb = new StringBuilder(secret);
        while (sb.length() < 32) {
            sb.append("0");
        }
        return sb.toString();
    }
}
