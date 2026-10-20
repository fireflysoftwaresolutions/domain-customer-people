package io.fireflysoftware.domain.people.core.party.commands;

import org.fireflyframework.cqrs.command.Command;
import io.fireflysoftware.core.customer.sdk.model.PartyGroupMembershipDTO;

import java.util.UUID;

public class RegisterPartyGroupMembershipCommand extends PartyGroupMembershipDTO implements Command<UUID> {
    
    /**
     * Sets the party ID for this command and returns the command instance for method chaining.
     * 
     * @param partyId the party ID to assign to this party group membership command
     * @return this command instance for method chaining
     */
    public RegisterPartyGroupMembershipCommand withPartyId(UUID partyId) {
        this.setPartyId(partyId);
        return this;
    }
}
