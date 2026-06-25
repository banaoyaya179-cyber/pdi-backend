package bf.gov.conasur.pdi.dto.response;

import bf.gov.conasur.pdi.entity.Notification;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class NotificationResponse {
    private final Long id;
    private final String message;
    private final LocalDateTime dateCreation;
    private final boolean lue;
    private final Integer idSite;
    private final String nomSite;

    public NotificationResponse(Notification n) {
        this.id = n.getId();
        this.message = n.getMessage();
        this.dateCreation = n.getDateCreation();
        this.lue = n.isLue();
        this.idSite = n.getSite() != null ? n.getSite().getId() : null;
        this.nomSite = n.getSite() != null ? n.getSite().getNomSite() : null;
    }
}
