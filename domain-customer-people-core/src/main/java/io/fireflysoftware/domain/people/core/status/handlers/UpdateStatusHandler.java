package io.fireflysoftware.domain.people.core.status.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import io.fireflysoftware.core.customer.sdk.api.PartyStatusesApi;
import io.fireflysoftware.domain.people.core.status.commands.UpdateStatusCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class UpdateStatusHandler extends CommandHandler<UpdateStatusCommand, UUID> {

    private final PartyStatusesApi partyStatusesApi;

    public UpdateStatusHandler(PartyStatusesApi partyStatusesApi) {
        this.partyStatusesApi = partyStatusesApi;
    }

    @Override
    protected Mono<UUID> doHandle(UpdateStatusCommand cmd) {
        return partyStatusesApi.updatePartyStatus(cmd.getPartyId(), cmd, UUID.randomUUID().toString())
                .mapNotNull(partyStatusDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(partyStatusDTO).getPartyStatusId()));
    }
}