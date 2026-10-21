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


package io.fireflysoftware.domain.people.core.business.commands;

import io.fireflysoftware.domain.people.core.attribute.commands.RegisterPartyAttributeCommand;
import io.fireflysoftware.domain.people.core.classification.commands.RegisterPartyIndustryClassificationCommand;
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
        List<RegisterPartyGroupMembershipCommand> groupMemberships,
        List<RegisterPartyIndustryClassificationCommand> industryClassifications,
        List<RegisterPartyAttributeCommand> attributes
) {}
