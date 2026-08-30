package com.medcase.datasource.properties;

import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.medcase.datasource.hikari.HikariDataSourcePlus;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.flyway.autoconfigure.FlywayProperties;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;

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

        FluentConfiguration flywayConfig = Flyway.configure();
        flywayConfig.baselineOnMigrate(flywayProperties.isBaselineOnMigrate())
                .dataSource(ds)
                .locations(flywayProperties.getLocations().toArray(new String[0]))
                .table(flywayProperties.getTable())
                .failOnMissingLocations(flywayProperties.isFailOnMissingLocations())
                .callbackLocations(flywayProperties.getCallbackLocations().toArray(new String[0]))
                .encoding(flywayProperties.getEncoding())
                .connectRetries(flywayProperties.getConnectRetries())
                .lockRetryCount(flywayProperties.getLockRetryCount())
                .defaultSchema(flywayProperties.getDefaultSchema())
                .schemas(flywayProperties.getSchemas().toArray(new String[0]))
                .createSchemas(flywayProperties.isCreateSchemas())
                .tablespace(flywayProperties.getTablespace())
                .baselineDescription(flywayProperties.getBaselineDescription())
                .baselineVersion(flywayProperties.getBaselineVersion())
                .installedBy(flywayProperties.getInstalledBy())
                .placeholders(flywayProperties.getPlaceholders())
                .placeholderPrefix(flywayProperties.getPlaceholderPrefix())
                .placeholderSuffix(flywayProperties.getPlaceholderSuffix())
                .placeholderSeparator(flywayProperties.getPlaceholderSeparator())
                .placeholderReplacement(flywayProperties.isPlaceholderReplacement())
                .sqlMigrationPrefix(flywayProperties.getSqlMigrationPrefix())
                .sqlMigrationSeparator(flywayProperties.getSqlMigrationSeparator())
                .sqlMigrationSuffixes(flywayProperties.getSqlMigrationSuffixes().toArray(new String[0]))
                .repeatableSqlMigrationPrefix(flywayProperties.getRepeatableSqlMigrationPrefix())
                .target(flywayProperties.getTarget())
                .group(flywayProperties.isGroup())
                .mixed(flywayProperties.isMixed())
                .validateOnMigrate(flywayProperties.isValidateOnMigrate())
                .outOfOrder(flywayProperties.isOutOfOrder())
                .skipDefaultCallbacks(flywayProperties.isSkipDefaultCallbacks())
                .skipDefaultResolvers(flywayProperties.isSkipDefaultResolvers())
                .validateMigrationNaming(flywayProperties.isValidateMigrationNaming())
                .cleanDisabled(flywayProperties.isCleanDisabled())
                .executeInTransaction(flywayProperties.isExecuteInTransaction())
                .scriptPlaceholderPrefix(flywayProperties.getScriptPlaceholderPrefix())
                .scriptPlaceholderSuffix(flywayProperties.getScriptPlaceholderSuffix())
                .powershellExecutable(flywayProperties.getPowershellExecutable())
                .jdbcProperties(flywayProperties.getJdbcProperties());

        if (!flywayProperties.getIgnoreMigrationPatterns().isEmpty()) {
            flywayConfig.ignoreMigrationPatterns(
                    flywayProperties.getIgnoreMigrationPatterns().toArray(new String[0]));
        }

        flywayConfig.load().migrate();
    }
}
