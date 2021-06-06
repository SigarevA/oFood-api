package ru.vsu.ofoodApi.oFoodApi.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.vsu.ofoodApi.oFoodApi.entities.Promotion;

import java.util.Date;

@Repository
public interface PromotionRepo extends ReactiveMongoRepository<Promotion, String> {
    Flux<Promotion> findAllByStartBeforeAndEndAfter(Date currentForStart, Date current);
}