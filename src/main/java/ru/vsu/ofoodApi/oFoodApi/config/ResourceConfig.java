package ru.vsu.ofoodApi.oFoodApi.config;


import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Configuration
public class ResourceConfig {

    private static final String PROJECT_ID = "ofood-314711";
    private final Logger LOG = LoggerFactory.getLogger(ResourceConfig.class);

    @Bean
    public Resource loadJsonKey() {
        return new ClassPathResource("ofood-314711-c34f3cc08911.json");
    }

    @Bean
    public Mono<Credentials> getCredentials(@Qualifier("loadJsonKey") Resource resource) {
        Credentials credentials = null;
        LOG.debug("getFileName : {}", resource.getFilename());
        try {
            credentials = GoogleCredentials
                .fromStream(resource.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOG.debug("credentials : {}", credentials);
        return Mono.justOrEmpty(credentials).doOnNext(cred -> LOG.debug("credit next"));
    }

    @Bean
    public Mono<Storage> getStorage(Mono<Credentials> credentialsMono) {
        return credentialsMono.map(
            credentials -> StorageOptions.newBuilder()
                .setCredentials(credentials)
                .setProjectId(PROJECT_ID)
                .build()
        )
            .map(StorageOptions::getService);
    }
}