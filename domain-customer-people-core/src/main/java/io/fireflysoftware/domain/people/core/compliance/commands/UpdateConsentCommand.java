package io.fireflysoftware.domain.people.core.compliance.commands;

import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

/**
 * Command to upsert (update) the consent record identified by
 * {@code (partyId, consentId)}. Carries the boolean {@code granted} flag and
 * an optional {@code applicationId} soft link to the originating application.
 * <p>
 * The translation from inbound domain-level status strings (e.g. GRANTED,
 * REVOKED) to the {@code granted} boolean is the responsibility of the caller
 * (typically the experience tier). This command intentionally exposes the
 * downstream contract — a boolean flag — to keep the domain handler thin.
 */
public record UpdateConsentCommand(
        UUID partyId,
        UUID consentId,
        boolean granted,
        UUID applicationId
) implements Command<Void> {}
