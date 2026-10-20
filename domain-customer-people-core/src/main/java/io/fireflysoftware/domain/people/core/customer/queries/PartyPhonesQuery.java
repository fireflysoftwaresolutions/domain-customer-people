package io.fireflysoftware.domain.people.core.customer.queries;

import io.fireflysoftware.core.customer.sdk.model.PaginationResponsePhoneContactDTO;
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
public class PartyPhonesQuery extends PaginationResponsePhoneContactDTO implements Query<PaginationResponsePhoneContactDTO> {
    private UUID partyId;

    @Override
    public String getCacheKey() {
        if (!isCacheable()) {
            return null;
        }
        return "PartyPhonesQuery:" + getPartyId();
    }
}
