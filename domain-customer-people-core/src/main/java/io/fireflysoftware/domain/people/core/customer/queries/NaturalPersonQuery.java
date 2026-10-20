package io.fireflysoftware.domain.people.core.customer.queries;

import org.fireflyframework.cqrs.query.Query;
import io.fireflysoftware.core.customer.sdk.model.NaturalPersonDTO;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class NaturalPersonQuery extends NaturalPersonDTO implements Query<NaturalPersonDTO> {
    private UUID partyId;

    @Override
    public String getCacheKey() {
        if (!isCacheable()) {
            return null;
        }
        return "NaturalPersonQuery:" + getPartyId();
    }
}