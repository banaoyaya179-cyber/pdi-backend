package bf.gov.conasur.pdi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class AuthResponse {
    private final String token;
    private final String email;
    private final String role;
    private final String nom;
    private final String prenom;
    private final boolean requires2FA;
    private final boolean doubleAuthActive;
}
