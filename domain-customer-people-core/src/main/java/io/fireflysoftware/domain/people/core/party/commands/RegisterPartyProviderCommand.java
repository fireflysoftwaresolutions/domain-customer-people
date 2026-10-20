package io.fireflysoftware.domain.people.core.party.commands;

import org.fireflyframework.cqrs.command.Command;
import io.fireflysoftware.core.customer.sdk.model.PartyProviderDTO;

import java.util.UUID;

public class RegisterPartyProviderCommand extends PartyProviderDTO implements Command<UUID> {
    
    /**
     * Sets the party ID for this command and returns the command instance for method chaining.
     * 
     * @param partyId the party ID to assign to this party provider command
     * @return this command instance for method chaining
     */
    public RegisterPartyProviderCommand withPartyId(UUID partyId) {
        this.setPartyId(partyId);
        return this;
    }
}
