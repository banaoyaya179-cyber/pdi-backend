package bf.gov.conasur.pdi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class Setup2FAResponse {
    private final String secret;
    private final String otpAuthUrl;
    private final String qrCodeUrl;
}
