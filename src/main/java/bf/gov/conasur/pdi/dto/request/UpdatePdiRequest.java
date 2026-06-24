package bf.gov.conasur.pdi.dto.request;

import bf.gov.conasur.pdi.enums.StatutPDI;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdatePdiRequest {

    @NotNull
    private StatutPDI statutCourant;

    @NotNull
    private Integer idSiteCourant;

    private Integer idMenage;
}
