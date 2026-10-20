package io.fireflysoftware.domain.people.core.customer.commands;

import org.fireflyframework.cqrs.command.Command;
import io.fireflysoftware.core.customer.sdk.model.NaturalPersonDTO;

import java.util.UUID;

public class RegisterNaturalPersonCommand extends NaturalPersonDTO implements Command<UUID> {
    
    /**
     * Sets the party ID for this command and returns the command instance for method chaining.
     * 
     * @param partyId the party ID to assign to this natural person command
     * @return this command instance for method chaining
     */
    public RegisterNaturalPersonCommand withPartyId(UUID partyId) {
        this.setPartyId(partyId);
        return this;
    }
}
