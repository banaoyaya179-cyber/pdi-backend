package bf.gov.conasur.pdi.repository;

import bf.gov.conasur.pdi.entity.Deplacement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DeplacementRepository extends JpaRepository<Deplacement, Long> {
    List<Deplacement> findByPdiIdOrderByDateMouvementDesc(Integer idPdi);
}
