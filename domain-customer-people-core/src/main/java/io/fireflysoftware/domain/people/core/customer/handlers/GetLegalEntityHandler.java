package io.fireflysoftware.domain.people.core.customer.handlers;

import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import org.fireflyframework.web.error.exceptions.BusinessException;
import io.fireflysoftware.core.customer.sdk.api.LegalEntitiesApi;
import io.fireflysoftware.core.customer.sdk.model.LegalEntityDTO;
import io.fireflysoftware.domain.people.core.customer.queries.LegalEntityQuery;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.UUID;

@QueryHandlerComponent
public class GetLegalEntityHandler extends QueryHandler<LegalEntityQuery, LegalEntityDTO> {

    private final LegalEntitiesApi legalEntitiesApi;

    public GetLegalEntityHandler(LegalEntitiesApi legalEntitiesApi) {
        this.legalEntitiesApi = legalEntitiesApi;
    }

    @Override
    protected Mono<LegalEntityDTO> doHandle(LegalEntityQuery cmd) {
        return legalEntitiesApi.getLegalEntityByPartyId(cmd.getPartyId(), UUID.randomUUID().toString())
                .onErrorMap(WebClientResponseException.NotFound.class, ex -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "LEGAL_ENTITY_NOT_FOUND",
                        "Legal entity not found for partyId: " + cmd.getPartyId()));
    }
}