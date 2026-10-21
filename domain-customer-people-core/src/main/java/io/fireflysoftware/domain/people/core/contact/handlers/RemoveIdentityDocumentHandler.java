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
import io.fireflysoftware.core.customer.sdk.api.IdentityDocumentsApi;
import io.fireflysoftware.domain.people.core.contact.commands.RemoveIdentityDocumentCommand;
import reactor.core.publisher.Mono;

import java.util.UUID;

@CommandHandlerComponent
public class RemoveIdentityDocumentHandler extends CommandHandler<RemoveIdentityDocumentCommand, Void> {

    private final IdentityDocumentsApi identityDocumentsApi;

    public RemoveIdentityDocumentHandler(IdentityDocumentsApi identityDocumentsApi) {
        this.identityDocumentsApi = identityDocumentsApi;
    }

    @Override
    protected Mono<Void> doHandle(RemoveIdentityDocumentCommand cmd) {
        return identityDocumentsApi
                .deleteIdentityDocument(cmd.partyId(), cmd.identityDocumentId(), UUID.randomUUID().toString())
                .then();
    }
}