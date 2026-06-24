package bf.gov.conasur.pdi.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DeplacementRequest {

    @NotNull(message = "L'identifiant de la PDI est obligatoire")
    private Integer idPdi;

    @NotNull(message = "Le site de destination est obligatoire")
    private Integer idSiteDestination;

    private String motif;

    // Si true, déplace tout le ménage
    private boolean toutLeMenage = false;
}
