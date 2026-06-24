package bf.gov.conasur.pdi.repository;

import bf.gov.conasur.pdi.entity.SiteAccueil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SiteAccueilRepository extends JpaRepository<SiteAccueil, Integer> {

    List<SiteAccueil> findByCommuneProvinceRegionId(Integer idRegion);

    @Query("SELECT s FROM SiteAccueil s WHERE s.commune.province.id = :idProvince")
    List<SiteAccueil> findByProvince(Integer idProvince);
}
