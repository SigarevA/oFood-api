package ru.vsu.ofoodApi.oFoodApi.config.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.vsu.ofoodApi.oFoodApi.jwt.JWTfromHedear;


import java.util.function.Function;
import java.util.function.Predicate;


@Component
public class BearerServerAuthenticationConverter implements ServerAuthenticationConverter {

    private static final Logger LOG = LoggerFactory.getLogger(BearerServerAuthenticationConverter.class);
    private final String BEARER = "Bearer ";
    private final Predicate<String> matchBearerLength = authValue -> authValue.length() > BEARER.length();
    private final Function<String, Mono<String>> isolateBearerValue = authValue -> Mono.justOrEmpty(authValue.substring(BEARER.length()));


    private JWTfromHedear jwTfromHedear;

    @Autowired
    public BearerServerAuthenticationConverter(JWTfromHedear jwTfromHedear) {
        this.jwTfromHedear = jwTfromHedear;
    }

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        LOG.debug("convert");
        LOG.debug("heads : {}", exchange.getRequest().getHeaders());
        return Mono.just(exchange)
                .flatMap(jwTfromHedear::extract).log()
                .filter(matchBearerLength).log()
                .flatMap(isolateBearerValue)
                .map(token -> (Authentication) new BearerTokenAuthenticationToken(token))
               // .switchIfEmpty(Mono.just(new BearerTokenAuthenticationToken("@")))
            ;
    }
}