package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.AppSetting;
import cm.univ.maroua.enspm.stage.repository.AppSettingRepository;
import cm.univ.maroua.enspm.stage.service.dto.AppSettingDTO;
import cm.univ.maroua.enspm.stage.service.dto.AppSettingUpdateDTO;
import cm.univ.maroua.enspm.stage.service.mapper.AppSettingMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Service
@Transactional
public class AppSettingService {

    private static final String MASKED_SECRET = "********";

    private static final Set<String> FIXED_KEYS = Set.of(
            "SMTP_HOST",
            "SMTP_PORT",
            "SMTP_SECURITY_MODE",
            "SMTP_AUTH_ENABLED",
            "SMTP_AUTH_MECHANISMS",
            "SMTP_SSL_TRUST",
            "SMTP_CONNECTION_TIMEOUT_MS",
            "SMTP_TIMEOUT_MS",
            "SMTP_WRITE_TIMEOUT_MS",
            "SMTP_FROM_ADDRESS",
            "SMTP_USERNAME",
            "SMTP_PASSWORD",
            "MAIL_TEMPLATE_BODY",
            "MAIL_PUBLIC_EVALUATION_URL_PREFIX",
            "LETTRE_STAGE_TEMPLATE_PATH",
            "EVALUATION_DELAY_DAYS",
            "MAIL_SUBJECT_DECLARATION",
            "MAIL_SUBJECT_VALIDATION",
            "MAIL_SUBJECT_RAPPEL"
    );

    private final AppSettingRepository appSettingRepository;
    private final AppSettingMapper appSettingMapper;
    private final AppSettingCryptoService appSettingCryptoService;

    public AppSettingService(
            AppSettingRepository appSettingRepository,
            AppSettingMapper appSettingMapper,
            AppSettingCryptoService appSettingCryptoService) {
        this.appSettingRepository = appSettingRepository;
        this.appSettingMapper = appSettingMapper;
        this.appSettingCryptoService = appSettingCryptoService;
    }

    @Transactional(readOnly = true)
    public List<AppSettingDTO> findAll() {
        return appSettingRepository.findAllByOrderByCleAsc().stream()
                .map(this::toPublicDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AppSettingDTO findOne(String cle) {
        AppSetting setting = appSettingRepository.findByCle(cle)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Parametre introuvable: " + cle));
        return toPublicDto(setting);
    }

    public AppSettingDTO update(String cle, AppSettingUpdateDTO updateDTO) {
        AppSetting setting = appSettingRepository.findByCle(cle)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Parametre introuvable: " + cle));

        if (!FIXED_KEYS.contains(cle) || !setting.isModifiable()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Ce parametre ne peut pas etre modifie");
        }

        String sanitizedValue = updateDTO.valeur().trim();

        validateValue(cle, sanitizedValue);
        validateSmtpConsistency(cle, sanitizedValue);

        String persistedValue = setting.isSecret()
            ? appSettingCryptoService.encrypt(sanitizedValue)
            : sanitizedValue;

        setting.setValeur(persistedValue);
        setting = appSettingRepository.save(setting);

        return toPublicDto(setting);
    }

    @Transactional(readOnly = true)
    public String getRawValue(String cle) {
        AppSetting setting = appSettingRepository.findByCle(cle)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Parametre introuvable: " + cle));
        if (setting.isSecret()) {
            return appSettingCryptoService.decrypt(setting.getValeur());
        }
        return setting.getValeur();
    }

    private AppSettingDTO toPublicDto(AppSetting setting) {
        AppSettingDTO dto = appSettingMapper.toDto(setting);
        if (!dto.secret()) {
            return dto;
        }
        return new AppSettingDTO(
                dto.id(),
                dto.cle(),
                MASKED_SECRET,
                dto.type(),
                dto.description(),
                dto.secret(),
                dto.modifiable(),
                dto.updatedAt());
    }

    private void validateValue(String cle, String valeur) {
        if (valeur == null || valeur.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La valeur est obligatoire");
        }

        String trimmed = valeur.trim();
        switch (cle) {
            case "SMTP_PORT" -> {
                int port = parseInteger(trimmed, "Le port SMTP doit etre un entier");
                if (port < 1 || port > 65535) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Le port SMTP doit etre compris entre 1 et 65535");
                }
            }
            case "SMTP_SECURITY_MODE" -> {
                if (!Set.of("NONE", "STARTTLS", "SSL_TLS").contains(trimmed)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "SMTP_SECURITY_MODE doit valoir NONE, STARTTLS ou SSL_TLS");
                }
            }
            case "SMTP_AUTH_ENABLED" -> parseBoolean(trimmed,
                    "SMTP_AUTH_ENABLED doit valoir true ou false");
            case "SMTP_AUTH_MECHANISMS" -> {
                Set<String> allowed = Set.of("LOGIN", "PLAIN", "NTLM", "CRAM-MD5");
                String[] mechanisms = trimmed.split("\\s+");
                if (mechanisms.length == 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Au moins un mecanisme SMTP est requis");
                }
                for (String mechanism : mechanisms) {
                    if (!allowed.contains(mechanism)) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Mecanisme SMTP non supporte: " + mechanism);
                    }
                }
            }
            case "SMTP_CONNECTION_TIMEOUT_MS", "SMTP_TIMEOUT_MS", "SMTP_WRITE_TIMEOUT_MS" -> {
                int timeout = parseInteger(trimmed, "Le timeout SMTP doit etre un entier");
                if (timeout < 1000 || timeout > 120000) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Le timeout SMTP doit etre entre 1000 et 120000 ms");
                }
            }
            case "SMTP_FROM_ADDRESS" -> {
                if (!trimmed.contains("@") || trimmed.startsWith("@") || trimmed.endsWith("@")) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "SMTP_FROM_ADDRESS doit etre un email valide");
                }
            }
            case "EVALUATION_DELAY_DAYS" -> {
                int days = parseInteger(trimmed, "Le delai d'evaluation doit etre un entier");
                if (days < 1 || days > 365) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Le delai d'evaluation doit etre entre 1 et 365 jours");
                }
            }
            case "SMTP_HOST",
                    "SMTP_SSL_TRUST",
                    "SMTP_USERNAME",
                    "SMTP_PASSWORD",
                    "MAIL_TEMPLATE_BODY",
                    "MAIL_PUBLIC_EVALUATION_URL_PREFIX",
                    "LETTRE_STAGE_TEMPLATE_PATH",
                    "MAIL_SUBJECT_DECLARATION",
                    "MAIL_SUBJECT_VALIDATION",
                    "MAIL_SUBJECT_RAPPEL" -> {
                // Required + non-empty is already enforced above.
            }
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cle de parametre non supportee: " + cle);
        }
    }

    private void validateSmtpConsistency(String updatedKey, String updatedValue) {
        if (!updatedKey.startsWith("SMTP_")) {
            return;
        }

        String mode = getSettingValue("SMTP_SECURITY_MODE", updatedKey, updatedValue);
        int port = parseInteger(getSettingValue("SMTP_PORT", updatedKey, updatedValue),
                "Le port SMTP doit etre un entier");
        boolean authEnabled = parseBoolean(getSettingValue("SMTP_AUTH_ENABLED", updatedKey, updatedValue),
                "SMTP_AUTH_ENABLED doit valoir true ou false");

        if ("SSL_TLS".equals(mode) && port == 587) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Pour SSL_TLS, utilisez generalement le port 465 (587 est reserve a STARTTLS)");
        }

        if ("STARTTLS".equals(mode) && port == 465) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Pour STARTTLS, utilisez generalement le port 587 (465 est reserve a SSL/TLS)");
        }

        if (authEnabled) {
            String username = getSettingValue("SMTP_USERNAME", updatedKey, updatedValue);
            String password = getSettingValue("SMTP_PASSWORD", updatedKey, updatedValue);
            if (username.isBlank() || password.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "SMTP_USERNAME et SMTP_PASSWORD sont obligatoires quand SMTP_AUTH_ENABLED=true");
            }
        }
    }

    private String getSettingValue(String key, String updatedKey, String updatedValue) {
        if (key.equals(updatedKey)) {
            return updatedValue;
        }
        return appSettingRepository.findByCle(key)
                .map(setting -> setting.isSecret()
                        ? appSettingCryptoService.decrypt(setting.getValeur())
                        : setting.getValeur())
                .map(String::trim)
                .orElse("");
    }

    private int parseInteger(String value, String errorMessage) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
    }

    private boolean parseBoolean(String value, String errorMessage) {
        if (!Stream.of("true", "false").anyMatch(value::equalsIgnoreCase)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
        return Boolean.parseBoolean(value);
    }
}