package ru.vsu.ofoodApi.oFoodApi.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import ru.vsu.ofoodApi.oFoodApi.entities.Dish;

public interface DishRepo extends ReactiveMongoRepository<Dish, String> {
    Flux<Dish> findByCategoryIdAndDeletionDateIsNull(String id);
    Flux<Dish> findAllByDeletionDateIsNull();
}