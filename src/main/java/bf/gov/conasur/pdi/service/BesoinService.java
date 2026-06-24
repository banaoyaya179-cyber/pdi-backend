package bf.gov.conasur.pdi.service;

import bf.gov.conasur.pdi.dto.request.AideRequest;
import bf.gov.conasur.pdi.dto.request.BesoinRequest;
import bf.gov.conasur.pdi.dto.response.AideResponse;
import bf.gov.conasur.pdi.dto.response.BesoinResponse;
import bf.gov.conasur.pdi.entity.AideHumanitaire;
import bf.gov.conasur.pdi.entity.Besoin;
import bf.gov.conasur.pdi.entity.Menage;
import bf.gov.conasur.pdi.entity.Pdi;
import bf.gov.conasur.pdi.enums.StatutBesoin;
import bf.gov.conasur.pdi.exception.ResourceNotFoundException;
import bf.gov.conasur.pdi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BesoinService {

    private final BesoinRepository besoinRepository;
    private final AideHumanitaireRepository aideRepository;
    private final PdiRepository pdiRepository;
    private final MenageRepository menageRepository;

    // ===== DÉCLARER UN BESOIN =====
    public BesoinResponse declarer(BesoinRequest req) {
        if (req.getIdPdi() == null && req.getIdMenage() == null) {
            throw new IllegalArgumentException(
                    "Un besoin doit concerner une PDI ou un ménage.");
        }
        if (req.getIdPdi() != null && req.getIdMenage() != null) {
            throw new IllegalArgumentException(
                    "Un besoin ne peut pas concerner à la fois une PDI et un ménage.");
        }

        Besoin besoin = Besoin.builder()
                .categorie(req.getCategorie())
                .priorite(req.getPriorite())
                .build();

        if (req.getIdPdi() != null) {
            Pdi pdi = pdiRepository.findById(req.getIdPdi())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "PDI introuvable : " + req.getIdPdi()));
            besoin.setPdi(pdi);
        } else {
            Menage menage = menageRepository.findById(req.getIdMenage())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Ménage introuvable : " + req.getIdMenage()));
            besoin.setMenage(menage);
        }

        return new BesoinResponse(besoinRepository.save(besoin));
    }

    // ===== CLÔTURER UN BESOIN =====
    public BesoinResponse cloturer(Long id) {
        Besoin besoin = getBesoinOuErreur(id);
        besoin.setStatut(StatutBesoin.SATISFAIT);
        return new BesoinResponse(besoinRepository.save(besoin));
    }

    // ===== CONSULTER BESOINS D'UNE PDI =====
    @Transactional(readOnly = true)
    public List<BesoinResponse> findByPdi(Integer idPdi) {
        return besoinRepository.findByPdiId(idPdi)
                .stream().map(BesoinResponse::new).toList();
    }

    // ===== ENREGISTRER UNE AIDE =====
    public AideResponse enregistrerAide(AideRequest req) {
        Besoin besoin = getBesoinOuErreur(req.getIdBesoin());

        AideHumanitaire aide = AideHumanitaire.builder()
                .typeAide(req.getTypeAide())
                .quantite(req.getQuantite())
                .donateur(req.getDonateur())
                .besoin(besoin)
                .build();

        // Passer le besoin en cours de traitement
        if (besoin.getStatut() == StatutBesoin.DECLARE) {
            besoin.setStatut(StatutBesoin.EN_COURS);
            besoinRepository.save(besoin);
        }

        return new AideResponse(aideRepository.save(aide));
    }

    // ===== HISTORIQUE AIDES D'UN BESOIN =====
    @Transactional(readOnly = true)
    public List<AideResponse> historiqueAides(Long idBesoin) {
        return aideRepository.findByBesoinId(idBesoin)
                .stream().map(AideResponse::new).toList();
    }

    private Besoin getBesoinOuErreur(Long id) {
        return besoinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Besoin introuvable : " + id));
    }
}
