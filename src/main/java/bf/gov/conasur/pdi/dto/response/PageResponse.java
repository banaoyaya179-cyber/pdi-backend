package bf.gov.conasur.pdi.dto.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

@Getter
public class PageResponse<T> {

    private final List<T> contenu;
    private final int pageActuelle;
    private final int totalPages;
    private final long totalElements;
    private final boolean dernierePage;

    public PageResponse(Page<?> page, Function<Object, T> mapper) {
        this.contenu = page.getContent().stream()
                .map(mapper)
                .toList();
        this.pageActuelle = page.getNumber();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.dernierePage = page.isLast();
    }
}
