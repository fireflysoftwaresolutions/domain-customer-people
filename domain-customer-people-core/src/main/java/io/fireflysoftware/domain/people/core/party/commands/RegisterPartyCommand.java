package io.fireflysoftware.domain.people.core.party.commands;

import org.fireflyframework.cqrs.command.Command;
import io.fireflysoftware.core.customer.sdk.model.PartyDTO;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class RegisterPartyCommand extends PartyDTO implements Command<UUID> {}
