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
import io.fireflysoftware.core.customer.sdk.api.PartiesApi;
import io.fireflysoftware.domain.people.core.party.commands.RegisterPartyCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class RegisterPartyHandler extends CommandHandler<RegisterPartyCommand, UUID> {

    private final PartiesApi partiesApi;

    public RegisterPartyHandler(PartiesApi partiesApi) {
        this.partiesApi = partiesApi;
    }

    @Override
    protected Mono<UUID> doHandle(RegisterPartyCommand cmd) {

        return partiesApi
                .createParty(cmd, UUID.randomUUID().toString())
                .mapNotNull(partyDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(partyDTO).getPartyId()));
    }
}
