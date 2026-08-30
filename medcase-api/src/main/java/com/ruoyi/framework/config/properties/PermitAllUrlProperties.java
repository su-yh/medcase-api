package com.ruoyi.framework.config.properties;

import com.ruoyi.common.annotation.Anonymous;
import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 设置Anonymous注解允许匿名访问的方法URL
 */
@Configuration
public class PermitAllUrlProperties implements InitializingBean, ApplicationContextAware {
    /** 匹配路径中的占位变量，例如 /user/{id} */
    private static final Pattern PATTERN = Pattern.compile("\\{(.*?)\\}");
    /** 将路径变量替换为通配符，便于做匿名路由匹配 */
    private static final String ASTERISK = "*";

    private ApplicationContext applicationContext;

    @Getter
    private final Map<RequestMethod, Set<String>> methodUrls = new LinkedHashMap<>();

    @Override
    public void afterPropertiesSet() {
        RequestMappingHandlerMapping mapping = applicationContext.getBean("requestMappingHandlerMapping",
                RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();

        map.keySet().forEach(info -> {
            HandlerMethod handlerMethod = map.get(info);
            if (AnnotationUtils.findAnnotation(handlerMethod.getMethod(), Anonymous.class) != null) {
                collectUrls(info);
            }
        });
    }

    private void collectUrls(RequestMappingInfo info) {
        Set<String> paths = Objects.requireNonNull(info.getPathPatternsCondition()).getPatternValues();
        Set<RequestMethod> requestMethods = info.getMethodsCondition().getMethods();
        if (requestMethods.isEmpty()) {
            for (RequestMethod requestMethod : RequestMethod.values()) {
                Set<String> urlsByMethod = methodUrls.computeIfAbsent(requestMethod, key -> new LinkedHashSet<>());
                paths.forEach(url -> urlsByMethod.add(PATTERN.matcher(url).replaceAll(ASTERISK)));
            }
            return;
        }
        requestMethods.forEach(requestMethod -> {
            Set<String> urlsByMethod = methodUrls.computeIfAbsent(requestMethod, key -> new LinkedHashSet<>());
            paths.forEach(url -> urlsByMethod.add(PATTERN.matcher(url).replaceAll(ASTERISK)));
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.applicationContext = context;
    }
}
