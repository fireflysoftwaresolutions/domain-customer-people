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


package io.fireflysoftware.domain.people.core.customer.handlers;

import io.fireflysoftware.core.customer.sdk.api.EmailContactsApi;
import io.fireflysoftware.core.customer.sdk.model.FilterRequestEmailContactDTO;
import io.fireflysoftware.core.customer.sdk.model.PaginationResponseEmailContactDTO;
import io.fireflysoftware.domain.people.core.customer.queries.PartyEmailsQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetPartyEmailsHandler Tests")
class GetPartyEmailsHandlerTest {

    @Mock
    private EmailContactsApi emailContactsApi;

    private GetPartyEmailsHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GetPartyEmailsHandler(emailContactsApi);
    }

    @Test
    @DisplayName("Should return email contacts for a given partyId")
    void shouldReturnEmails() {
        UUID partyId = UUID.randomUUID();
        PaginationResponseEmailContactDTO expectedResponse = new PaginationResponseEmailContactDTO();

        when(emailContactsApi.filterEmailContacts(eq(partyId), any(FilterRequestEmailContactDTO.class), any(String.class)))
                .thenReturn(Mono.just(expectedResponse));

        PartyEmailsQuery query = PartyEmailsQuery.builder().partyId(partyId).build();

        StepVerifier.create(handler.doHandle(query))
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(emailContactsApi).filterEmailContacts(eq(partyId), any(FilterRequestEmailContactDTO.class), any(String.class));
    }

    @Test
    @DisplayName("Should propagate error when API call fails")
    void shouldPropagateError() {
        UUID partyId = UUID.randomUUID();

        when(emailContactsApi.filterEmailContacts(eq(partyId), any(FilterRequestEmailContactDTO.class), any(String.class)))
                .thenReturn(Mono.error(new RuntimeException("API error")));

        PartyEmailsQuery query = PartyEmailsQuery.builder().partyId(partyId).build();

        StepVerifier.create(handler.doHandle(query))
                .expectErrorMatches(e -> e instanceof RuntimeException && e.getMessage().equals("API error"))
                .verify();
    }
}
