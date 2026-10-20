package io.fireflysoftware.domain.people.core.contact.commands;

import org.fireflyframework.cqrs.command.Command;
import io.fireflysoftware.core.customer.sdk.model.AddressDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateAddressCommand extends AddressDTO implements Command<UUID> {

    private UUID addressId;

    /**
     * Sets the party ID for this command and returns the command instance for method chaining.
     * 
     * @param partyId the party ID to assign to this address command
     * @return this command instance for method chaining
     */
    public UpdateAddressCommand withPartyId(UUID partyId) {
        this.setPartyId(partyId);
        return this;
    }

    /**
     * Sets the address ID for this command and returns the command instance for method chaining.
     * 
     * @param addressId the address ID to assign to this address command
     * @return this command instance for method chaining
     */
    public UpdateAddressCommand withAddressId(UUID addressId) {
        this.setAddressId(addressId);
        return this;
    }

}
