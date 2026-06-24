package bf.gov.conasur.pdi.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateMenageRequest {

    // Le site d'accueil initial du ménage
    @NotNull(message = "L'identifiant du site est obligatoire")
    private Integer idSite;

    // Chef de ménage optionnel à la création (assigné après enrôlement des membres)
    private Integer idChefMenage;
}
