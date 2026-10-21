/*
 * Copyright 2025 Firefly Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package io.fireflysoftware.domain.people.core.party.services.impl;

import io.fireflysoftware.domain.people.core.party.commands.RegisterPartyRelationshipCommand;
import io.fireflysoftware.domain.people.core.party.commands.RemovePartyRelationshipCommand;
import io.fireflysoftware.domain.people.core.party.commands.UpdatePartyRelationshipCommand;
import io.fireflysoftware.domain.people.core.party.workflows.RegisterPartyRelationshipSaga;
import io.fireflysoftware.domain.people.core.party.workflows.UpdatePartyRelationshipSaga;
import io.fireflysoftware.domain.people.core.party.workflows.RemovePartyRelationshipSaga;
import org.fireflyframework.orchestration.saga.engine.SagaResult;
import org.fireflyframework.orchestration.saga.engine.SagaEngine;
import org.fireflyframework.orchestration.saga.engine.StepInputs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PartyServiceImpl Tests")
class PartyServiceImplTest {

    @Mock
    private SagaEngine sagaEngine;

    @Mock
    private SagaResult sagaResult;

    private PartyServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PartyServiceImpl(sagaEngine);
    }

    @Test
    @DisplayName("Should register party relationship successfully")
    void testRegisterPartyRelationship_ShouldExecuteSaga() {
        // Given
        RegisterPartyRelationshipCommand command = mock(RegisterPartyRelationshipCommand.class);
        when(sagaEngine.execute(eq("RegisterPartyRelationshipSaga"), any(StepInputs.class)))
                .thenReturn(Mono.just(sagaResult));

        // When
        Mono<SagaResult> result = service.registerPartyRelationship(command);

        // Then
        StepVerifier.create(result)
                .expectNext(sagaResult)
                .verifyComplete();

        verify(sagaEngine).execute(eq("RegisterPartyRelationshipSaga"), any(StepInputs.class));
    }

    @Test
    @DisplayName("Should update party relationship successfully")
    void testUpdatePartyRelationship_ShouldExecuteSaga() {
        // Given
        UUID relationId = UUID.randomUUID();
        UpdatePartyRelationshipCommand command = mock(UpdatePartyRelationshipCommand.class);
        when(command.withPartyRelationshipId(relationId)).thenReturn(command);
        when(sagaEngine.execute(eq("UpdatePartyRelationshipSaga"), any(StepInputs.class)))
                .thenReturn(Mono.just(sagaResult));

        // When
        Mono<SagaResult> result = service.updatePartyRelationship(command, relationId);

        // Then
        StepVerifier.create(result)
                .expectNext(sagaResult)
                .verifyComplete();

        verify(command).withPartyRelationshipId(relationId);
        verify(sagaEngine).execute(eq("UpdatePartyRelationshipSaga"), any(StepInputs.class));
    }

    @Test
    @DisplayName("Should remove party relationship successfully")
    void testRemovePartyRelationship_ShouldExecuteSaga() {
        // Given
        UUID relationId = UUID.randomUUID();
        when(sagaEngine.execute(eq("RemovePartyRelationshipSaga"), any(StepInputs.class)))
                .thenReturn(Mono.just(sagaResult));

        // When
        Mono<SagaResult> result = service.removePartyRelationship(relationId);

        // Then
        StepVerifier.create(result)
                .expectNext(sagaResult)
                .verifyComplete();

        verify(sagaEngine).execute(eq("RemovePartyRelationshipSaga"), any(StepInputs.class));
    }

    @Test
    @DisplayName("Should handle saga execution errors in registerPartyRelationship")
    void testRegisterPartyRelationship_ShouldHandleErrors() {
        // Given
        RegisterPartyRelationshipCommand command = mock(RegisterPartyRelationshipCommand.class);
        RuntimeException error = new RuntimeException("Saga execution failed");
        when(sagaEngine.execute(eq("RegisterPartyRelationshipSaga"), any(StepInputs.class)))
                .thenReturn(Mono.error(error));

        // When
        Mono<SagaResult> result = service.registerPartyRelationship(command);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(sagaEngine).execute(eq("RegisterPartyRelationshipSaga"), any(StepInputs.class));
    }

    @Test
    @DisplayName("Should handle saga execution errors in updatePartyRelationship")
    void testUpdatePartyRelationship_ShouldHandleErrors() {
        // Given
        UUID relationId = UUID.randomUUID();
        UpdatePartyRelationshipCommand command = mock(UpdatePartyRelationshipCommand.class);
        when(command.withPartyRelationshipId(relationId)).thenReturn(command);
        RuntimeException error = new RuntimeException("Saga execution failed");
        when(sagaEngine.execute(eq("UpdatePartyRelationshipSaga"), any(StepInputs.class)))
                .thenReturn(Mono.error(error));

        // When
        Mono<SagaResult> result = service.updatePartyRelationship(command, relationId);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(command).withPartyRelationshipId(relationId);
        verify(sagaEngine).execute(eq("UpdatePartyRelationshipSaga"), any(StepInputs.class));
    }

    @Test
    @DisplayName("Should handle saga execution errors in removePartyRelationship")
    void testRemovePartyRelationship_ShouldHandleErrors() {
        // Given
        UUID relationId = UUID.randomUUID();
        RuntimeException error = new RuntimeException("Saga execution failed");
        when(sagaEngine.execute(eq("RemovePartyRelationshipSaga"), any(StepInputs.class)))
                .thenReturn(Mono.error(error));

        // When
        Mono<SagaResult> result = service.removePartyRelationship(relationId);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(sagaEngine).execute(eq("RemovePartyRelationshipSaga"), any(StepInputs.class));
    }

    @Test
    @DisplayName("Constructor should set saga engine dependency")
    void testConstructor_ShouldSetSagaEngine() {
        // When
        PartyServiceImpl newService = new PartyServiceImpl(sagaEngine);

        // Then
        assertNotNull(newService);
        // We can't directly access the private field, but we can verify it works by calling a method
        RegisterPartyRelationshipCommand command = mock(RegisterPartyRelationshipCommand.class);
        when(sagaEngine.execute(eq("RegisterPartyRelationshipSaga"), any(StepInputs.class)))
                .thenReturn(Mono.just(sagaResult));

        Mono<SagaResult> result = newService.registerPartyRelationship(command);

        StepVerifier.create(result)
                .expectNext(sagaResult)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle null command in registerPartyRelationship")
    void testRegisterPartyRelationship_WithNullCommand_ShouldExecuteSaga() {
        // Given
        RegisterPartyRelationshipCommand command = null;
        when(sagaEngine.execute(eq("RegisterPartyRelationshipSaga"), any(StepInputs.class)))
                .thenReturn(Mono.just(sagaResult));

        // When
        Mono<SagaResult> result = service.registerPartyRelationship(command);

        // Then
        StepVerifier.create(result)
                .expectNext(sagaResult)
                .verifyComplete();

        verify(sagaEngine).execute(eq("RegisterPartyRelationshipSaga"), any(StepInputs.class));
    }

    @Test
    @DisplayName("Should handle null command in updatePartyRelationship")
    void testUpdatePartyRelationship_WithNullCommand_ShouldThrowException() {
        // Given
        UUID relationId = UUID.randomUUID();
        UpdatePartyRelationshipCommand command = null;

        // When & Then
        assertThrows(NullPointerException.class, () -> {
            service.updatePartyRelationship(command, relationId);
        });
    }

    @Test
    @DisplayName("Should handle null relationId in updatePartyRelationship")
    void testUpdatePartyRelationship_WithNullRelationId_ShouldExecuteSaga() {
        // Given
        UUID relationId = null;
        UpdatePartyRelationshipCommand command = mock(UpdatePartyRelationshipCommand.class);
        when(command.withPartyRelationshipId(relationId)).thenReturn(command);
        when(sagaEngine.execute(eq("UpdatePartyRelationshipSaga"), any(StepInputs.class)))
                .thenReturn(Mono.just(sagaResult));

        // When
        Mono<SagaResult> result = service.updatePartyRelationship(command, relationId);

        // Then
        StepVerifier.create(result)
                .expectNext(sagaResult)
                .verifyComplete();

        verify(command).withPartyRelationshipId(relationId);
        verify(sagaEngine).execute(eq("UpdatePartyRelationshipSaga"), any(StepInputs.class));
    }

    @Test
    @DisplayName("Should handle null relationId in removePartyRelationship")
    void testRemovePartyRelationship_WithNullRelationId_ShouldExecuteSaga() {
        // Given
        UUID relationId = null;
        when(sagaEngine.execute(eq("RemovePartyRelationshipSaga"), any(StepInputs.class)))
                .thenReturn(Mono.just(sagaResult));

        // When
        Mono<SagaResult> result = service.removePartyRelationship(relationId);

        // Then
        StepVerifier.create(result)
                .expectNext(sagaResult)
                .verifyComplete();

        verify(sagaEngine).execute(eq("RemovePartyRelationshipSaga"), any(StepInputs.class));
    }
}