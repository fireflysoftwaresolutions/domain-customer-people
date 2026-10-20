package io.fireflysoftware.domain.people.core.contact.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import io.fireflysoftware.core.customer.sdk.api.PhoneContactsApi;
import io.fireflysoftware.domain.people.core.contact.commands.UpdatePhoneCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class UpdatePhoneHandler extends CommandHandler<UpdatePhoneCommand, UUID> {

    private final PhoneContactsApi phoneContactsApi;

    public UpdatePhoneHandler(PhoneContactsApi phoneContactsApi) {
        this.phoneContactsApi = phoneContactsApi;
    }

    @Override
    protected Mono<UUID> doHandle(UpdatePhoneCommand cmd) {
        return phoneContactsApi.updatePhoneContact(cmd.getPartyId(), cmd.getPhoneContactId(), cmd, UUID.randomUUID().toString())
                .mapNotNull(phoneContactDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(phoneContactDTO).getPhoneContactId()));
    }
}