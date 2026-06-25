package bf.gov.conasur.pdi.dto.request;

import bf.gov.conasur.pdi.enums.RoleType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateUtilisateurRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @NotBlank @Email(message = "Email invalide")
    private String email;

    @NotBlank @Size(min = 8, message = "Mot de passe minimum 8 caractères")
    private String motDePasse;

    @NotNull(message = "Le rôle est obligatoire")
    private RoleType role;
}
