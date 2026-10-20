package io.fireflysoftware.domain.people.infra;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "api-configuration.common-platform.contract-mgmt")
@Data
public class ContractMgmtProperties {

    private String basePath;

}
