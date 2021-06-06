package ru.vsu.ofoodApi.oFoodApi.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.vsu.ofoodApi.oFoodApi.dto.CreationPromotionDTO;
import ru.vsu.ofoodApi.oFoodApi.entities.Promotion;
import ru.vsu.ofoodApi.oFoodApi.errors.CreationPromotionError;
import ru.vsu.ofoodApi.oFoodApi.repositories.PromotionRepo;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PromotionService {

    private final Logger LOG = LoggerFactory.getLogger(RestaurantService.class);
    private PromotionRepo promotionRepo;
    private MapperService mapperService;

    @Autowired
    public PromotionService(
        PromotionRepo promotionRepo,
        MapperService mapperService
    ) {
        this.promotionRepo = promotionRepo;
        this.mapperService = mapperService;
    }

    public Mono<Promotion> findPromotionById(String id ) {
        return promotionRepo.findById(id);
    }

    private Mono<Boolean> checkValidPromotion(CreationPromotionDTO creationPromotionDTO) {
        Date currentDate = new Date();
        if (creationPromotionDTO.getDiscount() < 0 || creationPromotionDTO.getDiscount() > 100) {
            return Mono.error(new CreationPromotionError("discount exceeded limit"));
        } else if (creationPromotionDTO.getStart().after(creationPromotionDTO.getEnd())) {
            return Mono.error(new CreationPromotionError("start date after end date"));
        } else if (creationPromotionDTO.getStart().before(currentDate)) {
            return Mono.error(new CreationPromotionError("start date after end date 2"));
        } else if (creationPromotionDTO.getDishes().isEmpty()) {
            return Mono.error(new CreationPromotionError("dishes empty"));
        } else
            return Mono.just(true);
    }

    private boolean checkNullField(CreationPromotionDTO creationPromotion) {
        return Objects.nonNull(creationPromotion.getName()) &&
            Objects.nonNull(creationPromotion.getDescribe()) &&
            Objects.nonNull(creationPromotion.getDishes()) &&
            Objects.nonNull(creationPromotion.getEnd()) &&
            Objects.nonNull(creationPromotion.getStart())
            ;
    }

    public Mono<Promotion> createPromotion(CreationPromotionDTO creationPromotion) {
        LOG.debug("promotion : {}", creationPromotion);
        return Mono.just(creationPromotion)
            .filter(this::checkNullField)
            .switchIfEmpty(Mono.error(new CreationPromotionError("empty property")))
            .filterWhen(this::checkValidPromotion)
            .map(mapperService::convertToPromotion)
            .flatMap(promotionRepo::save)
        ;
    }

    private Integer getMax(List<Integer> promotions) {
        int max = 0;
        for (int discount : promotions) {
            if (max < discount)
                max = discount;
        }
        return max;
    }

    public Mono<Integer> getDiscountForTheProduct(String id) {
        return getCurrentPromotions()
            .filter(promotion -> Objects.nonNull(promotion.getDishes())
                && promotion.getDishes().contains(id))
            .map(Promotion::getDiscount)
            .collect(Collectors.toList())
            .map(this::getMax)
        ;
    }

    public Flux<Promotion> getCurrentPromotions() {
         Date currentDate = new Date();
         return promotionRepo.findAllByStartBeforeAndEndAfter(currentDate, currentDate);
    }
}