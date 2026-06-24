package bf.gov.conasur.pdi.repository;

import bf.gov.conasur.pdi.entity.Commune;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommuneRepository extends JpaRepository<Commune, Integer> {
    List<Commune> findByProvinceId(Integer idProvince);
}
