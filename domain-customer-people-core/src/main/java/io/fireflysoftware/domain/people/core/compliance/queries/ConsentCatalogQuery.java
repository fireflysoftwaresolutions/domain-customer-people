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


package io.fireflysoftware.domain.people.core.compliance.queries;

import io.fireflysoftware.domain.people.core.compliance.queries.responses.ConsentCatalogResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fireflyframework.cqrs.query.Query;

import java.util.List;

/**
 * Query the active consent catalogue maintained by
 * {@code core-common-reference-master-data}.
 * <p>
 * When {@link #applicableProduct} is set, only catalogue entries that target
 * that product (or that have no product affinity at all) are returned. The
 * filter is intentionally permissive so global consents (terms of service,
 * privacy policy) are always surfaced alongside product-specific ones.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsentCatalogQuery implements Query<List<ConsentCatalogResponse>> {

    /** Optional product filter, e.g. {@code PERSONAL_LOAN} or {@code LEASING}. */
    private String applicableProduct;

    /**
     * The framework's default cache key derives only from the class name + metadata
     * hash, so two requests with different {@code applicableProduct} values would
     * collide and the first variant cached would be served forever. Folding the
     * filter value into the key partitions the cache per product (and ALL for the
     * unfiltered variant) so each scope keeps its own entry.
     */
    @Override
    public String getCacheKey() {
        if (!isCacheable()) {
            return null;
        }
        return "ConsentCatalogQuery:" + (applicableProduct == null ? "ALL" : applicableProduct);
    }

}
