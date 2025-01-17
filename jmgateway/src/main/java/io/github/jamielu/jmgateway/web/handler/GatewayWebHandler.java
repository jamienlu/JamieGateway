package io.github.jamielu.jmgateway.web.handler;

import io.github.jamielu.jmgateway.filter.GatewayFilter;
import io.github.jamielu.jmgateway.plugin.DefaultGatewayPluginChain;
import io.github.jamielu.jmgateway.plugin.GatewayPlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author jamieLu
 * @create 2024-05-26
 */
@Slf4j
@Component("gatewayWebHandler")
public class GatewayWebHandler implements WebHandler {
    @Autowired
    List<GatewayPlugin> plugins;

    @Autowired
    List<GatewayFilter> filters;
    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        log.info("### jmgateway web handler ###");
        if(plugins == null || plugins.isEmpty()) {
            String mock = """
                    {"result":"no plugin"}
                    """;
            return exchange.getResponse()
                    .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(mock.getBytes())));
        }

        for(GatewayFilter filter : filters) {
            filter.filter(exchange);
        }

        return new DefaultGatewayPluginChain(plugins).handle(exchange);
    }
}
