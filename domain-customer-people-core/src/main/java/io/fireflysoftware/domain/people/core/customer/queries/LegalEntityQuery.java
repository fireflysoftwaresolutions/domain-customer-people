package io.fireflysoftware.domain.people.core.customer.queries;

import org.fireflyframework.cqrs.query.Query;
import io.fireflysoftware.core.customer.sdk.model.LegalEntityDTO;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class LegalEntityQuery extends LegalEntityDTO implements Query<LegalEntityDTO> {
    private UUID partyId;

    @Override
    public String getCacheKey() {
        if (!isCacheable()) {
            return null;
        }
        return "LegalEntityQuery:" + getPartyId();
    }
}