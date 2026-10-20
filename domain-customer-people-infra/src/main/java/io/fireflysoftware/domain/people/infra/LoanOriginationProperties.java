package io.fireflysoftware.domain.people.infra;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "api-configuration.core-lending.loan-origination")
@Data
public class LoanOriginationProperties {

    private String basePath;

}
