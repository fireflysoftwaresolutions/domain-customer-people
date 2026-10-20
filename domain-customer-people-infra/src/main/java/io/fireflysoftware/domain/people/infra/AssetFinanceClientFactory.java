package io.fireflysoftware.domain.people.infra;

import io.fireflysoftware.core.lending.assetfinance.sdk.api.AssetFinanceAgreementApi;
import io.fireflysoftware.core.lending.assetfinance.sdk.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class AssetFinanceClientFactory {

    private final ApiClient apiClient;

    @Autowired
    public AssetFinanceClientFactory(AssetFinanceProperties properties) {
        this.apiClient = new ApiClient();
        this.apiClient.setBasePath(properties.getBasePath());
    }

    @Bean
    public AssetFinanceAgreementApi assetFinanceAgreementApi() {
        return new AssetFinanceAgreementApi(apiClient);
    }
}
