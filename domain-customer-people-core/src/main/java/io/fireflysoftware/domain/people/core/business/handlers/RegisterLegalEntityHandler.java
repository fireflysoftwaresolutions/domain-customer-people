package io.fireflysoftware.domain.people.core.business.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import io.fireflysoftware.core.customer.sdk.api.LegalEntitiesApi;
import io.fireflysoftware.core.customer.sdk.api.NaturalPersonsApi;
import io.fireflysoftware.domain.people.core.business.commands.RegisterLegalEntityCommand;
import io.fireflysoftware.domain.people.core.customer.commands.RegisterNaturalPersonCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class RegisterLegalEntityHandler extends CommandHandler<RegisterLegalEntityCommand, UUID> {

    private final LegalEntitiesApi legalEntitiesApi;

    public RegisterLegalEntityHandler(LegalEntitiesApi legalEntitiesApi) {
        this.legalEntitiesApi = legalEntitiesApi;
    }

    @Override
    protected Mono<UUID> doHandle(RegisterLegalEntityCommand cmd) {
        return legalEntitiesApi
                .createLegalEntity(cmd.getPartyId(), cmd, UUID.randomUUID().toString())
                .mapNotNull(legalEntityDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(legalEntityDTO)).getLegalEntityId());
    }
}