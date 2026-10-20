package io.fireflysoftware.domain.people.core.party.commands;

import org.fireflyframework.cqrs.command.Command;
import java.util.UUID;

public record RemovePartyRelationshipCommand(
    UUID partyRelationshipId
) implements Command<Void> {}