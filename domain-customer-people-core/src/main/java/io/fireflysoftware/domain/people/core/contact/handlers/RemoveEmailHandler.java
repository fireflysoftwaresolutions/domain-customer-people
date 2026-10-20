package io.fireflysoftware.domain.people.core.contact.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import io.fireflysoftware.core.customer.sdk.api.EmailContactsApi;
import io.fireflysoftware.domain.people.core.contact.commands.RemoveEmailCommand;
import reactor.core.publisher.Mono;

import java.util.UUID;

@CommandHandlerComponent
public class RemoveEmailHandler extends CommandHandler<RemoveEmailCommand, Void> {

    private final EmailContactsApi emailContactsApi;

    public RemoveEmailHandler(EmailContactsApi emailContactsApi) {
        this.emailContactsApi = emailContactsApi;
    }

    @Override
    protected Mono<Void> doHandle(RemoveEmailCommand cmd) {
        return emailContactsApi
                .deleteEmailContact(cmd.partyId(), cmd.emailId(), UUID.randomUUID().toString())
                .then();
    }
}