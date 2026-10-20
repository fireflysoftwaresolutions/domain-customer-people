package io.fireflysoftware.domain.people.core.contact.commands;

import org.fireflyframework.cqrs.command.Command;
import java.util.UUID;

public record RemoveEmailCommand(
    UUID partyId,
    UUID emailId
) implements Command<Void> {}