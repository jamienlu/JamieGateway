package io.github.jamielu.jmgateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author jamieLu
 * @create 2025-01-17
 */
@Component("gatewayFilter")
@Slf4j
public class DefaultGatewayFilter implements GatewayFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange) {
       log.info("### filters: default filter ...");
        exchange.getRequest().getHeaders().toSingleValueMap()
                .forEach((k, v) -> log.info(k + ":" + v));
        return Mono.empty();
    }
}
