package com.medcase.common.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;

/**
 * 提供当前应用的构建版本。
 *
 * @author medcase
 */
@Service
public class ProjectVersionService {
    private final ObjectProvider<BuildProperties> buildPropertiesProvider;

    public ProjectVersionService(ObjectProvider<BuildProperties> buildPropertiesProvider) {
        this.buildPropertiesProvider = buildPropertiesProvider;
    }

    public String getVersion() {
        BuildProperties buildProperties = buildPropertiesProvider.getIfAvailable();
        if (buildProperties != null && buildProperties.getVersion() != null) {
            return buildProperties.getVersion();
        }
        return "unknown";
    }
}
