package io.fireflysoftware.domain.people.core.attribute.commands;

import io.fireflysoftware.core.customer.sdk.model.PartyAttributeDTO;
import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

/**
 * Stores an effective-dated fact on a party, including employment, housing, loans, and debts.
 */
public class RegisterPartyAttributeCommand extends PartyAttributeDTO implements Command<UUID> {

    public RegisterPartyAttributeCommand withPartyId(UUID partyId) {
        this.setPartyId(partyId);
        return this;
    }
}
