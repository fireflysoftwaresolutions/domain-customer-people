package io.fireflysoftware.domain.people.core.party.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import io.fireflysoftware.core.customer.sdk.api.PartyGroupMembershipsApi;
import io.fireflysoftware.domain.people.core.party.commands.RegisterPartyGroupMembershipCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class RegisterPartyGroupMembershipHandler extends CommandHandler<RegisterPartyGroupMembershipCommand, UUID> {

    private final PartyGroupMembershipsApi partyGroupMembershipsApi;

    public RegisterPartyGroupMembershipHandler(PartyGroupMembershipsApi partyGroupMembershipsApi) {
        this.partyGroupMembershipsApi = partyGroupMembershipsApi;
    }

    @Override
    protected Mono<UUID> doHandle(RegisterPartyGroupMembershipCommand cmd) {
        return partyGroupMembershipsApi
                .createPartyGroupMembership(cmd.getPartyId(), cmd, UUID.randomUUID().toString())
                .mapNotNull(partyGroupMembershipDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(partyGroupMembershipDTO).getPartyGroupMembershipId()));
    }
}