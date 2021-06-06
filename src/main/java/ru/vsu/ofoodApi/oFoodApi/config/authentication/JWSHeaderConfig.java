package ru.vsu.ofoodApi.oFoodApi.config.authentication;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JWSHeaderConfig {

    @Bean
    public JWSHeader getJWSHeader () {
        return  new JWSHeader(JWSAlgorithm.HS256);
    }
}
