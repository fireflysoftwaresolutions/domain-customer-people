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


package io.fireflysoftware.domain.people.core.business.services;

import io.fireflysoftware.core.customer.sdk.model.LegalEntityDTO;
import io.fireflysoftware.domain.people.core.business.commands.RegisterBusinessCommand;
import io.fireflysoftware.domain.people.core.business.commands.UpdateBusinessCommand;
import org.fireflyframework.orchestration.saga.engine.SagaResult;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface BusinessService {

    /**
     * Registers a new business and initiates the corresponding saga process.
     *
     * @param command the command containing all necessary details for registering a business,
     *                including party, legal entity, status history, identity documents,
     *                and associated information such as addresses, emails, phones,
     *                economic activities, providers, relationships, and group memberships.
     * @return a {@code Mono} emitting the result of the saga process for registering the business,
     *         which encapsulates the outcome and details of the operation.
     */
    Mono<SagaResult> registerBusiness(RegisterBusinessCommand command);
    
    /**
     * Updates an existing business based on the provided command and initiates the corresponding saga process.
     *
     * @param command the command containing the updated details for the business,
     *                including changes to party information, legal entity, status history,
     *                identity documents, and associated details such as addresses, emails,
     *                phones, economic activities, providers, relationships, and group memberships.
     * @return a {@code Mono} emitting the result of the saga process for updating the business,
     *         which encapsulates the outcome and details of the operation.
     */
    Mono<SagaResult> updateBusiness(UpdateBusinessCommand command);

    /**
     * Retrieves business information by business ID.
     *
     * @param businessId the unique identifier of the business to retrieve
     * @return a {@code Mono} emitting the business information as a {@code LegalEntityDTO}
     */
    Mono<LegalEntityDTO> getBusinessInfo(UUID businessId);
}
