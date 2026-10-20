package io.fireflysoftware.domain.people.core.party.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import io.fireflysoftware.core.customer.sdk.api.PartyRelationshipsApi;
import io.fireflysoftware.domain.people.core.party.commands.RegisterPartyRelationshipCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class RegisterPartyRelationshipHandler extends CommandHandler<RegisterPartyRelationshipCommand, UUID> {

    private final PartyRelationshipsApi partyRelationshipsApi;

    public RegisterPartyRelationshipHandler(PartyRelationshipsApi partyRelationshipsApi) {
        this.partyRelationshipsApi = partyRelationshipsApi;
    }

    @Override
    protected Mono<UUID> doHandle(RegisterPartyRelationshipCommand cmd) {
        return partyRelationshipsApi
                .createPartyRelationship(cmd, UUID.randomUUID().toString())
                .mapNotNull(partyRelationshipDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(partyRelationshipDTO).getPartyRelationshipId()));
    }
}