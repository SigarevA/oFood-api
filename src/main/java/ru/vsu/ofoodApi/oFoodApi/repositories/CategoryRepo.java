package ru.vsu.ofoodApi.oFoodApi.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.vsu.ofoodApi.oFoodApi.entities.Category;

public interface CategoryRepo extends ReactiveMongoRepository<Category, String> {
    Mono<Category> findByNumberPage(int numberPage);
    Flux<Category> findAllByDeletionDateIsNull();
}