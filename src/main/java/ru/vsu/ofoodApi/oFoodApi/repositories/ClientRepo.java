package ru.vsu.ofoodApi.oFoodApi.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.vsu.ofoodApi.oFoodApi.entities.Client;

import java.util.Date;

@Repository
public interface ClientRepo extends ReactiveMongoRepository<Client, String> {
    @Query(sort = "{datePublication : -1}")
    Flux<Client> findAllByRegistrationDateBefore(Date flag);
}