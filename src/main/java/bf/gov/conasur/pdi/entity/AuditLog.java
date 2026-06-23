package bf.gov.conasur.pdi.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name = "t_audit_log")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_log") private Long id;
    @Column(name = "horodatage", nullable = false) private LocalDateTime horodatage = LocalDateTime.now();
    @Column(name = "action", nullable = false, length = 100) private String action;
    @Column(name = "ip_source", nullable = false, length = 45) private String ipSource;
    @Column(name = "entite_impactee", length = 100) private String entiteImpactee;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utilisateur") private Utilisateur utilisateur;
}
