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