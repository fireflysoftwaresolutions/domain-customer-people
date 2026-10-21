package io.fireflysoftware.domain.people.core.classification.workflows;

import io.fireflysoftware.core.customer.sdk.model.PartyIndustryClassificationDTO;
import io.fireflysoftware.domain.people.core.classification.commands.UpdatePartyIndustryClassificationCommand;
import io.fireflysoftware.domain.people.core.classification.queries.IndustryClassificationQuery;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.cqrs.query.QueryBus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Saga(name = "UpdateIndustryClassificationSaga")
@Service
public class UpdateIndustryClassificationSaga {

    private final CommandBus commandBus;
    private final QueryBus queryBus;

    public UpdateIndustryClassificationSaga(CommandBus commandBus, QueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }

    @SagaStep(id = "readIndustryClassification")
    @StepEvent(type = "industryClassification.read")
    public Mono<PartyIndustryClassificationDTO> readIndustryClassification(
            UpdatePartyIndustryClassificationCommand cmd, ExecutionContext ctx) {
        return queryBus.query(new IndustryClassificationQuery(cmd.getPartyId(), cmd.classificationId()))
                .doOnNext(previous -> ctx.putVariable("previousClassification", previous));
    }

    @SagaStep(id = "updateIndustryClassification", dependsOn = "readIndustryClassification", compensate = "restoreIndustryClassification")
    @StepEvent(type = "industryClassification.updated")
    public Mono<UUID> updateIndustryClassification(UpdatePartyIndustryClassificationCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd);
    }

    public Mono<Void> restoreIndustryClassification(UUID classificationId, ExecutionContext ctx) {
        PartyIndustryClassificationDTO previous = ctx.getVariableAs("previousClassification", PartyIndustryClassificationDTO.class);
        if (previous == null) {
            return Mono.empty();
        }
        UpdatePartyIndustryClassificationCommand restore = new UpdatePartyIndustryClassificationCommand();
        BeanUtils.copyProperties(previous, restore);
        return commandBus.send(restore).then();
    }
}
