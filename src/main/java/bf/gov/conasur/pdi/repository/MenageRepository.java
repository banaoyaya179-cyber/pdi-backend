package bf.gov.conasur.pdi.repository;

import bf.gov.conasur.pdi.entity.Menage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MenageRepository extends JpaRepository<Menage, Integer> {
    boolean existsByCodeUnique(String codeUnique);
    Optional<Menage> findByCodeUnique(String codeUnique);
}
