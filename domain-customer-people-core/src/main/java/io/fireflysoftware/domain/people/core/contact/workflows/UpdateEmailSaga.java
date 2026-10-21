package io.fireflysoftware.domain.people.core.contact.workflows;

import io.fireflysoftware.core.customer.sdk.api.EmailContactsApi;
import io.fireflysoftware.core.customer.sdk.model.EmailContactDTO;
import io.fireflysoftware.domain.people.core.contact.commands.UpdateEmailCommand;
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

import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.EVENT_EMAIL_UPDATED;
import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.SAGA_UPDATE_EMAIL_NAME;
import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.STEP_UPDATE_EMAIL;

/**
 * Updates an email and restores the previous row if a later step fails.
 */
@Saga(name = SAGA_UPDATE_EMAIL_NAME)
@Service
public class UpdateEmailSaga {

    private final CommandBus commandBus;
    private final EmailContactsApi emailContactsApi;

    @Autowired
    public UpdateEmailSaga(CommandBus commandBus, EmailContactsApi emailContactsApi) {
        this.commandBus = commandBus;
        this.emailContactsApi = emailContactsApi;
    }

    @SagaStep(id = "readEmail")
    @StepEvent(type = "email.read")
    public Mono<EmailContactDTO> readEmail(UpdateEmailCommand cmd, ExecutionContext ctx) {
        return emailContactsApi.getEmailContactById(cmd.getPartyId(), cmd.getEmailContactId(), UUID.randomUUID().toString())
                .doOnNext(previous -> ctx.putVariable("previousEmail", previous));
    }

    @SagaStep(id = STEP_UPDATE_EMAIL, dependsOn = "readEmail", compensate = "restoreEmail")
    @StepEvent(type = EVENT_EMAIL_UPDATED)
    public Mono<UUID> updateEmail(UpdateEmailCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd);
    }

    public Mono<Void> restoreEmail(UUID emailId, ExecutionContext ctx) {
        EmailContactDTO previous = ctx.getVariableAs("previousEmail", EmailContactDTO.class);
        if (previous == null) {
            return Mono.empty();
        }
        UpdateEmailCommand restore = new UpdateEmailCommand();
        BeanUtils.copyProperties(previous, restore);
        restore.withPartyId(previous.getPartyId());
        restore.withEmailContactId(previous.getEmailContactId() != null ? previous.getEmailContactId() : emailId);
        return commandBus.send(restore).then();
    }
}
