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

import io.fireflysoftware.core.customer.sdk.api.ConsentsApi;
import io.fireflysoftware.core.customer.sdk.model.FilterRequestConsentDTO;
import io.fireflysoftware.core.customer.sdk.model.PaginationResponse;
import io.fireflysoftware.domain.people.core.customer.queries.PartyConsentsQuery;
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
@DisplayName("GetPartyConsentsHandler Tests")
class GetPartyConsentsHandlerTest {

    @Mock
    private ConsentsApi consentsApi;

    private GetPartyConsentsHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GetPartyConsentsHandler(consentsApi);
    }

    @Test
    @DisplayName("Should return consents for a given partyId")
    void shouldReturnConsents() {
        UUID partyId = UUID.randomUUID();
        PaginationResponse expectedResponse = new PaginationResponse();

        when(consentsApi.filterConsents(eq(partyId), any(FilterRequestConsentDTO.class), any(String.class)))
                .thenReturn(Mono.just(expectedResponse));

        PartyConsentsQuery query = PartyConsentsQuery.builder().partyId(partyId).build();

        StepVerifier.create(handler.doHandle(query))
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(consentsApi).filterConsents(eq(partyId), any(FilterRequestConsentDTO.class), any(String.class));
    }

    @Test
    @DisplayName("Should propagate error when API call fails")
    void shouldPropagateError() {
        UUID partyId = UUID.randomUUID();

        when(consentsApi.filterConsents(eq(partyId), any(FilterRequestConsentDTO.class), any(String.class)))
                .thenReturn(Mono.error(new RuntimeException("API error")));

        PartyConsentsQuery query = PartyConsentsQuery.builder().partyId(partyId).build();

        StepVerifier.create(handler.doHandle(query))
                .expectErrorMatches(e -> e instanceof RuntimeException && e.getMessage().equals("API error"))
                .verify();
    }
}
