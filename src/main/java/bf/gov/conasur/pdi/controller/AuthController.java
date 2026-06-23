package bf.gov.conasur.pdi.controller;

import bf.gov.conasur.pdi.dto.request.LoginRequest;
import bf.gov.conasur.pdi.dto.response.AuthResponse;
import bf.gov.conasur.pdi.entity.Utilisateur;
import bf.gov.conasur.pdi.repository.UtilisateurRepository;
import bf.gov.conasur.pdi.security.JwtUtils;
import bf.gov.conasur.pdi.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UtilisateurRepository utilisateurRepository;
    private final AuditLogService auditLogService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletRequest httpRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMotDePasse())
        );

        Utilisateur user = utilisateurRepository.findByEmail(request.getEmail()).orElseThrow();
        String token = jwtUtils.generateToken(user.getEmail(), user.getRole().getLibelle().name());

        auditLogService.log(
                "CONNEXION",
                httpRequest.getRemoteAddr(),
                "Utilisateur#" + user.getId(),
                user
        );

        return ResponseEntity.ok(new AuthResponse(
                token,
                user.getEmail(),
                user.getRole().getLibelle().name(),
                user.getNom(),
                user.getPrenom()
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me(Authentication authentication) {
        Utilisateur user = utilisateurRepository
                .findByEmail(authentication.getName()).orElseThrow();
        return ResponseEntity.ok(new AuthResponse(
                null,
                user.getEmail(),
                user.getRole().getLibelle().name(),
                user.getNom(),
                user.getPrenom()
        ));
    }
}
