package com.medcase.biz.sms;

import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * 根据短信开关决定是否校验短信配置。
 *
 * @author suyh
 */
public class AliyunSmsPropertiesGroupSequenceProvider
        implements DefaultGroupSequenceProvider<AliyunSmsProperties> {
    @Override
    public List<Class<?>> getValidationGroups(AliyunSmsProperties properties) {
        List<Class<?>> groups = new ArrayList<>();
        groups.add(AliyunSmsProperties.class);
        if (properties != null && properties.isEnabled()) {
            groups.add(AliyunSmsEnabled.class);
        }
        return groups;
    }
}
