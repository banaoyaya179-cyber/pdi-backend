package bf.gov.conasur.pdi.service;

import bf.gov.conasur.pdi.dto.request.CreateMenageRequest;
import bf.gov.conasur.pdi.dto.response.MenageResponse;
import bf.gov.conasur.pdi.entity.Menage;
import bf.gov.conasur.pdi.entity.Pdi;
import bf.gov.conasur.pdi.exception.ResourceNotFoundException;
import bf.gov.conasur.pdi.repository.MenageRepository;
import bf.gov.conasur.pdi.repository.PdiRepository;
import bf.gov.conasur.pdi.util.CodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MenageService {

    private final MenageRepository menageRepository;
    private final PdiRepository pdiRepository;
    private final CodeGenerator codeGenerator;

    // ===== CRÉER UN MÉNAGE =====
    public MenageResponse creer(CreateMenageRequest req) {
        String code = genererCodeUnique();

        Menage menage = Menage.builder()
                .codeUnique(code)
                .build();

        // Assigner chef de ménage si fourni
        if (req.getIdChefMenage() != null) {
            Pdi chef = pdiRepository.findById(req.getIdChefMenage())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "PDI introuvable : " + req.getIdChefMenage()));
            menage.setChefMenage(chef);
        }

        Menage sauvegarde = menageRepository.save(menage);
        List<Pdi> membres = pdiRepository.findByMenageId(sauvegarde.getId());
        return new MenageResponse(sauvegarde, membres);
    }

    // ===== CONSULTER UN MÉNAGE =====
    @Transactional(readOnly = true)
    public MenageResponse findById(Integer id) {
        Menage menage = getMenageOuErreur(id);
        List<Pdi> membres = pdiRepository.findByMenageId(id);
        return new MenageResponse(menage, membres);
    }

    // ===== DÉSIGNER CHEF DE MÉNAGE =====
    public MenageResponse designerChef(Integer idMenage, Integer idPdi) {
        Menage menage = getMenageOuErreur(idMenage);
        Pdi chef = pdiRepository.findById(idPdi)
                .orElseThrow(() -> new ResourceNotFoundException("PDI introuvable : " + idPdi));

        // Vérifier que la PDI appartient bien à ce ménage
        if (!chef.getMenage().getId().equals(idMenage)) {
            throw new IllegalArgumentException(
                    "La PDI #" + idPdi + " n'appartient pas au ménage #" + idMenage);
        }

        menage.setChefMenage(chef);
        Menage sauvegarde = menageRepository.save(menage);
        List<Pdi> membres = pdiRepository.findByMenageId(idMenage);
        return new MenageResponse(sauvegarde, membres);
    }

    // ===== HELPER =====
    private Menage getMenageOuErreur(Integer id) {
        return menageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ménage introuvable : " + id));
    }

    private String genererCodeUnique() {
        String code = codeGenerator.genererCodeMenage();
        // S'assurer de l'unicité
        while (menageRepository.existsByCodeUnique(code)) {
            code = codeGenerator.genererCodeMenage();
        }
        return code;
    }
}
