package io.fireflysoftware.domain.people.core.customer.dto;

import io.fireflysoftware.core.contract.sdk.model.ContractDTO;
import io.fireflysoftware.core.lending.assetfinance.sdk.model.AssetFinanceAgreementDTO;
import io.fireflysoftware.core.lending.origination.sdk.model.LoanApplicationDTO;
import io.fireflysoftware.core.lending.personalloans.sdk.model.PersonalLoanAgreementDTO;
import io.fireflysoftware.core.lending.servicing.sdk.model.LoanServicingCaseDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * Aggregated cross-core financial profile for a single party.
 *
 * <p>Each section may be empty when the party has no rows in the underlying
 * table — sections are independent and one missing section does not fail the
 * other sections.</p>
 */
@Data
@Builder
public class CustomerFinancialProfileDTO {

    private UUID partyId;
    private List<ContractDTO> contracts;
    private List<LoanApplicationDTO> loanApplications;
    private List<LoanServicingCaseDTO> loanServicingCases;
    private List<PersonalLoanAgreementDTO> personalLoanAgreements;
    private List<AssetFinanceAgreementDTO> assetFinanceAgreements;
}
