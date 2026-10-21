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


package io.fireflysoftware.domain.people.core.contact.commands;

import org.fireflyframework.cqrs.command.Command;
import io.fireflysoftware.core.customer.sdk.model.AddressDTO;
import io.fireflysoftware.core.customer.sdk.model.EmailContactDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateEmailCommand extends EmailContactDTO implements Command<UUID> {

    private UUID emailContactId;

    /**
     * Sets the party ID for this command and returns the command instance for method chaining.
     * 
     * @param partyId the party ID to assign to this address command
     * @return this command instance for method chaining
     */
    public UpdateEmailCommand withPartyId(UUID partyId) {
        this.setPartyId(partyId);
        return this;
    }

    /**
     * Sets the email contact ID for this command and returns the command instance for method chaining.
     *
     * @param emailId the email contact ID to assign to this command
     * @return this command instance for method chaining
     */
    public UpdateEmailCommand withEmailContactId(UUID emailId) {
        this.setEmailContactId(emailId);
        return this;
    }

    /**
     * Sets the primary status for this email and returns the command instance for method chaining.
     *
     * @param primary the primary status to assign to this email
     * @return this command instance for method chaining
     */
    public UpdateEmailCommand withPrimary(Boolean primary) {
        this.setIsPrimary(primary);
        return this;
    }

}
