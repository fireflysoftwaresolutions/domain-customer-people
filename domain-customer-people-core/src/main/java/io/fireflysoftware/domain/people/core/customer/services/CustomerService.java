package io.fireflysoftware.domain.people.core.customer.services;

import io.fireflysoftware.core.customer.sdk.model.FilterRequestPartyDTO;
import io.fireflysoftware.core.customer.sdk.model.NaturalPersonDTO;
import io.fireflysoftware.domain.people.core.customer.commands.RegisterCustomerCommand;
import io.fireflysoftware.domain.people.core.customer.commands.UpdateCustomerCommand;
import io.fireflysoftware.domain.people.core.customer.dto.CustomerFinancialProfileDTO;
import io.fireflysoftware.domain.people.core.customer.dto.CustomerSearchResultDTO;
import org.fireflyframework.orchestration.saga.engine.SagaResult;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CustomerService {
    /**
     * Registers a new customer using the provided registration command.
     * This operation is orchestrated as a saga to ensure data consistency across multiple steps.
     *
     * @param command the registration command containing all customer information
     * @return a Mono containing the saga execution result
     */
    Mono<SagaResult> registerCustomer(RegisterCustomerCommand command);

    /**
     * Updates the information of an existing customer based on the provided update command.
     * This operation is part of a saga to ensure data consistency across multiple related systems.
     *
     * @param command the command containing the updated customer details
     * @return a Mono containing the result of the saga execution
     */
    Mono<SagaResult> updateCustomer(UpdateCustomerCommand command);

    /**
     * Retrieves the information of a customer identified by the provided customer ID.
     *
     * @param customerId the unique identifier of the customer whose information is to be retrieved
     * @return a Mono containing the customer's information as a NaturalPersonDTO, or an empty Mono if no customer is found
     */
    Mono<NaturalPersonDTO> getCustomerInfo(UUID customerId);

    /**
     * Returns the aggregated cross-core financial profile for a party — contracts,
     * loan applications, servicing cases, personal-loan agreements and supply-chain
     * finance agreements. Empty sections are returned as empty lists, not errors.
     *
     * @param partyId the party identifier
     * @return the populated financial profile
     */
    Mono<CustomerFinancialProfileDTO> getFinancialProfile(UUID partyId);

    /**
     * Searches the customer-mgmt party catalog using a filter request and
     * enriches each matching party with its associated natural-person or
     * legal-entity record (chosen by {@code party.partyKind}).
     *
     * <p>Proxies POST /api/v1/parties/filter on core-common-customer-mgmt
     * and fans out per-row lookups in parallel. A failed enrichment lookup
     * is logged and the row is returned with the bare party only.</p>
     *
     * @param filterRequest the filter, range and pagination criteria
     * @return a paginated list of enriched parties
     */
    Mono<CustomerSearchResultDTO> filterCustomers(FilterRequestPartyDTO filterRequest);
}
