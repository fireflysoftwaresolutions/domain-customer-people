package io.fireflysoftware.domain.people.core.party.commands;

import org.fireflyframework.cqrs.command.Command;
import java.util.UUID;

public record RemovePartyProviderCommand(
    UUID partyId,
    UUID partyProviderId
) implements Command<Void> {}