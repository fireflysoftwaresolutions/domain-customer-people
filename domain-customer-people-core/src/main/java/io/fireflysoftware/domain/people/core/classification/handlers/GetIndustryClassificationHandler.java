package io.fireflysoftware.domain.people.core.classification.handlers;

import io.fireflysoftware.core.customer.sdk.api.IndustryClassificationsApi;
import io.fireflysoftware.core.customer.sdk.model.PartyIndustryClassificationDTO;
import io.fireflysoftware.domain.people.core.classification.queries.IndustryClassificationQuery;
import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import reactor.core.publisher.Mono;

import java.util.UUID;

@QueryHandlerComponent
public class GetIndustryClassificationHandler
        extends QueryHandler<IndustryClassificationQuery, PartyIndustryClassificationDTO> {

    private final IndustryClassificationsApi industryClassificationsApi;

    public GetIndustryClassificationHandler(IndustryClassificationsApi industryClassificationsApi) {
        this.industryClassificationsApi = industryClassificationsApi;
    }

    @Override
    protected Mono<PartyIndustryClassificationDTO> doHandle(IndustryClassificationQuery query) {
        return industryClassificationsApi.get(query.partyId(), query.classificationId(), UUID.randomUUID().toString());
    }
}
