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

import org.fireflyframework.cqrs.command.Command;
import io.fireflysoftware.core.customer.sdk.model.LegalEntityDTO;
import io.fireflysoftware.core.customer.sdk.model.NaturalPersonDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class RegisterLegalEntityCommand extends LegalEntityDTO implements Command<UUID> {


    /**
     * Sets the party ID for the legal entity and returns the current command instance.
     *
     * @param partyId the unique identifier of the party to associate with the legal entity
     * @return the current instance of {@code RegisterLegalEntityCommand} with the party ID set
     */
    public RegisterLegalEntityCommand withPartyId(UUID partyId) {
        this.setPartyId(partyId);
        return this;
    }
}
