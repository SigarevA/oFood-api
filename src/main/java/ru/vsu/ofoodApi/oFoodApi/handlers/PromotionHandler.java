package ru.vsu.ofoodApi.oFoodApi.handlers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.vsu.ofoodApi.oFoodApi.dto.CreationPromotionDTO;
import ru.vsu.ofoodApi.oFoodApi.dto.DetailPromotionDTO;
import ru.vsu.ofoodApi.oFoodApi.dto.DishInDetailPromotionDTO;
import ru.vsu.ofoodApi.oFoodApi.entities.Dish;
import ru.vsu.ofoodApi.oFoodApi.entities.Promotion;
import ru.vsu.ofoodApi.oFoodApi.errors.CreationPromotionError;
import ru.vsu.ofoodApi.oFoodApi.responses.StatusResponse;
import ru.vsu.ofoodApi.oFoodApi.responses.StatusWithMsgResponse;
import ru.vsu.ofoodApi.oFoodApi.services.DishService;
import ru.vsu.ofoodApi.oFoodApi.services.PromotionService;

@Component
public class PromotionHandler {
    private final Logger LOG = LoggerFactory.getLogger(OrderHandler.class);
    private PromotionService promotionService;
    private DishService dishService;

    @Autowired
    public PromotionHandler(
        PromotionService promotionService,
        DishService dishService
    ) {
        this.promotionService = promotionService;
        this.dishService = dishService;
    }

    public Mono<ServerResponse> getPromotions(ServerRequest request) {
        return ServerResponse.ok().body(
            promotionService.getCurrentPromotions(),
            Promotion.class
        );
    }

    public Mono<ServerResponse> createPromotion(ServerRequest request) {
        return request.bodyToMono(CreationPromotionDTO.class)
            .flatMap(promotionService::createPromotion)
            .flatMap(this::processSuccess)
            .onErrorResume(this::processFailure)
            ;
    }

    private Mono<ServerResponse> processSuccess(Promotion promotion) {
        return ServerResponse.ok().body(
            Mono.just(new StatusResponse(
                StatusResponse.SUCCESS)
            ),
            StatusResponse.class
        );
    }

    private Mono<ServerResponse> processFailure(Throwable ex) {
        LOG.error("error", ex);
        if (ex instanceof CreationPromotionError)
            return ServerResponse.badRequest().body(
                Mono.just(new StatusWithMsgResponse(StatusResponse.FAILURE, ex.getMessage())),
                StatusResponse.class
            );
        LOG.error("error", ex);
        return ServerResponse.badRequest().build();
    }

    public Mono<ServerResponse> getDetailPromotion(ServerRequest request) {
        String id = request.pathVariable("id");
        return promotionService.findPromotionById(id)
            .flatMap(this::processSuccessDetailPromotion)
            .switchIfEmpty(
                ServerResponse.badRequest()
                    .body(
                        new StatusWithMsgResponse(StatusResponse.FAILURE, "Promotion not found"),
                        StatusWithMsgResponse.class
                    )
            );
    }

    private Mono<ServerResponse> processSuccessDetailPromotion(Promotion promotion) {
        return dishService.getDishesFromIterableId(promotion.getDishes())
            .map(this::convert)
            .collectList()
            .map(dishes -> {
                    DetailPromotionDTO detail = new DetailPromotionDTO();
                    detail.setDishes(dishes);
                    return detail;
                }
            )
            .map(detail -> fillDetail(promotion, detail))
            .flatMap(detail ->
                ServerResponse.ok().body(
                    Mono.just(detail),
                    DetailPromotionDTO.class
                )
            )
            ;
    }

    private DishInDetailPromotionDTO convert(Dish dish) {
        return new DishInDetailPromotionDTO(
            dish.getId(),
            dish.getName()
        );
    }

    private DetailPromotionDTO fillDetail(Promotion promotion, DetailPromotionDTO detail) {
        detail.setStart(promotion.getStart());
        detail.setEnd(promotion.getEnd());
        detail.setId(promotion.getId());
        detail.setCanceled(promotion.isCanceled());
        detail.setDescribe(promotion.getDescribe());
        detail.setName(promotion.getName());
        detail.setDiscount(promotion.getDiscount());
        return detail;
    }
}