package cm.univ.maroua.enspm.stage.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Parametre applicatif persiste en base de donnees.
 *
 * <p>Utilise pour centraliser des clefs de configuration metier et technique,
 * avec support de type, confidentialite et controle de modifiabilite.</p>
 */
@Entity
@Table(name = "app_setting")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cle", nullable = false, unique = true, updatable = false, length = 100)
    private String cle;

    @Lob
    @Column(name = "valeur", nullable = false)
    private String valeur;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private AppSettingType type;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "secret", nullable = false)
    private boolean secret;

    @Column(name = "modifiable", nullable = false)
    private boolean modifiable;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}