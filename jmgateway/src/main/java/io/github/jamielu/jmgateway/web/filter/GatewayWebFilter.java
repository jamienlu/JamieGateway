package io.github.jamielu.jmgateway.web.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @author jamieLu
 * @create 2025-01-17
 */
@Component
@Slf4j
public class GatewayWebFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("===>>> jm Gateway web filter ...");
        if(exchange.getRequest().getQueryParams().getFirst("mock")==null) {
            return chain.filter(exchange);
        }
        String mock = """
                {"result": "mock"}
                """;
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory().wrap(mock.getBytes())));
    }
}
