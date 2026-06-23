package bf.gov.conasur.pdi.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
@Entity @Table(name = "t_aide_humanitaire")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AideHumanitaire {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aide") private Long id;
    @Column(name = "type_aide", nullable = false, length = 50) private String typeAide;
    @Column(name = "quantite", nullable = false, precision = 10, scale = 2) private BigDecimal quantite;
    @Column(name = "donateur", length = 100) private String donateur;
    @Column(name = "date_distribution", nullable = false) private LocalDate dateDistribution = LocalDate.now();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_besoin", nullable = false) private Besoin besoin;
}
