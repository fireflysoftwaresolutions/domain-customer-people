package io.fireflysoftware.domain.people.core.classification.handlers;

import io.fireflysoftware.core.customer.sdk.api.IndustryClassificationsApi;
import io.fireflysoftware.domain.people.core.classification.commands.UpdatePartyIndustryClassificationCommand;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class UpdatePartyIndustryClassificationHandler
        extends CommandHandler<UpdatePartyIndustryClassificationCommand, UUID> {

    private final IndustryClassificationsApi industryClassificationsApi;

    public UpdatePartyIndustryClassificationHandler(IndustryClassificationsApi industryClassificationsApi) {
        this.industryClassificationsApi = industryClassificationsApi;
    }

    @Override
    protected Mono<UUID> doHandle(UpdatePartyIndustryClassificationCommand cmd) {
        return industryClassificationsApi
                .update(cmd.getPartyId(), cmd.classificationId(), cmd, UUID.randomUUID().toString())
                .mapNotNull(dto -> Objects.requireNonNull(Objects.requireNonNull(dto).getPartyIndustryClassificationId()));
    }
}
