package bf.gov.conasur.pdi.entity;
import bf.gov.conasur.pdi.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name = "t_role")
@Getter @Setter @NoArgsConstructor
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_role") private Integer id;
    @Enumerated(EnumType.STRING)
    @Column(name = "libelle", nullable = false, unique = true, length = 50)
    private RoleType libelle;
}
