package bf.gov.conasur.pdi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private String role;
    private String nom;
    private String prenom;
}
