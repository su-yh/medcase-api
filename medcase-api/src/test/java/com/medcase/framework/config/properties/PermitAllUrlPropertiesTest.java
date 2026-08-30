package com.medcase.framework.config.properties;

import com.medcase.common.annotation.Anonymous;
import java.lang.reflect.Method;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PermitAllUrlPropertiesTest {
    @Test
    void collectsAnonymousUrlsByHttpMethod() throws Exception {
        DemoController controller = new DemoController();
        Method postOnly = DemoController.class.getMethod("postOnly");
        Method open = DemoController.class.getMethod("open");

        RequestMappingInfo postInfo = RequestMappingInfo.paths("/login").methods(RequestMethod.POST).build();
        RequestMappingInfo anyInfo = RequestMappingInfo.paths("/health").build();

        RequestMappingHandlerMapping mapping = new RequestMappingHandlerMapping() {
            @Override
            public Map<RequestMappingInfo, HandlerMethod> getHandlerMethods() {
                return Map.of(
                        postInfo, new HandlerMethod(controller, postOnly),
                        anyInfo, new HandlerMethod(controller, open));
            }
        };

        StaticApplicationContext context = new StaticApplicationContext();
        context.getBeanFactory().registerSingleton("requestMappingHandlerMapping", mapping);

        PermitAllUrlProperties properties = new PermitAllUrlProperties();
        properties.setApplicationContext(context);
        properties.afterPropertiesSet();

        assertTrue(properties.getMethodUrls().get(RequestMethod.POST).contains("/login"));
        assertTrue(properties.getMethodUrls().get(RequestMethod.GET).contains("/health"));
        assertTrue(properties.getMethodUrls().get(RequestMethod.POST).contains("/health"));
        assertEquals(1, properties.getMethodUrls().get(RequestMethod.POST).stream().filter("/login"::equals).count());
    }

    static class DemoController {
        @Anonymous
        @RequestMapping(value = "/login", method = RequestMethod.POST)
        public void postOnly() {
        }

        @Anonymous
        @RequestMapping("/health")
        public void open() {
        }
    }
}
