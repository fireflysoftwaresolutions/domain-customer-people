package io.fireflysoftware.domain.people.infra;

import io.fireflysoftware.common.reference.master.data.sdk.api.ConsentCatalogApi;
import io.fireflysoftware.core.customer.sdk.api.*;
import io.fireflysoftware.core.customer.sdk.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Default implementation of the ClientFactory interface.
 * Creates client service instances using the appropriate API clients and dependencies.
 */
@Component
public class ClientFactory {

    private final ApiClient apiClient;
    private final io.fireflysoftware.common.reference.master.data.sdk.invoker.ApiClient referenceMasterDataApiClient;

    @Autowired
    public ClientFactory(
            CustomerMgmtProperties customerMgmtProperties,
            ReferenceMasterDataProperties referenceMasterDataProperties) {
        this.apiClient = new ApiClient();
        this.apiClient.setBasePath(customerMgmtProperties.getBasePath());

        this.referenceMasterDataApiClient = new io.fireflysoftware.common.reference.master.data.sdk.invoker.ApiClient();
        this.referenceMasterDataApiClient.setBasePath(referenceMasterDataProperties.getBasePath());
    }

    @Bean
    public PartiesApi partiesApi() {
        return new PartiesApi(apiClient);
    }

    @Bean
    public NaturalPersonsApi naturalPersonsApi() {
        return new NaturalPersonsApi(apiClient);
    }

    @Bean
    public LegalEntitiesApi legalEntitiesApi() {
        return new LegalEntitiesApi(apiClient);
    }

    @Bean
    public PartyStatusesApi partyStatusesApi() {
        return new PartyStatusesApi(apiClient);
    }

    @Bean
    public PoliticallyExposedPersonsApi politicallyExposedPersonsApi() {
        return new PoliticallyExposedPersonsApi(apiClient);
    }

    @Bean
    public IdentityDocumentsApi identityDocumentsApi() {
        return new IdentityDocumentsApi(apiClient);
    }

    @Bean
    public AddressesApi addressesApi() {
        return new AddressesApi(apiClient);
    }

    @Bean
    public EmailContactsApi emailContactsApi() {
        return new EmailContactsApi(apiClient);
    }

    @Bean
    public PhoneContactsApi phoneContactsApi() {
        return new PhoneContactsApi(apiClient);
    }

    @Bean
    public PartyEconomicActivitiesApi partyEconomicActivitiesApi() {
        return new PartyEconomicActivitiesApi(apiClient);
    }

    @Bean
    public ConsentsApi consentsApi() {
        return new ConsentsApi(apiClient);
    }

    @Bean
    public PartyProvidersApi partyProvidersApi() {
        return new PartyProvidersApi(apiClient);
    }

    @Bean
    public PartyRelationshipsApi partyRelationshipsApi() {
        return new PartyRelationshipsApi(apiClient);
    }

    @Bean
    public PartyGroupMembershipsApi partyGroupMembershipsApi() {
        return new PartyGroupMembershipsApi(apiClient);
    }

    @Bean
    public ConsentCatalogApi consentCatalogApi() {
        return new ConsentCatalogApi(referenceMasterDataApiClient);
    }

    @Bean
    public IndustryClassificationsApi industryClassificationsApi() {
        return new IndustryClassificationsApi(apiClient);
    }

    @Bean
    public PartyAttributesApi partyAttributesApi() {
        return new PartyAttributesApi(apiClient);
    }

    @Bean
    public PartyHierarchyApi partyHierarchyApi() {
        return new PartyHierarchyApi(apiClient);
    }

}
