package bf.gov.conasur.pdi.entity;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name = "t_commune")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Commune {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_commune") private Integer id;
    @Column(name = "code_commune", nullable = false, unique = true, length = 10) private String codeCommune;
    @Column(name = "nom_commune", nullable = false, length = 100) private String nomCommune;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_province", nullable = false) private Province province;
}
