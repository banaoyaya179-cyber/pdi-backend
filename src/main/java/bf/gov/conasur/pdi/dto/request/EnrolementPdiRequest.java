package bf.gov.conasur.pdi.dto.request;

import bf.gov.conasur.pdi.enums.Sexe;
import bf.gov.conasur.pdi.enums.StatutPDI;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class EnrolementPdiRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100)
    private String prenom;

    @NotNull(message = "Le sexe est obligatoire")
    private Sexe sexe;

    @NotNull(message = "La date de naissance est obligatoire")
    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate dateNaissance;

    @NotNull(message = "Le statut est obligatoire")
    private StatutPDI statutCourant;

    @NotNull(message = "L'identifiant du ménage est obligatoire")
    private Integer idMenage;

    @NotNull(message = "L'identifiant du site d'accueil est obligatoire")
    private Integer idSiteCourant;
}
