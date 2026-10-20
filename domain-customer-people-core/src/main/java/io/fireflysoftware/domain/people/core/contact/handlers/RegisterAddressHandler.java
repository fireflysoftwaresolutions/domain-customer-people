package io.fireflysoftware.domain.people.core.contact.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import io.fireflysoftware.core.customer.sdk.api.AddressesApi;
import io.fireflysoftware.core.customer.sdk.model.AddressDTO;
import io.fireflysoftware.domain.people.core.contact.commands.RegisterAddressCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class RegisterAddressHandler extends CommandHandler<RegisterAddressCommand, UUID> {

    private final AddressesApi addressesApi;

    public RegisterAddressHandler(AddressesApi addressesApi) {
        this.addressesApi = addressesApi;
    }

    @Override
    protected Mono<UUID> doHandle(RegisterAddressCommand cmd) {
        return addressesApi
                .createAddress(cmd.getPartyId(), cmd, UUID.randomUUID().toString())
                .mapNotNull(addressDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(addressDTO).getAddressId()));
    }
}