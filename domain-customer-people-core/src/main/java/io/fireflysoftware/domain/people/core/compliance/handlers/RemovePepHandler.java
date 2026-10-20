package io.fireflysoftware.domain.people.core.compliance.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import io.fireflysoftware.core.customer.sdk.api.PoliticallyExposedPersonsApi;
import io.fireflysoftware.domain.people.core.compliance.commands.RemovePepCommand;
import reactor.core.publisher.Mono;

import java.util.UUID;

@CommandHandlerComponent
public class RemovePepHandler extends CommandHandler<RemovePepCommand, Void> {

    private final PoliticallyExposedPersonsApi politicallyExposedPersonsApi;

    public RemovePepHandler(PoliticallyExposedPersonsApi politicallyExposedPersonsApi) {
        this.politicallyExposedPersonsApi = politicallyExposedPersonsApi;
    }

    @Override
    protected Mono<Void> doHandle(RemovePepCommand cmd) {
        return politicallyExposedPersonsApi
                .deletePoliticallyExposedPerson(cmd.partyId(), cmd.pepId(), UUID.randomUUID().toString())
                .then();
    }
}