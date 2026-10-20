package io.fireflysoftware.domain.people.core.contact.commands;

import org.fireflyframework.cqrs.command.Command;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdatePreferredChannelCommand implements Command<Void>{

    UpdateEmailCommand emailCommand;
    UpdatePhoneCommand phoneCommand;
    private UUID partyId;

    public UpdatePreferredChannelCommand withPartyId(UUID partyId) {
        this.setPartyId(partyId);
        return this;
    }

}
