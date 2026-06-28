package bf.gov.conasur.pdi.service;

import bf.gov.conasur.pdi.dto.request.CreateSiteRequest;
import bf.gov.conasur.pdi.entity.Commune;
import bf.gov.conasur.pdi.entity.Province;
import bf.gov.conasur.pdi.entity.Region;
import bf.gov.conasur.pdi.entity.SiteAccueil;
import bf.gov.conasur.pdi.repository.CommuneRepository;
import bf.gov.conasur.pdi.repository.NotificationRepository;
import bf.gov.conasur.pdi.repository.PdiRepository;
import bf.gov.conasur.pdi.repository.SiteAccueilRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires — SiteAccueilService")
class SiteAccueilServiceTest {

    @Mock private SiteAccueilRepository siteAccueilRepository;
    @Mock private CommuneRepository communeRepository;
    @Mock private PdiRepository pdiRepository;
    @Mock private NotificationRepository notificationRepository;

    @InjectMocks private SiteAccueilService siteAccueilService;

    private Commune commune;
    private SiteAccueil site;

    @BeforeEach
    void setUp() {
        Region region = new Region();
        region.setId(1);
        region.setNomRegion("Centre-Nord");

        Province province = new Province();
        province.setId(1);
        province.setNomProvince("Sanmatenga");
        province.setRegion(region);

        commune = new Commune();
        commune.setId(1);
        commune.setNomCommune("Kaya");
        commune.setProvince(province);

        site = new SiteAccueil();
        site.setId(1);
        site.setNomSite("Site de Kaya Nord");
        site.setCapaciteMaximale(2500);
        site.setCommune(commune);
    }

    @Test
    @DisplayName("Création d'un site avec coordonnées GPS valides")
    void creer_siteValide_succes() {
        CreateSiteRequest req = new CreateSiteRequest();
        req.setNomSite("Site de Kaya Nord");
        req.setCapaciteMaximale(2500);
        req.setIdCommune(1);
        req.setLatitude(13.0917);
        req.setLongitude(-1.0800);

        when(communeRepository.findById(1)).thenReturn(Optional.of(commune));
        when(siteAccueilRepository.save(any())).thenReturn(site);
        when(pdiRepository.countBySiteCourantId(any())).thenReturn(0L);

        var response = siteAccueilService.creer(req);

        assertNotNull(response);
        assertEquals("Site de Kaya Nord", response.getNomSite());
        verify(siteAccueilRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Récupération de tous les sites")
    void findAll_retourneListe() {
        when(siteAccueilRepository.findAll()).thenReturn(List.of(site));
        when(pdiRepository.countBySiteCourantId(any())).thenReturn(10L);

        var liste = siteAccueilService.findAll();

        assertNotNull(liste);
        assertEquals(1, liste.size());
    }

    @Test
    @DisplayName("Calcul statut NORMAL si taux < 70%")
    void calculerStatut_tauxNormal() {
        when(siteAccueilRepository.findAll()).thenReturn(List.of(site));
        when(pdiRepository.countBySiteCourantId(any())).thenReturn(100L);

        var liste = siteAccueilService.findAll();
        assertEquals("NORMAL", liste.get(0).getStatut());
    }

    @Test
    @DisplayName("Calcul statut CRITIQUE si taux entre 70% et 99%")
    void calculerStatut_tauxCritique() {
        when(siteAccueilRepository.findAll()).thenReturn(List.of(site));
        when(pdiRepository.countBySiteCourantId(any())).thenReturn(2000L);

        var liste = siteAccueilService.findAll();
        assertEquals("CRITIQUE", liste.get(0).getStatut());
    }

    @Test
    @DisplayName("Calcul statut SATURE si taux >= 100%")
    void calculerStatut_tauxSature() {
        when(siteAccueilRepository.findAll()).thenReturn(List.of(site));
        when(pdiRepository.countBySiteCourantId(any())).thenReturn(2500L);

        var liste = siteAccueilService.findAll();
        assertEquals("SATURE", liste.get(0).getStatut());
    }
}
