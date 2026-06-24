package bf.gov.conasur.pdi.controller;

import bf.gov.conasur.pdi.dto.request.DeplacementRequest;
import bf.gov.conasur.pdi.dto.response.DeplacementResponse;
import bf.gov.conasur.pdi.service.DeplacementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agent/deplacements")
@RequiredArgsConstructor
public class DeplacementController {

    private final DeplacementService deplacementService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_AGENT', 'ROLE_ADMIN')")
    public ResponseEntity<List<DeplacementResponse>> enregistrer(
            @Valid @RequestBody DeplacementRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(deplacementService.enregistrer(req));
    }

    @GetMapping("/pdi/{idPdi}")
    @PreAuthorize("hasAnyAuthority('ROLE_AGENT', 'ROLE_ADMIN', 'ROLE_RESPONSABLE')")
    public ResponseEntity<List<DeplacementResponse>> historique(@PathVariable Integer idPdi) {
        return ResponseEntity.ok(deplacementService.historiqueParPdi(idPdi));
    }
}
