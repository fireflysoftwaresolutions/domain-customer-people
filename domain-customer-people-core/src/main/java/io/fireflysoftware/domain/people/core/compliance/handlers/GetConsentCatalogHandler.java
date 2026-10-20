package io.fireflysoftware.domain.people.core.compliance.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fireflysoftware.common.reference.master.data.sdk.api.ConsentCatalogApi;
import io.fireflysoftware.common.reference.master.data.sdk.model.ConsentCatalogDTO;
import io.fireflysoftware.common.reference.master.data.sdk.model.PaginationResponse;
import io.fireflysoftware.domain.people.core.compliance.queries.ConsentCatalogQuery;
import io.fireflysoftware.domain.people.core.compliance.queries.responses.ConsentCatalogResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import org.fireflyframework.web.error.exceptions.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Reads the active consents from {@code core-common-reference-master-data}
 * and projects them to {@link ConsentCatalogResponse}, applying the
 * optional product filter from the query.
 *
 * <p>Inactive entries are dropped server-side so callers never have to
 * second-guess the {@code status} field. Results are sorted by
 * {@code sortOrder} ascending so the channel can render them as-is.
 */
@Slf4j
@RequiredArgsConstructor
@QueryHandlerComponent
public class GetConsentCatalogHandler
        extends QueryHandler<ConsentCatalogQuery, List<ConsentCatalogResponse>> {

    private static final int CATALOG_PAGE_SIZE = 200;

    private final ConsentCatalogApi consentCatalogApi;
    private final ObjectMapper objectMapper;

    @Override
    protected Mono<List<ConsentCatalogResponse>> doHandle(ConsentCatalogQuery query) {
        log.debug("Fetching consent catalogue for applicableProduct={}", query.getApplicableProduct());
        return consentCatalogApi
                .listConsentCatalog(0, CATALOG_PAGE_SIZE, null, null, UUID.randomUUID().toString())
                .onErrorMap(WebClientRequestException.class, ex -> new BusinessException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "CONSENT_CATALOG_UNAVAILABLE",
                        "Consent catalog service unavailable: " + ex.getMessage()))
                .flatMapMany(this::extractContent)
                .filter(dto -> dto.getStatus() == ConsentCatalogDTO.StatusEnum.ACTIVE)
                .filter(dto -> matchesProduct(dto, query.getApplicableProduct()))
                .map(this::toResponse)
                .sort(Comparator.comparing(
                        ConsentCatalogResponse::getOrder,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collectList();
    }

    private Flux<ConsentCatalogDTO> extractContent(PaginationResponse page) {
        if (page == null || page.getContent() == null) {
            return Flux.empty();
        }
        return Flux.fromIterable(page.getContent())
                .map(item -> objectMapper.convertValue(item, ConsentCatalogDTO.class));
    }

    /**
     * Lets the catalogue surface global consents (no {@code applicableProduct})
     * alongside product-scoped ones so the channel does not need a second call
     * for the universal terms-of-service / privacy entries.
     */
    private static boolean matchesProduct(ConsentCatalogDTO dto, String requestedProduct) {
        if (requestedProduct == null) {
            return true;
        }
        String entryProduct = dto.getApplicableProduct();
        return entryProduct == null || Objects.equals(entryProduct, requestedProduct);
    }

    private ConsentCatalogResponse toResponse(ConsentCatalogDTO dto) {
        return ConsentCatalogResponse.builder()
                .consentId(dto.getConsentId())
                .consentType(dto.getConsentType())
                .description(dto.getConsentDescription())
                .version(dto.getConsentVersion())
                .required(Boolean.TRUE.equals(dto.getIsRequired()))
                .order(dto.getSortOrder())
                .applicableProduct(dto.getApplicableProduct())
                .build();
    }
}
