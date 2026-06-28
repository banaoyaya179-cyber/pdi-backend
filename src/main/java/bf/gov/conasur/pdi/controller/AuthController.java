package bf.gov.conasur.pdi.controller;

import bf.gov.conasur.pdi.dto.request.LoginRequest;
import bf.gov.conasur.pdi.dto.request.Verify2FARequest;
import bf.gov.conasur.pdi.dto.response.AuthResponse;
import bf.gov.conasur.pdi.dto.response.Setup2FAResponse;
import bf.gov.conasur.pdi.entity.Utilisateur;
import bf.gov.conasur.pdi.repository.UtilisateurRepository;
import bf.gov.conasur.pdi.security.JwtUtils;
import bf.gov.conasur.pdi.service.AuditLogService;
import bf.gov.conasur.pdi.service.TwoFactorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UtilisateurRepository utilisateurRepository;
    private final AuditLogService auditLogService;
    private final TwoFactorService twoFactorService;

    private AuthResponse buildAuthResponse(Utilisateur user, String token,
                                            boolean requires2FA, boolean doubleAuthActive) {
        return new AuthResponse(
                token,
                user.getEmail(),
                user.getRole().getLibelle().name(),
                user.getNom(),
                user.getPrenom(),
                requires2FA,
                doubleAuthActive,
                user.getIdSiteAffecte()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request,
                                   HttpServletRequest httpRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMotDePasse())
        );

        Utilisateur user = utilisateurRepository.findByEmail(request.getEmail()).orElseThrow();
        auditLogService.log("CONNEXION", httpRequest.getRemoteAddr(),
                "Utilisateur#" + user.getId(), user);

        if (user.isDoubleAuthActive()) {
            return ResponseEntity.ok(Map.of(
                    "requires2FA", true,
                    "email", user.getEmail()
            ));
        }

        String token = jwtUtils.generateToken(user.getEmail(), user.getRole().getLibelle().name());
        return ResponseEntity.ok(buildAuthResponse(user, token, false, false));
    }

    @PostMapping("/verify-2fa")
    public ResponseEntity<?> verify2FA(@Valid @RequestBody Verify2FARequest request,
                                        HttpServletRequest httpRequest) {
        boolean valide = twoFactorService.verifier(request.getEmail(), request.getCode());
        if (!valide) {
            return ResponseEntity.status(401).body(Map.of("erreur", "Code 2FA invalide ou expiré."));
        }

        Utilisateur user = utilisateurRepository.findByEmail(request.getEmail()).orElseThrow();
        String token = jwtUtils.generateToken(user.getEmail(), user.getRole().getLibelle().name());
        auditLogService.log("CONNEXION_2FA", httpRequest.getRemoteAddr(),
                "Utilisateur#" + user.getId(), user);

        return ResponseEntity.ok(buildAuthResponse(user, token, false, true));
    }

    @PostMapping("/setup-2fa")
    public ResponseEntity<Setup2FAResponse> setup2FA(Authentication authentication) {
        return ResponseEntity.ok(twoFactorService.setup(authentication.getName()));
    }

    @PostMapping("/activate-2fa")
    public ResponseEntity<?> activate2FA(@RequestBody Map<String, Integer> body,
                                          Authentication authentication) {
        boolean ok = twoFactorService.activer(authentication.getName(), body.get("code"));
        if (!ok) return ResponseEntity.badRequest().body(Map.of("erreur", "Code invalide."));
        return ResponseEntity.ok(Map.of("message", "2FA activé avec succès."));
    }

    @PostMapping("/disable-2fa")
    public ResponseEntity<?> disable2FA(Authentication authentication) {
        twoFactorService.desactiver(authentication.getName());
        return ResponseEntity.ok(Map.of("message", "2FA désactivé."));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me(Authentication authentication) {
        Utilisateur user = utilisateurRepository
                .findByEmail(authentication.getName()).orElseThrow();
        return ResponseEntity.ok(buildAuthResponse(user, null, false, user.isDoubleAuthActive()));
    }
}
