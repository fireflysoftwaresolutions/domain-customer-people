package io.fireflysoftware.domain.people.core.business.commands;

import org.fireflyframework.cqrs.command.Command;
import io.fireflysoftware.core.customer.sdk.model.LegalEntityDTO;
import io.fireflysoftware.core.customer.sdk.model.NaturalPersonDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class RegisterLegalEntityCommand extends LegalEntityDTO implements Command<UUID> {


    /**
     * Sets the party ID for the legal entity and returns the current command instance.
     *
     * @param partyId the unique identifier of the party to associate with the legal entity
     * @return the current instance of {@code RegisterLegalEntityCommand} with the party ID set
     */
    public RegisterLegalEntityCommand withPartyId(UUID partyId) {
        this.setPartyId(partyId);
        return this;
    }
}
