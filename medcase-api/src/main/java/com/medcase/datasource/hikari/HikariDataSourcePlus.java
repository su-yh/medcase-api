package com.medcase.datasource.hikari;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.flyway.autoconfigure.FlywayProperties;

/**
 * @author suyh
 * @since 2025-02-18
 */
@Data
public class HikariDataSourcePlus extends HikariDataSource {
    @NestedConfigurationProperty
    private final FlywayProperties flyway = new FlywayProperties();
}
