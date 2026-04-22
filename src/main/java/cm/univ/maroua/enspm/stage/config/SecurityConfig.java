package cm.univ.maroua.enspm.stage.config;

import cm.univ.maroua.enspm.stage.config.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration globale de securite Spring Security.
 *
 * <p>Cette configuration active un modele stateless base sur JWT, desactive CSRF
 * (adaptation API REST), autorise quelques routes publiques (authentification,
 * evaluation publique, Swagger) et protege les operations d'administration.</p>
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Construit la chaine de filtres de securite HTTP.
     *
     * @param http configuration HTTP Spring Security
     * @return chaine de filtres configuree
     * @throws Exception en cas d'erreur de configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        // Static frontend resources and SPA entry point
                        .requestMatchers("/", "/index.html", "/favicon.svg", "/icons.svg", "/logo-enspm.png").permitAll()
                        .requestMatchers("/assets/**", "/css/**", "/js/**").permitAll()
                        // Public API endpoints
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/stages/declarer").permitAll()
                        .requestMatchers("/api/v1/public/evaluations/**").permitAll()
                        // Role-restricted API endpoints
                        .requestMatchers("/api/v1/users/**").hasRole("SUPER_ADMIN")
                        // All other /api/** calls require a valid authenticated session
                        .requestMatchers("/api/**").authenticated()
                        // SPA history-mode fallback routes
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Fournit l'encodeur de mot de passe utilise pour les comptes applicatifs.
     *
     * @return encodeur BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Expose le gestionnaire d'authentification principal.
     *
     * @param authenticationConfiguration configuration standard de Spring Security
     * @return gestionnaire d'authentification
     * @throws Exception en cas d'erreur de recuperation
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
