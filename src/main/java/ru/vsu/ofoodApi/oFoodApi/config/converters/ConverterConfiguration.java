package ru.vsu.ofoodApi.oFoodApi.config.converters;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;

@Configuration
@EnableReactiveMongoRepositories(basePackages = "ru.vsu.ofoodApi.oFoodApi")
@EnableTransactionManagement
public class ConverterConfiguration {

    @Bean
    public ReactiveTransactionManager transactionManager(ReactiveMongoDatabaseFactory da) {
        return new ReactiveMongoTransactionManager(da);
    }

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(List.of(
                new DateToLongConverter(),
                new LongToDateConverter()
        ));
    }
}