package io.fireflysoftware.domain.people.core.business.commands;

import org.fireflyframework.cqrs.command.Command;
import io.fireflysoftware.core.customer.sdk.model.LegalEntityDTO;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class UpdateBusinessCommand extends LegalEntityDTO implements Command<UUID> {}