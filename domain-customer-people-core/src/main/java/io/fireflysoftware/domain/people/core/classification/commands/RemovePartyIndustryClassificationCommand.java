package io.fireflysoftware.domain.people.core.classification.commands;

import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

/**
 * Deletes an industry classification. Compensation recreates the previous row.
 */
public record RemovePartyIndustryClassificationCommand(
        UUID partyId,
        UUID classificationId
) implements Command<Void> {
}
