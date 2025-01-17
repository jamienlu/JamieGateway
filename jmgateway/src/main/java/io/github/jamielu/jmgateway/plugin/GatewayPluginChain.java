package io.github.jamielu.jmgateway.plugin;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author jamieLu
 * @create 2024-05-26
 */
public interface GatewayPluginChain {
    Mono<Void> handle(ServerWebExchange exchange);
}
