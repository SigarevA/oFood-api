package ru.vsu.ofoodApi.oFoodApi.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.vsu.ofoodApi.oFoodApi.entities.Restaurant;
import ru.vsu.ofoodApi.oFoodApi.repositories.RestaurantRepo;

@Service
public class RestaurantService {
    private final Logger LOG = LoggerFactory.getLogger(RestaurantService.class);
    private RestaurantRepo restaurantRepo;

    @Autowired
    public RestaurantService(RestaurantRepo restaurantRepo) {
        this.restaurantRepo = restaurantRepo;
    }

    public Mono<Restaurant> findRestaurantById(String id) {
        return restaurantRepo.findById(id);
    }
}