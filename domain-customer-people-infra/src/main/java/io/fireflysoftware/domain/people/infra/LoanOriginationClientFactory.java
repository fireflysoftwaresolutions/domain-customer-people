package io.fireflysoftware.domain.people.infra;

import io.fireflysoftware.core.lending.origination.sdk.api.ApplicationPartyQueryApi;
import io.fireflysoftware.core.lending.origination.sdk.api.LoanApplicationsApi;
import io.fireflysoftware.core.lending.origination.sdk.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class LoanOriginationClientFactory {

    private final ApiClient apiClient;

    @Autowired
    public LoanOriginationClientFactory(LoanOriginationProperties properties) {
        this.apiClient = new ApiClient();
        this.apiClient.setBasePath(properties.getBasePath());
    }

    @Bean
    public ApplicationPartyQueryApi applicationPartyQueryApi() {
        return new ApplicationPartyQueryApi(apiClient);
    }

    @Bean
    public LoanApplicationsApi loanApplicationsApi() {
        return new LoanApplicationsApi(apiClient);
    }
}
