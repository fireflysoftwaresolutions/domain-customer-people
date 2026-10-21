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


package io.fireflysoftware.domain.people.core.contact.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import io.fireflysoftware.core.customer.sdk.api.AddressesApi;
import io.fireflysoftware.core.customer.sdk.api.EmailContactsApi;
import io.fireflysoftware.domain.people.core.contact.commands.UpdateAddressCommand;
import io.fireflysoftware.domain.people.core.contact.commands.UpdateEmailCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class UpdateEmailHandler extends CommandHandler<UpdateEmailCommand, UUID> {

    private final EmailContactsApi emailContactsApi;

    public UpdateEmailHandler(EmailContactsApi emailContactsApi) {
        this.emailContactsApi = emailContactsApi;
    }

    @Override
    protected Mono<UUID> doHandle(UpdateEmailCommand cmd) {
        return emailContactsApi.updateEmailContact(cmd.getPartyId(), cmd.getEmailContactId(), cmd, UUID.randomUUID().toString())
                .mapNotNull(emailContactDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(emailContactDTO).getEmailContactId()));
    }
}