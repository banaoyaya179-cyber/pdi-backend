package bf.gov.conasur.pdi.service;

import bf.gov.conasur.pdi.dto.request.DeplacementRequest;
import bf.gov.conasur.pdi.dto.response.DeplacementResponse;
import bf.gov.conasur.pdi.entity.Deplacement;
import bf.gov.conasur.pdi.entity.Pdi;
import bf.gov.conasur.pdi.entity.SiteAccueil;
import bf.gov.conasur.pdi.enums.StatutPDI;
import bf.gov.conasur.pdi.exception.ResourceNotFoundException;
import bf.gov.conasur.pdi.repository.DeplacementRepository;
import bf.gov.conasur.pdi.repository.PdiRepository;
import bf.gov.conasur.pdi.repository.SiteAccueilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DeplacementService {

    private final DeplacementRepository deplacementRepository;
    private final PdiRepository pdiRepository;
    private final SiteAccueilRepository siteAccueilRepository;

    public List<DeplacementResponse> enregistrer(DeplacementRequest req) {
        Pdi pdi = pdiRepository.findById(req.getIdPdi())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "PDI introuvable : " + req.getIdPdi()));

        SiteAccueil destination = siteAccueilRepository.findById(req.getIdSiteDestination())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Site introuvable : " + req.getIdSiteDestination()));

        List<Pdi> pdisADeplacer = new ArrayList<>();

        if (req.isToutLeMenage()) {
            pdisADeplacer = pdiRepository.findByMenageId(pdi.getMenage().getId());
        } else {
            pdisADeplacer.add(pdi);
        }

        List<DeplacementResponse> resultats = new ArrayList<>();

        for (Pdi p : pdisADeplacer) {
            SiteAccueil origine = p.getSiteCourant();

            if (origine.getId().equals(destination.getId())) continue;

            Deplacement deplacement = Deplacement.builder()
                    .pdi(p)
                    .siteOrigine(origine)
                    .siteDestination(destination)
                    .motif(req.getMotif())
                    .build();

            deplacementRepository.save(deplacement);

            // Mettre à jour le site courant de la PDI
            p.setSiteCourant(destination);
            if (req.isToutLeMenage()) {
                p.setStatutCourant(StatutPDI.DEPLACE_MULTIPLE);
            }
            pdiRepository.save(p);

            resultats.add(new DeplacementResponse(deplacement));
        }

        return resultats;
    }

    @Transactional(readOnly = true)
    public List<DeplacementResponse> historiqueParPdi(Integer idPdi) {
        if (!pdiRepository.existsById(idPdi)) {
            throw new ResourceNotFoundException("PDI introuvable : " + idPdi);
        }
        return deplacementRepository.findByPdiIdOrderByDateMouvementDesc(idPdi)
                .stream().map(DeplacementResponse::new).toList();
    }
}
