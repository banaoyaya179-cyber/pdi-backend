package bf.gov.conasur.pdi.dto.response;

import bf.gov.conasur.pdi.enums.Sexe;
import bf.gov.conasur.pdi.enums.StatutPDI;
import bf.gov.conasur.pdi.entity.Pdi;
import lombok.Getter;

import java.time.LocalDate;
import java.time.Period;

@Getter
public class PdiResponse {

    private final Integer id;
    private final String nom;
    private final String prenom;
    private final Sexe sexe;
    private final LocalDate dateNaissance;
    private final int age;
    private final StatutPDI statutCourant;
    private final LocalDate dateEnrolement;
    private final Integer idMenage;
    private final String codeUniqueMenage;
    private final Integer idSiteCourant;
    private final String nomSiteCourant;
    private final String region;
    private final String province;
    private final String commune;

    public PdiResponse(Pdi p) {
        this.id = p.getId();
        this.nom = p.getNom();
        this.prenom = p.getPrenom();
        this.sexe = p.getSexe();
        this.dateNaissance = p.getDateNaissance();
        this.age = Period.between(p.getDateNaissance(), LocalDate.now()).getYears();
        this.statutCourant = p.getStatutCourant();
        this.dateEnrolement = p.getDateEnrolement();
        this.idMenage = p.getMenage().getId();
        this.codeUniqueMenage = p.getMenage().getCodeUnique();
        this.idSiteCourant = p.getSiteCourant().getId();
        this.nomSiteCourant = p.getSiteCourant().getNomSite();
        this.region = p.getSiteCourant().getCommune().getProvince().getRegion().getNomRegion();
        this.province = p.getSiteCourant().getCommune().getProvince().getNomProvince();
        this.commune = p.getSiteCourant().getCommune().getNomCommune();
    }
}
