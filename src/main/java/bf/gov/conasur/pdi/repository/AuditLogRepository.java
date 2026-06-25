package bf.gov.conasur.pdi.repository;

import bf.gov.conasur.pdi.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query("""
        SELECT a FROM AuditLog a
        LEFT JOIN FETCH a.utilisateur u
        WHERE (:action IS NULL OR a.action LIKE :actionPattern)
        AND (:email IS NULL OR u.email LIKE :emailPattern)
        ORDER BY a.horodatage DESC
    """)
    Page<AuditLog> filtrer(
            @Param("action") String action,
            @Param("actionPattern") String actionPattern,
            @Param("email") String email,
            @Param("emailPattern") String emailPattern,
            Pageable pageable);
}
