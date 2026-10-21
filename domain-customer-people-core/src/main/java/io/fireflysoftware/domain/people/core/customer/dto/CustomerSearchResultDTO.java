/*
 * Copyright 2025 Firefly Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
