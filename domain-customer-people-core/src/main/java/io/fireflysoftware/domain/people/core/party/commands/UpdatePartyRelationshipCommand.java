package io.fireflysoftware.domain.people.core.party.commands;

import org.fireflyframework.cqrs.command.Command;
import io.fireflysoftware.core.customer.sdk.model.PartyRelationshipDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdatePartyRelationshipCommand extends PartyRelationshipDTO implements Command<UUID> {
    private UUID partyRelationshipId;

    public UpdatePartyRelationshipCommand withPartyRelationshipId(UUID relationshipId) {
        this.setPartyRelationshipId(relationshipId);
        return this;
    }
}
