package io.fireflysoftware.domain.people.core.compliance.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import io.fireflysoftware.core.customer.sdk.api.ConsentsApi;
import io.fireflysoftware.domain.people.core.compliance.commands.RemoveConsentCommand;
import reactor.core.publisher.Mono;

import java.util.UUID;

@CommandHandlerComponent
public class RemoveConsentHandler extends CommandHandler<RemoveConsentCommand, Void> {

    private final ConsentsApi consentsApi;

    public RemoveConsentHandler(ConsentsApi consentsApi) {
        this.consentsApi = consentsApi;
    }

    @Override
    protected Mono<Void> doHandle(RemoveConsentCommand cmd) {
        return consentsApi
                .deleteConsent(cmd.partyId(), cmd.consentId(), UUID.randomUUID().toString())
                .then();
    }
}