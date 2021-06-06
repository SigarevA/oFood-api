package ru.vsu.ofoodApi.oFoodApi.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.function.Predicate;


@Component
public class JWTCustomVerifier {

    private final Logger LOG = LoggerFactory.getLogger(JWTCustomVerifier.class);

    @Value("${SECRET_KEY}")
    private String secret;

    private JWSVerifier jwsVerifier;

    public JWTCustomVerifier() {
    }

    public Mono<SignedJWT> check(String token) {
        LOG.debug("token : {}", token);
        this.jwsVerifier = this.buildJWSVerifier();
        return Mono.justOrEmpty(createJWS(token))
                .filter(isNotExpired)
                .filter(validSignature);
    }

    private Predicate<SignedJWT> isNotExpired = token -> getExpirationDate(token).after(Date.from(Instant.now()));

    private Predicate<SignedJWT> validSignature = token -> {
        try {
            return token.verify(this.jwsVerifier);
        } catch (JOSEException e) {
            e.printStackTrace();
            return false;
        }
    };

    private MACVerifier buildJWSVerifier() {
        try {
            return new MACVerifier(secret);
        } catch (JOSEException e) {
            e.printStackTrace();
            return null;
        }
    }

    private SignedJWT createJWS(String token) {
        try {
            return SignedJWT.parse(token);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Date getExpirationDate(SignedJWT token) {
        try {
            return token.getJWTClaimsSet()
                    .getExpirationTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
