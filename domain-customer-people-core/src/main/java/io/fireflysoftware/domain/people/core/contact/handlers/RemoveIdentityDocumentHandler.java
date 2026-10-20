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