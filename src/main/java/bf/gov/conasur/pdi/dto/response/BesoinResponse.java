package bf.gov.conasur.pdi.dto.response;

import bf.gov.conasur.pdi.entity.Besoin;
import bf.gov.conasur.pdi.enums.CategorieBesoin;
import bf.gov.conasur.pdi.enums.Priorite;
import bf.gov.conasur.pdi.enums.StatutBesoin;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class BesoinResponse {

    private final Long id;
    private final CategorieBesoin categorie;
    private final Priorite priorite;
    private final StatutBesoin statut;
    private final LocalDate dateDeclaration;
    private final Integer idPdi;
    private final String nomPdi;
    private final Integer idMenage;
    private final String codeMenage;

    public BesoinResponse(Besoin b) {
        this.id = b.getId();
        this.categorie = b.getCategorie();
        this.priorite = b.getPriorite();
        this.statut = b.getStatut();
        this.dateDeclaration = b.getDateDeclaration();

        if (b.getPdi() != null) {
            this.idPdi = b.getPdi().getId();
            this.nomPdi = b.getPdi().getNom() + " " + b.getPdi().getPrenom();
        } else {
            this.idPdi = null;
            this.nomPdi = null;
        }

        if (b.getMenage() != null) {
            this.idMenage = b.getMenage().getId();
            this.codeMenage = b.getMenage().getCodeUnique();
        } else {
            this.idMenage = null;
            this.codeMenage = null;
        }
    }
}
