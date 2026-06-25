package bf.gov.conasur.pdi.controller;

import bf.gov.conasur.pdi.dto.response.NotificationResponse;
import bf.gov.conasur.pdi.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/non-lues")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESPONSABLE')")
    public ResponseEntity<List<NotificationResponse>> getNonLues() {
        notificationService.verifierSaturation();
        return ResponseEntity.ok(notificationService.getNonLues());
    }

    @GetMapping("/toutes")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESPONSABLE')")
    public ResponseEntity<List<NotificationResponse>> getToutes() {
        return ResponseEntity.ok(notificationService.getToutes());
    }

    @GetMapping("/count")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESPONSABLE')")
    public ResponseEntity<Map<String, Long>> count() {
        return ResponseEntity.ok(Map.of("nonLues", notificationService.countNonLues()));
    }

    @PutMapping("/{id}/lue")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESPONSABLE')")
    public ResponseEntity<Void> marquerLue(@PathVariable Long id) {
        notificationService.marquerLue(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/toutes-lues")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESPONSABLE')")
    public ResponseEntity<Void> marquerToutesLues() {
        notificationService.marquerToutesLues();
        return ResponseEntity.ok().build();
    }
}
