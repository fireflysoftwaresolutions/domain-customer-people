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
        cmd.setCnaeCode("6201");
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
        assertThat(captured.getCnaeCode()).isEqualTo("6201");
        assertThat(captured.getContactName()).isEqualTo("Jane Doe");
        assertThat(captured.getContactPosition()).isEqualTo("CFO");
        assertThat(captured.getContactEmail()).isEqualTo("jane.doe@acme.example");
        assertThat(captured.getContactPhone()).isEqualTo("+34911223344");
    }
}
