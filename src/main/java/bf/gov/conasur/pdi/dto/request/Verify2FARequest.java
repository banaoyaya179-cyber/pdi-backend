package bf.gov.conasur.pdi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Verify2FARequest {
    @NotBlank
    private String email;
    @NotNull
    private Integer code;
}
