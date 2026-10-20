package io.fireflysoftware.domain.people.core.contact.commands;

import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

public record RemoveIdentityDocumentCommand(
        UUID partyId,
        UUID identityDocumentId
) implements Command<Void> {}