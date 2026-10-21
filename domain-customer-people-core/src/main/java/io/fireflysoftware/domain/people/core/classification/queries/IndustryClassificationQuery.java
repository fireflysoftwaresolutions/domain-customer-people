package io.fireflysoftware.domain.people.core.classification.queries;

import io.fireflysoftware.core.customer.sdk.model.PartyIndustryClassificationDTO;
import org.fireflyframework.cqrs.query.Query;

import java.util.UUID;

public record IndustryClassificationQuery(UUID partyId, UUID classificationId)
        implements Query<PartyIndustryClassificationDTO> {
}
