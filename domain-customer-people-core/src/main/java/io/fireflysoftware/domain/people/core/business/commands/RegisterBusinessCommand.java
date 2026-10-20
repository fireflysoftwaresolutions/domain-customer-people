package io.fireflysoftware.domain.people.core.business.commands;

import io.fireflysoftware.domain.people.core.party.commands.RegisterPartyCommand;
import io.fireflysoftware.domain.people.core.status.commands.RegisterPartyStatusEntryCommand;
import io.fireflysoftware.domain.people.core.contact.commands.RegisterIdentityDocumentCommand;
import io.fireflysoftware.domain.people.core.contact.commands.RegisterAddressCommand;
import io.fireflysoftware.domain.people.core.contact.commands.RegisterEmailCommand;
import io.fireflysoftware.domain.people.core.contact.commands.RegisterPhoneCommand;
import io.fireflysoftware.domain.people.core.compliance.commands.RegisterEconomicActivityLinkCommand;
import io.fireflysoftware.domain.people.core.party.commands.RegisterPartyProviderCommand;
import io.fireflysoftware.domain.people.core.party.commands.RegisterPartyRelationshipCommand;
import io.fireflysoftware.domain.people.core.party.commands.RegisterPartyGroupMembershipCommand;

import java.util.List;


public record RegisterBusinessCommand(
        RegisterPartyCommand party,
        RegisterLegalEntityCommand legalEntity,
        List<RegisterPartyStatusEntryCommand> statusHistory,
        List<RegisterIdentityDocumentCommand> identityDocuments,
        List<RegisterAddressCommand> addresses,
        List<RegisterEmailCommand> emails,
        List<RegisterPhoneCommand> phones,
        List<RegisterEconomicActivityLinkCommand> economicActivities,
        List<RegisterPartyProviderCommand> providers,
        List<RegisterPartyRelationshipCommand> relationships,
        List<RegisterPartyGroupMembershipCommand> groupMemberships
) {}
