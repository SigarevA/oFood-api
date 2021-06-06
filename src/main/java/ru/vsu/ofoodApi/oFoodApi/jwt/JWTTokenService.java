package ru.vsu.ofoodApi.oFoodApi.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Period;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;


@Component
public class JWTTokenService {

    private final JWTCustomSigner jwtCustomSigner;
    private final static Logger LOG = LoggerFactory.getLogger(JWTTokenService.class);

    private final JWSHeader jwsHeader;

    @Autowired
    public JWTTokenService(JWTCustomSigner jwtCustomSigner, @Qualifier("getJWSHeader") JWSHeader jwsHeader) {
        this.jwtCustomSigner = jwtCustomSigner;
        this.jwsHeader = jwsHeader;
    }

    public String generateToken(String subject, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        SignedJWT signedJWT;
        JWTClaimsSet claimsSet;

        claimsSet = new JWTClaimsSet.Builder()
                .subject(subject)
                .issuer("ru.cs.vsu.crocodile")
                .expirationTime(new Date(getExpiration()))
                .claim("roles", authorities
                        .stream()
                        .map(GrantedAuthority.class::cast)
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(",")))
                .build();

        signedJWT = new SignedJWT(jwsHeader, claimsSet);

        try {
            signedJWT.sign(jwtCustomSigner.getSigner());
        } catch (JOSEException e) {
            e.printStackTrace();
        }

        return signedJWT.serialize();
    }

    private static long getExpiration(){
        Date now = new Date();
        LOG.debug("### date of issue : {} ", now.getTime());
        return new Date().toInstant()
                .plus(Period.ofDays(1))
                .toEpochMilli();
    }
}