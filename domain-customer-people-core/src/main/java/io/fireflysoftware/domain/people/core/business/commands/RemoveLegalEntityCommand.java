package io.fireflysoftware.domain.people.core.business.commands;

import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

public record RemoveLegalEntityCommand(
        UUID partyId,
        UUID legalEntityId
) implements Command<Void>{}