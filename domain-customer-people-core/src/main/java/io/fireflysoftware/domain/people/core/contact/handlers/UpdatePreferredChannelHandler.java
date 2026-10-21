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
import io.fireflysoftware.core.customer.sdk.api.EmailContactsApi;
import io.fireflysoftware.core.customer.sdk.api.PhoneContactsApi;
import io.fireflysoftware.core.customer.sdk.model.*;
import io.fireflysoftware.domain.people.core.contact.commands.UpdateEmailCommand;
import io.fireflysoftware.domain.people.core.contact.commands.UpdatePhoneCommand;
import io.fireflysoftware.domain.people.core.contact.commands.UpdatePreferredChannelCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class UpdatePreferredChannelHandler extends CommandHandler<UpdatePreferredChannelCommand, Void> {

    private final EmailContactsApi emailContactsApi;
    private final PhoneContactsApi phoneContactsApi;

    public UpdatePreferredChannelHandler(EmailContactsApi emailContactsApi, PhoneContactsApi phoneContactsApi) {
        this.emailContactsApi = emailContactsApi;
        this.phoneContactsApi = phoneContactsApi;
    }

    @Override
    protected Mono<Void> doHandle(UpdatePreferredChannelCommand cmd) {
        return Mono.when(
                processEmailCommand(cmd),
                processPhoneCommand(cmd)
        );
    }

    private Mono<Void> processEmailCommand(UpdatePreferredChannelCommand cmd) {
        if (cmd.getEmailCommand() == null) {
            return Mono.empty();
        }
        
        // First, set all existing email contacts to non-primary
        FilterRequestEmailContactDTO filterRequest = new FilterRequestEmailContactDTO();
        filterRequest.setFilters(new EmailContactDTO(cmd.getEmailCommand().getEmailContactId()));
        return emailContactsApi.filterEmailContacts(cmd.getPartyId(), filterRequest, UUID.randomUUID().toString())
                .flatMap(paginationResponse -> {
                    // Set all existing email contacts to non-primary
                    return Mono.when(
                            Objects.requireNonNull(paginationResponse.getContent()).stream()
                                    .map(emailContact -> {
                                        emailContact.setIsPrimary(false);
                                        return emailContactsApi.updateEmailContact(
                                                cmd.getPartyId(),
                                                emailContact.getEmailContactId(),
                                                emailContact,
                                                null
                                        );
                                    })
                                    .toArray(Mono[]::new)
                    );
                })
                .then(Mono.defer(() -> {
                    // Then set the current email contact to primary
                    UpdateEmailCommand emailCommand = cmd.getEmailCommand().withPrimary(true);
                    return emailContactsApi.updateEmailContact(cmd.getPartyId(), emailCommand.getEmailContactId(), emailCommand, UUID.randomUUID().toString());
                }))
                .then();
    }

    private Mono<Void> processPhoneCommand(UpdatePreferredChannelCommand cmd) {
        if (cmd.getPhoneCommand() == null) {
            return Mono.empty();
        }
        
        // First, set all existing phone contacts to non-primary
        FilterRequestPhoneContactDTO filterRequest = new FilterRequestPhoneContactDTO();
        filterRequest.setFilters(new PhoneContactDTO(cmd.getPhoneCommand().getPhoneContactId()));
        return phoneContactsApi.filterPhoneContacts(cmd.getPartyId(), filterRequest, UUID.randomUUID().toString())
                .flatMap(paginationResponse -> {
                    // Set all existing phone contacts to non-primary
                    return Mono.when(
                            Objects.requireNonNull(paginationResponse.getContent()).stream()
                                    .map(phoneContact -> {
                                        phoneContact.setIsPrimary(false);
                                        return phoneContactsApi.updatePhoneContact(
                                                cmd.getPartyId(),
                                                phoneContact.getPhoneContactId(),
                                                phoneContact,
                                                null
                                        );
                                    })
                                    .toArray(Mono[]::new)
                    );
                })
                .then(Mono.defer(() -> {
                    // Then set the current phone contact to primary
                    UpdatePhoneCommand phoneCommand = cmd.getPhoneCommand().withPrimary(true);
                    return phoneContactsApi.updatePhoneContact(cmd.getPartyId(), phoneCommand.getPhoneContactId(), phoneCommand, UUID.randomUUID().toString());
                }))
                .then();
    }
}