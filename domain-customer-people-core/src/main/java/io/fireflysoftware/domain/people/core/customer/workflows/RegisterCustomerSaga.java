package io.fireflysoftware.domain.people.core.customer.workflows;
import io.fireflysoftware.domain.people.core.business.commands.RegisterLegalEntityCommand;
import io.fireflysoftware.domain.people.core.business.commands.RemoveLegalEntityCommand;
import io.fireflysoftware.domain.people.core.compliance.commands.RemoveConsentCommand;
import io.fireflysoftware.domain.people.core.compliance.commands.RemovePepCommand;
import io.fireflysoftware.domain.people.core.contact.commands.*;
import io.fireflysoftware.domain.people.core.compliance.commands.RemoveEconomicActivityLinkCommand;
import io.fireflysoftware.domain.people.core.contact.commands.RemoveIdentityDocumentCommand;
import io.fireflysoftware.domain.people.core.customer.commands.RemoveNaturalPersonCommand;
import io.fireflysoftware.domain.people.core.party.commands.RemovePartyCommand;
import io.fireflysoftware.domain.people.core.party.commands.RemovePartyProviderCommand;
import io.fireflysoftware.domain.people.core.party.commands.RegisterPartyGroupMembershipCommand;
import io.fireflysoftware.domain.people.core.party.commands.RegisterPartyRelationshipCommand;
import io.fireflysoftware.domain.people.core.party.commands.RegisterPartyProviderCommand;
import io.fireflysoftware.domain.people.core.compliance.commands.RegisterConsentCommand;
import io.fireflysoftware.domain.people.core.compliance.commands.RegisterEconomicActivityLinkCommand;
import io.fireflysoftware.domain.people.core.contact.commands.RegisterIdentityDocumentCommand;
import io.fireflysoftware.domain.people.core.compliance.commands.RegisterPepCommand;
import io.fireflysoftware.domain.people.core.party.commands.RemovePartyGroupMembershipCommand;
import io.fireflysoftware.domain.people.core.party.commands.RemovePartyRelationshipCommand;
import io.fireflysoftware.domain.people.core.status.commands.RegisterPartyStatusEntryCommand;
import io.fireflysoftware.domain.people.core.customer.commands.RegisterNaturalPersonCommand;
import io.fireflysoftware.domain.people.core.attribute.commands.RegisterPartyAttributeCommand;
import io.fireflysoftware.domain.people.core.attribute.commands.RemovePartyAttributeCommand;
import io.fireflysoftware.domain.people.core.classification.commands.RegisterPartyIndustryClassificationCommand;
import io.fireflysoftware.domain.people.core.classification.commands.RemovePartyIndustryClassificationCommand;
import io.fireflysoftware.domain.people.core.party.commands.RegisterPartyCommand;

import org.fireflyframework.cqrs.command.CommandBus;
import io.fireflysoftware.domain.people.core.status.commands.RemovePartyStatusEntryCommand;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static io.fireflysoftware.domain.people.core.utils.constants.GlobalConstants.CTX_PARTY_ID;
import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.*;


/**
 * Saga orchestrator for customer registration processes.
 * 
 * This orchestrator manages the distributed transaction for registering customers,
 * coordinating multiple steps including party creation, person details registration,
 * contact information setup, and relationship establishment. Each step is designed
 * to be compensatable to ensure data consistency in case of failures.
 * 
 * The orchestrator handles both natural persons and legal entities, with conditional
 * logic to process only relevant information based on the customer type.
 */
@Saga(name = SAGA_REGISTER_CUSTOMER_NAME)
@Service
public class RegisterCustomerSaga {

    // The framework treats a @SagaStep that emits Mono.empty() as a step FAILURE, which
    // dead-letters the saga even when the step intentionally has no work to do. For optional
    // branches (e.g. natural-person-only flows that have no legal entity, no PEP and no
    // consents) we emit this sentinel UUID instead so the step counts as "success – nothing
    // produced". Compensation methods skip it.
    private static final UUID SKIPPED_STEP_SENTINEL = new UUID(0L, 0L);

    private final CommandBus commandBus;

    @Autowired
    public RegisterCustomerSaga(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    private static boolean isSkipped(UUID id) {
        return id == null || SKIPPED_STEP_SENTINEL.equals(id);
    }

    @SagaStep(id = STEP_REGISTER_PARTY, compensate = COMPENSATE_REMOVE_PARTY)
    @StepEvent(type = EVENT_PARTY_REGISTERED)
    public Mono<UUID> registerParty(RegisterPartyCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd)
                .doOnNext(partyId -> ctx.putVariable(CTX_PARTY_ID, partyId));
    }

    public Mono<Void> removeParty(UUID partyId) {
        return commandBus.send(new RemovePartyCommand(partyId));
    }

    @SagaStep(id = STEP_REGISTER_NATURAL_PERSON, compensate = COMPENSATE_REMOVE_NATURAL_PERSON, dependsOn = STEP_REGISTER_PARTY)
    @StepEvent(type = EVENT_NATURAL_PERSON_REGISTERED)
    public Mono<UUID> registerNaturalPerson(RegisterNaturalPersonCommand cmd, ExecutionContext ctx) {
        return cmd == null
                ? Mono.just(SKIPPED_STEP_SENTINEL)
                : commandBus.send(cmd.withPartyId((UUID) ctx.getVariable(CTX_PARTY_ID)));
    }

    public Mono<Void> removeNaturalPerson(UUID naturalPersonId, ExecutionContext ctx) {
        return isSkipped(naturalPersonId)
                ? Mono.empty()
                : commandBus.send(new RemoveNaturalPersonCommand((UUID) ctx.getVariable(CTX_PARTY_ID), naturalPersonId));
    }

    @SagaStep(id = STEP_REGISTER_LEGAL_ENTITY, compensate = COMPENSATE_REMOVE_LEGAL_ENTITY, dependsOn = STEP_REGISTER_PARTY)
    @StepEvent(type = EVENT_LEGAL_ENTITY_REGISTERED)
    public Mono<UUID> registerLegalEntity(RegisterLegalEntityCommand cmd, ExecutionContext ctx) {
        return cmd == null
                ? Mono.just(SKIPPED_STEP_SENTINEL)
                : commandBus.send(cmd.withPartyId((UUID) ctx.getVariable(CTX_PARTY_ID)));
    }

    public Mono<Void> removeLegalEntity(UUID legalEntityId, ExecutionContext ctx) {
        return isSkipped(legalEntityId)
                ? Mono.empty()
                : commandBus.send(new RemoveLegalEntityCommand((UUID) ctx.getVariable(CTX_PARTY_ID), legalEntityId));
    }

    @SagaStep(id = STEP_REGISTER_STATUS_ENTRY, compensate = COMPENSATE_REMOVE_STATUS_ENTRY, dependsOn = STEP_REGISTER_PARTY)
    @StepEvent(type = EVENT_PARTY_STATUS_REGISTERED)
    public Mono<UUID> registerStatusEntry(RegisterPartyStatusEntryCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd.withPartyId((UUID) ctx.getVariable(CTX_PARTY_ID)));
    }

    public Mono<Void> removeStatusEntry(UUID partyStatusId, ExecutionContext ctx) {
        return commandBus.send(new RemovePartyStatusEntryCommand((UUID) ctx.getVariable(CTX_PARTY_ID), partyStatusId));
    }

    @SagaStep(id = STEP_REGISTER_PEP, compensate = COMPENSATE_REMOVE_PEP, dependsOn = STEP_REGISTER_PARTY)
    @StepEvent(type = EVENT_PEP_REGISTERED)
    public Mono<UUID> registerPep(RegisterPepCommand cmd, ExecutionContext ctx) {
        return cmd == null
                ? Mono.just(SKIPPED_STEP_SENTINEL)
                : commandBus.send(cmd.withPartyId((UUID) ctx.getVariable(CTX_PARTY_ID)));
    }

    public Mono<Void> removePep(UUID pepId, ExecutionContext ctx) {
        return isSkipped(pepId)
                ? Mono.empty()
                : commandBus.send(new RemovePepCommand((UUID) ctx.getVariable(CTX_PARTY_ID), pepId));
    }

    @SagaStep(id = STEP_REGISTER_IDENTITY_DOCUMENT, compensate = COMPENSATE_REMOVE_IDENTITY_DOCUMENT, dependsOn = STEP_REGISTER_PARTY)
    @StepEvent(type = EVENT_IDENTITY_DOCUMENT_REGISTERED)
    public Mono<UUID> registerIdentityDocument(RegisterIdentityDocumentCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd.withPartyId((UUID) ctx.getVariable(CTX_PARTY_ID)));
    }

    public Mono<Void> removeIdentityDocument(UUID identityDocumentId, ExecutionContext ctx) {
        return commandBus.send(new RemoveIdentityDocumentCommand((UUID) ctx.getVariable(CTX_PARTY_ID), identityDocumentId));
    }

    @SagaStep(id = STEP_REGISTER_ADDRESS, compensate = COMPENSATE_REMOVE_ADDRESS, dependsOn = STEP_REGISTER_PARTY)
    @StepEvent(type = EVENT_ADDRESS_REGISTERED)
    public Mono<UUID> registerAddress(RegisterAddressCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd.withPartyId((UUID) ctx.getVariable(CTX_PARTY_ID)));
    }

    public Mono<Void> removeAddress(UUID addressId, ExecutionContext ctx) {
        return commandBus.send(new RemoveAddressCommand((UUID) ctx.getVariable(CTX_PARTY_ID), addressId));
    }

    @SagaStep(id = STEP_REGISTER_EMAIL, compensate = COMPENSATE_REMOVE_EMAIL, dependsOn = STEP_REGISTER_PARTY)
    @StepEvent(type = EVENT_EMAIL_REGISTERED)
    public Mono<UUID> registerEmail(RegisterEmailCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd.withPartyId((UUID) ctx.getVariable(CTX_PARTY_ID)));
    }

    public Mono<Void> removeEmail(UUID emailId, ExecutionContext ctx) {
        return commandBus.send(new RemoveEmailCommand((UUID) ctx.getVariable(CTX_PARTY_ID), emailId));
    }

    @SagaStep(id = STEP_REGISTER_PHONE, compensate = COMPENSATE_REMOVE_PHONE, dependsOn = STEP_REGISTER_PARTY)
    @StepEvent(type = EVENT_PHONE_REGISTERED)
    public Mono<UUID> registerPhone(RegisterPhoneCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd.withPartyId((UUID) ctx.getVariable(CTX_PARTY_ID)));
    }

    public Mono<Void> removePhone(UUID phoneId, ExecutionContext ctx) {
        return commandBus.send(new RemovePhoneCommand((UUID) ctx.getVariable(CTX_PARTY_ID), phoneId));
    }

    @SagaStep(id = STEP_REGISTER_ECONOMIC_ACTIVITY_LINK, compensate = COMPENSATE_REMOVE_ECONOMIC_ACTIVITY_LINK, dependsOn = STEP_REGISTER_PARTY)
    @StepEvent(type = EVENT_ECONOMIC_ACTIVITY_REGISTERED)
    public Mono<UUID> registerEconomicActivityLink(RegisterEconomicActivityLinkCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd.withPartyId((UUID) ctx.getVariable(CTX_PARTY_ID)));
    }

    public Mono<Void> removeEconomicActivityLink(UUID economicActivityId, ExecutionContext ctx) {
        return commandBus.send(new RemoveEconomicActivityLinkCommand((UUID) ctx.getVariable(CTX_PARTY_ID), economicActivityId));
    }

    @SagaStep(id = STEP_REGISTER_CONSENT, compensate = COMPENSATE_REMOVE_CONSENT, dependsOn = STEP_REGISTER_PARTY)
    @StepEvent(type = EVENT_CONSENT_REGISTERED)
    public Mono<UUID> registerConsent(RegisterConsentCommand cmd, ExecutionContext ctx) {
        return cmd == null
                ? Mono.just(SKIPPED_STEP_SENTINEL)
                : commandBus.send(cmd.withPartyId((UUID) ctx.getVariable(CTX_PARTY_ID)));
    }

    public Mono<Void> removeConsent(UUID consentId, ExecutionContext ctx) {
        return isSkipped(consentId)
                ? Mono.empty()
                : commandBus.send(new RemoveConsentCommand((UUID) ctx.getVariable(CTX_PARTY_ID), consentId));
    }

    @SagaStep(id = STEP_REGISTER_PARTY_PROVIDER, compensate = COMPENSATE_REMOVE_PARTY_PROVIDER, dependsOn = STEP_REGISTER_PARTY)
    @StepEvent(type = EVENT_PARTY_PROVIDER_REGISTERED)
    public Mono<UUID> registerPartyProvider(RegisterPartyProviderCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd.withPartyId((UUID) ctx.getVariable(CTX_PARTY_ID)));
    }

    public Mono<Void> removePartyProvider(UUID partyProviderId, ExecutionContext ctx) {
        return commandBus.send(new RemovePartyProviderCommand((UUID) ctx.getVariable(CTX_PARTY_ID), partyProviderId));
    }

    @SagaStep(id = STEP_REGISTER_PARTY_RELATIONSHIP, compensate = COMPENSATE_REMOVE_PARTY_RELATIONSHIP, dependsOn = STEP_REGISTER_PARTY)
    @StepEvent(type = EVENT_PARTY_RELATIONSHIP_REGISTERED)
    public Mono<UUID> registerPartyRelationship(RegisterPartyRelationshipCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd);
    }

    public Mono<Void> removePartyRelationship(UUID partyRelationshipId, ExecutionContext ctx) {
        return commandBus.send(new RemovePartyRelationshipCommand(partyRelationshipId));
    }

    @SagaStep(id = STEP_REGISTER_PARTY_GROUP_MEMBERSHIP, compensate = COMPENSATE_REMOVE_PARTY_GROUP_MEMBERSHIP, dependsOn = STEP_REGISTER_PARTY)
    @StepEvent(type = EVENT_PARTY_GROUP_MEMBERSHIP_REGISTERED)
    public Mono<UUID> registerPartyGroupMembership(RegisterPartyGroupMembershipCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd.withPartyId((UUID) ctx.getVariable(CTX_PARTY_ID)));
    }

    public Mono<Void> removePartyGroupMembership(UUID partyGroupMembershipId, ExecutionContext ctx) {
        return commandBus.send(new RemovePartyGroupMembershipCommand((UUID) ctx.getVariable(CTX_PARTY_ID), partyGroupMembershipId));
    }

    @SagaStep(id = "registerIndustryClassification", compensate = "removeIndustryClassification", dependsOn = STEP_REGISTER_PARTY)
    @StepEvent(type = "industryClassification.registered")
    public Mono<UUID> registerIndustryClassification(RegisterPartyIndustryClassificationCommand cmd, ExecutionContext ctx) {
        return cmd == null
                ? Mono.just(SKIPPED_STEP_SENTINEL)
                : commandBus.send(cmd.withPartyId((UUID) ctx.getVariable(CTX_PARTY_ID)));
    }

    public Mono<Void> removeIndustryClassification(UUID classificationId, ExecutionContext ctx) {
        return isSkipped(classificationId)
                ? Mono.empty()
                : commandBus.send(new RemovePartyIndustryClassificationCommand((UUID) ctx.getVariable(CTX_PARTY_ID), classificationId));
    }

    @SagaStep(id = "registerPartyAttribute", compensate = "removePartyAttribute", dependsOn = STEP_REGISTER_PARTY)
    @StepEvent(type = "partyAttribute.registered")
    public Mono<UUID> registerPartyAttribute(RegisterPartyAttributeCommand cmd, ExecutionContext ctx) {
        return cmd == null
                ? Mono.just(SKIPPED_STEP_SENTINEL)
                : commandBus.send(cmd.withPartyId((UUID) ctx.getVariable(CTX_PARTY_ID)));
    }

    public Mono<Void> removePartyAttribute(UUID attributeId, ExecutionContext ctx) {
        return isSkipped(attributeId)
                ? Mono.empty()
                : commandBus.send(new RemovePartyAttributeCommand((UUID) ctx.getVariable(CTX_PARTY_ID), attributeId));
    }

}
