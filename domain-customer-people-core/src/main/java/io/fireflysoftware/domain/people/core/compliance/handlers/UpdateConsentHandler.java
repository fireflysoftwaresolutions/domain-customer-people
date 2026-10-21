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


package io.fireflysoftware.domain.people.core.compliance.handlers;

import io.fireflysoftware.core.customer.sdk.api.ConsentsApi;
import io.fireflysoftware.core.customer.sdk.model.ConsentDTO;
import io.fireflysoftware.domain.people.core.compliance.commands.UpdateConsentCommand;
import io.fireflysoftware.domain.people.core.util.IdempotencyKeys;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import org.fireflyframework.web.error.exceptions.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Handles {@link UpdateConsentCommand} by upserting the consent record in the
 * customer-mgmt core service via {@link ConsentsApi#updateConsent}.
 * <p>
 * The downstream {@code ConsentDTO} body is validated server-side with
 * {@code consentTypeId} required (and other fields such as {@code channel}
 * are populated at creation time). A naive PUT that only carries
 * {@code (partyId, consentId, granted, applicationId)} would therefore fail
 * with HTTP&nbsp;400 ("Consent type ID is required"). To preserve the
 * immutable identity fields of the consent record while still letting the
 * caller flip the {@code granted} flag (and optionally attach an
 * {@code applicationId} soft link), the handler:
 * <ol>
 *   <li>Fetches the existing consent via {@link ConsentsApi#getConsentById(java.util.UUID, java.util.UUID, String)}.
 *       A 404 from core surfaces as {@code BusinessException(NOT_FOUND, "CONSENT_NOT_FOUND", ...)}.</li>
 *   <li>Mutates only the fields that the command intends to change:
 *       {@code granted} (always), {@code applicationId} (when non-null),
 *       {@code revokedAt} (stamped with {@code now()} when {@code granted=false}
 *       — PSD2 audit requirement), and {@code grantedAt} (stamped with
 *       {@code now()} when {@code granted=true} and the existing record had
 *       no grantedAt).</li>
 *   <li>Issues PUT {@link ConsentsApi#updateConsent(java.util.UUID, java.util.UUID, ConsentDTO, String)}
 *       with a deterministic idempotency key derived from
 *       {@code (partyId, consentId)} so retries collapse to the same
 *       downstream entry.</li>
 * </ol>
 */
@CommandHandlerComponent
public class UpdateConsentHandler extends CommandHandler<UpdateConsentCommand, Void> {

    private final ConsentsApi consentsApi;

    public UpdateConsentHandler(ConsentsApi consentsApi) {
        this.consentsApi = consentsApi;
    }

    @Override
    protected Mono<Void> doHandle(UpdateConsentCommand cmd) {
        return consentsApi.getConsentById(cmd.partyId(), cmd.consentId(), null)
                .onErrorMap(this::isNotFound, ex -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "CONSENT_NOT_FOUND",
                        "Consent " + cmd.consentId() + " not found for party " + cmd.partyId()))
                .switchIfEmpty(Mono.error(new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "CONSENT_NOT_FOUND",
                        "Consent " + cmd.consentId() + " not found for party " + cmd.partyId())))
                .map(existing -> mergeConsent(existing, cmd))
                .flatMap(merged -> {
                    String idempotencyKey = IdempotencyKeys.of(
                            "update-consent-merged",
                            cmd.partyId().toString(),
                            cmd.consentId().toString());
                    return consentsApi.updateConsent(
                            cmd.partyId(), cmd.consentId(), merged, idempotencyKey);
                })
                .then();
    }

    /**
     * Applies the command's intended mutations to the existing consent DTO,
     * preserving immutable fields (notably {@code consentTypeId}, {@code channel})
     * that core requires on the PUT body.
     */
    private ConsentDTO mergeConsent(ConsentDTO existing, UpdateConsentCommand cmd) {
        existing.setGranted(cmd.granted());
        if (cmd.applicationId() != null) {
            existing.setApplicationId(cmd.applicationId());
        }
        LocalDateTime now = LocalDateTime.now();
        if (cmd.granted()) {
            // PSD2 audit: stamp grantedAt only when transitioning to granted
            // and the existing record has no grantedAt yet, so re-affirmations
            // do not silently overwrite the original grant timestamp.
            if (existing.getGrantedAt() == null) {
                existing.setGrantedAt(now);
            }
        } else {
            // PSD2 audit: every revocation must carry a fresh revokedAt.
            existing.setRevokedAt(now);
        }
        return existing;
    }

    private boolean isNotFound(Throwable ex) {
        return ex instanceof WebClientResponseException wcre
                && wcre.getStatusCode().value() == HttpStatus.NOT_FOUND.value();
    }
}
