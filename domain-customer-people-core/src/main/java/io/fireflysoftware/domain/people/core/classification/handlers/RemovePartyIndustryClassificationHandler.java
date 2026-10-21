package io.fireflysoftware.domain.people.core.classification.handlers;

import io.fireflysoftware.core.customer.sdk.api.IndustryClassificationsApi;
import io.fireflysoftware.domain.people.core.classification.commands.RemovePartyIndustryClassificationCommand;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;

import java.util.UUID;

@CommandHandlerComponent
public class RemovePartyIndustryClassificationHandler
        extends CommandHandler<RemovePartyIndustryClassificationCommand, Void> {

    private final IndustryClassificationsApi industryClassificationsApi;

    public RemovePartyIndustryClassificationHandler(IndustryClassificationsApi industryClassificationsApi) {
        this.industryClassificationsApi = industryClassificationsApi;
    }

    @Override
    protected Mono<Void> doHandle(RemovePartyIndustryClassificationCommand cmd) {
        return industryClassificationsApi
                .delete(cmd.partyId(), cmd.classificationId(), UUID.randomUUID().toString())
                .then();
    }
}
