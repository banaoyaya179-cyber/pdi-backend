package bf.gov.conasur.pdi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "t_utilisateur")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilisateur")
    private Integer id;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "mot_de_passe", nullable = false, length = 255)
    private String motDePasse;

    @Builder.Default
    @Column(name = "double_auth_active")
    private boolean doubleAuthActive = false;

    @Builder.Default
    @Column(name = "compte_actif")
    private boolean compteActif = true;

    @Column(name = "secret_2fa", length = 100)
    private String secret2FA;

    @Column(name = "id_site_affecte")
    private Integer idSiteAffecte;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_role", nullable = false)
    private Role role;
}
