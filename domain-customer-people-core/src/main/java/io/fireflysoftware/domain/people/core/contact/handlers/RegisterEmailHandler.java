package io.fireflysoftware.domain.people.core.contact.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import io.fireflysoftware.core.customer.sdk.api.EmailContactsApi;
import io.fireflysoftware.core.customer.sdk.model.EmailContactDTO;
import io.fireflysoftware.domain.people.core.contact.commands.RegisterEmailCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class RegisterEmailHandler extends CommandHandler<RegisterEmailCommand, UUID> {

    private final EmailContactsApi emailContactsApi;

    public RegisterEmailHandler(EmailContactsApi emailContactsApi) {
        this.emailContactsApi = emailContactsApi;
    }

    @Override
    protected Mono<UUID> doHandle(RegisterEmailCommand cmd) {
        return emailContactsApi
                .createEmailContact(cmd.getPartyId(), cmd, UUID.randomUUID().toString())
                .mapNotNull(emailContactDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(emailContactDTO).getEmailContactId()));
    }
}