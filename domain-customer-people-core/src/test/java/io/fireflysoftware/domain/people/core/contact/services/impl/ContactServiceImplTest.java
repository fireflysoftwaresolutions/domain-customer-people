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


package io.fireflysoftware.domain.people.core.contact.services.impl;

import io.fireflysoftware.domain.people.core.contact.commands.*;
import io.fireflysoftware.domain.people.core.contact.workflows.*;
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
@DisplayName("ContactServiceImpl Tests")
class ContactServiceImplTest {

    @Mock
    private SagaEngine sagaEngine;

    @Mock
    private SagaResult sagaResult;

    private ContactServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ContactServiceImpl(sagaEngine);
    }

    @Test
    @DisplayName("Should add address successfully")
    void testAddAddress_ShouldExecuteSaga() {
        // Given
        UUID partyId = UUID.randomUUID();
        RegisterAddressCommand command = mock(RegisterAddressCommand.class);
        when(command.withPartyId(partyId)).thenReturn(command);
        when(sagaEngine.execute(eq("AddAddressSaga"), any(StepInputs.class)))
                .thenReturn(Mono.just(sagaResult));

        // When
        Mono<SagaResult> result = service.addAddress(partyId, command);

        // Then
        StepVerifier.create(result)
                .expectNext(sagaResult)
                .verifyComplete();

        verify(command).withPartyId(partyId);
        verify(sagaEngine).execute(eq("AddAddressSaga"), any(StepInputs.class));
    }

    @Test
    @DisplayName("Should update address successfully")
    void testUpdateAddress_ShouldExecuteSaga() {
        // Given
        UUID partyId = UUID.randomUUID();
        UUID addressId = UUID.randomUUID();
        UpdateAddressCommand command = mock(UpdateAddressCommand.class);
        when(command.withAddressId(addressId)).thenReturn(command);
        when(command.withPartyId(partyId)).thenReturn(command);
        when(sagaEngine.execute(eq("UpdateAddressSaga"), any(StepInputs.class)))
                .thenReturn(Mono.just(sagaResult));

        // When
        Mono<Void> result = service.updateAddress(partyId, addressId, command);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(command).withAddressId(addressId);
        verify(command).withPartyId(partyId);
        verify(sagaEngine).execute(eq("UpdateAddressSaga"), any(StepInputs.class));
    }

    @Test
    @DisplayName("Should remove address successfully")
    void testRemoveAddress_ShouldExecuteSaga() {
        // Given
        UUID partyId = UUID.randomUUID();
        UUID addressId = UUID.randomUUID();
        when(sagaEngine.execute(eq("RemoveAddressSaga"), any(StepInputs.class)))
                .thenReturn(Mono.just(sagaResult));

        // When
        Mono<SagaResult> result = service.removeAddress(partyId, addressId);

        // Then
        StepVerifier.create(result)
                .expectNext(sagaResult)
                .verifyComplete();

        verify(sagaEngine).execute(eq("RemoveAddressSaga"), any(StepInputs.class));
    }

    @Test
    @DisplayName("Should add email successfully")
    void testAddEmail_ShouldExecuteSaga() {
        // Given
        UUID partyId = UUID.randomUUID();
        RegisterEmailCommand command = mock(RegisterEmailCommand.class);
        when(command.withPartyId(partyId)).thenReturn(command);
        when(sagaEngine.execute(eq("AddEmailSaga"), any(StepInputs.class)))
                .thenReturn(Mono.just(sagaResult));

        // When
        Mono<SagaResult> result = service.addEmail(partyId, command);

        // Then
        StepVerifier.create(result)
                .expectNext(sagaResult)
                .verifyComplete();

        verify(command).withPartyId(partyId);
        verify(sagaEngine).execute(eq("AddEmailSaga"), any(StepInputs.class));
    }

    @Test
    @DisplayName("Should update email successfully")
    void testUpdateEmail_ShouldExecuteSaga() {
        // Given
        UUID partyId = UUID.randomUUID();
        UUID emailId = UUID.randomUUID();
        UpdateEmailCommand command = mock(UpdateEmailCommand.class);
        when(command.withEmailContactId(emailId)).thenReturn(command);
        when(command.withPartyId(partyId)).thenReturn(command);
        when(sagaEngine.execute(eq("UpdateEmailSaga"), any(StepInputs.class)))
                .thenReturn(Mono.just(sagaResult));

        // When
        Mono<Void> result = service.updateEmail(partyId, emailId, command);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(command).withEmailContactId(emailId);
        verify(command).withPartyId(partyId);
        verify(sagaEngine).execute(eq("UpdateEmailSaga"), any(StepInputs.class));
    }

    @Test
    @DisplayName("Should remove email successfully")
    void testRemoveEmail_ShouldExecuteSaga() {
        // Given
        UUID partyId = UUID.randomUUID();
        UUID emailId = UUID.randomUUID();
        when(sagaEngine.execute(eq("RemoveEmailSaga"), any(StepInputs.class)))
                .thenReturn(Mono.just(sagaResult));

        // When
        Mono<SagaResult> result = service.removeEmail(partyId, emailId);

        // Then
        StepVerifier.create(result)
                .expectNext(sagaResult)
                .verifyComplete();

        verify(sagaEngine).execute(eq("RemoveEmailSaga"), any(StepInputs.class));
    }

    @Test
    @DisplayName("Should add phone successfully")
    void testAddPhone_ShouldExecuteSaga() {
        // Given
        UUID partyId = UUID.randomUUID();
        RegisterPhoneCommand command = mock(RegisterPhoneCommand.class);
        when(command.withPartyId(partyId)).thenReturn(command);
        when(sagaEngine.execute(eq("AddPhoneSaga"), any(StepInputs.class)))
                .thenReturn(Mono.just(sagaResult));

        // When
        Mono<SagaResult> result = service.addPhone(partyId, command);

        // Then
        StepVerifier.create(result)
                .expectNext(sagaResult)
                .verifyComplete();

        verify(command).withPartyId(partyId);
        verify(sagaEngine).execute(eq("AddPhoneSaga"), any(StepInputs.class));
    }

    @Test
    @DisplayName("Should update phone successfully")
    void testUpdatePhone_ShouldExecuteSaga() {
        // Given
        UUID partyId = UUID.randomUUID();
        UUID phoneId = UUID.randomUUID();
        UpdatePhoneCommand command = mock(UpdatePhoneCommand.class);
        when(command.withPhoneContactId(phoneId)).thenReturn(command);
        when(command.withPartyId(partyId)).thenReturn(command);
        when(sagaEngine.execute(eq("UpdatePhoneSaga"), any(StepInputs.class)))
                .thenReturn(Mono.just(sagaResult));

        // When
        Mono<Void> result = service.updatePhone(partyId, phoneId, command);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(command).withPhoneContactId(phoneId);
        verify(command).withPartyId(partyId);
        verify(sagaEngine).execute(eq("UpdatePhoneSaga"), any(StepInputs.class));
    }

    @Test
    @DisplayName("Should remove phone successfully")
    void testRemovePhone_ShouldExecuteSaga() {
        // Given
        UUID partyId = UUID.randomUUID();
        UUID phoneId = UUID.randomUUID();
        when(sagaEngine.execute(eq("RemovePhoneSaga"), any(StepInputs.class)))
                .thenReturn(Mono.just(sagaResult));

        // When
        Mono<SagaResult> result = service.removePhone(partyId, phoneId);

        // Then
        StepVerifier.create(result)
                .expectNext(sagaResult)
                .verifyComplete();

        verify(sagaEngine).execute(eq("RemovePhoneSaga"), any(StepInputs.class));
    }

    @Test
    @DisplayName("Should set preferred channel successfully")
    void testSetPreferredChannel_ShouldExecuteSaga() {
        // Given
        UUID partyId = UUID.randomUUID();
        UpdatePreferredChannelCommand command = mock(UpdatePreferredChannelCommand.class);
        when(command.withPartyId(partyId)).thenReturn(command);
        when(sagaEngine.execute(eq("SetPreferredChannelSaga"), any(StepInputs.class)))
                .thenReturn(Mono.just(sagaResult));

        // When
        Mono<Void> result = service.setPreferredChannel(partyId, command);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(command).withPartyId(partyId);
        verify(sagaEngine).execute(eq("SetPreferredChannelSaga"), any(StepInputs.class));
    }

    @Test
    @DisplayName("Should add identity document successfully")
    void testAddIdentityDocument_ShouldExecuteSaga() {
        // Given
        UUID partyId = UUID.randomUUID();
        RegisterIdentityDocumentCommand command = mock(RegisterIdentityDocumentCommand.class);
        when(command.withPartyId(partyId)).thenReturn(command);
        when(sagaEngine.execute(eq("AddIdentityDocumentSaga"), any(StepInputs.class)))
                .thenReturn(Mono.just(sagaResult));

        // When
        Mono<SagaResult> result = service.addIdentityDocument(partyId, command);

        // Then
        StepVerifier.create(result)
                .expectNext(sagaResult)
                .verifyComplete();

        verify(command).withPartyId(partyId);
        verify(sagaEngine).execute(eq("AddIdentityDocumentSaga"), any(StepInputs.class));
    }

    @Test
    @DisplayName("Should remove identity document successfully")
    void testRemoveIdentityDocument_ShouldExecuteSaga() {
        // Given
        UUID partyId = UUID.randomUUID();
        UUID identityDocumentId = UUID.randomUUID();
        when(sagaEngine.execute(eq("RemoveIdentityDocumentSaga"), any(StepInputs.class)))
                .thenReturn(Mono.just(sagaResult));

        // When
        Mono<SagaResult> result = service.removeIdentityDocument(partyId, identityDocumentId);

        // Then
        StepVerifier.create(result)
                .expectNext(sagaResult)
                .verifyComplete();

        verify(sagaEngine).execute(eq("RemoveIdentityDocumentSaga"), any(StepInputs.class));
    }

    @Test
    @DisplayName("Should handle saga execution errors")
    void testAddAddress_ShouldHandleErrors() {
        // Given
        UUID partyId = UUID.randomUUID();
        RegisterAddressCommand command = mock(RegisterAddressCommand.class);
        when(command.withPartyId(partyId)).thenReturn(command);
        RuntimeException error = new RuntimeException("Saga execution failed");
        when(sagaEngine.execute(eq("AddAddressSaga"), any(StepInputs.class)))
                .thenReturn(Mono.error(error));

        // When
        Mono<SagaResult> result = service.addAddress(partyId, command);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(sagaEngine).execute(eq("AddAddressSaga"), any(StepInputs.class));
    }

    @Test
    @DisplayName("Constructor should set saga engine dependency")
    void testConstructor_ShouldSetSagaEngine() {
        // When
        ContactServiceImpl newService = new ContactServiceImpl(sagaEngine);

        // Then
        assertNotNull(newService);
        // We can't directly access the private field, but we can verify it works by calling a method
        UUID partyId = UUID.randomUUID();
        RegisterAddressCommand command = mock(RegisterAddressCommand.class);
        when(command.withPartyId(partyId)).thenReturn(command);
        when(sagaEngine.execute(eq("AddAddressSaga"), any(StepInputs.class)))
                .thenReturn(Mono.just(sagaResult));

        Mono<SagaResult> result = newService.addAddress(partyId, command);

        StepVerifier.create(result)
                .expectNext(sagaResult)
                .verifyComplete();
    }
}