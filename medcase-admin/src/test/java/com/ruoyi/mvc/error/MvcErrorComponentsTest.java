package com.ruoyi.mvc.error;

import com.ruoyi.mvc.configurer.BaseWebMvcConfigurer;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MvcErrorComponentsTest {

    @Test
    void errorComponentsAreRegisteredByComponentScan() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean(StaticMessageSource.class);
            context.scan("com.ruoyi.mvc.error", "com.ruoyi.mvc.configurer");
            context.refresh();

            assertNotNull(context.getBean(BaseErrorAttributes.class));
            BaseWebMvcConfigurer configurer = context.getBean(BaseWebMvcConfigurer.class);
            List<HandlerExceptionResolver> resolvers = new ArrayList<>();
            resolvers.add(new DefaultHandlerExceptionResolver());

            configurer.extendHandlerExceptionResolvers(resolvers);

            assertInstanceOf(BaseHandlerExceptionResolver.class, resolvers.get(0));
        }
    }
}
