package io.fireflysoftware.domain.people.core.compliance.queries.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Journey-friendly projection of a {@code consent_catalog} record.
 * <p>
 * Strips infrastructure metadata (timestamps, source) and surfaces only
 * what a channel needs to render an opt-in checkbox: the stable consent
 * identifier, its label/description, version, whether ticking it is
 * mandatory and the rendering order.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsentCatalogResponse {

    /** Stable consent identifier, used as the opt-in option value. */
    private UUID consentId;

    /** Type code (TERMS, PRIVACY, MARKETING, ...). */
    private String consentType;

    /** Human-readable description, used as the checkbox label. */
    private String description;

    /** Schema version of the consent text, used for audit. */
    private String version;

    /** Whether the user MUST tick this consent before continuing. */
    private boolean required;

    /** Rendering order, ascending. */
    private Integer order;

    /** Product this consent applies to (null = global). */
    private String applicableProduct;

}
