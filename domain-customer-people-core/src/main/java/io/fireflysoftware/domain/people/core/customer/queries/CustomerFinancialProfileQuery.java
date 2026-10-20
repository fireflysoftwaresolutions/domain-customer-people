package io.fireflysoftware.domain.people.core.customer.queries;

import io.fireflysoftware.domain.people.core.customer.dto.CustomerFinancialProfileDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fireflyframework.cqrs.query.Query;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerFinancialProfileQuery implements Query<CustomerFinancialProfileDTO> {

    private UUID partyId;

    @Override
    public String getCacheKey() {
        if (!isCacheable()) {
            return null;
        }
        return "CustomerFinancialProfileQuery:" + partyId;
    }
}
