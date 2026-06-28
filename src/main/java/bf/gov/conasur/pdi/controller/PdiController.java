package bf.gov.conasur.pdi.controller;

import bf.gov.conasur.pdi.dto.request.EnrolementPdiRequest;
import bf.gov.conasur.pdi.dto.request.UpdatePdiRequest;
import bf.gov.conasur.pdi.dto.response.PageResponse;
import bf.gov.conasur.pdi.dto.response.PdiResponse;
import bf.gov.conasur.pdi.enums.StatutPDI;
import bf.gov.conasur.pdi.service.AuditLogService;
import bf.gov.conasur.pdi.service.PdiService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agent/pdi")
@RequiredArgsConstructor
public class PdiController {

    private final PdiService pdiService;
    private final AuditLogService auditLogService;

    // POST /api/agent/pdi — Enrôler une nouvelle PDI
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_AGENT', 'ROLE_ADMIN')")
    public ResponseEntity<PdiResponse> enroler(@Valid @RequestBody EnrolementPdiRequest req,
                                                Authentication auth,
                                                HttpServletRequest httpReq) {
        PdiResponse response = pdiService.enroler(req);
        auditLogService.log("ENROLEMENT_PDI", httpReq.getRemoteAddr(),
                "PDI#" + response.getId(), null);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/agent/pdi/{id} — Consulter une PDI
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_AGENT', 'ROLE_ADMIN', 'ROLE_RESPONSABLE')")
    public ResponseEntity<PdiResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(pdiService.findById(id));
    }

    // GET /api/agent/pdi — Recherche paginée multicritères
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_AGENT', 'ROLE_ADMIN', 'ROLE_RESPONSABLE')")
    public ResponseEntity<PageResponse<PdiResponse>> rechercher(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String prenom,
            @RequestParam(required = false) StatutPDI statut,
            @RequestParam(required = false) Integer idSite,
            @RequestParam(required = false) Integer idRegion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int taille) {

        return ResponseEntity.ok(
                pdiService.rechercher(nom, prenom, statut, idSite, idRegion, page, taille));
    }

    // DELETE /api/admin/pdi/{id} — Supprimer une PDI (ADMIN uniquement)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> supprimer(@PathVariable Integer id, HttpServletRequest httpReq) {
        pdiService.supprimer(id);
        auditLogService.log("SUPPRESSION_PDI", httpReq.getRemoteAddr(), "PDI#" + id, null);
        return ResponseEntity.noContent().build();
    }

    // PUT /api/agent/pdi/{id} — Mettre à jour une PDI
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_AGENT', 'ROLE_ADMIN')")
    public ResponseEntity<PdiResponse> mettreAJour(@PathVariable Integer id,
                                                    @Valid @RequestBody UpdatePdiRequest req,
                                                    HttpServletRequest httpReq) {
        PdiResponse response = pdiService.mettreAJour(id, req);
        auditLogService.log("MODIFICATION_PDI", httpReq.getRemoteAddr(),
                "PDI#" + id, null);
        return ResponseEntity.ok(response);
    }
}
