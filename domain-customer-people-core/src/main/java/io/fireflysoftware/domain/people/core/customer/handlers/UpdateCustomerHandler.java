package io.fireflysoftware.domain.people.core.customer.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import io.fireflysoftware.core.customer.sdk.api.NaturalPersonsApi;
import io.fireflysoftware.core.customer.sdk.model.NaturalPersonDTO;
import io.fireflysoftware.domain.people.core.customer.commands.UpdateCustomerCommand;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import reactor.core.publisher.Mono;

import java.beans.PropertyDescriptor;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@CommandHandlerComponent
public class UpdateCustomerHandler extends CommandHandler<UpdateCustomerCommand, UUID> {

    // Skip identity, audit and read-only fields when merging patch onto the existing record:
    // the path params already pin identity, and timestamps belong to the persistence layer.
    private static final Set<String> NON_PATCHABLE_FIELDS = Set.of(
            "class", "naturalPersonId", "partyId", "createdAt", "updatedAt");

    private final NaturalPersonsApi naturalPersonsApi;

    public UpdateCustomerHandler(NaturalPersonsApi naturalPersonsApi) {
        this.naturalPersonsApi = naturalPersonsApi;
    }

    @Override
    protected Mono<UUID> doHandle(UpdateCustomerCommand cmd) {
        // Core's PUT /natural-persons is a full-replace and rejects payloads missing @NotBlank
        // fields like givenName / familyName1. Callers (BFF workflows) typically post a thin
        // patch with only the fields they want to change, so we fetch the existing record,
        // overlay the non-null patch fields, and send the merged DTO back. The (1:1) party
        // ↔ natural-person relationship lets us resolve naturalPersonId from partyId for free.
        return naturalPersonsApi.getNaturalPersonByPartyId(cmd.getPartyId(), UUID.randomUUID().toString())
                .switchIfEmpty(Mono.error(new IllegalStateException(
                        "No natural-person record found for partyId=" + cmd.getPartyId())))
                .flatMap(existing -> {
                    NaturalPersonDTO merged = mergeNonNull(existing, cmd);
                    UUID naturalPersonId = Objects.requireNonNull(merged.getNaturalPersonId(),
                            "naturalPersonId missing on existing record for partyId=" + cmd.getPartyId());
                    return naturalPersonsApi.updateNaturalPerson(
                                    cmd.getPartyId(), naturalPersonId, merged, UUID.randomUUID().toString())
                            .mapNotNull(dto -> Objects.requireNonNull(
                                    Objects.requireNonNull(dto).getNaturalPersonId()));
                });
    }

    // Apply every non-null property from `patch` onto `existing` while leaving identity and
    // audit columns untouched, so that a partial update from the BFF doesn't drop required
    // fields from the full-replace PUT.
    private static NaturalPersonDTO mergeNonNull(NaturalPersonDTO existing, NaturalPersonDTO patch) {
        BeanWrapper src = new BeanWrapperImpl(patch);
        BeanWrapper dst = new BeanWrapperImpl(existing);
        for (PropertyDescriptor pd : src.getPropertyDescriptors()) {
            String name = pd.getName();
            if (NON_PATCHABLE_FIELDS.contains(name) || !dst.isWritableProperty(name)) {
                continue;
            }
            Object value = src.getPropertyValue(name);
            if (value != null) {
                dst.setPropertyValue(name, value);
            }
        }
        return existing;
    }
}
