package cm.univ.maroua.enspm.stage.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Etudiant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String nom;

    private String email;

    private String telephone;

    @NotNull
    @Column(unique = true)
    private String matricule;

    @JsonIgnore
    @OneToMany(mappedBy = "etudiant")
    private List<Inscription> inscriptions;

    @JsonIgnore
    @OneToMany(mappedBy = "etudiant")
    private List<Stage> stages;
}
