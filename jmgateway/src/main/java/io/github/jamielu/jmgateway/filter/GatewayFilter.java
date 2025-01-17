package io.github.jamielu.jmgateway.filter;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author jamieLu
 * @create 2024-05-26
 */
public interface GatewayFilter {
    Mono<Void> filter(ServerWebExchange exchange);
}
