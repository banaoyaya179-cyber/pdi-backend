package bf.gov.conasur.pdi.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class CodeGenerator {

    private final AtomicInteger compteur = new AtomicInteger(1);

    public String genererCodeMenage() {
        int annee = LocalDate.now().getYear();
        int numero = compteur.getAndIncrement();
        return String.format("MEN-%d-%04d", annee, numero);
    }
}
