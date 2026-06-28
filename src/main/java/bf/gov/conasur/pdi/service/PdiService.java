package bf.gov.conasur.pdi.service;

import bf.gov.conasur.pdi.dto.request.EnrolementPdiRequest;
import bf.gov.conasur.pdi.dto.request.UpdatePdiRequest;
import bf.gov.conasur.pdi.dto.response.PageResponse;
import bf.gov.conasur.pdi.dto.response.PdiResponse;
import bf.gov.conasur.pdi.entity.Menage;
import bf.gov.conasur.pdi.entity.Pdi;
import bf.gov.conasur.pdi.entity.SiteAccueil;
import bf.gov.conasur.pdi.enums.StatutPDI;
import bf.gov.conasur.pdi.exception.DoublonException;
import bf.gov.conasur.pdi.exception.ResourceNotFoundException;
import bf.gov.conasur.pdi.repository.AideHumanitaireRepository;
import bf.gov.conasur.pdi.repository.BesoinRepository;
import bf.gov.conasur.pdi.repository.DeplacementRepository;
import bf.gov.conasur.pdi.repository.MenageRepository;
import bf.gov.conasur.pdi.repository.PdiRepository;
import bf.gov.conasur.pdi.repository.SiteAccueilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PdiService {

    private final PdiRepository pdiRepository;
    private final MenageRepository menageRepository;
    private final SiteAccueilRepository siteAccueilRepository;
    private final DeplacementRepository deplacementRepository;
    private final BesoinRepository besoinRepository;
    private final AideHumanitaireRepository aideHumanitaireRepository;

    public PdiResponse enroler(EnrolementPdiRequest req) {
        if (pdiRepository.existsByNomIgnoreCaseAndPrenomIgnoreCaseAndDateNaissance(
                req.getNom(), req.getPrenom(), req.getDateNaissance())) {
            throw new DoublonException(
                "Une PDI avec le nom '" + req.getNom() + " " + req.getPrenom()
                + "' née le " + req.getDateNaissance() + " existe déjà dans le système.");
        }

        Menage menage = menageRepository.findById(req.getIdMenage())
                .orElseThrow(() -> new ResourceNotFoundException("Ménage introuvable : " + req.getIdMenage()));

        SiteAccueil site = siteAccueilRepository.findById(req.getIdSiteCourant())
                .orElseThrow(() -> new ResourceNotFoundException("Site introuvable : " + req.getIdSiteCourant()));

        Pdi pdi = Pdi.builder()
                .nom(req.getNom().toUpperCase().trim())
                .prenom(req.getPrenom().trim())
                .sexe(req.getSexe())
                .dateNaissance(req.getDateNaissance())
                .statutCourant(req.getStatutCourant())
                .menage(menage)
                .siteCourant(site)
                .build();

        return new PdiResponse(pdiRepository.save(pdi));
    }

    @Transactional(readOnly = true)
    public PdiResponse findById(Integer id) {
        return new PdiResponse(getPdiOuErreur(id));
    }

    @Transactional(readOnly = true)
    public PageResponse<PdiResponse> rechercher(String nom, String prenom,
            StatutPDI statut, Integer idSite, Integer idRegion,
            int page, int taille) {

        Pageable pageable = PageRequest.of(page, taille, Sort.by("nom").ascending());

        // Construire les patterns de recherche
        String nomPattern = (nom != null && !nom.isBlank()) ? "%" + nom.toUpperCase() + "%" : null;
        String prenomPattern = (prenom != null && !prenom.isBlank()) ? "%" + prenom + "%" : null;

        Page<Pdi> resultats = pdiRepository.rechercherPdi(
                nom, nomPattern, prenom, prenomPattern,
                statut, idSite, idRegion, pageable);

        return new PageResponse<>(resultats, o -> new PdiResponse((Pdi) o));
    }

    public PdiResponse mettreAJour(Integer id, UpdatePdiRequest req) {
        Pdi pdi = getPdiOuErreur(id);

        SiteAccueil site = siteAccueilRepository.findById(req.getIdSiteCourant())
                .orElseThrow(() -> new ResourceNotFoundException("Site introuvable : " + req.getIdSiteCourant()));

        pdi.setStatutCourant(req.getStatutCourant());
        pdi.setSiteCourant(site);

        if (req.getIdMenage() != null) {
            Menage menage = menageRepository.findById(req.getIdMenage())
                    .orElseThrow(() -> new ResourceNotFoundException("Ménage introuvable : " + req.getIdMenage()));
            pdi.setMenage(menage);
        }

        return new PdiResponse(pdiRepository.save(pdi));
    }

    public void supprimer(Integer id) {
        Pdi pdi = getPdiOuErreur(id);
        aideHumanitaireRepository.deleteAll(
            besoinRepository.findByPdiId(id).stream()
                .flatMap(b -> aideHumanitaireRepository.findByBesoinId(b.getId()).stream())
                .toList()
        );
        besoinRepository.deleteByPdiId(id);
        deplacementRepository.deleteByPdiId(id);
        pdiRepository.delete(pdi);
    }

    private Pdi getPdiOuErreur(Integer id) {
        return pdiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PDI introuvable : " + id));
    }
}
