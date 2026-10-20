package io.fireflysoftware.domain.people.core.compliance.commands;

import org.fireflyframework.cqrs.command.Command;
import io.fireflysoftware.core.customer.sdk.model.ConsentDTO;

import java.time.LocalDateTime;
import java.util.UUID;

public class RegisterConsentCommand extends ConsentDTO implements Command<UUID> {
    
    /**
     * Sets the party ID for this command and returns the command instance for method chaining.
     * 
     * @param partyId the party ID to assign to this consent command
     * @return this command instance for method chaining
     */
    public RegisterConsentCommand withPartyId(UUID partyId) {
        this.setPartyId(partyId);
        return this;
    }
}
