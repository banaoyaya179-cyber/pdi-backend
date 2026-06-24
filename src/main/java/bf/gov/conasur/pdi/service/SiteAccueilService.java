package bf.gov.conasur.pdi.service;

import bf.gov.conasur.pdi.dto.request.CreateSiteRequest;
import bf.gov.conasur.pdi.dto.response.SiteAccueilResponse;
import bf.gov.conasur.pdi.entity.Commune;
import bf.gov.conasur.pdi.entity.SiteAccueil;
import bf.gov.conasur.pdi.exception.ResourceNotFoundException;
import bf.gov.conasur.pdi.repository.CommuneRepository;
import bf.gov.conasur.pdi.repository.PdiRepository;
import bf.gov.conasur.pdi.repository.SiteAccueilRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SiteAccueilService {

    private final SiteAccueilRepository siteAccueilRepository;
    private final CommuneRepository communeRepository;
    private final PdiRepository pdiRepository;

    private final GeometryFactory geometryFactory =
            new GeometryFactory(new PrecisionModel(), 4326);

    public SiteAccueilResponse creer(CreateSiteRequest req) {
        Commune commune = communeRepository.findById(req.getIdCommune())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Commune introuvable : " + req.getIdCommune()));

        Point point = geometryFactory.createPoint(
                new Coordinate(req.getLongitude(), req.getLatitude()));

        SiteAccueil site = SiteAccueil.builder()
                .nomSite(req.getNomSite())
                .capaciteMaximale(req.getCapaciteMaximale())
                .commune(commune)
                .localisationGps(point)
                .build();

        SiteAccueil sauvegarde = siteAccueilRepository.save(site);
        long occupation = pdiRepository.countBySiteCourantId(sauvegarde.getId());
        return new SiteAccueilResponse(sauvegarde, occupation);
    }

    @Transactional(readOnly = true)
    public SiteAccueilResponse findById(Integer id) {
        SiteAccueil site = siteAccueilRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Site introuvable : " + id));
        long occupation = pdiRepository.countBySiteCourantId(id);
        return new SiteAccueilResponse(site, occupation);
    }

    @Transactional(readOnly = true)
    public List<SiteAccueilResponse> findAll() {
        return siteAccueilRepository.findAll().stream()
                .map(s -> new SiteAccueilResponse(s,
                        pdiRepository.countBySiteCourantId(s.getId())))
                .toList();
    }
}
