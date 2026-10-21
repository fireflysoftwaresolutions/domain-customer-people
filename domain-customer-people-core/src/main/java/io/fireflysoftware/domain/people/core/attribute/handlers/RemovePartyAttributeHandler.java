package io.fireflysoftware.domain.people.core.attribute.handlers;

import io.fireflysoftware.core.customer.sdk.api.PartyAttributesApi;
import io.fireflysoftware.domain.people.core.attribute.commands.RemovePartyAttributeCommand;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;

import java.util.UUID;

@CommandHandlerComponent
public class RemovePartyAttributeHandler extends CommandHandler<RemovePartyAttributeCommand, Void> {

    private final PartyAttributesApi partyAttributesApi;

    public RemovePartyAttributeHandler(PartyAttributesApi partyAttributesApi) {
        this.partyAttributesApi = partyAttributesApi;
    }

    @Override
    protected Mono<Void> doHandle(RemovePartyAttributeCommand cmd) {
        return partyAttributesApi
                .delete1(cmd.partyId(), cmd.attributeId(), UUID.randomUUID().toString())
                .then();
    }
}
