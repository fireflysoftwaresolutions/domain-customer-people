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


package io.fireflysoftware.domain.people.core.compliance.services;

import io.fireflysoftware.domain.people.core.compliance.commands.UpdateConsentCommand;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Service abstraction for compliance / consent operations exposed by
 * {@code domain-customer-people}.
 */
public interface ConsentService {

    /**
     * Upserts the consent record identified by {@code (partyId, consentId)}.
     * <p>
     * The {@code partyId} and {@code consentId} URL parameters are merged into
     * the supplied command body so callers do not need to set them on the body
     * themselves.
     *
     * @param partyId   owning party (URL parameter)
     * @param consentId consent record being updated (URL parameter)
     * @param command   the consent update payload
     * @return reactive completion signal
     */
    Mono<Void> updateConsent(UUID partyId, UUID consentId, UpdateConsentCommand command);
}
