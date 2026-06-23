package bf.gov.conasur.pdi.entity;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name = "t_province")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Province {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_province") private Integer id;
    @Column(name = "code_province", nullable = false, unique = true, length = 10) private String codeProvince;
    @Column(name = "nom_province", nullable = false, length = 100) private String nomProvince;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_region", nullable = false) private Region region;
}
