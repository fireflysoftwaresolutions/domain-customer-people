package io.fireflysoftware.domain.people.core.customer.workflows;

import io.fireflysoftware.core.customer.sdk.model.NaturalPersonDTO;
import io.fireflysoftware.domain.people.core.customer.commands.UpdateCustomerCommand;
import io.fireflysoftware.domain.people.core.customer.queries.NaturalPersonQuery;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.cqrs.query.QueryBus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.EVENT_CUSTOMER_CHANGED;
import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.SAGA_UPDATE_CUSTOMER_SAGA;
import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.STEP_UPDATE_CUSTOMER;

/**
 * Updates a natural person and restores the previous record if a later step fails.
 * Occupation and monthly income stay on the person. Employment, housing, loans, and
 * debts are party attributes.
 */
@Saga(name = SAGA_UPDATE_CUSTOMER_SAGA)
@Service
public class UpdateCustomerSaga {

    private final CommandBus commandBus;
    private final QueryBus queryBus;

    @Autowired
    public UpdateCustomerSaga(CommandBus commandBus, QueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }

    @SagaStep(id = "readCustomer")
    @StepEvent(type = "customer.read")
    public Mono<NaturalPersonDTO> readCustomer(UpdateCustomerCommand cmd, ExecutionContext ctx) {
        return queryBus.query(NaturalPersonQuery.builder().partyId(cmd.getPartyId()).build())
                .doOnNext(previous -> ctx.putVariable("previousCustomer", previous));
    }

    @SagaStep(id = STEP_UPDATE_CUSTOMER, dependsOn = "readCustomer", compensate = "restoreCustomer")
    @StepEvent(type = EVENT_CUSTOMER_CHANGED)
    public Mono<UUID> updateCustomer(UpdateCustomerCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd);
    }

    public Mono<Void> restoreCustomer(UUID naturalPersonId, ExecutionContext ctx) {
        NaturalPersonDTO previous = ctx.getVariableAs("previousCustomer", NaturalPersonDTO.class);
        if (previous == null) {
            return Mono.empty();
        }
        UpdateCustomerCommand restore = new UpdateCustomerCommand();
        BeanUtils.copyProperties(previous, restore);
        return commandBus.send(restore).then();
    }
}
