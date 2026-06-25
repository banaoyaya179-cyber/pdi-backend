package bf.gov.conasur.pdi.dto.response;

import bf.gov.conasur.pdi.entity.AuditLog;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class AuditLogResponse {
    private final Long id;
    private final LocalDateTime horodatage;
    private final String action;
    private final String ipSource;
    private final String entiteImpactee;
    private final String nomUtilisateur;
    private final String emailUtilisateur;

    public AuditLogResponse(AuditLog log) {
        this.id = log.getId();
        this.horodatage = log.getHorodatage();
        this.action = log.getAction();
        this.ipSource = log.getIpSource();
        this.entiteImpactee = log.getEntiteImpactee();
        this.nomUtilisateur = log.getUtilisateur() != null
                ? log.getUtilisateur().getNom() + " " + log.getUtilisateur().getPrenom()
                : "Système";
        this.emailUtilisateur = log.getUtilisateur() != null
                ? log.getUtilisateur().getEmail() : "-";
    }
}
