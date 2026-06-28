package bf.gov.conasur.pdi.service;

import bf.gov.conasur.pdi.dto.request.EnrolementPdiRequest;
import bf.gov.conasur.pdi.entity.Commune;
import bf.gov.conasur.pdi.entity.Menage;
import bf.gov.conasur.pdi.entity.Pdi;
import bf.gov.conasur.pdi.entity.Province;
import bf.gov.conasur.pdi.entity.Region;
import bf.gov.conasur.pdi.entity.SiteAccueil;
import bf.gov.conasur.pdi.enums.StatutPDI;
import bf.gov.conasur.pdi.exception.DoublonException;
import bf.gov.conasur.pdi.repository.AideHumanitaireRepository;
import bf.gov.conasur.pdi.repository.BesoinRepository;
import bf.gov.conasur.pdi.repository.DeplacementRepository;
import bf.gov.conasur.pdi.repository.MenageRepository;
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
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires — PdiService")
class PdiServiceTest {

    @Mock private PdiRepository pdiRepository;
    @Mock private MenageRepository menageRepository;
    @Mock private SiteAccueilRepository siteAccueilRepository;
    @Mock private DeplacementRepository deplacementRepository;
    @Mock private BesoinRepository besoinRepository;
    @Mock private AideHumanitaireRepository aideHumanitaireRepository;

    @InjectMocks private PdiService pdiService;

    private Menage menage;
    private SiteAccueil site;
    private Pdi pdi;
    private EnrolementPdiRequest request;

    @BeforeEach
    void setUp() {
        Region region = new Region();
        region.setId(1);
        region.setNomRegion("Sahel");

        Province province = new Province();
        province.setId(1);
        province.setNomProvince("Soum");
        province.setRegion(region);

        Commune commune = new Commune();
        commune.setId(1);
        commune.setNomCommune("Djibo");
        commune.setProvince(province);

        site = new SiteAccueil();
        site.setId(1);
        site.setNomSite("Site de Djibo");
        site.setCapaciteMaximale(500);
        site.setCommune(commune);

        menage = new Menage();
        menage.setId(1);
        menage.setCodeUnique("MNG-2026-00001");

        pdi = Pdi.builder()
                .nom("OUEDRAOGO")
                .prenom("Issaka")
                .sexe(bf.gov.conasur.pdi.enums.Sexe.M)
                .dateNaissance(LocalDate.of(1990, 1, 1))
                .statutCourant(StatutPDI.DEPLACE_INITIAL)
                .menage(menage)
                .siteCourant(site)
                .build();
        pdi.setId(1);

        request = new EnrolementPdiRequest();
        request.setNom("OUEDRAOGO");
        request.setPrenom("Issaka");
        request.setSexe(bf.gov.conasur.pdi.enums.Sexe.M);
        request.setDateNaissance(LocalDate.of(1990, 1, 1));
        request.setStatutCourant(StatutPDI.DEPLACE_INITIAL);
        request.setIdMenage(1);
        request.setIdSiteCourant(1);
    }

    @Test
    @DisplayName("Enrôlement réussi d'une nouvelle PDI")
    void enroler_nouvellesPdi_succes() {
        when(pdiRepository.existsByNomIgnoreCaseAndPrenomIgnoreCaseAndDateNaissance(
                any(), any(), any())).thenReturn(false);
        when(menageRepository.findById(1)).thenReturn(Optional.of(menage));
        when(siteAccueilRepository.findById(1)).thenReturn(Optional.of(site));
        when(pdiRepository.save(any())).thenReturn(pdi);

        var response = pdiService.enroler(request);

        assertNotNull(response);
        assertEquals("OUEDRAOGO", response.getNom());
        verify(pdiRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Enrôlement bloqué si doublon détecté")
    void enroler_doublon_leveException() {
        when(pdiRepository.existsByNomIgnoreCaseAndPrenomIgnoreCaseAndDateNaissance(
                any(), any(), any())).thenReturn(true);

        assertThrows(DoublonException.class, () -> pdiService.enroler(request));
        verify(pdiRepository, never()).save(any());
    }

    @Test
    @DisplayName("Recherche d'une PDI par ID existant")
    void findById_pdiExistante_retourneResponse() {
        when(pdiRepository.findById(1)).thenReturn(Optional.of(pdi));

        var response = pdiService.findById(1);

        assertNotNull(response);
        assertEquals(1, response.getId());
    }

    @Test
    @DisplayName("Recherche d'une PDI par ID inexistant lève une exception")
    void findById_pdiInexistante_leveException() {
        when(pdiRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(bf.gov.conasur.pdi.exception.ResourceNotFoundException.class,
                () -> pdiService.findById(999));
    }

    @Test
    @DisplayName("Suppression PDI sans dépendances")
    void supprimer_pdiSansDependances_succes() {
        when(pdiRepository.findById(1)).thenReturn(Optional.of(pdi));
        when(besoinRepository.findByPdiId(1)).thenReturn(Collections.emptyList());
        doNothing().when(deplacementRepository).deleteByPdiId(1);
        doNothing().when(besoinRepository).deleteByPdiId(1);

        pdiService.supprimer(1);

        verify(pdiRepository, times(1)).delete(pdi);
    }

    @Test
    @DisplayName("Suppression PDI inexistante lève une exception")
    void supprimer_pdiInexistante_leveException() {
        when(pdiRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(bf.gov.conasur.pdi.exception.ResourceNotFoundException.class,
                () -> pdiService.supprimer(999));
        verify(pdiRepository, never()).delete(any());
    }
}
