package bf.gov.conasur.pdi.entity;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;
@Entity @Table(name = "t_site_accueil")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SiteAccueil {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_site") private Integer id;
    @Column(name = "nom_site", nullable = false, length = 150) private String nomSite;
    @Column(name = "capacite_maximale", nullable = false) private Integer capaciteMaximale;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_commune", nullable = false) private Commune commune;
    @Column(name = "localisation_gps", columnDefinition = "GEOMETRY(Point, 4326)")
    private Point localisationGps;
}
