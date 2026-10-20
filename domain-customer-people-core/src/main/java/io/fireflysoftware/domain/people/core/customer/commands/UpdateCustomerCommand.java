package io.fireflysoftware.domain.people.core.customer.commands;

import org.fireflyframework.cqrs.command.Command;
import io.fireflysoftware.core.customer.sdk.model.NaturalPersonDTO;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class UpdateCustomerCommand extends NaturalPersonDTO implements Command<UUID> {}