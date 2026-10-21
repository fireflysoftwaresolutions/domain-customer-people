/*
 * Copyright 2025 Firefly Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package io.fireflysoftware.domain.people.core.customer.handlers;

import io.fireflysoftware.core.contract.sdk.api.ContractsApi;
import io.fireflysoftware.core.contract.sdk.api.GlobalContractPartiesApi;
import io.fireflysoftware.core.contract.sdk.model.ContractDTO;
import io.fireflysoftware.core.contract.sdk.model.ContractPartyDTO;
import io.fireflysoftware.core.lending.assetfinance.sdk.api.AssetFinanceAgreementApi;
import io.fireflysoftware.core.lending.assetfinance.sdk.model.AssetFinanceAgreementDTO;
import io.fireflysoftware.core.lending.assetfinance.sdk.model.FilterRequestAssetFinanceAgreementDTO;
import io.fireflysoftware.core.lending.origination.sdk.api.ApplicationPartyQueryApi;
import io.fireflysoftware.core.lending.origination.sdk.api.LoanApplicationsApi;
import io.fireflysoftware.core.lending.origination.sdk.model.ApplicationPartyDTO;
import io.fireflysoftware.core.lending.origination.sdk.model.LoanApplicationDTO;
import io.fireflysoftware.core.lending.personalloans.sdk.api.PersonalLoanAgreementApi;
import io.fireflysoftware.core.lending.personalloans.sdk.model.FilterRequestPersonalLoanAgreementDTO;
import io.fireflysoftware.core.lending.personalloans.sdk.model.PersonalLoanAgreementDTO;
import io.fireflysoftware.core.lending.servicing.sdk.api.LoanServicingCaseApi;
import io.fireflysoftware.core.lending.servicing.sdk.model.FilterRequestLoanServicingCaseDTO;
import io.fireflysoftware.core.lending.servicing.sdk.model.LoanServicingCaseDTO;
import io.fireflysoftware.domain.people.core.customer.dto.CustomerFinancialProfileDTO;
import io.fireflysoftware.domain.people.core.customer.queries.CustomerFinancialProfileQuery;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@QueryHandlerComponent
public class GetCustomerFinancialProfileHandler
        extends QueryHandler<CustomerFinancialProfileQuery, CustomerFinancialProfileDTO> {

    private final GlobalContractPartiesApi globalContractPartiesApi;
    private final ContractsApi contractsApi;
    private final ApplicationPartyQueryApi applicationPartyQueryApi;
    private final LoanApplicationsApi loanApplicationsApi;
    private final LoanServicingCaseApi loanServicingCaseApi;
    private final PersonalLoanAgreementApi personalLoanAgreementApi;
    private final AssetFinanceAgreementApi assetFinanceAgreementApi;

    public GetCustomerFinancialProfileHandler(
            GlobalContractPartiesApi globalContractPartiesApi,
            ContractsApi contractsApi,
            ApplicationPartyQueryApi applicationPartyQueryApi,
            LoanApplicationsApi loanApplicationsApi,
            LoanServicingCaseApi loanServicingCaseApi,
            PersonalLoanAgreementApi personalLoanAgreementApi,
            AssetFinanceAgreementApi assetFinanceAgreementApi) {
        this.globalContractPartiesApi = globalContractPartiesApi;
        this.contractsApi = contractsApi;
        this.applicationPartyQueryApi = applicationPartyQueryApi;
        this.loanApplicationsApi = loanApplicationsApi;
        this.loanServicingCaseApi = loanServicingCaseApi;
        this.personalLoanAgreementApi = personalLoanAgreementApi;
        this.assetFinanceAgreementApi = assetFinanceAgreementApi;
    }

    @Override
    protected Mono<CustomerFinancialProfileDTO> doHandle(CustomerFinancialProfileQuery query) {
        UUID partyId = query.getPartyId();
        log.debug("Building financial profile for partyId={}", partyId);

        Mono<List<ContractDTO>> contractsMono = fetchContracts(partyId);
        Mono<List<LoanApplicationDTO>> applicationsMono = fetchLoanApplications(partyId);

        return Mono.zip(contractsMono, applicationsMono)
                .flatMap(tuple -> {
                    List<ContractDTO> contracts = tuple.getT1();
                    List<LoanApplicationDTO> applications = tuple.getT2();

                    List<UUID> contractIds = contracts.stream()
                            .map(ContractDTO::getContractId)
                            .filter(java.util.Objects::nonNull)
                            .toList();
                    List<UUID> applicationIds = applications.stream()
                            .map(LoanApplicationDTO::getLoanApplicationId)
                            .filter(java.util.Objects::nonNull)
                            .toList();

                    Mono<List<LoanServicingCaseDTO>> servicingCasesMono = fetchServicingCases(contractIds);
                    Mono<List<PersonalLoanAgreementDTO>> personalLoansMono = fetchPersonalLoanAgreements(applicationIds);

                    return servicingCasesMono
                            .flatMap(servicingCases -> {
                                List<UUID> servicingCaseIds = servicingCases.stream()
                                        .map(LoanServicingCaseDTO::getLoanServicingCaseId)
                                        .filter(java.util.Objects::nonNull)
                                        .toList();
                                return Mono.zip(
                                        personalLoansMono,
                                        fetchAssetFinanceAgreements(servicingCaseIds)
                                ).map(t -> CustomerFinancialProfileDTO.builder()
                                        .partyId(partyId)
                                        .contracts(contracts)
                                        .loanApplications(applications)
                                        .loanServicingCases(servicingCases)
                                        .personalLoanAgreements(t.getT1())
                                        .assetFinanceAgreements(t.getT2())
                                        .build());
                            });
                });
    }

    private Mono<List<ContractDTO>> fetchContracts(UUID partyId) {
        return globalContractPartiesApi
                .getContractPartiesByPartyId(partyId, null, idempotencyKey())
                .map(page -> page.getContent() == null ? List.<ContractPartyDTO>of() : page.getContent())
                .flatMapMany(Flux::fromIterable)
                .map(ContractPartyDTO::getContractId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .flatMap(id -> contractsApi.getContractById(id, idempotencyKey())
                        .onErrorResume(WebClientResponseException.NotFound.class, ex -> Mono.empty()))
                .collectList()
                .onErrorResume(ex -> {
                    log.warn("Contracts section failed for partyId={}: {}", partyId, ex.toString());
                    return Mono.just(Collections.emptyList());
                });
    }

    private Mono<List<LoanApplicationDTO>> fetchLoanApplications(UUID partyId) {
        return applicationPartyQueryApi.findByPartyId(partyId, idempotencyKey())
                .map(ApplicationPartyDTO::getLoanApplicationId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .flatMap(id -> loanApplicationsApi.getLoanApplication(id, idempotencyKey())
                        .onErrorResume(WebClientResponseException.NotFound.class, ex -> Mono.empty()))
                .collectList()
                .onErrorResume(ex -> {
                    log.warn("Loan applications section failed for partyId={}: {}", partyId, ex.toString());
                    return Mono.just(Collections.emptyList());
                });
    }

    private Mono<List<LoanServicingCaseDTO>> fetchServicingCases(List<UUID> contractIds) {
        if (contractIds.isEmpty()) {
            return Mono.just(Collections.emptyList());
        }
        return Flux.fromIterable(contractIds)
                .flatMap(contractId -> {
                    LoanServicingCaseDTO filters = new LoanServicingCaseDTO();
                    filters.setContractId(contractId);
                    FilterRequestLoanServicingCaseDTO req = new FilterRequestLoanServicingCaseDTO();
                    req.setFilters(filters);
                    return loanServicingCaseApi.findAllServicingCases(req, idempotencyKey())
                            .map(page -> page.getContent() == null ? List.<LoanServicingCaseDTO>of() : page.getContent())
                            .onErrorResume(ex -> {
                                log.warn("Servicing-case lookup failed for contractId={}: {}", contractId, ex.toString());
                                return Mono.just(Collections.emptyList());
                            });
                })
                .flatMapIterable(list -> list)
                .collectList();
    }

    private Mono<List<PersonalLoanAgreementDTO>> fetchPersonalLoanAgreements(List<UUID> applicationIds) {
        if (applicationIds.isEmpty()) {
            return Mono.just(Collections.emptyList());
        }
        return Flux.fromIterable(applicationIds)
                .flatMap(appId -> {
                    PersonalLoanAgreementDTO filters = new PersonalLoanAgreementDTO();
                    filters.setApplicationId(appId);
                    FilterRequestPersonalLoanAgreementDTO req = new FilterRequestPersonalLoanAgreementDTO();
                    req.setFilters(filters);
                    return personalLoanAgreementApi.findAll(req, idempotencyKey())
                            .map(page -> page.getContent() == null ? List.<PersonalLoanAgreementDTO>of() : page.getContent())
                            .onErrorResume(ex -> {
                                log.warn("Personal-loan lookup failed for applicationId={}: {}", appId, ex.toString());
                                return Mono.just(Collections.emptyList());
                            });
                })
                .flatMapIterable(list -> list)
                .collectList();
    }

    private Mono<List<AssetFinanceAgreementDTO>> fetchAssetFinanceAgreements(List<UUID> servicingCaseIds) {
        if (servicingCaseIds.isEmpty()) {
            return Mono.just(Collections.emptyList());
        }
        return Flux.fromIterable(servicingCaseIds)
                .flatMap(caseId -> {
                    AssetFinanceAgreementDTO filters = new AssetFinanceAgreementDTO();
                    filters.setLoanServicingCaseId(caseId);
                    FilterRequestAssetFinanceAgreementDTO req = new FilterRequestAssetFinanceAgreementDTO();
                    req.setFilters(filters);
                    return assetFinanceAgreementApi.findAll(req, idempotencyKey())
                            .map(page -> page.getContent() == null ? List.<AssetFinanceAgreementDTO>of() : page.getContent())
                            .onErrorResume(ex -> {
                                log.warn("Asset-finance lookup failed for servicingCaseId={}: {}", caseId, ex.toString());
                                return Mono.just(Collections.emptyList());
                            });
                })
                .flatMapIterable(list -> list)
                .collectList();
    }

    private static String idempotencyKey() {
        return UUID.randomUUID().toString();
    }
}
