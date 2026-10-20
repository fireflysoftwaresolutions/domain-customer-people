package io.fireflysoftware.domain.people.infra;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "api-configuration.core-lending.asset-finance")
@Data
public class AssetFinanceProperties {

    private String basePath;

}
