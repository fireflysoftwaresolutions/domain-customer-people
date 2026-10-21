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


package io.fireflysoftware.domain.people.core.status.commands;

import org.fireflyframework.cqrs.command.Command;
import io.fireflysoftware.core.customer.sdk.model.PartyStatusDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateStatusCommand extends PartyStatusDTO implements Command<UUID> {

    /**
     * Sets the party ID for this command and returns the command instance for method chaining.
     * 
     * @param partyId the party ID to assign to this address command
     * @return this command instance for method chaining
     */
    public UpdateStatusCommand withPartyId(UUID partyId) {
        this.setPartyId(partyId);
        return this;
    }

    /**
     * Sets the status code for this command and returns the command instance for method chaining.
     *
     * @param statusCode the status code to assign to this command
     * @return this command instance for method chaining
     */
    public UpdateStatusCommand withStatusCode(PartyStatusDTO.StatusCodeEnum statusCode) {
        this.setStatusCode(statusCode);
        return this;
    }

    /**
     * Sets the reason for the status update and returns the command instance
     * for method chaining.
     *
     * @param reason the reason to assign to the status update
     * @return this command instance for method chaining
     */
    public UpdateStatusCommand withReason(String reason) {
        this.setStatusReason(reason);
        return this;
    }
}
