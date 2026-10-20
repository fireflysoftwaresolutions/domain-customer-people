package io.fireflysoftware.domain.people.core.contact.commands;

import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

public record RemoveAddressCommand(
        UUID partyId,
        UUID addressId
) implements Command<Void> {}
