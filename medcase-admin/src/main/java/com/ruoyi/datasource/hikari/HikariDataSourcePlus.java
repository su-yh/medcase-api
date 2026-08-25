package com.ruoyi.datasource.hikari;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author suyh
 * @since 2025-02-18
 */
@Data
public class HikariDataSourcePlus extends HikariDataSource {
    @NestedConfigurationProperty
    @Valid
    private final FlywayProperties flyway = new FlywayProperties();
}
