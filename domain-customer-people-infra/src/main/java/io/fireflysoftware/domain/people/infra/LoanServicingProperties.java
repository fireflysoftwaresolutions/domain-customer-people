package io.fireflysoftware.domain.people.infra;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "api-configuration.core-lending.loan-servicing")
@Data
public class LoanServicingProperties {

    private String basePath;

}
