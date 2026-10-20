package io.fireflysoftware.domain.people.core.customer.queries;

import io.fireflysoftware.core.customer.sdk.model.FilterRequestPartyDTO;
import io.fireflysoftware.domain.people.core.customer.dto.CustomerSearchResultDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fireflyframework.cqrs.query.Query;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterCustomersQuery implements Query<CustomerSearchResultDTO> {

    private FilterRequestPartyDTO filterRequest;

    @Override
    public String getCacheKey() {
        return null;
    }
}
