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