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


package io.fireflysoftware.domain.people.core.compliance.commands;

import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

/**
 * Command to upsert (update) the consent record identified by
 * {@code (partyId, consentId)}. Carries the boolean {@code granted} flag and
 * an optional {@code applicationId} soft link to the originating application.
 * <p>
 * The translation from inbound domain-level status strings (e.g. GRANTED,
 * REVOKED) to the {@code granted} boolean is the responsibility of the caller
 * (typically the experience tier). This command intentionally exposes the
 * downstream contract — a boolean flag — to keep the domain handler thin.
 */
public record UpdateConsentCommand(
        UUID partyId,
        UUID consentId,
        boolean granted,
        UUID applicationId
) implements Command<Void> {}
