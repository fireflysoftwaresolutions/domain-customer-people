package io.fireflysoftware.domain.people.core.contact.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import io.fireflysoftware.core.customer.sdk.api.AddressesApi;
import io.fireflysoftware.domain.people.core.contact.commands.RemoveAddressCommand;
import reactor.core.publisher.Mono;

import java.util.UUID;

@CommandHandlerComponent
public class RemoveAddressHandler extends CommandHandler<RemoveAddressCommand, Void> {

    private final AddressesApi addressesApi;

    public RemoveAddressHandler(AddressesApi addressesApi) {
        this.addressesApi = addressesApi;
    }

    @Override
    protected Mono<Void> doHandle(RemoveAddressCommand cmd) {
        return addressesApi
                .deleteAddress(cmd.partyId(), cmd.addressId(), UUID.randomUUID().toString())
                .then();
    }
}