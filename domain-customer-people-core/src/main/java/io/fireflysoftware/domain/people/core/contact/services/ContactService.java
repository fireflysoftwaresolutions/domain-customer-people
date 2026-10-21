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


package io.fireflysoftware.domain.people.core.contact.services;

import io.fireflysoftware.domain.people.core.contact.commands.RegisterAddressCommand;
import io.fireflysoftware.domain.people.core.contact.commands.RegisterEmailCommand;
import io.fireflysoftware.domain.people.core.contact.commands.RegisterIdentityDocumentCommand;
import io.fireflysoftware.domain.people.core.contact.commands.RegisterPhoneCommand;
import io.fireflysoftware.domain.people.core.contact.commands.UpdateAddressCommand;
import io.fireflysoftware.domain.people.core.contact.commands.UpdateEmailCommand;
import io.fireflysoftware.domain.people.core.contact.commands.UpdatePhoneCommand;
import io.fireflysoftware.domain.people.core.contact.commands.UpdatePreferredChannelCommand;
import org.fireflyframework.orchestration.saga.engine.SagaResult;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ContactService {

    /**
     * Adds a new address to an existing customer.
     * This operation is orchestrated as a saga to ensure data consistency.
     *
     * @param partyId the ID of the party/customer to add the address to
     * @param addressCommand the command containing the address information
     * @return a Mono containing the saga execution result
     */
    Mono<SagaResult> addAddress(UUID partyId, RegisterAddressCommand addressCommand);

    /**
     * Updates an existing address for a given customer. The operation modifies
     * the specified address fields while maintaining version history to ensure data consistency.
     *
     * @param partyId the ID of the customer/party whose address is being updated
     * @param addressId the ID of the address to be updated
     * @param addressData the data of the address to update, encapsulated in an UpdateAddressCommand
     * @return a Mono of Void, completing when the address update is successful
     */
    Mono<Void> updateAddress(UUID partyId, UUID addressId, UpdateAddressCommand addressData);

    /**
     * Removes an address for a specified customer. This operation can either remove
     * the address entirely or mark it with an end-date, preserving audit and historical data.
     *
     * @param partyId the unique identifier of the customer/party owning the address
     * @param addressId the unique identifier of the address to be removed
     * @return a Mono containing the saga execution result, indicating success or failure of the operation
     */
    Mono<SagaResult> removeAddress(UUID partyId, UUID addressId);

    /**
     * Adds a new email to an existing customer.
     * This operation is orchestrated as a saga to ensure data consistency.
     *
     * @param partyId the ID of the party/customer to add the email to
     * @param emailCommand the command containing the email information
     * @return a Mono containing the saga execution result
     */
    Mono<SagaResult> addEmail(UUID partyId, RegisterEmailCommand emailCommand);

    /**
     * Updates an existing email for a given customer. The operation modifies
     * the specified email fields while maintaining version history to ensure data consistency.
     *
     * @param partyId the ID of the customer/party whose email is being updated
     * @param emailId the ID of the email to be updated
     * @param emailData the data of the email to update, encapsulated in an UpdateEmailCommand
     * @return a Mono of Void, completing when the email update is successful
     */
    Mono<Void> updateEmail(UUID partyId, UUID emailId, UpdateEmailCommand emailData);

    /**
     * Removes an email for a specified customer. This operation can either remove
     * the email entirely or mark it with an end-date, preserving audit and historical data.
     *
     * @param partyId the unique identifier of the customer/party owning the email
     * @param emailId the unique identifier of the email to be removed
     * @return a Mono containing the saga execution result, indicating success or failure of the operation
     */
    Mono<SagaResult> removeEmail(UUID partyId, UUID emailId);

    /**
     * Adds a new phone to an existing customer.
     * This operation is orchestrated as a saga to ensure data consistency.
     *
     * @param partyId the ID of the party/customer to add the phone to
     * @param phoneCommand the command containing the phone information
     * @return a Mono containing the saga execution result
     */
    Mono<SagaResult> addPhone(UUID partyId, RegisterPhoneCommand phoneCommand);

    /**
     * Updates an existing phone for a given customer. The operation modifies
     * the specified phone fields while maintaining version history to ensure data consistency.
     *
     * @param partyId the ID of the customer/party whose phone is being updated
     * @param phoneId the ID of the phone to be updated
     * @param phoneData the data of the phone to update, encapsulated in an UpdatePhoneCommand
     * @return a Mono of Void, completing when the phone update is successful
     */
    Mono<Void> updatePhone(UUID partyId, UUID phoneId, UpdatePhoneCommand phoneData);

    /**
     * Removes a phone for a specified customer. This operation can either remove
     * the phone entirely or mark it with an end-date, preserving audit and historical data.
     *
     * @param partyId the unique identifier of the customer/party owning the phone
     * @param phoneId the unique identifier of the phone to be removed
     * @return a Mono containing the saga execution result, indicating success or failure of the operation
     */
    Mono<SagaResult> removePhone(UUID partyId, UUID phoneId);

    /**
     * Sets the preferred communication channel for a specific customer/party based on the provided command.
     * This operation updates the preference to ensure future communication aligns with the customer's choice.
     *
     * @param partyId the unique identifier of the customer/party whose preferred channel is being updated
     * @param channelData the command containing the preferred channel information (e.g., email or phone)
     * @return a Mono of Void, completing when the preferred channel update is successful
     */
    Mono<Void> setPreferredChannel(UUID partyId, UpdatePreferredChannelCommand channelData);

    /**
     * Adds a new identity document to an existing customer.
     * This operation is orchestrated as a saga to ensure data consistency.
     *
     * @param partyId the ID of the party/customer to add the identity document to
     * @param identityDocumentCommand the command containing the identity document information
     * @return a Mono containing the saga execution result
     */
    Mono<SagaResult> addIdentityDocument(UUID partyId, RegisterIdentityDocumentCommand identityDocumentCommand);

    /**
     * Removes an identity document for a specified customer. This operation can either remove
     * the identity document entirely or mark it with an end-date, preserving audit and historical data.
     *
     * @param partyId the unique identifier of the customer/party owning the identity document
     * @param identityDocumentId the unique identifier of the identity document to be removed
     * @return a Mono containing the saga execution result, indicating success or failure of the operation
     */
    Mono<SagaResult> removeIdentityDocument(UUID partyId, UUID identityDocumentId);
}