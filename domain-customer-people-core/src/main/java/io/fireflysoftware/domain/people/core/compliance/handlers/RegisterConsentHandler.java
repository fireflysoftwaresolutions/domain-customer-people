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


package io.fireflysoftware.domain.people.core.compliance.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import io.fireflysoftware.core.customer.sdk.api.ConsentsApi;
import io.fireflysoftware.core.customer.sdk.model.ConsentDTO;
import io.fireflysoftware.domain.people.core.compliance.commands.RegisterConsentCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class RegisterConsentHandler extends CommandHandler<RegisterConsentCommand, UUID> {

    private final ConsentsApi consentsApi;

    public RegisterConsentHandler(ConsentsApi consentsApi) {
        this.consentsApi = consentsApi;
    }

    @Override
    protected Mono<UUID> doHandle(RegisterConsentCommand cmd) {
        return consentsApi
                .createConsent(cmd.getPartyId(), cmd, UUID.randomUUID().toString())
                .mapNotNull(consentDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(consentDTO).getConsentId()));
    }
}