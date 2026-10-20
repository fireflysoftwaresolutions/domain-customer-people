package io.fireflysoftware.domain.people.core.status.commands;

import org.fireflyframework.cqrs.command.Command;
import io.fireflysoftware.core.customer.sdk.model.PartyStatusDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateStatusCommand extends PartyStatusDTO implements Command<UUID> {

    /**
     * Sets the party ID for this command and returns the command instance for method chaining.
     * 
     * @param partyId the party ID to assign to this address command
     * @return this command instance for method chaining
     */
    public UpdateStatusCommand withPartyId(UUID partyId) {
        this.setPartyId(partyId);
        return this;
    }

    /**
     * Sets the status code for this command and returns the command instance for method chaining.
     *
     * @param statusCode the status code to assign to this command
     * @return this command instance for method chaining
     */
    public UpdateStatusCommand withStatusCode(PartyStatusDTO.StatusCodeEnum statusCode) {
        this.setStatusCode(statusCode);
        return this;
    }

    /**
     * Sets the reason for the status update and returns the command instance
     * for method chaining.
     *
     * @param reason the reason to assign to the status update
     * @return this command instance for method chaining
     */
    public UpdateStatusCommand withReason(String reason) {
        this.setStatusReason(reason);
        return this;
    }
}
