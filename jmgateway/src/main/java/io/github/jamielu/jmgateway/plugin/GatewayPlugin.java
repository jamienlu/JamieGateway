package io.github.jamielu.jmgateway.plugin;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author jamieLu
 * @create 2024-05-26
 */
public interface GatewayPlugin {
    String GATEWAY_PREFIX = "/gw";

    void start();
    void stop();

    String getName();

    boolean support(ServerWebExchange exchange);

    Mono<Void> handle(ServerWebExchange exchange, GatewayPluginChain chain);
}
