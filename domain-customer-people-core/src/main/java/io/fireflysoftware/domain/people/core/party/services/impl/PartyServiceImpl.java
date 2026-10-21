package io.fireflysoftware.domain.people.core.party.services.impl;

import io.fireflysoftware.domain.people.core.party.commands.RegisterPartyRelationshipCommand;
import io.fireflysoftware.domain.people.core.party.commands.RemovePartyRelationshipCommand;
import io.fireflysoftware.domain.people.core.party.commands.UpdatePartyRelationshipCommand;
import io.fireflysoftware.domain.people.core.party.services.PartyService;
import io.fireflysoftware.domain.people.core.party.workflows.RegisterPartyRelationshipSaga;
import io.fireflysoftware.domain.people.core.party.workflows.UpdatePartyRelationshipSaga;
import io.fireflysoftware.domain.people.core.party.workflows.RemovePartyRelationshipSaga;
import org.fireflyframework.orchestration.saga.engine.SagaResult;
import org.fireflyframework.orchestration.saga.engine.SagaEngine;
import org.fireflyframework.orchestration.saga.engine.StepInputs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class PartyServiceImpl implements PartyService {

    private final SagaEngine engine;

    @Autowired
    public PartyServiceImpl(SagaEngine engine) {
        this.engine = engine;
    }


    @Override
    public Mono<SagaResult> registerPartyRelationship(RegisterPartyRelationshipCommand command) {
        StepInputs inputs = StepInputs.builder()
                .forStepId("registerPartyRelationship", command)
                .build();

        return engine.execute("RegisterPartyRelationshipSaga", inputs);
    }

    @Override
    public Mono<SagaResult> updatePartyRelationship(UpdatePartyRelationshipCommand command, UUID relationId) {
        UpdatePartyRelationshipCommand prepared = command.withPartyRelationshipId(relationId);
        StepInputs inputs = StepInputs.builder()
                .forStepId("readPartyRelationship", prepared)
                .forStepId("updatePartyRelationship", prepared)
                .build();

        return engine.execute("UpdatePartyRelationshipSaga", inputs);
    }

    @Override
    public Mono<SagaResult> removePartyRelationship(UUID relationId) {
        RemovePartyRelationshipCommand command = new RemovePartyRelationshipCommand(relationId);
        StepInputs inputs = StepInputs.builder()
                .forStepId("removePartyRelationship", command)
                .build();

        return engine.execute("RemovePartyRelationshipSaga", inputs);
    }

}
