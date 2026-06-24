package bf.gov.conasur.pdi.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class AideRequest {

    @NotNull(message = "L'identifiant du besoin est obligatoire")
    private Long idBesoin;

    @NotBlank(message = "Le type d'aide est obligatoire")
    @Size(max = 50)
    private String typeAide;

    @NotNull(message = "La quantité est obligatoire")
    @DecimalMin(value = "0.01", message = "La quantité doit être positive")
    private BigDecimal quantite;

    @Size(max = 100)
    private String donateur;
}
