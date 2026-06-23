package bf.gov.conasur.pdi.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
@Entity @Table(name = "t_deplacement")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Deplacement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_deplacement") private Long id;
    @Column(name = "date_mouvement", nullable = false) private LocalDate dateMouvement = LocalDate.now();
    @Column(name = "motif", columnDefinition = "TEXT") private String motif;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pdi", nullable = false) private Pdi pdi;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_site_origine", nullable = false) private SiteAccueil siteOrigine;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_site_destination", nullable = false) private SiteAccueil siteDestination;
}
