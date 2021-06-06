package ru.vsu.ofoodApi.oFoodApi.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.vsu.ofoodApi.oFoodApi.entities.Order;
import ru.vsu.ofoodApi.oFoodApi.entities.StatusOrder;

@Repository
public interface OrderRepo extends ReactiveMongoRepository<Order, String> {
    Flux<Order> findAllByStatus(StatusOrder statusOrder);
}