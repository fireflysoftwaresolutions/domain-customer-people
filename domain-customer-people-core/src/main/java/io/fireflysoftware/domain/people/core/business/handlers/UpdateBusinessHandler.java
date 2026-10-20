package io.fireflysoftware.domain.people.core.business.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import io.fireflysoftware.core.customer.sdk.api.LegalEntitiesApi;
import io.fireflysoftware.core.customer.sdk.api.NaturalPersonsApi;
import io.fireflysoftware.domain.people.core.business.commands.UpdateBusinessCommand;
import io.fireflysoftware.domain.people.core.customer.commands.UpdateCustomerCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class UpdateBusinessHandler extends CommandHandler<UpdateBusinessCommand, UUID> {

    private final LegalEntitiesApi legalEntitiesApi;

    public UpdateBusinessHandler(LegalEntitiesApi legalEntitiesApi) {
        this.legalEntitiesApi = legalEntitiesApi;
    }

    @Override
    protected Mono<UUID> doHandle(UpdateBusinessCommand cmd) {
        return legalEntitiesApi.updateLegalEntity(cmd.getPartyId(), cmd.getLegalEntityId(), cmd, UUID.randomUUID().toString())
                .mapNotNull(legalEntityDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(legalEntityDTO).getLegalEntityId()));
    }
}