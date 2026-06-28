package bf.gov.conasur.pdi.service;

import bf.gov.conasur.pdi.dto.request.CreateUtilisateurRequest;
import bf.gov.conasur.pdi.entity.Role;
import bf.gov.conasur.pdi.entity.Utilisateur;
import bf.gov.conasur.pdi.enums.RoleType;
import bf.gov.conasur.pdi.exception.DoublonException;
import bf.gov.conasur.pdi.exception.ResourceNotFoundException;
import bf.gov.conasur.pdi.repository.RoleRepository;
import bf.gov.conasur.pdi.repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires — UtilisateurService")
class UtilisateurServiceTest {

    @Mock private UtilisateurRepository utilisateurRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UtilisateurService utilisateurService;

    private Role roleAgent;
    private Role roleAdmin;
    private Utilisateur utilisateur;

    @BeforeEach
    void setUp() {
        roleAgent = new Role();
        roleAgent.setId(2);
        roleAgent.setLibelle(RoleType.ROLE_AGENT);

        roleAdmin = new Role();
        roleAdmin.setId(1);
        roleAdmin.setLibelle(RoleType.ROLE_ADMIN);

        utilisateur = Utilisateur.builder()
                .nom("OUEDRAOGO")
                .prenom("Moussa")
                .email("agent.test@conasur.gov.bf")
                .motDePasse("$2a$12$hashedpassword")
                .role(roleAgent)
                .compteActif(true)
                .doubleAuthActive(false)
                .build();
        utilisateur.setId(1);
    }

    @Test
    @DisplayName("Création utilisateur — succès")
    void creer_utilisateurValide_succes() {
        CreateUtilisateurRequest req = new CreateUtilisateurRequest();
        req.setNom("OUEDRAOGO");
        req.setPrenom("Moussa");
        req.setEmail("agent.test@conasur.gov.bf");
        req.setMotDePasse("MotDePasse123!");
        req.setRole(RoleType.ROLE_AGENT);

        when(utilisateurRepository.existsByEmail(any())).thenReturn(false);
        when(roleRepository.findByLibelle(RoleType.ROLE_AGENT)).thenReturn(Optional.of(roleAgent));
        when(passwordEncoder.encode(any())).thenReturn("$2a$12$hashedpassword");
        when(utilisateurRepository.save(any())).thenReturn(utilisateur);

        var response = utilisateurService.creer(req);

        assertNotNull(response);
        assertEquals("OUEDRAOGO", response.getNom());
        verify(utilisateurRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Création avec email existant lève exception")
    void creer_emailExistant_leveException() {
        CreateUtilisateurRequest req = new CreateUtilisateurRequest();
        req.setEmail("agent.test@conasur.gov.bf");
        req.setRole(RoleType.ROLE_AGENT);

        when(utilisateurRepository.existsByEmail("agent.test@conasur.gov.bf")).thenReturn(true);

        assertThrows(DoublonException.class, () -> utilisateurService.creer(req));
        verify(utilisateurRepository, never()).save(any());
    }

    @Test
    @DisplayName("Liste tous les utilisateurs")
    void findAll_retourneListe() {
        when(utilisateurRepository.findAll()).thenReturn(List.of(utilisateur));

        var liste = utilisateurService.findAll();

        assertNotNull(liste);
        assertEquals(1, liste.size());
    }

    @Test
    @DisplayName("Suspension compte actif — compte désactivé")
    void toggleCompte_compteActif_desactive() {
        when(utilisateurRepository.findById(1)).thenReturn(Optional.of(utilisateur));
        when(utilisateurRepository.save(any())).thenReturn(utilisateur);

        var response = utilisateurService.toggleCompte(1);

        assertNotNull(response);
        verify(utilisateurRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Toggle compte inexistant lève exception")
    void toggleCompte_utilisateurInexistant_leveException() {
        when(utilisateurRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> utilisateurService.toggleCompte(999));
    }

    @Test
    @DisplayName("Changer rôle utilisateur — succès")
    void changerRole_utilisateurExistant_succes() {
        when(utilisateurRepository.findById(1)).thenReturn(Optional.of(utilisateur));
        when(roleRepository.findByLibelle(RoleType.ROLE_ADMIN)).thenReturn(Optional.of(roleAdmin));
        when(utilisateurRepository.save(any())).thenReturn(utilisateur);

        var response = utilisateurService.changerRole(1, RoleType.ROLE_ADMIN);

        assertNotNull(response);
        verify(utilisateurRepository, times(1)).save(any());
    }
}
