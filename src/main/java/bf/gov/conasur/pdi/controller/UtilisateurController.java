package bf.gov.conasur.pdi.controller;

import bf.gov.conasur.pdi.dto.request.CreateUtilisateurRequest;
import bf.gov.conasur.pdi.dto.response.UtilisateurResponse;
import bf.gov.conasur.pdi.enums.RoleType;
import bf.gov.conasur.pdi.service.UtilisateurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/utilisateurs")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    @GetMapping
    public ResponseEntity<List<UtilisateurResponse>> findAll() {
        return ResponseEntity.ok(utilisateurService.findAll());
    }

    @PostMapping
    public ResponseEntity<UtilisateurResponse> creer(@Valid @RequestBody CreateUtilisateurRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(utilisateurService.creer(req));
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<UtilisateurResponse> toggleCompte(@PathVariable Integer id) {
        return ResponseEntity.ok(utilisateurService.toggleCompte(id));
    }

    @PutMapping("/{id}/role/{role}")
    public ResponseEntity<UtilisateurResponse> changerRole(@PathVariable Integer id,
                                                            @PathVariable RoleType role) {
        return ResponseEntity.ok(utilisateurService.changerRole(id, role));
    }
}
