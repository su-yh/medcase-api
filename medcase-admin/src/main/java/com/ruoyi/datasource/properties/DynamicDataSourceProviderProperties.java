package com.ruoyi.datasource.properties;

import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.ruoyi.datasource.hikari.HikariDataSourcePlus;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author suyh
 * @since 2024-03-20
 */
@ConfigurationProperties(prefix = DynamicDataSourceProperties.PREFIX)
@Data
public class DynamicDataSourceProviderProperties implements DynamicDataSourceProvider, InitializingBean {

    @Valid
    private final Map<String, HikariDataSourcePlus> hikari = new HashMap<>();

    @Override
    public synchronized Map<String, DataSource> loadDataSources() {
        return new HashMap<>(hikari);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Collection<HikariDataSourcePlus> hikariDataSourcePluses = hikari.values();
        for (HikariDataSourcePlus ds : hikariDataSourcePluses) {
            doFlyway(ds);
        }
    }

    public static void doFlyway(HikariDataSourcePlus ds) throws Exception {
        FlywayProperties flywayProperties = ds.getFlyway();
        if (!flywayProperties.isEnabled()) {
            return;
        }

        String[] locations = flywayProperties.getLocations().toArray(new String[0]);
        FluentConfiguration cdsWebFlywayConfig = new FluentConfiguration();
        cdsWebFlywayConfig.baselineOnMigrate(true)
                .dataSource(ds)
                .locations(locations)
                .table(flywayProperties.getTable())
                .validateOnMigrate(flywayProperties.isValidateOnMigrate())
                .ignoreFutureMigrations(flywayProperties.isIgnoreFutureMigrations())
                .outOfOrder(flywayProperties.isOutOfOrder());
        Flyway cdsWebFlyway = cdsWebFlywayConfig.load();
        FlywayMigrationInitializer flywayMigrationInitializer = new FlywayMigrationInitializer(cdsWebFlyway, null);
        flywayMigrationInitializer.afterPropertiesSet();
    }
}
