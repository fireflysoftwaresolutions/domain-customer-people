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