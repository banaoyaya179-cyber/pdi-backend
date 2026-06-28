package bf.gov.conasur.pdi.service;

import bf.gov.conasur.pdi.dto.request.CreateMenageRequest;
import bf.gov.conasur.pdi.entity.*;
import bf.gov.conasur.pdi.enums.StatutPDI;
import bf.gov.conasur.pdi.exception.ResourceNotFoundException;
import bf.gov.conasur.pdi.repository.MenageRepository;
import bf.gov.conasur.pdi.repository.PdiRepository;
import bf.gov.conasur.pdi.util.CodeGenerator;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires — MenageService")
class MenageServiceTest {

    @Mock private MenageRepository menageRepository;
    @Mock private PdiRepository pdiRepository;
    @Mock private CodeGenerator codeGenerator;

    @InjectMocks private MenageService menageService;

    private Menage menage;
    private Pdi pdi;

    @BeforeEach
    void setUp() {
        Region region = new Region();
        region.setId(1); region.setNomRegion("Centre-Nord");

        Province province = new Province();
        province.setId(1); province.setNomProvince("Sanmatenga"); province.setRegion(region);

        Commune commune = new Commune();
        commune.setId(1); commune.setNomCommune("Kaya"); commune.setProvince(province);

        SiteAccueil site = new SiteAccueil();
        site.setId(1); site.setNomSite("Site de Kaya");
        site.setCapaciteMaximale(2500); site.setCommune(commune);

        menage = new Menage();
        menage.setId(1); menage.setCodeUnique("MNG-2026-00001");

        pdi = Pdi.builder()
                .nom("SANKARA").prenom("Thomas")
                .sexe(bf.gov.conasur.pdi.enums.Sexe.M)
                .dateNaissance(LocalDate.of(1975, 8, 15))
                .statutCourant(StatutPDI.RETOURNE)
                .menage(menage).siteCourant(site)
                .build();
        pdi.setId(1);
        menage.setChefMenage(pdi);
    }

    @Test
    @DisplayName("Création ménage sans chef — succès")
    void creer_menageSansChef_succes() {
        CreateMenageRequest req = new CreateMenageRequest();

        when(codeGenerator.genererCodeMenage()).thenReturn("MNG-2026-00002");
        when(menageRepository.existsByCodeUnique(any())).thenReturn(false);
        when(menageRepository.save(any())).thenReturn(menage);
        when(pdiRepository.findByMenageId(1)).thenReturn(Collections.emptyList());

        var response = menageService.creer(req);

        assertNotNull(response);
        verify(menageRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Création ménage avec chef — succès")
    void creer_menageAvecChef_succes() {
        CreateMenageRequest req = new CreateMenageRequest();
        req.setIdChefMenage(1);

        when(codeGenerator.genererCodeMenage()).thenReturn("MNG-2026-00003");
        when(menageRepository.existsByCodeUnique(any())).thenReturn(false);
        when(pdiRepository.findById(1)).thenReturn(Optional.of(pdi));
        when(menageRepository.save(any())).thenReturn(menage);
        when(pdiRepository.findByMenageId(1)).thenReturn(Collections.singletonList(pdi));

        var response = menageService.creer(req);

        assertNotNull(response);
        verify(pdiRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Consultation ménage existant")
    void findById_menageExistant_succes() {
        when(menageRepository.findById(1)).thenReturn(Optional.of(menage));
        when(pdiRepository.findByMenageId(1)).thenReturn(Collections.singletonList(pdi));

        var response = menageService.findById(1);

        assertNotNull(response);
        assertEquals(1, response.getId());
    }

    @Test
    @DisplayName("Consultation ménage inexistant lève exception")
    void findById_menageInexistant_leveException() {
        when(menageRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> menageService.findById(999));
    }

    @Test
    @DisplayName("Désigner chef de ménage — succès")
    void designerChef_pdiAppartientMenage_succes() {
        when(menageRepository.findById(1)).thenReturn(Optional.of(menage));
        when(pdiRepository.findById(1)).thenReturn(Optional.of(pdi));
        when(menageRepository.save(any())).thenReturn(menage);
        when(pdiRepository.findByMenageId(1)).thenReturn(Collections.singletonList(pdi));

        var response = menageService.designerChef(1, 1);

        assertNotNull(response);
        verify(menageRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Désigner chef PDI hors ménage lève exception")
    void designerChef_pdiHorsMenage_leveException() {
        Menage autreMenage = new Menage();
        autreMenage.setId(99);
        autreMenage.setCodeUnique("MNG-2026-00099");

        Pdi pdiAutreMenage = Pdi.builder()
                .nom("AUTRE").prenom("PDI")
                .sexe(bf.gov.conasur.pdi.enums.Sexe.F)
                .dateNaissance(LocalDate.of(1990, 1, 1))
                .statutCourant(StatutPDI.DEPLACE_INITIAL)
                .menage(autreMenage).siteCourant(pdi.getSiteCourant())
                .build();
        pdiAutreMenage.setId(2);

        when(menageRepository.findById(1)).thenReturn(Optional.of(menage));
        when(pdiRepository.findById(2)).thenReturn(Optional.of(pdiAutreMenage));

        assertThrows(IllegalArgumentException.class,
                () -> menageService.designerChef(1, 2));
    }
}
