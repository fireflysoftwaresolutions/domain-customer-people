package io.fireflysoftware.domain.people.core.party.commands;

import org.fireflyframework.cqrs.command.Command;
import io.fireflysoftware.core.customer.sdk.model.PartyDTO;

import java.util.UUID;

public record RemovePartyCommand(
        UUID partyId
) implements Command<Void>{}
