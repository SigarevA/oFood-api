package ru.vsu.ofoodApi.oFoodApi.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import ru.vsu.ofoodApi.oFoodApi.entities.Restaurant;

public interface RestaurantRepo extends ReactiveMongoRepository<Restaurant, String> {
}
