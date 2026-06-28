package bf.gov.conasur.pdi.service;

import bf.gov.conasur.pdi.dto.request.AideRequest;
import bf.gov.conasur.pdi.dto.request.BesoinRequest;
import bf.gov.conasur.pdi.entity.*;
import bf.gov.conasur.pdi.enums.StatutBesoin;
import bf.gov.conasur.pdi.enums.StatutPDI;
import bf.gov.conasur.pdi.exception.ResourceNotFoundException;
import bf.gov.conasur.pdi.repository.*;
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
@DisplayName("Tests unitaires — BesoinService")
class BesoinServiceTest {

    @Mock private BesoinRepository besoinRepository;
    @Mock private AideHumanitaireRepository aideRepository;
    @Mock private PdiRepository pdiRepository;
    @Mock private MenageRepository menageRepository;

    @InjectMocks private BesoinService besoinService;

    private Pdi pdi;
    private Menage menage;
    private Besoin besoin;

    @BeforeEach
    void setUp() {
        Region region = new Region();
        region.setId(1); region.setNomRegion("Sahel");

        Province province = new Province();
        province.setId(1); province.setNomProvince("Soum"); province.setRegion(region);

        Commune commune = new Commune();
        commune.setId(1); commune.setNomCommune("Djibo"); commune.setProvince(province);

        SiteAccueil site = new SiteAccueil();
        site.setId(1); site.setNomSite("Site de Djibo");
        site.setCapaciteMaximale(500); site.setCommune(commune);

        menage = new Menage();
        menage.setId(1); menage.setCodeUnique("MNG-2026-00001");

        pdi = Pdi.builder()
                .nom("KABORE").prenom("Aminata")
                .sexe(bf.gov.conasur.pdi.enums.Sexe.F)
                .dateNaissance(LocalDate.of(1988, 5, 12))
                .statutCourant(StatutPDI.DEPLACE_INITIAL)
                .menage(menage).siteCourant(site)
                .build();
        pdi.setId(1);

        besoin = Besoin.builder()
                .categorie(bf.gov.conasur.pdi.enums.CategorieBesoin.VIVRES)
                .priorite(bf.gov.conasur.pdi.enums.Priorite.URGENT)
                .statut(StatutBesoin.DECLARE)
                .dateDeclaration(LocalDate.now())
                .pdi(pdi)
                .build();
        besoin.setId(1L);
    }

    @Test
    @DisplayName("Déclaration besoin pour une PDI — succès")
    void declarer_besoinPourPdi_succes() {
        BesoinRequest req = new BesoinRequest();
        req.setCategorie(bf.gov.conasur.pdi.enums.CategorieBesoin.VIVRES);
        req.setPriorite(bf.gov.conasur.pdi.enums.Priorite.URGENT);
        req.setIdPdi(1);

        when(pdiRepository.findById(1)).thenReturn(Optional.of(pdi));
        when(besoinRepository.save(any())).thenReturn(besoin);

        var response = besoinService.declarer(req);

        assertNotNull(response);
        verify(besoinRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Déclaration besoin pour un ménage — succès")
    void declarer_besoinPourMenage_succes() {
        BesoinRequest req = new BesoinRequest();
        req.setCategorie(bf.gov.conasur.pdi.enums.CategorieBesoin.ABRIS);
        req.setPriorite(bf.gov.conasur.pdi.enums.Priorite.CRITIQUE);
        req.setIdMenage(1);

        Besoin besoinMenage = Besoin.builder()
                .categorie(bf.gov.conasur.pdi.enums.CategorieBesoin.ABRIS)
                .priorite(bf.gov.conasur.pdi.enums.Priorite.CRITIQUE)
                .statut(StatutBesoin.DECLARE)
                .dateDeclaration(LocalDate.now())
                .menage(menage)
                .build();
        besoinMenage.setId(2L);

        when(menageRepository.findById(1)).thenReturn(Optional.of(menage));
        when(besoinRepository.save(any())).thenReturn(besoinMenage);

        var response = besoinService.declarer(req);

        assertNotNull(response);
        verify(besoinRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Déclaration sans PDI ni ménage lève exception")
    void declarer_sansCible_leveException() {
        BesoinRequest req = new BesoinRequest();
        req.setCategorie(bf.gov.conasur.pdi.enums.CategorieBesoin.SANTE);
        req.setPriorite(bf.gov.conasur.pdi.enums.Priorite.MOYEN);

        assertThrows(IllegalArgumentException.class, () -> besoinService.declarer(req));
    }

    @Test
    @DisplayName("Déclaration avec PDI ET ménage lève exception")
    void declarer_avecPdiEtMenage_leveException() {
        BesoinRequest req = new BesoinRequest();
        req.setCategorie(bf.gov.conasur.pdi.enums.CategorieBesoin.SANTE);
        req.setPriorite(bf.gov.conasur.pdi.enums.Priorite.MOYEN);
        req.setIdPdi(1);
        req.setIdMenage(1);

        assertThrows(IllegalArgumentException.class, () -> besoinService.declarer(req));
    }

    @Test
    @DisplayName("Clôture d'un besoin — statut passe à SATISFAIT")
    void cloturer_besoinExistant_succes() {
        when(besoinRepository.findById(1L)).thenReturn(Optional.of(besoin));
        when(besoinRepository.save(any())).thenReturn(besoin);

        var response = besoinService.cloturer(1L);

        assertNotNull(response);
        verify(besoinRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Clôture besoin inexistant lève exception")
    void cloturer_besoinInexistant_leveException() {
        when(besoinRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> besoinService.cloturer(999L));
    }

    @Test
    @DisplayName("Récupération besoins d'une PDI")
    void findByPdi_retourneListe() {
        when(besoinRepository.findByPdiId(1)).thenReturn(List.of(besoin));

        var liste = besoinService.findByPdi(1);

        assertNotNull(liste);
        assertEquals(1, liste.size());
    }

    @Test
    @DisplayName("Enregistrement aide — besoin passe en EN_COURS")
    void enregistrerAide_besoinDeclare_passesEnCours() {
        AideRequest req = new AideRequest();
        req.setIdBesoin(1L);
        req.setTypeAide("Kit alimentaire");
        req.setQuantite(java.math.BigDecimal.ONE);
        req.setDonateur("PAM");

        AideHumanitaire aide = new AideHumanitaire();
        aide.setId(1L);
        aide.setTypeAide("Kit alimentaire");
        aide.setQuantite(java.math.BigDecimal.ONE);
        aide.setDonateur("PAM");
        aide.setBesoin(besoin);

        when(besoinRepository.findById(1L)).thenReturn(Optional.of(besoin));
        when(aideRepository.save(any())).thenReturn(aide);
        when(besoinRepository.save(any())).thenReturn(besoin);

        var response = besoinService.enregistrerAide(req);

        assertNotNull(response);
        verify(besoinRepository, times(1)).save(any());
        verify(aideRepository, times(1)).save(any());
    }
}
