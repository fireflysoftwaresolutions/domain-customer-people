package io.fireflysoftware.domain.people.core.status.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import io.fireflysoftware.core.customer.sdk.api.PartyStatusesApi;
import io.fireflysoftware.domain.people.core.status.commands.RemovePartyStatusEntryCommand;
import reactor.core.publisher.Mono;

import java.util.UUID;

@CommandHandlerComponent
public class RemovePartyStatusEntryHandler extends CommandHandler<RemovePartyStatusEntryCommand, Void> {

    private final PartyStatusesApi partyStatusesApi;

    public RemovePartyStatusEntryHandler(PartyStatusesApi partyStatusesApi) {
        this.partyStatusesApi = partyStatusesApi;
    }

    @Override
    protected Mono<Void> doHandle(RemovePartyStatusEntryCommand cmd) {
        return partyStatusesApi.deletePartyStatus(cmd.partyId(), cmd.partyStatusId(), UUID.randomUUID().toString()).then();
    }
}