package io.fireflysoftware.domain.people.core.classification.services;

import io.fireflysoftware.core.customer.sdk.model.PartyAncestorDTO;
import io.fireflysoftware.core.customer.sdk.model.PartyRelationshipDTO;
import io.fireflysoftware.domain.people.core.attribute.commands.RegisterPartyAttributeCommand;
import io.fireflysoftware.domain.people.core.attribute.commands.RemovePartyAttributeCommand;
import io.fireflysoftware.domain.people.core.attribute.commands.UpdatePartyAttributeCommand;
import io.fireflysoftware.domain.people.core.classification.commands.RegisterPartyIndustryClassificationCommand;
import io.fireflysoftware.domain.people.core.classification.commands.RemovePartyIndustryClassificationCommand;
import io.fireflysoftware.domain.people.core.classification.commands.UpdatePartyIndustryClassificationCommand;
import io.fireflysoftware.domain.people.core.hierarchy.queries.PartyAncestorsQuery;
import io.fireflysoftware.domain.people.core.hierarchy.queries.PartyOwnersQuery;
import io.fireflysoftware.domain.people.core.hierarchy.queries.PartySubsidiariesQuery;
import org.fireflyframework.cqrs.query.QueryBus;
import org.fireflyframework.orchestration.saga.engine.SagaEngine;
import org.fireflyframework.orchestration.saga.engine.SagaResult;
import org.fireflyframework.orchestration.saga.engine.StepInputs;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
public class PartyFactsService {

    private final SagaEngine engine;
    private final QueryBus queryBus;

    public PartyFactsService(SagaEngine engine, QueryBus queryBus) {
        this.engine = engine;
        this.queryBus = queryBus;
    }

    public Mono<SagaResult> registerClassification(RegisterPartyIndustryClassificationCommand command) {
        return engine.execute("RegisterIndustryClassificationSaga", StepInputs.builder()
                .forStepId("registerIndustryClassification", command)
                .build());
    }

    public Mono<SagaResult> updateClassification(UpdatePartyIndustryClassificationCommand command) {
        return engine.execute("UpdateIndustryClassificationSaga", StepInputs.builder()
                .forStepId("readIndustryClassification", command)
                .forStepId("updateIndustryClassification", command)
                .build());
    }

    public Mono<SagaResult> removeClassification(RemovePartyIndustryClassificationCommand command) {
        return engine.execute("RemoveIndustryClassificationSaga", StepInputs.builder()
                .forStepId("readIndustryClassificationForDelete", command)
                .forStepId("removeIndustryClassification", command)
                .build());
    }

    public Mono<SagaResult> registerAttribute(RegisterPartyAttributeCommand command) {
        return engine.execute("RegisterPartyAttributeSaga", StepInputs.builder()
                .forStepId("registerPartyAttribute", command)
                .build());
    }

    public Mono<SagaResult> updateAttribute(UpdatePartyAttributeCommand command) {
        return engine.execute("UpdatePartyAttributeSaga", StepInputs.builder()
                .forStepId("readPartyAttribute", command)
                .forStepId("updatePartyAttribute", command)
                .build());
    }

    public Mono<SagaResult> removeAttribute(RemovePartyAttributeCommand command) {
        return engine.execute("RemovePartyAttributeSaga", StepInputs.builder()
                .forStepId("readPartyAttributeForDelete", command)
                .forStepId("removePartyAttribute", command)
                .build());
    }

    public Mono<List<PartyRelationshipDTO>> owners(UUID partyId) {
        return queryBus.query(new PartyOwnersQuery(partyId));
    }

    public Mono<List<PartyRelationshipDTO>> subsidiaries(UUID partyId) {
        return queryBus.query(new PartySubsidiariesQuery(partyId));
    }

    public Mono<List<PartyAncestorDTO>> ancestors(UUID partyId, int depth) {
        return queryBus.query(new PartyAncestorsQuery(partyId, depth));
    }
}
