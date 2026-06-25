package bf.gov.conasur.pdi.service;

import bf.gov.conasur.pdi.dto.request.CreateUtilisateurRequest;
import bf.gov.conasur.pdi.dto.response.UtilisateurResponse;
import bf.gov.conasur.pdi.entity.Role;
import bf.gov.conasur.pdi.entity.Utilisateur;
import bf.gov.conasur.pdi.exception.DoublonException;
import bf.gov.conasur.pdi.exception.ResourceNotFoundException;
import bf.gov.conasur.pdi.repository.RoleRepository;
import bf.gov.conasur.pdi.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UtilisateurResponse> findAll() {
        return utilisateurRepository.findAll()
                .stream().map(UtilisateurResponse::new).toList();
    }

    public UtilisateurResponse creer(CreateUtilisateurRequest req) {
        if (utilisateurRepository.existsByEmail(req.getEmail())) {
            throw new DoublonException("Un compte avec l'email " + req.getEmail() + " existe déjà.");
        }

        Role role = roleRepository.findByLibelle(req.getRole())
                .orElseThrow(() -> new ResourceNotFoundException("Rôle introuvable : " + req.getRole()));

        Utilisateur user = Utilisateur.builder()
                .nom(req.getNom().toUpperCase().trim())
                .prenom(req.getPrenom().trim())
                .email(req.getEmail().toLowerCase().trim())
                .motDePasse(passwordEncoder.encode(req.getMotDePasse()))
                .role(role)
                .compteActif(true)
                .doubleAuthActive(false)
                .build();

        return new UtilisateurResponse(utilisateurRepository.save(user));
    }

    public UtilisateurResponse toggleCompte(Integer id) {
        Utilisateur user = getOuErreur(id);
        user.setCompteActif(!user.isCompteActif());
        return new UtilisateurResponse(utilisateurRepository.save(user));
    }

    public UtilisateurResponse changerRole(Integer id, bf.gov.conasur.pdi.enums.RoleType roleType) {
        Utilisateur user = getOuErreur(id);
        Role role = roleRepository.findByLibelle(roleType)
                .orElseThrow(() -> new ResourceNotFoundException("Rôle introuvable"));
        user.setRole(role);
        return new UtilisateurResponse(utilisateurRepository.save(user));
    }

    private Utilisateur getOuErreur(Integer id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + id));
    }
}
