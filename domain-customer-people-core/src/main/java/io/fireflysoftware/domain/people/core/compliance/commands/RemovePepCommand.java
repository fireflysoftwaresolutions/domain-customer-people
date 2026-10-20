package io.fireflysoftware.domain.people.core.compliance.commands;

import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

public record RemovePepCommand(
    UUID partyId,
    UUID pepId
) implements Command<Void> {}