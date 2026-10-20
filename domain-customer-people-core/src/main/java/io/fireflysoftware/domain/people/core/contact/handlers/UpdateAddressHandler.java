package io.fireflysoftware.domain.people.core.contact.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import io.fireflysoftware.core.customer.sdk.api.AddressesApi;
import io.fireflysoftware.core.customer.sdk.api.NaturalPersonsApi;
import io.fireflysoftware.domain.people.core.contact.commands.UpdateAddressCommand;
import io.fireflysoftware.domain.people.core.customer.commands.UpdateCustomerCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class UpdateAddressHandler extends CommandHandler<UpdateAddressCommand, UUID> {

    private final AddressesApi addressesApi;

    public UpdateAddressHandler(AddressesApi addressesApi) {
        this.addressesApi = addressesApi;
    }

    @Override
    protected Mono<UUID> doHandle(UpdateAddressCommand cmd) {
        return addressesApi.updateAddress(cmd.getPartyId(), cmd.getAddressId(), cmd, UUID.randomUUID().toString())
                .mapNotNull(addressDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(addressDTO).getAddressId()));
    }
}