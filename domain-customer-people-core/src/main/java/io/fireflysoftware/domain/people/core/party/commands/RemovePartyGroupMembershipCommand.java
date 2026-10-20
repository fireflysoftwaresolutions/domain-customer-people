package io.fireflysoftware.domain.people.core.party.commands;

import org.fireflyframework.cqrs.command.Command;
import java.util.UUID;

public record RemovePartyGroupMembershipCommand(
    UUID partyId,
    UUID partyGroupMembershipId
) implements Command<Void> {}