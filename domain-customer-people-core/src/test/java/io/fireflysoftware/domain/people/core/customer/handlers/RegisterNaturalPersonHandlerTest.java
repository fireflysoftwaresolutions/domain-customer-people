package io.fireflysoftware.domain.people.core.customer.handlers;

import io.fireflysoftware.core.customer.sdk.api.NaturalPersonsApi;
import io.fireflysoftware.core.customer.sdk.model.NaturalPersonDTO;
import io.fireflysoftware.domain.people.core.customer.commands.RegisterNaturalPersonCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterNaturalPersonHandler Tests")
class RegisterNaturalPersonHandlerTest {

    @Mock
    private NaturalPersonsApi naturalPersonsApi;

    private RegisterNaturalPersonHandler handler;

    @BeforeEach
    void setUp() {
        handler = new RegisterNaturalPersonHandler(naturalPersonsApi);
    }

    @Test
    @DisplayName("Should propagate numberOfChildren and maritalStatus to the SDK call")
    void shouldPropagateNewNaturalPersonFieldsToSdk() {
        UUID partyId = UUID.randomUUID();
        UUID naturalPersonId = UUID.randomUUID();

        RegisterNaturalPersonCommand cmd = new RegisterNaturalPersonCommand();
        cmd.setPartyId(partyId);
        cmd.setNumberOfChildren(3);
        cmd.setMaritalStatus(NaturalPersonDTO.MaritalStatusEnum.MARRIED);

        NaturalPersonDTO response = mock(NaturalPersonDTO.class);
        when(response.getNaturalPersonId()).thenReturn(naturalPersonId);

        when(naturalPersonsApi.createNaturalPerson(eq(partyId), any(NaturalPersonDTO.class), any(String.class)))
                .thenReturn(Mono.just(response));

        StepVerifier.create(handler.doHandle(cmd))
                .expectNext(naturalPersonId)
                .verifyComplete();

        ArgumentCaptor<NaturalPersonDTO> captor = ArgumentCaptor.forClass(NaturalPersonDTO.class);
        verify(naturalPersonsApi).createNaturalPerson(eq(partyId), captor.capture(), any(String.class));

        NaturalPersonDTO captured = captor.getValue();
        assertThat(captured.getNumberOfChildren()).isEqualTo(3);
        assertThat(captured.getMaritalStatus()).isEqualTo(NaturalPersonDTO.MaritalStatusEnum.MARRIED);
    }
}
