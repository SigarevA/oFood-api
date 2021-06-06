package ru.vsu.ofoodApi.oFoodApi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import ru.vsu.ofoodApi.oFoodApi.config.authentication.BearerServerAuthenticationConverter;
import ru.vsu.ofoodApi.oFoodApi.config.authentication.BearerTokenReactiveAuthenticationManager;

@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfig {

    private final Logger LOG = LoggerFactory.getLogger(WebSecurityConfig.class);

    private BearerTokenReactiveAuthenticationManager bearerTokenReactiveAuthenticationManager;
    private BearerServerAuthenticationConverter bearerServerAuthenticationConverter;

    @Autowired
    public WebSecurityConfig(
        BearerTokenReactiveAuthenticationManager bearerTokenReactiveAuthenticationManager,
        BearerServerAuthenticationConverter bearerServerAuthenticationConverter) {
        this.bearerTokenReactiveAuthenticationManager = bearerTokenReactiveAuthenticationManager;
        this.bearerServerAuthenticationConverter = bearerServerAuthenticationConverter;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        LOG.debug("http config");
        return http
            .httpBasic().disable()
            .logout().disable()
            .csrf().disable()
            .authorizeExchange()
            .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .and()
            .authorizeExchange()
        //    .pathMatchers(HttpMethod.GET, "/order/**").authenticated()
        //    .pathMatchers(HttpMethod.POST, "/order/manage").authenticated()
        //    .pathMatchers(HttpMethod.POST, "/promotion/create").authenticated()
            .and()
            .authorizeExchange()
            .anyExchange().permitAll()
            .and()
            .addFilterAt(bearerAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
            .build();
    }

    private AuthenticationWebFilter bearerAuthenticationFilter() {
        LOG.debug("bearerAuthenticationFilter");
        AuthenticationWebFilter bearerAuthenticationFilter;
        ReactiveAuthenticationManager authManager;
        authManager = bearerTokenReactiveAuthenticationManager;
        bearerAuthenticationFilter = new AuthenticationWebFilter(authManager);
        bearerAuthenticationFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/api/**"));
        bearerAuthenticationFilter.setServerAuthenticationConverter(bearerServerAuthenticationConverter);
        return bearerAuthenticationFilter;
    }
}