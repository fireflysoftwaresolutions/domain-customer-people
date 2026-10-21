package io.fireflysoftware.domain.people.core.attribute.commands;

import io.fireflysoftware.core.customer.sdk.model.PartyAttributeDTO;
import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

public class UpdatePartyAttributeCommand extends PartyAttributeDTO implements Command<UUID> {

    private UUID attributeId;

    public UpdatePartyAttributeCommand withPartyId(UUID partyId) {
        this.setPartyId(partyId);
        return this;
    }

    public UpdatePartyAttributeCommand withAttributeId(UUID attributeId) {
        this.attributeId = attributeId;
        return this;
    }

    public UUID attributeId() {
        return attributeId;
    }
}
