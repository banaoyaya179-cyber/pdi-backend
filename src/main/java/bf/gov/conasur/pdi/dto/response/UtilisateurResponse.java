package bf.gov.conasur.pdi.dto.response;

import bf.gov.conasur.pdi.entity.Utilisateur;
import lombok.Getter;

@Getter
public class UtilisateurResponse {
    private final Integer id;
    private final String nom;
    private final String prenom;
    private final String email;
    private final String role;
    private final boolean compteActif;
    private final boolean doubleAuthActive;

    public UtilisateurResponse(Utilisateur u) {
        this.id = u.getId();
        this.nom = u.getNom();
        this.prenom = u.getPrenom();
        this.email = u.getEmail();
        this.role = u.getRole().getLibelle().name();
        this.compteActif = u.isCompteActif();
        this.doubleAuthActive = u.isDoubleAuthActive();
    }
}
