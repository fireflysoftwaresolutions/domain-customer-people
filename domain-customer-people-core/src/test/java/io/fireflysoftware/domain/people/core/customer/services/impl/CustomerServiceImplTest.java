package io.fireflysoftware.domain.people.core.customer.services.impl;

import org.fireflyframework.cqrs.query.QueryBus;
import io.fireflysoftware.domain.people.core.customer.commands.RegisterCustomerCommand;
import io.fireflysoftware.domain.people.core.customer.commands.UpdateCustomerCommand;
import io.fireflysoftware.domain.people.core.customer.workflows.RegisterCustomerSaga;
import io.fireflysoftware.domain.people.core.customer.workflows.UpdateCustomerSaga;
import org.fireflyframework.orchestration.saga.engine.SagaResult;
import org.fireflyframework.orchestration.saga.engine.SagaEngine;
import org.fireflyframework.orchestration.saga.engine.StepInputs;
import io.fireflysoftware.domain.people.core.party.commands.RegisterPartyCommand;
import io.fireflysoftware.domain.people.core.customer.commands.RegisterNaturalPersonCommand;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerServiceImpl Tests")
class CustomerServiceImplTest {

    @Mock
    private SagaEngine sagaEngine;

    @Mock
    private QueryBus queryBus;

    @Mock
    private SagaResult sagaResult;

    private CustomerServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CustomerServiceImpl(sagaEngine, queryBus);
    }

    @Test
    @DisplayName("Should register customer successfully")
    void testRegisterCustomer_ShouldExecuteSaga() {
        // Given
        RegisterCustomerCommand command = mock(RegisterCustomerCommand.class);
        when(sagaResult.isSuccess()).thenReturn(true);
        when(sagaEngine.execute(eq("RegisterCustomerSaga"), any(StepInputs.class)))
                .thenReturn(Mono.just(sagaResult));

        // When
        Mono<SagaResult> result = service.registerCustomer(command);

        // Then
        StepVerifier.create(result)
                .expectNext(sagaResult)
                .verifyComplete();

        verify(sagaEngine).execute(eq("RegisterCustomerSaga"), any(StepInputs.class));
    }

    @Test
    @DisplayName("Should update customer successfully")
    void testUpdateCustomer_ShouldExecuteSaga() {
        // Given
        UpdateCustomerCommand command = mock(UpdateCustomerCommand.class);
        when(sagaResult.isSuccess()).thenReturn(true);
        when(sagaEngine.execute(eq("UpdateCustomerSaga"), any(StepInputs.class)))
                .thenReturn(Mono.just(sagaResult));

        // When
        Mono<SagaResult> result = service.updateCustomer(command);

        // Then
        StepVerifier.create(result)
                .expectNext(sagaResult)
                .verifyComplete();

        verify(sagaEngine).execute(eq("UpdateCustomerSaga"), any(StepInputs.class));
    }

    @Test
    @DisplayName("Should propagate saga failure as a reactive error")
    void testRegisterCustomer_ShouldPropagateSagaFailure() {
        // Given a saga that completes but reports isSuccess=false (dead-lettered step).
        // Without explicit propagation, the controller would return 200 with no body,
        // masking the failure to BFF callers.
        RegisterCustomerCommand command = mock(RegisterCustomerCommand.class);
        RuntimeException sagaError = new RuntimeException("registerParty failed");
        when(sagaResult.isSuccess()).thenReturn(false);
        when(sagaResult.error()).thenReturn(java.util.Optional.of(sagaError));
        when(sagaEngine.execute(eq("RegisterCustomerSaga"), any(StepInputs.class)))
                .thenReturn(Mono.just(sagaResult));

        StepVerifier.create(service.registerCustomer(command))
                .expectErrorMatches(thrown -> thrown == sagaError)
                .verify();
    }


    @Test
    @DisplayName("Should handle saga execution errors")
    void testRegisterCustomer_ShouldHandleErrors() {
        // Given
        RegisterCustomerCommand command = mock(RegisterCustomerCommand.class);
        RuntimeException error = new RuntimeException("Saga execution failed");
        when(sagaEngine.execute(eq("RegisterCustomerSaga"), any(StepInputs.class)))
                .thenReturn(Mono.error(error));

        // When
        Mono<SagaResult> result = service.registerCustomer(command);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(sagaEngine).execute(eq("RegisterCustomerSaga"), any(StepInputs.class));
    }

    /**
     * Defect B: a minimal RegisterCustomerCommand with all collection fields left null
     * (statusHistory, identityDocuments, addresses, emails, phones, economicActivities,
     * consents, providers, relationships, groupMemberships) must NOT trigger an NPE
     * when expanding into saga step inputs. The service must defensively default the
     * lists before calling {@code ExpandEach.of(...)}. This mirrors the Defect-2 fix
     * applied to {@code LoanOriginationServiceImpl.submitApplication}.
     */
    @Test
    @DisplayName("Should not throw NPE when all optional collections are null")
    void registerCustomer_doesNotThrowNpe_whenOptionalCollectionsAreNull() {
        RegisterPartyCommand party = RegisterPartyCommand.builder().build();
        RegisterNaturalPersonCommand naturalPerson = new RegisterNaturalPersonCommand();

        // All ten collection fields are intentionally null — this is exactly what
        // the IndividualOnboardingWorkflow.registerParty path posts when the
        // experience tier carries only party + naturalPerson on the inbound request.
        RegisterCustomerCommand command = new RegisterCustomerCommand(
                party,
                naturalPerson,
                null, // statusHistory
                null, // pep
                null, // identityDocuments
                null, // addresses
                null, // emails
                null, // phones
                null, // economicActivities
                null, // consents
                null, // providers
                null, // relationships
                null, // groupMemberships
                null, // industryClassifications
                null  // attributes
        );

        when(sagaResult.isSuccess()).thenReturn(true);
        when(sagaEngine.execute(eq("RegisterCustomerSaga"), any(StepInputs.class)))
                .thenReturn(Mono.just(sagaResult));

        StepVerifier.create(service.registerCustomer(command))
                .expectNext(sagaResult)
                .verifyComplete();

        ArgumentCaptor<StepInputs> captor = ArgumentCaptor.forClass(StepInputs.class);
        verify(sagaEngine).execute(eq("RegisterCustomerSaga"), captor.capture());
        // Reaching this point without an NPE during builder execution proves the fix.
        assertThat(captor.getValue()).isNotNull();
    }

    @Test
    @DisplayName("Constructor should set saga engine dependency")
    void testConstructor_ShouldSetSagaEngine() {
        // When
        CustomerServiceImpl newService = new CustomerServiceImpl(sagaEngine, queryBus);

        // Then
        assertNotNull(newService);
        // We can't directly access the private field, but we can verify it works by calling a method
        RegisterCustomerCommand command = mock(RegisterCustomerCommand.class);
        when(sagaResult.isSuccess()).thenReturn(true);
        when(sagaEngine.execute(eq("RegisterCustomerSaga"), any(StepInputs.class)))
                .thenReturn(Mono.just(sagaResult));

        Mono<SagaResult> result = newService.registerCustomer(command);

        StepVerifier.create(result)
                .expectNext(sagaResult)
                .verifyComplete();
    }
}