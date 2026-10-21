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


package io.fireflysoftware.domain.people.core.contact.workflows;

import io.fireflysoftware.core.customer.sdk.api.PhoneContactsApi;
import io.fireflysoftware.core.customer.sdk.model.PhoneContactDTO;
import io.fireflysoftware.domain.people.core.contact.commands.UpdatePhoneCommand;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.EVENT_PHONE_UPDATED;
import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.SAGA_UPDATE_PHONE_NAME;
import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.STEP_UPDATE_PHONE;

/**
 * Updates a phone and restores the previous row if a later step fails.
 */
@Saga(name = SAGA_UPDATE_PHONE_NAME)
@Service
public class UpdatePhoneSaga {

    private final CommandBus commandBus;
    private final PhoneContactsApi phoneContactsApi;

    @Autowired
    public UpdatePhoneSaga(CommandBus commandBus, PhoneContactsApi phoneContactsApi) {
        this.commandBus = commandBus;
        this.phoneContactsApi = phoneContactsApi;
    }

    @SagaStep(id = "readPhone")
    @StepEvent(type = "phone.read")
    public Mono<PhoneContactDTO> readPhone(UpdatePhoneCommand cmd, ExecutionContext ctx) {
        return phoneContactsApi.getPhoneContactById(cmd.getPartyId(), cmd.getPhoneContactId(), UUID.randomUUID().toString())
                .doOnNext(previous -> ctx.putVariable("previousPhone", previous));
    }

    @SagaStep(id = STEP_UPDATE_PHONE, dependsOn = "readPhone", compensate = "restorePhone")
    @StepEvent(type = EVENT_PHONE_UPDATED)
    public Mono<UUID> updatePhone(UpdatePhoneCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd);
    }

    public Mono<Void> restorePhone(UUID phoneId, ExecutionContext ctx) {
        PhoneContactDTO previous = ctx.getVariableAs("previousPhone", PhoneContactDTO.class);
        if (previous == null) {
            return Mono.empty();
        }
        UpdatePhoneCommand restore = new UpdatePhoneCommand();
        BeanUtils.copyProperties(previous, restore);
        restore.withPartyId(previous.getPartyId());
        restore.withPhoneContactId(previous.getPhoneContactId() != null ? previous.getPhoneContactId() : phoneId);
        return commandBus.send(restore).then();
    }
}
