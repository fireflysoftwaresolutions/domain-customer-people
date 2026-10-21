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


package io.fireflysoftware.domain.people.core.status.services;

import io.fireflysoftware.domain.people.core.status.commands.UpdateStatusCommand;
import org.fireflyframework.orchestration.saga.engine.SagaResult;
import reactor.core.publisher.Mono;

public interface StatusService {

    /**
     * Updates the status of a party based on the provided command.
     * This operation is orchestrated as a saga to ensure data consistency
     * across multiple related systems involved in the process.
     *
     * @param updateStatusCommand the command containing the party ID and the new status to be applied
     * @return a Mono containing the saga execution result, indicating success or failure of the operation
     */
    Mono<SagaResult> updateStatus(UpdateStatusCommand updateStatusCommand);
}