package cm.univ.maroua.enspm.stage.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = { "code" }),
    @UniqueConstraint(columnNames = { "par_defaut_unique_key" })
})
public class Bareme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    private String libelle;

    @Column(nullable = false)
    private Boolean actif = Boolean.TRUE;

    @Column(name = "par_defaut", nullable = false)
    private Boolean parDefaut = Boolean.FALSE;

    @Column(name = "par_defaut_unique_key")
    private String parDefautUniqueKey;

    @PrePersist
    @PreUpdate
    private void syncParDefautUniqueKey() {
        parDefautUniqueKey = Boolean.TRUE.equals(parDefaut) ? "DEFAULT" : null;
    }
}