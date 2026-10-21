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


package io.fireflysoftware.domain.people.core.contact.workflows;

import io.fireflysoftware.domain.people.core.contact.commands.RegisterAddressCommand;
import io.fireflysoftware.domain.people.core.contact.commands.RegisterEmailCommand;
import io.fireflysoftware.domain.people.core.contact.commands.RegisterIdentityDocumentCommand;
import io.fireflysoftware.domain.people.core.contact.commands.RegisterPhoneCommand;
import io.fireflysoftware.domain.people.core.contact.commands.RemoveAddressCommand;
import io.fireflysoftware.domain.people.core.contact.commands.RemoveEmailCommand;
import io.fireflysoftware.domain.people.core.contact.commands.RemoveIdentityDocumentCommand;
import io.fireflysoftware.domain.people.core.contact.commands.RemovePhoneCommand;
import io.fireflysoftware.domain.people.core.party.commands.RegisterPartyRelationshipCommand;
import io.fireflysoftware.domain.people.core.party.commands.RemovePartyRelationshipCommand;
import io.fireflysoftware.domain.people.core.party.workflows.RegisterPartyRelationshipSaga;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The standalone registration sagas delete the row the core already allows them to delete.
 */
@ExtendWith(MockitoExtension.class)
class AddContactCompensationTest {

    @Mock
    private CommandBus commandBus;

    @Test
    void addEmailDeletesTheCreatedContact() {
        UUID partyId = UUID.randomUUID();
        UUID emailId = UUID.randomUUID();
        when(commandBus.send(any(RegisterEmailCommand.class))).thenReturn(Mono.just(emailId));
        when(commandBus.send(any(RemoveEmailCommand.class))).thenReturn(Mono.empty());
        AddEmailSaga saga = new AddEmailSaga(commandBus);
        ExecutionContext ctx = ExecutionContext.forSaga("AddEmailSaga", "test");

        StepVerifier.create(saga.registerEmail(new RegisterEmailCommand().withPartyId(partyId), ctx))
                .expectNext(emailId)
                .verifyComplete();
        StepVerifier.create(saga.removeEmail(emailId, ctx)).verifyComplete();

        ArgumentCaptor<RemoveEmailCommand> captor = ArgumentCaptor.forClass(RemoveEmailCommand.class);
        verify(commandBus).send(captor.capture());
        assertThat(captor.getValue().partyId()).isEqualTo(partyId);
        assertThat(captor.getValue().emailId()).isEqualTo(emailId);
    }

    @Test
    void addPhoneDeletesTheCreatedContact() {
        UUID partyId = UUID.randomUUID();
        UUID phoneId = UUID.randomUUID();
        when(commandBus.send(any(RegisterPhoneCommand.class))).thenReturn(Mono.just(phoneId));
        when(commandBus.send(any(RemovePhoneCommand.class))).thenReturn(Mono.empty());
        AddPhoneSaga saga = new AddPhoneSaga(commandBus);
        ExecutionContext ctx = ExecutionContext.forSaga("AddPhoneSaga", "test");

        StepVerifier.create(saga.registerPhone(new RegisterPhoneCommand().withPartyId(partyId), ctx))
                .expectNext(phoneId)
                .verifyComplete();
        StepVerifier.create(saga.removePhone(phoneId, ctx)).verifyComplete();

        ArgumentCaptor<RemovePhoneCommand> captor = ArgumentCaptor.forClass(RemovePhoneCommand.class);
        verify(commandBus).send(captor.capture());
        assertThat(captor.getValue().partyId()).isEqualTo(partyId);
        assertThat(captor.getValue().phoneId()).isEqualTo(phoneId);
    }

    @Test
    void addAddressDeletesTheCreatedAddress() {
        UUID partyId = UUID.randomUUID();
        UUID addressId = UUID.randomUUID();
        when(commandBus.send(any(RegisterAddressCommand.class))).thenReturn(Mono.just(addressId));
        when(commandBus.send(any(RemoveAddressCommand.class))).thenReturn(Mono.empty());
        AddAddressSaga saga = new AddAddressSaga(commandBus);
        ExecutionContext ctx = ExecutionContext.forSaga("AddAddressSaga", "test");

        StepVerifier.create(saga.registerAddress(new RegisterAddressCommand().withPartyId(partyId), ctx))
                .expectNext(addressId)
                .verifyComplete();
        StepVerifier.create(saga.removeAddress(addressId, ctx)).verifyComplete();

        ArgumentCaptor<RemoveAddressCommand> captor = ArgumentCaptor.forClass(RemoveAddressCommand.class);
        verify(commandBus).send(captor.capture());
        assertThat(captor.getValue().partyId()).isEqualTo(partyId);
        assertThat(captor.getValue().addressId()).isEqualTo(addressId);
    }

    @Test
    void addIdentityDocumentDeletesTheCreatedDocument() {
        UUID partyId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();
        when(commandBus.send(any(RegisterIdentityDocumentCommand.class))).thenReturn(Mono.just(documentId));
        when(commandBus.send(any(RemoveIdentityDocumentCommand.class))).thenReturn(Mono.empty());
        AddIdentityDocumentSaga saga = new AddIdentityDocumentSaga(commandBus);
        ExecutionContext ctx = ExecutionContext.forSaga("AddIdentityDocumentSaga", "test");

        StepVerifier.create(saga.registerIdentityDocument(new RegisterIdentityDocumentCommand().withPartyId(partyId), ctx))
                .expectNext(documentId)
                .verifyComplete();
        StepVerifier.create(saga.removeIdentityDocument(documentId, ctx)).verifyComplete();

        ArgumentCaptor<RemoveIdentityDocumentCommand> captor = ArgumentCaptor.forClass(RemoveIdentityDocumentCommand.class);
        verify(commandBus).send(captor.capture());
        assertThat(captor.getValue().partyId()).isEqualTo(partyId);
        assertThat(captor.getValue().identityDocumentId()).isEqualTo(documentId);
    }

    @Test
    void addPartyRelationshipDeletesTheCreatedRelationship() {
        UUID relationshipId = UUID.randomUUID();
        when(commandBus.send(any(RegisterPartyRelationshipCommand.class))).thenReturn(Mono.just(relationshipId));
        when(commandBus.send(any(RemovePartyRelationshipCommand.class))).thenReturn(Mono.empty());
        RegisterPartyRelationshipSaga saga = new RegisterPartyRelationshipSaga(commandBus);
        ExecutionContext ctx = ExecutionContext.forSaga("RegisterPartyRelationshipSaga", "test");

        StepVerifier.create(saga.registerPartyRelationship(new RegisterPartyRelationshipCommand(), ctx))
                .expectNext(relationshipId)
                .verifyComplete();
        StepVerifier.create(saga.removePartyRelationship(relationshipId, ctx)).verifyComplete();

        ArgumentCaptor<RemovePartyRelationshipCommand> captor = ArgumentCaptor.forClass(RemovePartyRelationshipCommand.class);
        verify(commandBus).send(captor.capture());
        assertThat(captor.getValue().partyRelationshipId()).isEqualTo(relationshipId);
    }

    @Test
    void emailCompensationWithoutAPartyDoesNotCallTheCore() {
        AddEmailSaga saga = new AddEmailSaga(commandBus);
        ExecutionContext ctx = ExecutionContext.forSaga("AddEmailSaga", "test");

        StepVerifier.create(saga.removeEmail(UUID.randomUUID(), ctx)).verifyComplete();

        verify(commandBus, never()).send(any());
    }
}
