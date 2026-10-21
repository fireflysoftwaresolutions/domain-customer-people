package io.fireflysoftware.domain.people.core.classification.handlers;

import io.fireflysoftware.core.customer.sdk.api.IndustryClassificationsApi;
import io.fireflysoftware.domain.people.core.classification.commands.RegisterPartyIndustryClassificationCommand;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class RegisterPartyIndustryClassificationHandler
        extends CommandHandler<RegisterPartyIndustryClassificationCommand, UUID> {

    private final IndustryClassificationsApi industryClassificationsApi;

    public RegisterPartyIndustryClassificationHandler(IndustryClassificationsApi industryClassificationsApi) {
        this.industryClassificationsApi = industryClassificationsApi;
    }

    @Override
    protected Mono<UUID> doHandle(RegisterPartyIndustryClassificationCommand cmd) {
        return industryClassificationsApi
                .create(cmd.getPartyId(), cmd, UUID.randomUUID().toString())
                .mapNotNull(dto -> Objects.requireNonNull(Objects.requireNonNull(dto).getPartyIndustryClassificationId()));
    }
}
