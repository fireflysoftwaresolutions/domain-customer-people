package io.fireflysoftware.domain.people.infra;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for the {@code core-common-reference-master-data}
 * upstream service. Used by the consent-catalog query endpoint to project the
 * platform-wide consent catalogue into journey-friendly responses.
 */
@Configuration
@ConfigurationProperties(prefix = "api-configuration.common-platform.reference-master-data")
@Data
public class ReferenceMasterDataProperties {

    private String basePath;

}
