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


package io.fireflysoftware.domain.people.core.status.services.impl;

import io.fireflysoftware.domain.people.core.status.commands.UpdateStatusCommand;
import io.fireflysoftware.domain.people.core.status.workflows.UpdateStatusSaga;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StatusServiceImpl Tests")
class StatusServiceImplTest {

    @Mock
    private SagaEngine sagaEngine;

    @Mock
    private SagaResult sagaResult;

    private StatusServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new StatusServiceImpl(sagaEngine);
    }

    @Test
    @DisplayName("Should update status successfully")
    void testUpdateStatus_ShouldExecuteSaga() {
        // Given
        UpdateStatusCommand command = mock(UpdateStatusCommand.class);
        when(sagaEngine.execute(eq("UpdateStatusSaga"), any(StepInputs.class)))
                .thenReturn(Mono.just(sagaResult));

        // When
        Mono<SagaResult> result = service.updateStatus(command);

        // Then
        StepVerifier.create(result)
                .expectNext(sagaResult)
                .verifyComplete();

        verify(sagaEngine).execute(eq("UpdateStatusSaga"), any(StepInputs.class));
    }

    @Test
    @DisplayName("Should handle saga execution errors")
    void testUpdateStatus_ShouldHandleErrors() {
        // Given
        UpdateStatusCommand command = mock(UpdateStatusCommand.class);
        RuntimeException error = new RuntimeException("Saga execution failed");
        when(sagaEngine.execute(eq("UpdateStatusSaga"), any(StepInputs.class)))
                .thenReturn(Mono.error(error));

        // When
        Mono<SagaResult> result = service.updateStatus(command);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(sagaEngine).execute(eq("UpdateStatusSaga"), any(StepInputs.class));
    }

    @Test
    @DisplayName("Constructor should set saga engine dependency")
    void testConstructor_ShouldSetSagaEngine() {
        // When
        StatusServiceImpl newService = new StatusServiceImpl(sagaEngine);

        // Then
        assertNotNull(newService);
        // We can't directly access the private field, but we can verify it works by calling a method
        UpdateStatusCommand command = mock(UpdateStatusCommand.class);
        when(sagaEngine.execute(eq("UpdateStatusSaga"), any(StepInputs.class)))
                .thenReturn(Mono.just(sagaResult));

        Mono<SagaResult> result = newService.updateStatus(command);

        StepVerifier.create(result)
                .expectNext(sagaResult)
                .verifyComplete();
    }
}