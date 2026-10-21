package io.fireflysoftware.domain.people.core.classification.commands;

import io.fireflysoftware.core.customer.sdk.model.PartyIndustryClassificationDTO;
import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

/**
 * Replaces an industry classification. Compensation writes the previous row back.
 */
public class UpdatePartyIndustryClassificationCommand extends PartyIndustryClassificationDTO implements Command<UUID> {

    private UUID classificationId;

    public UpdatePartyIndustryClassificationCommand withPartyId(UUID partyId) {
        this.setPartyId(partyId);
        return this;
    }

    public UpdatePartyIndustryClassificationCommand withClassificationId(UUID classificationId) {
        this.classificationId = classificationId;
        return this;
    }

    public UUID classificationId() {
        return classificationId;
    }
}
