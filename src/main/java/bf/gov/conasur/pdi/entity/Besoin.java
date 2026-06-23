package bf.gov.conasur.pdi.entity;
import bf.gov.conasur.pdi.enums.CategorieBesoin;
import bf.gov.conasur.pdi.enums.Priorite;
import bf.gov.conasur.pdi.enums.StatutBesoin;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
@Entity @Table(name = "t_besoin")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Besoin {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_besoin") private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "categorie", nullable = false, length = 50) private CategorieBesoin categorie;
    @Enumerated(EnumType.STRING)
    @Column(name = "priorite", nullable = false, length = 20) private Priorite priorite;
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 30) private StatutBesoin statut = StatutBesoin.DECLARE;
    @Column(name = "date_declaration", nullable = false) private LocalDate dateDeclaration = LocalDate.now();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pdi") private Pdi pdi;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_menage") private Menage menage;
}
