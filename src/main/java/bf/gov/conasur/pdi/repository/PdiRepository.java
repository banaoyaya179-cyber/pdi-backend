package bf.gov.conasur.pdi.repository;

import bf.gov.conasur.pdi.entity.Pdi;
import bf.gov.conasur.pdi.enums.StatutPDI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PdiRepository extends JpaRepository<Pdi, Integer> {

    boolean existsByNomIgnoreCaseAndPrenomIgnoreCaseAndDateNaissance(
            String nom, String prenom, LocalDate dateNaissance);

    @Query("""
        SELECT p FROM Pdi p
        JOIN FETCH p.menage m
        JOIN FETCH p.siteCourant s
        JOIN FETCH s.commune c
        JOIN FETCH c.province pr
        JOIN FETCH pr.region r
        WHERE (:nom IS NULL OR p.nom LIKE :nomPattern)
        AND (:prenom IS NULL OR p.prenom LIKE :prenomPattern)
        AND (:statut IS NULL OR p.statutCourant = :statut)
        AND (:idSite IS NULL OR s.id = :idSite)
        AND (:idRegion IS NULL OR r.id = :idRegion)
    """)
    Page<Pdi> rechercherPdi(
            @Param("nom") String nom,
            @Param("nomPattern") String nomPattern,
            @Param("prenom") String prenom,
            @Param("prenomPattern") String prenomPattern,
            @Param("statut") StatutPDI statut,
            @Param("idSite") Integer idSite,
            @Param("idRegion") Integer idRegion,
            Pageable pageable);

    long countBySiteCourantId(Integer idSite);
    long countByStatutCourant(StatutPDI statut);
    List<Pdi> findByMenageId(Integer idMenage);
}
