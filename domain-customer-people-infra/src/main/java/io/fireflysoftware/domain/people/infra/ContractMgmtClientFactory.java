package io.fireflysoftware.domain.people.infra;

import io.fireflysoftware.core.contract.sdk.api.ContractsApi;
import io.fireflysoftware.core.contract.sdk.api.GlobalContractPartiesApi;
import io.fireflysoftware.core.contract.sdk.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ContractMgmtClientFactory {

    private final ApiClient apiClient;

    @Autowired
    public ContractMgmtClientFactory(ContractMgmtProperties properties) {
        this.apiClient = new ApiClient();
        this.apiClient.setBasePath(properties.getBasePath());
    }

    @Bean
    public GlobalContractPartiesApi globalContractPartiesApi() {
        return new GlobalContractPartiesApi(apiClient);
    }

    @Bean
    public ContractsApi contractsApi() {
        return new ContractsApi(apiClient);
    }
}
