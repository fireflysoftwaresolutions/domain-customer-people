package io.fireflysoftware.domain.people.core.compliance.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import io.fireflysoftware.core.customer.sdk.api.PartyEconomicActivitiesApi;
import io.fireflysoftware.domain.people.core.compliance.commands.RegisterEconomicActivityLinkCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class RegisterEconomicActivityHandler extends CommandHandler<RegisterEconomicActivityLinkCommand, UUID> {

    private final PartyEconomicActivitiesApi partyEconomicActivitiesApi;

    public RegisterEconomicActivityHandler(PartyEconomicActivitiesApi partyEconomicActivitiesApi) {
        this.partyEconomicActivitiesApi = partyEconomicActivitiesApi;
    }

    @Override
    protected Mono<UUID> doHandle(RegisterEconomicActivityLinkCommand cmd) {
        return partyEconomicActivitiesApi
                .createPartyEconomicActivity(cmd.getPartyId(), cmd, UUID.randomUUID().toString())
                .mapNotNull(economicActivityDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(economicActivityDTO).getEconomicActivityId()));
    }
}