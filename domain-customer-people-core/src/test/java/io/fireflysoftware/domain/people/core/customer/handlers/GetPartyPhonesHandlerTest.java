package io.fireflysoftware.domain.people.core.customer.handlers;

import io.fireflysoftware.core.customer.sdk.api.PhoneContactsApi;
import io.fireflysoftware.core.customer.sdk.model.FilterRequestPhoneContactDTO;
import io.fireflysoftware.core.customer.sdk.model.PaginationResponsePhoneContactDTO;
import io.fireflysoftware.domain.people.core.customer.queries.PartyPhonesQuery;
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
@DisplayName("GetPartyPhonesHandler Tests")
class GetPartyPhonesHandlerTest {

    @Mock
    private PhoneContactsApi phoneContactsApi;

    private GetPartyPhonesHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GetPartyPhonesHandler(phoneContactsApi);
    }

    @Test
    @DisplayName("Should return phone contacts for a given partyId")
    void shouldReturnPhones() {
        UUID partyId = UUID.randomUUID();
        PaginationResponsePhoneContactDTO expectedResponse = new PaginationResponsePhoneContactDTO();

        when(phoneContactsApi.filterPhoneContacts(eq(partyId), any(FilterRequestPhoneContactDTO.class), any(String.class)))
                .thenReturn(Mono.just(expectedResponse));

        PartyPhonesQuery query = PartyPhonesQuery.builder().partyId(partyId).build();

        StepVerifier.create(handler.doHandle(query))
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(phoneContactsApi).filterPhoneContacts(eq(partyId), any(FilterRequestPhoneContactDTO.class), any(String.class));
    }

    @Test
    @DisplayName("Should propagate error when API call fails")
    void shouldPropagateError() {
        UUID partyId = UUID.randomUUID();

        when(phoneContactsApi.filterPhoneContacts(eq(partyId), any(FilterRequestPhoneContactDTO.class), any(String.class)))
                .thenReturn(Mono.error(new RuntimeException("API error")));

        PartyPhonesQuery query = PartyPhonesQuery.builder().partyId(partyId).build();

        StepVerifier.create(handler.doHandle(query))
                .expectErrorMatches(e -> e instanceof RuntimeException && e.getMessage().equals("API error"))
                .verify();
    }
}
