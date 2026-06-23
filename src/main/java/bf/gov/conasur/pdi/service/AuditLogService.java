package bf.gov.conasur.pdi.service;

import bf.gov.conasur.pdi.entity.AuditLog;
import bf.gov.conasur.pdi.entity.Utilisateur;
import bf.gov.conasur.pdi.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void log(String action, String ipSource, String entiteImpactee, Utilisateur utilisateur) {
        AuditLog log = AuditLog.builder()
                .action(action)
                .ipSource(ipSource)
                .entiteImpactee(entiteImpactee)
                .utilisateur(utilisateur)
                .horodatage(LocalDateTime.now())
                .build();
        auditLogRepository.save(log);
    }
}
