package io.fireflysoftware.domain.people.core.status.commands;

import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

public record RemovePartyStatusEntryCommand(
        UUID partyId,
        UUID partyStatusId
) implements Command<Void> {}