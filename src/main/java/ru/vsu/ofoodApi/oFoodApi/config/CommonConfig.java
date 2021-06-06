package ru.vsu.ofoodApi.oFoodApi.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class CommonConfig {

    private final Logger LOG = LoggerFactory.getLogger(CommonConfig.class);

    @Bean
    public FirebaseOptions getFirebaseOptions() throws IOException {
        Resource resource = new ClassPathResource("ofood-da256-firebase-adminsdk-8qw0n-7f6f6c1b16.json");
        InputStream serviceAccount = resource.getInputStream();

        return FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build();
    }
}