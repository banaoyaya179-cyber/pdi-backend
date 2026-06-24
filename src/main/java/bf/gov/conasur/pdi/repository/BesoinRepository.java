package bf.gov.conasur.pdi.repository;

import bf.gov.conasur.pdi.entity.Besoin;
import bf.gov.conasur.pdi.enums.Priorite;
import bf.gov.conasur.pdi.enums.StatutBesoin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BesoinRepository extends JpaRepository<Besoin, Long> {
    List<Besoin> findByPdiId(Integer idPdi);
    List<Besoin> findByMenageId(Integer idMenage);
    List<Besoin> findByStatut(StatutBesoin statut);
    long countByPrioriteAndStatutNot(Priorite priorite, StatutBesoin statut);
}
