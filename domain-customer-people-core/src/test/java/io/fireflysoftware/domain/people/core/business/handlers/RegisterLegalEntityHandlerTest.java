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


package io.fireflysoftware.domain.people.core.business.handlers;

import io.fireflysoftware.core.customer.sdk.api.LegalEntitiesApi;
import io.fireflysoftware.core.customer.sdk.model.LegalEntityDTO;
import io.fireflysoftware.domain.people.core.business.commands.RegisterLegalEntityCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterLegalEntityHandler Tests")
class RegisterLegalEntityHandlerTest {

    @Mock
    private LegalEntitiesApi legalEntitiesApi;

    private RegisterLegalEntityHandler handler;

    @BeforeEach
    void setUp() {
        handler = new RegisterLegalEntityHandler(legalEntitiesApi);
    }

    @Test
    @DisplayName("Should propagate the 7 new legal-entity fields to the SDK call")
    void shouldPropagateNewLegalEntityFieldsToSdk() {
        UUID partyId = UUID.randomUUID();
        UUID legalEntityId = UUID.randomUUID();

        RegisterLegalEntityCommand cmd = new RegisterLegalEntityCommand();
        cmd.setPartyId(partyId);
        cmd.setEmployeeRange("50-249");
        cmd.setAnnualRevenue(new BigDecimal("12345678.90"));
        cmd.setContactName("Jane Doe");
        cmd.setContactPosition("CFO");
        cmd.setContactEmail("jane.doe@acme.example");
        cmd.setContactPhone("+34911223344");

        LegalEntityDTO response = mock(LegalEntityDTO.class);
        when(response.getLegalEntityId()).thenReturn(legalEntityId);

        when(legalEntitiesApi.createLegalEntity(eq(partyId), any(LegalEntityDTO.class), any(String.class)))
                .thenReturn(Mono.just(response));

        StepVerifier.create(handler.doHandle(cmd))
                .expectNext(legalEntityId)
                .verifyComplete();

        ArgumentCaptor<LegalEntityDTO> captor = ArgumentCaptor.forClass(LegalEntityDTO.class);
        verify(legalEntitiesApi).createLegalEntity(eq(partyId), captor.capture(), any(String.class));

        LegalEntityDTO captured = captor.getValue();
        assertThat(captured.getEmployeeRange()).isEqualTo("50-249");
        assertThat(captured.getAnnualRevenue()).isEqualByComparingTo(new BigDecimal("12345678.90"));
        assertThat(captured.getContactName()).isEqualTo("Jane Doe");
        assertThat(captured.getContactPosition()).isEqualTo("CFO");
        assertThat(captured.getContactEmail()).isEqualTo("jane.doe@acme.example");
        assertThat(captured.getContactPhone()).isEqualTo("+34911223344");
    }
}
