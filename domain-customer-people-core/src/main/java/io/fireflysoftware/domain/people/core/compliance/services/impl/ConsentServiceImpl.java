package io.fireflysoftware.domain.people.core.compliance.services.impl;

import io.fireflysoftware.domain.people.core.compliance.commands.UpdateConsentCommand;
import io.fireflysoftware.domain.people.core.compliance.services.ConsentService;
import org.fireflyframework.orchestration.saga.engine.SagaEngine;
import org.fireflyframework.orchestration.saga.engine.StepInputs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.SAGA_UPDATE_CONSENT_NAME;
import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.STEP_UPDATE_CONSENT;

@Service
public class ConsentServiceImpl implements ConsentService {

    private final SagaEngine engine;

    @Autowired
    public ConsentServiceImpl(SagaEngine engine) {
        this.engine = engine;
    }

    @Override
    public Mono<Void> updateConsent(UUID partyId, UUID consentId, UpdateConsentCommand command) {
        UpdateConsentCommand merged = new UpdateConsentCommand(
                partyId,
                consentId,
                command.granted(),
                command.applicationId()
        );

        StepInputs inputs = StepInputs.builder()
                .forStepId(STEP_UPDATE_CONSENT, merged)
                .build();

        return engine.execute(SAGA_UPDATE_CONSENT_NAME, inputs).then();
    }
}
