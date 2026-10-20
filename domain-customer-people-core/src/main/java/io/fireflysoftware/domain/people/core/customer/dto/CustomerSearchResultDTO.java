package io.fireflysoftware.domain.people.core.customer.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Paginated result of a customer search, where each row is an
 * {@link EnrichedPartyDTO} (the bare party plus its natural-person or
 * legal-entity association). Pagination metadata mirrors the upstream
 * {@code PaginationResponsePartyDTO} produced by core-common-customer-mgmt.
 */
@Data
@Builder
public class CustomerSearchResultDTO {

    private List<EnrichedPartyDTO> content;
    private Long totalElements;
    private Integer totalPages;
    private Integer currentPage;
}
