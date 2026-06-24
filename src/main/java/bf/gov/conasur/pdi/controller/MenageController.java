package bf.gov.conasur.pdi.controller;

import bf.gov.conasur.pdi.dto.request.CreateMenageRequest;
import bf.gov.conasur.pdi.dto.response.MenageResponse;
import bf.gov.conasur.pdi.service.MenageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agent/menages")
@RequiredArgsConstructor
public class MenageController {

    private final MenageService menageService;

    // POST /api/agent/menages — Créer un ménage
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_AGENT', 'ROLE_ADMIN')")
    public ResponseEntity<MenageResponse> creer(@Valid @RequestBody CreateMenageRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(menageService.creer(req));
    }

    // GET /api/agent/menages/{id} — Consulter un ménage avec ses membres
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_AGENT', 'ROLE_ADMIN', 'ROLE_RESPONSABLE')")
    public ResponseEntity<MenageResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(menageService.findById(id));
    }

    // PUT /api/agent/menages/{id}/chef/{idPdi} — Désigner chef de ménage
    @PutMapping("/{id}/chef/{idPdi}")
    @PreAuthorize("hasAnyAuthority('ROLE_AGENT', 'ROLE_ADMIN')")
    public ResponseEntity<MenageResponse> designerChef(@PathVariable Integer id,
                                                        @PathVariable Integer idPdi) {
        return ResponseEntity.ok(menageService.designerChef(id, idPdi));
    }
}
