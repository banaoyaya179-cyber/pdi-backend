package bf.gov.conasur.pdi.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "t_menage")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Menage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_menage")
    private Integer id;

    @Column(name = "code_unique", nullable = false, unique = true, length = 50)
    private String codeUnique;

    @Builder.Default
    @Column(name = "date_creation", nullable = false)
    private LocalDate dateCreation = LocalDate.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chef_menage")
    private Pdi chefMenage;
}
