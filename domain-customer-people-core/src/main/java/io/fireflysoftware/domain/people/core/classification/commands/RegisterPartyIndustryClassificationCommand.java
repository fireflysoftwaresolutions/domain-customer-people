package io.fireflysoftware.domain.people.core.classification.commands;

import io.fireflysoftware.core.customer.sdk.model.PartyIndustryClassificationDTO;
import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

/**
 * Stores an industry classification as a scheme and a code on a party.
 */
public class RegisterPartyIndustryClassificationCommand extends PartyIndustryClassificationDTO implements Command<UUID> {

    public RegisterPartyIndustryClassificationCommand withPartyId(UUID partyId) {
        this.setPartyId(partyId);
        return this;
    }
}
