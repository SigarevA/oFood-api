package ru.vsu.ofoodApi.oFoodApi.jwt;


import com.nimbusds.jwt.SignedJWT;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JWTtoAuthentication {

    public Mono<Authentication> create(SignedJWT signedJWTMono) {
        String subject;
        String auths;
        List authorities;

        try {
            subject = signedJWTMono.getJWTClaimsSet().getSubject();
            auths = (String) signedJWTMono.getJWTClaimsSet().getClaim("roles");
        } catch (ParseException e) {
            return Mono.empty();
        }

        authorities = Stream.of(auths.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return Mono.justOrEmpty(new UsernamePasswordAuthenticationToken(subject, null, authorities));
    }
}
