package io.github.jamielu.jmgateway.conf;

import io.github.jamienlu.discorridor.registry.api.RegistryCenter;
import io.github.jamienlu.discorridor.registry.nacos.NacosRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;

import java.util.Properties;

import static io.github.jamielu.jmgateway.plugin.GatewayPlugin.GATEWAY_PREFIX;

/**
 * @author jamieLu
 * @create 2024-05-26
 */
@Configuration
@Slf4j
public class GatewayConfig {
    @Bean
    RegistryCenter registryCenter() {
        return new NacosRegistryCenter("192.168.0.100:8848", "nacos","nacos");
    }
    @Bean
    ApplicationRunner runner(@Autowired ApplicationContext context) {
        return args -> {
            SimpleUrlHandlerMapping handlerMapping = context.getBean(SimpleUrlHandlerMapping.class);
            Properties mappings = new Properties();
            mappings.put(GATEWAY_PREFIX + "/**", "gatewayWebHandler");
            handlerMapping.setMappings(mappings);
            // url网关地址映射到handler处理器类
            handlerMapping.initApplicationContext();
            log.info("gateway start");
        };
    }

}
