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


package io.fireflysoftware.domain.people.core.party.services;

import io.fireflysoftware.domain.people.core.party.commands.RegisterPartyRelationshipCommand;
import io.fireflysoftware.domain.people.core.party.commands.UpdatePartyRelationshipCommand;
import org.fireflyframework.orchestration.saga.engine.SagaResult;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PartyService {

    /**
     * Registers a new party relationship based on the details provided in the command.
     *
     * @param command the {@link RegisterPartyRelationshipCommand} containing the details of the party relationship to be registered
     * @return a {@link Mono} emitting the result of the saga execution, encapsulated in a {@link SagaResult}
     */
    Mono<SagaResult> registerPartyRelationship(RegisterPartyRelationshipCommand command);
    
    /**
     * Updates an existing party relationship based on the details provided in the command.
     *
     * @param command the {@link UpdatePartyRelationshipCommand} containing the updated details of the party relationship
     * @param relationId the unique identifier of the relationship
     * @return a {@link Mono} emitting the result of the saga execution, encapsulated in a {@link SagaResult}
     */
    Mono<SagaResult> updatePartyRelationship(UpdatePartyRelationshipCommand command, UUID relationId);
    
    /**
     * Removes an existing party relationship.
     *
     * @param relationId the unique identifier of the relationship to be removed
     * @return a {@link Mono} emitting the result of the saga execution, encapsulated in a {@link SagaResult}
     */
    Mono<SagaResult> removePartyRelationship(UUID relationId);
}
