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


package io.fireflysoftware.domain.people.web.controller;

import io.fireflysoftware.domain.people.core.compliance.queries.ConsentCatalogQuery;
import io.fireflysoftware.domain.people.core.compliance.queries.responses.ConsentCatalogResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.query.QueryBus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Read-side endpoint that surfaces the platform-wide consent catalogue
 * to the experience layer.
 * <p>
 * The catalogue itself is owned by {@code core-common-reference-master-data};
 * this controller projects it into a journey-friendly shape (drops audit
 * metadata, sorts by display order) and lets callers narrow the result by
 * product so onboarding flows can render only the consents that apply to
 * their journey.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/consents/catalog")
@RequiredArgsConstructor
@Tag(name = "Consent Catalog", description = "Query endpoint for the active consent catalogue")
public class ConsentCatalogQueryController {

    private final QueryBus queryBus;

    @GetMapping
    @Operation(
            summary = "Get active consent catalog",
            description = "Returns every active consent template, optionally filtered by the "
                    + "product the channel is presenting. Global consents (no applicableProduct) "
                    + "are always included so terms-of-service and privacy entries don't require "
                    + "a separate call."
    )
    public Mono<ResponseEntity<List<ConsentCatalogResponse>>> getConsentCatalog(
            @Parameter(description = "Optional product filter, e.g. PERSONAL_LOAN or LEASING")
            @RequestParam(value = "applicableProduct", required = false) String applicableProduct) {
        log.debug("GET /api/v1/consents/catalog?applicableProduct={}", applicableProduct);
        return queryBus.<List<ConsentCatalogResponse>>query(
                        ConsentCatalogQuery.builder().applicableProduct(applicableProduct).build())
                .map(ResponseEntity::ok);
    }
}
