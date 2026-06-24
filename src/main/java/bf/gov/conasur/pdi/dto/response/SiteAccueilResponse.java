package bf.gov.conasur.pdi.dto.response;

import bf.gov.conasur.pdi.entity.SiteAccueil;
import lombok.Getter;

@Getter
public class SiteAccueilResponse {

    private final Integer id;
    private final String nomSite;
    private final Integer capaciteMaximale;
    private final long occupationActuelle;
    private final double tauxOccupation;
    private final String statut;
    private final Integer idCommune;
    private final String commune;
    private final String province;
    private final String region;
    private final Double latitude;
    private final Double longitude;

    public SiteAccueilResponse(SiteAccueil s, long occupation) {
        this.id = s.getId();
        this.nomSite = s.getNomSite();
        this.capaciteMaximale = s.getCapaciteMaximale();
        this.occupationActuelle = occupation;
        this.tauxOccupation = s.getCapaciteMaximale() > 0
                ? Math.round((occupation * 100.0 / s.getCapaciteMaximale()) * 10.0) / 10.0
                : 0.0;
        this.statut = calculerStatut(tauxOccupation);
        this.idCommune = s.getCommune().getId();
        this.commune = s.getCommune().getNomCommune();
        this.province = s.getCommune().getProvince().getNomProvince();
        this.region = s.getCommune().getProvince().getRegion().getNomRegion();
        this.latitude = s.getLocalisationGps() != null
                ? s.getLocalisationGps().getY() : null;
        this.longitude = s.getLocalisationGps() != null
                ? s.getLocalisationGps().getX() : null;
    }

    private String calculerStatut(double taux) {
        if (taux >= 100) return "SATURE";
        if (taux >= 70) return "CRITIQUE";
        return "NORMAL";
    }
}
