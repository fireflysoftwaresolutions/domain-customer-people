package io.fireflysoftware.domain.people.core.customer.queries;

import io.fireflysoftware.core.customer.sdk.model.PaginationResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.fireflyframework.cqrs.query.Query;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartyConsentsQuery extends PaginationResponse implements Query<PaginationResponse> {
    private UUID partyId;

    @Override
    public String getCacheKey() {
        if (!isCacheable()) {
            return null;
        }
        return "PartyConsentsQuery:" + getPartyId();
    }
}
