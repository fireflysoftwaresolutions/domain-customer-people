package io.fireflysoftware.domain.people.core.classification.workflows;

import io.fireflysoftware.core.customer.sdk.model.PartyIndustryClassificationDTO;
import io.fireflysoftware.domain.people.core.classification.commands.RegisterPartyIndustryClassificationCommand;
import io.fireflysoftware.domain.people.core.classification.commands.RemovePartyIndustryClassificationCommand;
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

@Saga(name = "RemoveIndustryClassificationSaga")
@Service
public class RemoveIndustryClassificationSaga {

    private final CommandBus commandBus;
    private final QueryBus queryBus;

    public RemoveIndustryClassificationSaga(CommandBus commandBus, QueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }

    @SagaStep(id = "readIndustryClassificationForDelete")
    @StepEvent(type = "industryClassification.read")
    public Mono<PartyIndustryClassificationDTO> readIndustryClassification(
            RemovePartyIndustryClassificationCommand cmd, ExecutionContext ctx) {
        return queryBus.query(new IndustryClassificationQuery(cmd.partyId(), cmd.classificationId()))
                .doOnNext(previous -> ctx.putVariable("previousClassification", previous));
    }

    @SagaStep(id = "removeIndustryClassification", dependsOn = "readIndustryClassificationForDelete",
            compensate = "restoreRemovedIndustryClassification")
    @StepEvent(type = "industryClassification.removed")
    public Mono<Void> removeIndustryClassification(RemovePartyIndustryClassificationCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd);
    }

    public Mono<Void> restoreRemovedIndustryClassification(Void ignored, ExecutionContext ctx) {
        PartyIndustryClassificationDTO previous = ctx.getVariableAs("previousClassification", PartyIndustryClassificationDTO.class);
        if (previous == null) {
            return Mono.empty();
        }
        RegisterPartyIndustryClassificationCommand restore = new RegisterPartyIndustryClassificationCommand();
        BeanUtils.copyProperties(previous, restore);
        return commandBus.send(restore).then();
    }
}
