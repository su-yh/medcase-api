package com.ruoyi.datasource;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.ruoyi.datasource.properties.DynamicDataSourceProviderProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author suyh
 * @since 2024-03-20
 */
@ConditionalOnProperty(prefix = DynamicDataSourceProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(DynamicDataSourceProviderProperties.class)
@AutoConfiguration
public class AdminDynamicDataSourceAutoConfiguration {
}
