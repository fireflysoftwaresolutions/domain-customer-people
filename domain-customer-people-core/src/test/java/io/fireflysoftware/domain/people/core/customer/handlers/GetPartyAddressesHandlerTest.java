package io.fireflysoftware.domain.people.core.customer.handlers;

import io.fireflysoftware.core.customer.sdk.api.AddressesApi;
import io.fireflysoftware.core.customer.sdk.model.FilterRequestAddressDTO;
import io.fireflysoftware.core.customer.sdk.model.PaginationResponse;
import io.fireflysoftware.domain.people.core.customer.queries.PartyAddressesQuery;
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
@DisplayName("GetPartyAddressesHandler Tests")
class GetPartyAddressesHandlerTest {

    @Mock
    private AddressesApi addressesApi;

    private GetPartyAddressesHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GetPartyAddressesHandler(addressesApi);
    }

    @Test
    @DisplayName("Should return addresses for a given partyId")
    void shouldReturnAddresses() {
        UUID partyId = UUID.randomUUID();
        PaginationResponse expectedResponse = new PaginationResponse();

        when(addressesApi.filterAddresses(eq(partyId), any(FilterRequestAddressDTO.class), any(String.class)))
                .thenReturn(Mono.just(expectedResponse));

        PartyAddressesQuery query = PartyAddressesQuery.builder().partyId(partyId).build();

        StepVerifier.create(handler.doHandle(query))
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(addressesApi).filterAddresses(eq(partyId), any(FilterRequestAddressDTO.class), any(String.class));
    }

    @Test
    @DisplayName("Should propagate error when API call fails")
    void shouldPropagateError() {
        UUID partyId = UUID.randomUUID();

        when(addressesApi.filterAddresses(eq(partyId), any(FilterRequestAddressDTO.class), any(String.class)))
                .thenReturn(Mono.error(new RuntimeException("API error")));

        PartyAddressesQuery query = PartyAddressesQuery.builder().partyId(partyId).build();

        StepVerifier.create(handler.doHandle(query))
                .expectErrorMatches(e -> e instanceof RuntimeException && e.getMessage().equals("API error"))
                .verify();
    }
}
