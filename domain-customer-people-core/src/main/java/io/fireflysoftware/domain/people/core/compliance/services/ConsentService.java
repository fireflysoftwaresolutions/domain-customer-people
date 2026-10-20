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
