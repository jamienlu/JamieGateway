package io.github.jamielu.jmgateway.plugin;

import com.alibaba.nacos.shaded.io.grpc.LoadBalancer;
import io.github.jamienlu.discorridor.common.constant.MetaConstant;
import io.github.jamienlu.discorridor.common.meta.InstanceMeta;
import io.github.jamienlu.discorridor.common.meta.ServiceMeta;
import io.github.jamienlu.discorridor.registry.api.RegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jamieLu
 * @create 2025-01-17
 */
@Component("rpc")
@Slf4j
public class RpcPlugin extends AbstractGatewayPlugin{
    public static final String NAME = "kkrpc";
    private String prefix = GATEWAY_PREFIX + "/" + NAME + "/";

    @Autowired
    private RegistryCenter registryCenter;
    AtomicInteger index = new AtomicInteger(0);

    private InstanceMeta choose(List<InstanceMeta> providers) {
        if (providers == null || providers.isEmpty()) {
            return null;
        } else if (providers.size() == 1) {
            return providers.get(0);
        } else {
            return  providers.get(index.getAndIncrement() & Integer.MAX_VALUE % providers.size());
        }
    }

    @Override
    public Mono<Void> doHandle(ServerWebExchange exchange, GatewayPluginChain chain) {
        log.info("### rpc plugin handle");
        String service = exchange.getRequest().getPath().value().substring(prefix.length());
        // 获取实例地址
        ServiceMeta serviceMeta = ServiceMeta.builder().group(MetaConstant.GROUP_DEFAULT).name("discorridor-name").app("discorridor-app").env("dev").namespace("public").build();
        List<InstanceMeta> instanceMetas = registryCenter.fectchAll(serviceMeta);
        InstanceMeta instanceMeta = choose(instanceMetas);
        log.info(" inst size=" + instanceMetas.size() +  ", inst  " + instanceMeta);
        String httpUrl = "http://" + instanceMeta.toAddress();
        log.info(" inst address=" + httpUrl);
        // 拿到请求的报文
        Flux<DataBuffer> requestBody = exchange.getRequest().getBody();
        // 通过webclient发送post请求
        WebClient client = WebClient.create(httpUrl);
        Mono<ResponseEntity<String>> entity = client.post()
                .header("Content-Type", "application/json")
                .body(requestBody, DataBuffer.class).retrieve().toEntity(String.class);
        // 通过entity获取响应报文
        Mono<String> body = entity.map(ResponseEntity::getBody);
        // 组装响应报文
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        exchange.getResponse().getHeaders().add("jm.gw.version", "v1.0.0");
        exchange.getResponse().getHeaders().add("jm.gw.plugin", getName());
        return body.flatMap(x->exchange.getResponse()
                        .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(x.getBytes()))))
                .then(chain.handle(exchange));

    }

    @Override
    public boolean doSupport(ServerWebExchange exchange) {
        return exchange.getRequest().getPath().value().startsWith(prefix);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
