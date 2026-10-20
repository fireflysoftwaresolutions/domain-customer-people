package io.fireflysoftware.domain.people.core.business.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import io.fireflysoftware.core.customer.sdk.api.LegalEntitiesApi;
import io.fireflysoftware.core.customer.sdk.api.NaturalPersonsApi;
import io.fireflysoftware.domain.people.core.business.commands.RemoveLegalEntityCommand;
import io.fireflysoftware.domain.people.core.customer.commands.RemoveNaturalPersonCommand;
import reactor.core.publisher.Mono;

import java.util.UUID;

@CommandHandlerComponent
public class RemoveLegalEntityHandler extends CommandHandler<RemoveLegalEntityCommand, Void> {

    private final LegalEntitiesApi legalEntitiesApi;

    public RemoveLegalEntityHandler(LegalEntitiesApi legalEntitiesApi) {
        this.legalEntitiesApi = legalEntitiesApi;
    }

    @Override
    protected Mono<Void> doHandle(RemoveLegalEntityCommand cmd) {
        return legalEntitiesApi.deleteLegalEntity(cmd.partyId(), cmd.legalEntityId(), UUID.randomUUID().toString()).then();
    }
}