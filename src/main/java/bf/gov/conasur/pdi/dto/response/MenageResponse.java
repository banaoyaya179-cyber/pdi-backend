package bf.gov.conasur.pdi.dto.response;

import bf.gov.conasur.pdi.entity.Menage;
import bf.gov.conasur.pdi.entity.Pdi;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class MenageResponse {

    private final Integer id;
    private final String codeUnique;
    private final LocalDate dateCreation;
    private final Integer idChefMenage;
    private final String nomChefMenage;
    private final List<PdiResponse> membres;
    private final int tailleMenage;

    public MenageResponse(Menage m, List<Pdi> membres) {
        this.id = m.getId();
        this.codeUnique = m.getCodeUnique();
        this.dateCreation = m.getDateCreation();

        if (m.getChefMenage() != null) {
            this.idChefMenage = m.getChefMenage().getId();
            this.nomChefMenage = m.getChefMenage().getNom()
                    + " " + m.getChefMenage().getPrenom();
        } else {
            this.idChefMenage = null;
            this.nomChefMenage = null;
        }

        this.membres = membres.stream().map(PdiResponse::new).toList();
        this.tailleMenage = membres.size();
    }
}
