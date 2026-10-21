package io.fireflysoftware.domain.people.core.business.services.impl;

import io.fireflysoftware.domain.people.core.business.commands.RegisterBusinessCommand;
import io.fireflysoftware.domain.people.core.business.commands.RegisterLegalEntityCommand;
import io.fireflysoftware.domain.people.core.business.commands.UpdateBusinessCommand;
import io.fireflysoftware.domain.people.core.party.commands.RegisterPartyCommand;
import org.fireflyframework.cqrs.query.QueryBus;
import org.fireflyframework.orchestration.saga.engine.SagaEngine;
import org.fireflyframework.orchestration.saga.engine.SagaResult;
import org.fireflyframework.orchestration.saga.engine.StepInputs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BusinessServiceImpl Tests")
class BusinessServiceImplTest {

    @Mock
    private SagaEngine sagaEngine;

    @Mock
    private QueryBus queryBus;

    @Mock
    private SagaResult sagaResult;

    private BusinessServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new BusinessServiceImpl(sagaEngine, queryBus);
    }

    @Test
    @DisplayName("Should register business successfully")
    void testRegisterBusiness_ShouldExecuteSaga() {
        RegisterBusinessCommand command = mock(RegisterBusinessCommand.class);
        when(sagaEngine.execute(eq("RegisterCustomerSaga"), any(StepInputs.class)))
                .thenReturn(Mono.just(sagaResult));

        StepVerifier.create(service.registerBusiness(command))
                .expectNext(sagaResult)
                .verifyComplete();

        verify(sagaEngine).execute(eq("RegisterCustomerSaga"), any(StepInputs.class));
    }

    @Test
    @DisplayName("Should update business successfully")
    void testUpdateBusiness_ShouldExecuteSaga() {
        UpdateBusinessCommand command = mock(UpdateBusinessCommand.class);
        when(sagaEngine.execute(eq("UpdateBusinessSaga"), any(StepInputs.class)))
                .thenReturn(Mono.just(sagaResult));

        StepVerifier.create(service.updateBusiness(command))
                .expectNext(sagaResult)
                .verifyComplete();

        verify(sagaEngine).execute(eq("UpdateBusinessSaga"), any(StepInputs.class));
    }

    /**
     * Defect B (sibling guard): a minimal RegisterBusinessCommand with all collection
     * fields left null (statusHistory, identityDocuments, addresses, emails, phones,
     * economicActivities, providers, relationships, groupMemberships) must NOT trigger
     * an NPE when expanding into saga step inputs. The service must defensively default
     * the lists before calling {@code ExpandEach.of(...)}.
     */
    @Test
    @DisplayName("Should not throw NPE when all optional collections are null")
    void registerBusiness_doesNotThrowNpe_whenOptionalCollectionsAreNull() {
        RegisterPartyCommand party = RegisterPartyCommand.builder().build();
        RegisterLegalEntityCommand legalEntity = new RegisterLegalEntityCommand();

        RegisterBusinessCommand command = new RegisterBusinessCommand(
                party,
                legalEntity,
                null, // statusHistory
                null, // identityDocuments
                null, // addresses
                null, // emails
                null, // phones
                null, // economicActivities
                null, // providers
                null, // relationships
                null, // groupMemberships
                null, // industryClassifications
                null  // attributes
        );

        when(sagaEngine.execute(eq("RegisterCustomerSaga"), any(StepInputs.class)))
                .thenReturn(Mono.just(sagaResult));

        StepVerifier.create(service.registerBusiness(command))
                .expectNext(sagaResult)
                .verifyComplete();

        ArgumentCaptor<StepInputs> captor = ArgumentCaptor.forClass(StepInputs.class);
        verify(sagaEngine).execute(eq("RegisterCustomerSaga"), captor.capture());
        // Reaching this point without an NPE during builder execution proves the fix.
        assertThat(captor.getValue()).isNotNull();
    }
}
