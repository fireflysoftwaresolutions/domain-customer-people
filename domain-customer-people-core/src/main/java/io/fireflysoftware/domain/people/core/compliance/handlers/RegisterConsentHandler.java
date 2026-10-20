package io.fireflysoftware.domain.people.core.compliance.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import io.fireflysoftware.core.customer.sdk.api.ConsentsApi;
import io.fireflysoftware.core.customer.sdk.model.ConsentDTO;
import io.fireflysoftware.domain.people.core.compliance.commands.RegisterConsentCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class RegisterConsentHandler extends CommandHandler<RegisterConsentCommand, UUID> {

    private final ConsentsApi consentsApi;

    public RegisterConsentHandler(ConsentsApi consentsApi) {
        this.consentsApi = consentsApi;
    }

    @Override
    protected Mono<UUID> doHandle(RegisterConsentCommand cmd) {
        return consentsApi
                .createConsent(cmd.getPartyId(), cmd, UUID.randomUUID().toString())
                .mapNotNull(consentDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(consentDTO).getConsentId()));
    }
}