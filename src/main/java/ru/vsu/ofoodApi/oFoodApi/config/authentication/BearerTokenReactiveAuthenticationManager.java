package ru.vsu.ofoodApi.oFoodApi.config.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.vsu.ofoodApi.oFoodApi.jwt.JWTCustomVerifier;
import ru.vsu.ofoodApi.oFoodApi.jwt.JWTtoAuthentication;


@Component
public class BearerTokenReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final Logger LOG = LoggerFactory.getLogger(BearerTokenReactiveAuthenticationManager.class);

    private JWTtoAuthentication jwTtoAuthentication;
    private JWTCustomVerifier jwtCustomVerifier;

    @Autowired
    public BearerTokenReactiveAuthenticationManager(JWTtoAuthentication jwTtoAuthentication, JWTCustomVerifier jwtCustomVerifier) {
        this.jwTtoAuthentication = jwTtoAuthentication;
        this.jwtCustomVerifier = jwtCustomVerifier;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication)
            .map(auth -> (String) auth.getPrincipal())
            .flatMap(jwtCustomVerifier::check)
            .flatMap(jwTtoAuthentication::create)
            .switchIfEmpty(Mono.error(new AuthenticationException("s") {
                @Override
                public String getMessage() {
                    return super.getMessage();
                }
            }));
    }
}