package cm.univ.maroua.enspm.stage.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Service
public class AppSettingCryptoService {

    private static final String PREFIX = "enc::";
    private static final int GCM_TAG_BITS = 128;
    private static final int GCM_IV_LENGTH = 12;

    private final SecureRandom secureRandom = new SecureRandom();
    private final SecretKeySpec secretKey;

    public AppSettingCryptoService(
            @Value("${app.settings.crypto-key:change-me-in-production}") String cryptoKey) {
        this.secretKey = new SecretKeySpec(buildAesKey(cryptoKey), "AES");
    }

    public String encrypt(String plainValue) {
        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] encrypted = cipher.doFinal(plainValue.getBytes(StandardCharsets.UTF_8));

            return PREFIX
                    + Base64.getEncoder().encodeToString(iv)
                    + ":"
                    + Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            throw new IllegalStateException("Impossible de chiffrer la valeur du parametre", ex);
        }
    }

    public String decrypt(String storedValue) {
        if (storedValue == null || !storedValue.startsWith(PREFIX)) {
            return storedValue;
        }

        String payload = storedValue.substring(PREFIX.length());
        String[] parts = payload.split(":", 2);
        if (parts.length != 2) {
            throw new IllegalStateException("Format de secret invalide");
        }

        try {
            byte[] iv = Base64.getDecoder().decode(parts[0]);
            byte[] encrypted = Base64.getDecoder().decode(parts[1]);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] plain = cipher.doFinal(encrypted);
            return new String(plain, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new IllegalStateException("Impossible de dechiffrer la valeur du parametre", ex);
        }
    }

    private byte[] buildAesKey(String rawKey) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(rawKey.getBytes(StandardCharsets.UTF_8));
            return Arrays.copyOf(digest, 16);
        } catch (Exception ex) {
            throw new IllegalStateException("Impossible de preparer la cle de chiffrement", ex);
        }
    }
}