package bf.gov.conasur.pdi.controller;

import bf.gov.conasur.pdi.dto.response.AuditLogResponse;
import bf.gov.conasur.pdi.dto.response.PageResponse;
import bf.gov.conasur.pdi.entity.AuditLog;
import bf.gov.conasur.pdi.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/audit")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping
    public ResponseEntity<PageResponse<AuditLogResponse>> getLogs(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int taille) {

        String actionPattern = (action != null && !action.isBlank()) ? "%" + action + "%" : null;
        String emailPattern = (email != null && !email.isBlank()) ? "%" + email + "%" : null;

        Page<AuditLog> resultats = auditLogRepository.filtrer(
                action, actionPattern, email, emailPattern,
                PageRequest.of(page, taille));

        return ResponseEntity.ok(new PageResponse<>(resultats, o -> new AuditLogResponse((AuditLog) o)));
    }
}
