package io.fireflysoftware.domain.people.core.contact.workflows;

import io.fireflysoftware.core.customer.sdk.api.AddressesApi;
import io.fireflysoftware.core.customer.sdk.model.AddressDTO;
import io.fireflysoftware.domain.people.core.contact.commands.UpdateAddressCommand;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.EVENT_ADDRESS_UPDATED;
import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.SAGA_UPDATE_ADDRESS_NAME;
import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.STEP_UPDATE_ADDRESS;

/**
 * Updates an address and restores the previous row if a later step fails.
 */
@Saga(name = SAGA_UPDATE_ADDRESS_NAME)
@Service
public class UpdateAddressSaga {

    private final CommandBus commandBus;
    private final AddressesApi addressesApi;

    @Autowired
    public UpdateAddressSaga(CommandBus commandBus, AddressesApi addressesApi) {
        this.commandBus = commandBus;
        this.addressesApi = addressesApi;
    }

    @SagaStep(id = "readAddress")
    @StepEvent(type = "address.read")
    public Mono<AddressDTO> readAddress(UpdateAddressCommand cmd, ExecutionContext ctx) {
        return addressesApi.getAddressById(cmd.getPartyId(), cmd.getAddressId(), UUID.randomUUID().toString())
                .doOnNext(previous -> ctx.putVariable("previousAddress", previous));
    }

    @SagaStep(id = STEP_UPDATE_ADDRESS, dependsOn = "readAddress", compensate = "restoreAddress")
    @StepEvent(type = EVENT_ADDRESS_UPDATED)
    public Mono<UUID> updateAddress(UpdateAddressCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd);
    }

    public Mono<Void> restoreAddress(UUID addressId, ExecutionContext ctx) {
        AddressDTO previous = ctx.getVariableAs("previousAddress", AddressDTO.class);
        if (previous == null) {
            return Mono.empty();
        }
        UpdateAddressCommand restore = new UpdateAddressCommand();
        BeanUtils.copyProperties(previous, restore);
        restore.withPartyId(previous.getPartyId());
        restore.withAddressId(previous.getAddressId() != null ? previous.getAddressId() : addressId);
        return commandBus.send(restore).then();
    }
}
