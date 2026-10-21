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
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateAddressCommand extends AddressDTO implements Command<UUID> {

    private UUID addressId;

    /**
     * Sets the party ID for this command and returns the command instance for method chaining.
     * 
     * @param partyId the party ID to assign to this address command
     * @return this command instance for method chaining
     */
    public UpdateAddressCommand withPartyId(UUID partyId) {
        this.setPartyId(partyId);
        return this;
    }

    /**
     * Sets the address ID for this command and returns the command instance for method chaining.
     * 
     * @param addressId the address ID to assign to this address command
     * @return this command instance for method chaining
     */
    public UpdateAddressCommand withAddressId(UUID addressId) {
        this.setAddressId(addressId);
        return this;
    }

}
