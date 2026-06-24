package bf.gov.conasur.pdi.controller;

import bf.gov.conasur.pdi.dto.request.AideRequest;
import bf.gov.conasur.pdi.dto.request.BesoinRequest;
import bf.gov.conasur.pdi.dto.response.AideResponse;
import bf.gov.conasur.pdi.dto.response.BesoinResponse;
import bf.gov.conasur.pdi.service.BesoinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class BesoinController {

    private final BesoinService besoinService;

    // POST /api/agent/besoins — Déclarer un besoin
    @PostMapping("/besoins")
    @PreAuthorize("hasAnyAuthority('ROLE_AGENT', 'ROLE_ADMIN')")
    public ResponseEntity<BesoinResponse> declarer(@Valid @RequestBody BesoinRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(besoinService.declarer(req));
    }

    // PUT /api/agent/besoins/{id}/cloturer — Clôturer un besoin
    @PutMapping("/besoins/{id}/cloturer")
    @PreAuthorize("hasAnyAuthority('ROLE_AGENT', 'ROLE_ADMIN')")
    public ResponseEntity<BesoinResponse> cloturer(@PathVariable Long id) {
        return ResponseEntity.ok(besoinService.cloturer(id));
    }

    // GET /api/agent/besoins/pdi/{idPdi} — Besoins d'une PDI
    @GetMapping("/besoins/pdi/{idPdi}")
    @PreAuthorize("hasAnyAuthority('ROLE_AGENT', 'ROLE_ADMIN', 'ROLE_RESPONSABLE')")
    public ResponseEntity<List<BesoinResponse>> findByPdi(@PathVariable Integer idPdi) {
        return ResponseEntity.ok(besoinService.findByPdi(idPdi));
    }

    // POST /api/agent/aides — Enregistrer une aide
    @PostMapping("/aides")
    @PreAuthorize("hasAnyAuthority('ROLE_AGENT', 'ROLE_ADMIN')")
    public ResponseEntity<AideResponse> enregistrerAide(@Valid @RequestBody AideRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(besoinService.enregistrerAide(req));
    }

    // GET /api/agent/aides/besoin/{idBesoin} — Historique aides
    @GetMapping("/aides/besoin/{idBesoin}")
    @PreAuthorize("hasAnyAuthority('ROLE_AGENT', 'ROLE_ADMIN', 'ROLE_RESPONSABLE')")
    public ResponseEntity<List<AideResponse>> historiqueAides(@PathVariable Long idBesoin) {
        return ResponseEntity.ok(besoinService.historiqueAides(idBesoin));
    }
}
