package bf.gov.conasur.pdi.repository;

import bf.gov.conasur.pdi.entity.Role;
import bf.gov.conasur.pdi.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByLibelle(RoleType libelle);
}
