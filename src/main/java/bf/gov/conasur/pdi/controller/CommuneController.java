package bf.gov.conasur.pdi.controller;

import bf.gov.conasur.pdi.entity.Commune;
import bf.gov.conasur.pdi.repository.CommuneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/communes")
@RequiredArgsConstructor
public class CommuneController {

    private final CommuneRepository communeRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> findAll() {
        List<Map<String, Object>> result = communeRepository.findAll()
            .stream()
            .map(c -> Map.<String, Object>of(
                "id",          c.getId(),
                "nomCommune",  c.getNomCommune(),
                "codeCommune", c.getCodeCommune(),
                "province",    c.getProvince().getNomProvince(),
                "region",      c.getProvince().getRegion().getNomRegion()
            ))
            .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}
