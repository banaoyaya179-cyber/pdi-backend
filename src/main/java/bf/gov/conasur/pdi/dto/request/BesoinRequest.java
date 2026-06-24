package bf.gov.conasur.pdi.dto.request;

import bf.gov.conasur.pdi.enums.CategorieBesoin;
import bf.gov.conasur.pdi.enums.Priorite;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BesoinRequest {

    @NotNull(message = "La catégorie est obligatoire")
    private CategorieBesoin categorie;

    @NotNull(message = "La priorité est obligatoire")
    private Priorite priorite;

    // Besoin individuel ou collectif (l'un ou l'autre obligatoire)
    private Integer idPdi;
    private Integer idMenage;
}
