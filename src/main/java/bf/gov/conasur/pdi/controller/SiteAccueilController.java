package bf.gov.conasur.pdi.controller;

import bf.gov.conasur.pdi.dto.request.CreateSiteRequest;
import bf.gov.conasur.pdi.dto.response.SiteAccueilResponse;
import bf.gov.conasur.pdi.service.SiteAccueilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agent/sites")
@RequiredArgsConstructor
public class SiteAccueilController {

    private final SiteAccueilService siteAccueilService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SiteAccueilResponse> creer(@Valid @RequestBody CreateSiteRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(siteAccueilService.creer(req));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_AGENT', 'ROLE_ADMIN', 'ROLE_RESPONSABLE')")
    public ResponseEntity<List<SiteAccueilResponse>> findAll() {
        return ResponseEntity.ok(siteAccueilService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_AGENT', 'ROLE_ADMIN', 'ROLE_RESPONSABLE')")
    public ResponseEntity<SiteAccueilResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(siteAccueilService.findById(id));
    }
}
