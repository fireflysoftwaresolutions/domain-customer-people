package io.fireflysoftware.domain.people.core.compliance.commands;

import org.fireflyframework.cqrs.command.Command;
import java.util.UUID;

public record RemoveEconomicActivityLinkCommand(
    UUID partyId,
    UUID economicActivityLinkId
) implements Command<Void> {}