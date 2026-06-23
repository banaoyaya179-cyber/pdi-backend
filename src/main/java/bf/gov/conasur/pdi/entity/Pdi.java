package bf.gov.conasur.pdi.entity;
import bf.gov.conasur.pdi.enums.Sexe;
import bf.gov.conasur.pdi.enums.StatutPDI;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
@Entity @Table(name = "t_pdi")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Pdi {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pdi") private Integer id;
    @Column(name = "nom", nullable = false, length = 100) private String nom;
    @Column(name = "prenom", nullable = false, length = 100) private String prenom;
    @Enumerated(EnumType.STRING)
    @Column(name = "sexe", nullable = false, length = 1) private Sexe sexe;
    @Column(name = "date_naissance", nullable = false) private LocalDate dateNaissance;
    @Enumerated(EnumType.STRING)
    @Column(name = "statut_courant", nullable = false, length = 30) private StatutPDI statutCourant;
    @Column(name = "date_enrolement", nullable = false) private LocalDate dateEnrolement = LocalDate.now();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_menage", nullable = false) private Menage menage;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_site_courant", nullable = false) private SiteAccueil siteCourant;
}
