package bf.gov.conasur.pdi.dto.response;

import bf.gov.conasur.pdi.entity.AideHumanitaire;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class AideResponse {

    private final Long id;
    private final String typeAide;
    private final BigDecimal quantite;
    private final String donateur;
    private final LocalDate dateDistribution;
    private final Long idBesoin;
    private final String categoriesBesoin;

    public AideResponse(AideHumanitaire a) {
        this.id = a.getId();
        this.typeAide = a.getTypeAide();
        this.quantite = a.getQuantite();
        this.donateur = a.getDonateur();
        this.dateDistribution = a.getDateDistribution();
        this.idBesoin = a.getBesoin().getId();
        this.categoriesBesoin = a.getBesoin().getCategorie().name();
    }
}
