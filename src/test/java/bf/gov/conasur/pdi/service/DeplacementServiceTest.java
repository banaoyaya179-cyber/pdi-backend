package bf.gov.conasur.pdi.service;

import bf.gov.conasur.pdi.dto.request.DeplacementRequest;
import bf.gov.conasur.pdi.entity.*;
import bf.gov.conasur.pdi.enums.StatutPDI;
import bf.gov.conasur.pdi.exception.ResourceNotFoundException;
import bf.gov.conasur.pdi.repository.DeplacementRepository;
import bf.gov.conasur.pdi.repository.PdiRepository;
import bf.gov.conasur.pdi.repository.SiteAccueilRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires — DeplacementService")
class DeplacementServiceTest {

    @Mock private DeplacementRepository deplacementRepository;
    @Mock private PdiRepository pdiRepository;
    @Mock private SiteAccueilRepository siteAccueilRepository;

    @InjectMocks private DeplacementService deplacementService;

    private Pdi pdi;
    private SiteAccueil siteOrigine;
    private SiteAccueil siteDestination;
    private Menage menage;

    @BeforeEach
    void setUp() {
        Region region = new Region();
        region.setId(1); region.setNomRegion("Sahel");

        Province province = new Province();
        province.setId(1); province.setNomProvince("Soum"); province.setRegion(region);

        Commune commune = new Commune();
        commune.setId(1); commune.setNomCommune("Djibo"); commune.setProvince(province);

        siteOrigine = new SiteAccueil();
        siteOrigine.setId(1); siteOrigine.setNomSite("Site Origine");
        siteOrigine.setCapaciteMaximale(500); siteOrigine.setCommune(commune);

        siteDestination = new SiteAccueil();
        siteDestination.setId(2); siteDestination.setNomSite("Site Destination");
        siteDestination.setCapaciteMaximale(1000); siteDestination.setCommune(commune);

        menage = new Menage();
        menage.setId(1); menage.setCodeUnique("MNG-2026-00001");

        pdi = Pdi.builder()
                .nom("TRAORE").prenom("Bakary")
                .sexe(bf.gov.conasur.pdi.enums.Sexe.M)
                .dateNaissance(LocalDate.of(1985, 3, 15))
                .statutCourant(StatutPDI.DEPLACE_INITIAL)
                .menage(menage).siteCourant(siteOrigine)
                .build();
        pdi.setId(1);
    }

    @Test
    @DisplayName("Déplacement individuel — succès")
    void enregistrer_deplacementIndividuel_succes() {
        DeplacementRequest req = new DeplacementRequest();
        req.setIdPdi(1);
        req.setIdSiteDestination(2);
        req.setMotif("Insécurité");
        req.setToutLeMenage(false);

        Deplacement deplacement = Deplacement.builder()
                .pdi(pdi).siteOrigine(siteOrigine)
                .siteDestination(siteDestination).motif("Insécurité")
                .build();

        when(pdiRepository.findById(1)).thenReturn(Optional.of(pdi));
        when(siteAccueilRepository.findById(2)).thenReturn(Optional.of(siteDestination));
        when(deplacementRepository.save(any())).thenReturn(deplacement);
        when(pdiRepository.save(any())).thenReturn(pdi);

        var resultats = deplacementService.enregistrer(req);

        assertNotNull(resultats);
        assertEquals(1, resultats.size());
        verify(deplacementRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Déplacement vers même site — ignoré")
    void enregistrer_memeSite_ignore() {
        DeplacementRequest req = new DeplacementRequest();
        req.setIdPdi(1);
        req.setIdSiteDestination(1);
        req.setMotif("Test");
        req.setToutLeMenage(false);

        when(pdiRepository.findById(1)).thenReturn(Optional.of(pdi));
        when(siteAccueilRepository.findById(1)).thenReturn(Optional.of(siteOrigine));

        var resultats = deplacementService.enregistrer(req);

        assertTrue(resultats.isEmpty());
        verify(deplacementRepository, never()).save(any());
    }

    @Test
    @DisplayName("Déplacement ménage entier — succès")
    void enregistrer_toutLeMenage_succes() {
        DeplacementRequest req = new DeplacementRequest();
        req.setIdPdi(1);
        req.setIdSiteDestination(2);
        req.setMotif("Regroupement");
        req.setToutLeMenage(true);

        Pdi pdi2 = Pdi.builder()
                .nom("TRAORE").prenom("Salimata")
                .sexe(bf.gov.conasur.pdi.enums.Sexe.F)
                .dateNaissance(LocalDate.of(1988, 6, 20))
                .statutCourant(StatutPDI.DEPLACE_INITIAL)
                .menage(menage).siteCourant(siteOrigine)
                .build();
        pdi2.setId(2);

        Deplacement dep1 = Deplacement.builder().pdi(pdi)
                .siteOrigine(siteOrigine).siteDestination(siteDestination).build();
        Deplacement dep2 = Deplacement.builder().pdi(pdi2)
                .siteOrigine(siteOrigine).siteDestination(siteDestination).build();

        when(pdiRepository.findById(1)).thenReturn(Optional.of(pdi));
        when(siteAccueilRepository.findById(2)).thenReturn(Optional.of(siteDestination));
        when(pdiRepository.findByMenageId(1)).thenReturn(List.of(pdi, pdi2));
        when(deplacementRepository.save(any())).thenReturn(dep1).thenReturn(dep2);
        when(pdiRepository.save(any())).thenReturn(pdi);

        var resultats = deplacementService.enregistrer(req);

        assertEquals(2, resultats.size());
        verify(deplacementRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("PDI introuvable lève exception")
    void enregistrer_pdiInexistante_leveException() {
        DeplacementRequest req = new DeplacementRequest();
        req.setIdPdi(999);
        req.setIdSiteDestination(2);

        when(pdiRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> deplacementService.enregistrer(req));
    }

    @Test
    @DisplayName("Historique déplacements d'une PDI")
    void historiqueParPdi_retourneListe() {
        Deplacement dep = Deplacement.builder()
                .pdi(pdi).siteOrigine(siteOrigine).siteDestination(siteDestination).build();

        when(pdiRepository.existsById(1)).thenReturn(true);
        when(deplacementRepository.findByPdiIdOrderByDateMouvementDesc(1))
                .thenReturn(List.of(dep));

        var liste = deplacementService.historiqueParPdi(1);

        assertNotNull(liste);
        assertEquals(1, liste.size());
    }

    @Test
    @DisplayName("Historique PDI inexistante lève exception")
    void historiqueParPdi_pdiInexistante_leveException() {
        when(pdiRepository.existsById(999)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> deplacementService.historiqueParPdi(999));
    }
}
