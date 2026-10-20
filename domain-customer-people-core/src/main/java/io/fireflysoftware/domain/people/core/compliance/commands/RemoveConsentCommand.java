package io.fireflysoftware.domain.people.core.compliance.commands;

import org.fireflyframework.cqrs.command.Command;
import java.util.UUID;

public record RemoveConsentCommand(
    UUID partyId,
    UUID consentId
) implements Command<Void> {}