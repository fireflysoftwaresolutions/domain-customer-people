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

import io.fireflysoftware.core.customer.sdk.model.LegalEntityDTO;
import io.fireflysoftware.core.customer.sdk.model.NaturalPersonDTO;
import io.fireflysoftware.core.customer.sdk.model.PartyDTO;
import lombok.Builder;
import lombok.Data;

/**
 * A {@link PartyDTO} enriched with its associated natural-person or legal-entity
 * record. Exactly one of the two association fields is populated for a given
 * party (based on {@code party.partyKind}); the other is {@code null}. Both
 * may be {@code null} if the enrichment lookup failed or no association exists
 * yet — the bare {@code party} is always preserved.
 */
@Data
@Builder
public class EnrichedPartyDTO {

    private PartyDTO party;
    private NaturalPersonDTO naturalPerson;
    private LegalEntityDTO legalEntity;
}
