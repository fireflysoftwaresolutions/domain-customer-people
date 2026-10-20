package io.fireflysoftware.domain.people.infra;

import io.fireflysoftware.core.lending.personalloans.sdk.api.PersonalLoanAgreementApi;
import io.fireflysoftware.core.lending.personalloans.sdk.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class PersonalLoansClientFactory {

    private final ApiClient apiClient;

    @Autowired
    public PersonalLoansClientFactory(PersonalLoansProperties properties) {
        this.apiClient = new ApiClient();
        this.apiClient.setBasePath(properties.getBasePath());
    }

    @Bean
    public PersonalLoanAgreementApi personalLoanAgreementApi() {
        return new PersonalLoanAgreementApi(apiClient);
    }
}
