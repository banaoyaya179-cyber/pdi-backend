package bf.gov.conasur.pdi.entity;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name = "t_region")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Region {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_region") private Integer id;
    @Column(name = "code_region", nullable = false, unique = true, length = 10) private String codeRegion;
    @Column(name = "nom_region", nullable = false, unique = true, length = 100) private String nomRegion;
}
