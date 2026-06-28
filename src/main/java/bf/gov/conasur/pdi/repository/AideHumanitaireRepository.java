package bf.gov.conasur.pdi.repository;

import bf.gov.conasur.pdi.entity.AideHumanitaire;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AideHumanitaireRepository extends JpaRepository<AideHumanitaire, Long> {
    List<AideHumanitaire> findByBesoinId(Long idBesoin);
    void deleteByBesoinId(Long idBesoin);
}
