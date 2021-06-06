package ru.vsu.ofoodApi.oFoodApi.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JWTfromHedear {

    private final Logger LOG = LoggerFactory.getLogger(JWTfromHedear.class);

    public Mono<String> extract(ServerWebExchange exchange) {
        LOG.debug("heads : {}", exchange.getRequest().getHeaders());
        return Mono.justOrEmpty(
                exchange.getRequest()
                        .getHeaders()
                        .getFirst(HttpHeaders.AUTHORIZATION)
        );
    }

}
