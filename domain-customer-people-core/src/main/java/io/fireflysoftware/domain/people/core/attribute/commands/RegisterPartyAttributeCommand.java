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


package io.fireflysoftware.domain.people.core.attribute.commands;

import io.fireflysoftware.core.customer.sdk.model.PartyAttributeDTO;
import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

/**
 * Stores an effective-dated fact on a party, including employment, housing, loans, and debts.
 */
public class RegisterPartyAttributeCommand extends PartyAttributeDTO implements Command<UUID> {

    public RegisterPartyAttributeCommand withPartyId(UUID partyId) {
        this.setPartyId(partyId);
        return this;
    }
}
