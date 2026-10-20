package io.fireflysoftware.domain.people.infra;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "api-configuration.core-lending.personal-loans")
@Data
public class PersonalLoansProperties {

    private String basePath;

}
