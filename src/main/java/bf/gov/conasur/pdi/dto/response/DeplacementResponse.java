package bf.gov.conasur.pdi.dto.response;

import bf.gov.conasur.pdi.entity.Deplacement;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class DeplacementResponse {

    private final Long id;
    private final LocalDate dateMouvement;
    private final String motif;
    private final Integer idPdi;
    private final String nomPdi;
    private final Integer idSiteOrigine;
    private final String nomSiteOrigine;
    private final Integer idSiteDestination;
    private final String nomSiteDestination;

    public DeplacementResponse(Deplacement d) {
        this.id = d.getId();
        this.dateMouvement = d.getDateMouvement();
        this.motif = d.getMotif();
        this.idPdi = d.getPdi().getId();
        this.nomPdi = d.getPdi().getNom() + " " + d.getPdi().getPrenom();
        this.idSiteOrigine = d.getSiteOrigine().getId();
        this.nomSiteOrigine = d.getSiteOrigine().getNomSite();
        this.idSiteDestination = d.getSiteDestination().getId();
        this.nomSiteDestination = d.getSiteDestination().getNomSite();
    }
}
