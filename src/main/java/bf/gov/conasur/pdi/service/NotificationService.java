package bf.gov.conasur.pdi.service;

import bf.gov.conasur.pdi.dto.response.NotificationResponse;
import bf.gov.conasur.pdi.entity.Notification;
import bf.gov.conasur.pdi.entity.SiteAccueil;
import bf.gov.conasur.pdi.exception.ResourceNotFoundException;
import bf.gov.conasur.pdi.repository.NotificationRepository;
import bf.gov.conasur.pdi.repository.PdiRepository;
import bf.gov.conasur.pdi.repository.SiteAccueilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SiteAccueilRepository siteAccueilRepository;
    private final PdiRepository pdiRepository;

    // Vérifier et générer des alertes pour tous les sites
    public void verifierSaturation() {
        List<SiteAccueil> sites = siteAccueilRepository.findAll();
        for (SiteAccueil site : sites) {
            long occupation = pdiRepository.countBySiteCourantId(site.getId());
            double taux = site.getCapaciteMaximale() > 0
                    ? (occupation * 100.0 / site.getCapaciteMaximale()) : 0;

            if (taux >= 100) {
                // Vérifier si une alerte non lue existe déjà pour ce site
                boolean dejaAlertee = notificationRepository
                        .findByLueFalseOrderByDateCreationDesc()
                        .stream()
                        .anyMatch(n -> n.getSite() != null
                                && n.getSite().getId().equals(site.getId())
                                && n.getMessage().contains("saturé"));

                if (!dejaAlertee) {
                    Notification notif = Notification.builder()
                            .message("⚠️ ALERTE : Le site « " + site.getNomSite()
                                    + " » est saturé (" + occupation + "/"
                                    + site.getCapaciteMaximale() + " PDI). "
                                    + "Action immédiate requise.")
                            .site(site)
                            .build();
                    notificationRepository.save(notif);
                }
            } else if (taux >= 70) {
                boolean dejaAlertee = notificationRepository
                        .findByLueFalseOrderByDateCreationDesc()
                        .stream()
                        .anyMatch(n -> n.getSite() != null
                                && n.getSite().getId().equals(site.getId())
                                && n.getMessage().contains("critique"));

                if (!dejaAlertee) {
                    Notification notif = Notification.builder()
                            .message("🔶 ATTENTION : Le site « " + site.getNomSite()
                                    + " » atteint un taux critique de "
                                    + Math.round(taux) + "% ("
                                    + occupation + "/" + site.getCapaciteMaximale() + " PDI).")
                            .site(site)
                            .build();
                    notificationRepository.save(notif);
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNonLues() {
        return notificationRepository.findByLueFalseOrderByDateCreationDesc()
                .stream().map(NotificationResponse::new).toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getToutes() {
        return notificationRepository.findAllByOrderByDateCreationDesc()
                .stream().map(NotificationResponse::new).toList();
    }

    @Transactional(readOnly = true)
    public long countNonLues() {
        return notificationRepository.countByLueFalse();
    }

    public void marquerLue(Long id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification introuvable : " + id));
        n.setLue(true);
        notificationRepository.save(n);
    }

    public void marquerToutesLues() {
        notificationRepository.findByLueFalseOrderByDateCreationDesc()
                .forEach(n -> { n.setLue(true); notificationRepository.save(n); });
    }
}
