package ru.vsu.ofoodApi.oFoodApi.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.vsu.ofoodApi.oFoodApi.entities.Restaurant;
import ru.vsu.ofoodApi.oFoodApi.services.RestaurantService;

@Component
public class RestaurantHandler {
    private final Logger LOG = LoggerFactory.getLogger(OrderHandler.class);
    private RestaurantService restaurantService;

    @Autowired
    public RestaurantHandler(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    public Mono<ServerResponse> getInfoRestaurantById(ServerRequest request) {
        String restaurantId = request.pathVariable("id");
        Mono<Restaurant> restaurantMono = restaurantService.findRestaurantById(restaurantId);
        return ServerResponse
            .ok()
            .body(
                restaurantMono,
                Restaurant.class
            );
    }

    public Mono<ServerResponse> createRestaurant(ServerRequest request) {
        return ServerResponse.ok().body(Mono.just("ds"), String.class);
    }
}
