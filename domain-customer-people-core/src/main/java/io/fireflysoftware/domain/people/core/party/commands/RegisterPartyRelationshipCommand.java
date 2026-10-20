package io.fireflysoftware.domain.people.core.party.commands;

import org.fireflyframework.cqrs.command.Command;
import io.fireflysoftware.core.customer.sdk.model.PartyRelationshipDTO;

import java.util.UUID;

public class RegisterPartyRelationshipCommand extends PartyRelationshipDTO implements Command<UUID> {}
