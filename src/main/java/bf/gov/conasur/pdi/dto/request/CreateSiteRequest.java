package bf.gov.conasur.pdi.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateSiteRequest {

    @NotBlank(message = "Le nom du site est obligatoire")
    @Size(max = 150)
    private String nomSite;

    @NotNull(message = "La capacité maximale est obligatoire")
    @Min(value = 1, message = "La capacité doit être supérieure à 0")
    private Integer capaciteMaximale;

    @NotNull(message = "L'identifiant de la commune est obligatoire")
    private Integer idCommune;

    @NotNull(message = "La latitude est obligatoire")
    @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0")
    private Double latitude;

    @NotNull(message = "La longitude est obligatoire")
    @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0")
    private Double longitude;
}
