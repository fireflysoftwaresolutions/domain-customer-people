package io.fireflysoftware.domain.people.core.attribute.commands;

import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

public record RemovePartyAttributeCommand(
        UUID partyId,
        UUID attributeId
) implements Command<Void> {
}
