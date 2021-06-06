package ru.vsu.ofoodApi.oFoodApi.handlers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.vsu.ofoodApi.oFoodApi.dto.CreationDishDTO;
import ru.vsu.ofoodApi.oFoodApi.dto.DeleteDishDTO;
import ru.vsu.ofoodApi.oFoodApi.dto.DishPreviewDTO;
import ru.vsu.ofoodApi.oFoodApi.entities.Dish;
import ru.vsu.ofoodApi.oFoodApi.errors.CreationDishException;
import ru.vsu.ofoodApi.oFoodApi.errors.DeleteDishException;
import ru.vsu.ofoodApi.oFoodApi.responses.StatusResponse;
import ru.vsu.ofoodApi.oFoodApi.responses.StatusWithMsgResponse;
import ru.vsu.ofoodApi.oFoodApi.services.DishService;
import ru.vsu.ofoodApi.oFoodApi.services.GoogleStorageService;
import ru.vsu.ofoodApi.oFoodApi.services.MapperService;
import ru.vsu.ofoodApi.oFoodApi.services.PromotionService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class DishHandler {

    private final Logger LOG = LoggerFactory.getLogger(DishHandler.class);
    private final DishService dishService;
    private final MapperService mapperService;
    private final PromotionService promotionService;
    private final GoogleStorageService googleStorageService;

    @Autowired
    public DishHandler(
        DishService dishService,
        MapperService mapperService,
        PromotionService promotionService,
        GoogleStorageService googleStorageService
    ) {
        this.dishService = dishService;
        this.mapperService = mapperService;
        this.promotionService = promotionService;
        this.googleStorageService = googleStorageService;
    }

    public Mono<ServerResponse> getDishesByCategory(ServerRequest request) {
        String categoryId = request.pathVariable("id");
        return ServerResponse.ok().body(
            dishService.getDishByCategory(categoryId)
                .flatMap(dish -> promotionService
                    .getDiscountForTheProduct(dish.getId())
                    .flatMap(discount -> Mono.just(dish)
                        .map(mapperService::convert)
                        .map(dishPreview -> {
                                dishPreview.setDiscount(discount);
                                return dishPreview;
                            }
                        )
                    )
                )
            ,
            DishPreviewDTO.class
        );
    }

    public Mono<ServerResponse> getCurrentAllDishes(ServerRequest request) {
        return ServerResponse.ok().body(
            dishService.findCurrentDishes().map(mapperService::convert),
            DishPreviewDTO.class
        );
    }

    public Mono<ServerResponse> createDish(ServerRequest request) {
        return request.multipartData()
            .map(MultiValueMap::toSingleValueMap)
            .flatMap(map -> uploadImage(map)
                .zipWith(multipartToCreationDishDto(map))
                .map(tuple2 -> {
                    tuple2.getT2().setPhotoPath(tuple2.getT1());
                    return tuple2.getT2();
                })
            )
        //return request.bodyToMono(CreationDishDTO.class)
            .flatMap(dishService::createDish)
            .flatMap(this::processSuccessCreateDish)
            .onErrorResume(this::processDishException);
    }

    private Mono<CreationDishDTO> multipartToCreationDishDto(Map<String, Part> maps) {
        return Mono.zip(
            getStringValue(maps.get("name").content()),
            getStringValue(maps.get("categoryId").content()),
            getStringValue(maps.get("price").content()),
            getStringValue(maps.get("description").content())
        )
            .map(tuple4 -> {
                CreationDishDTO creationDishDTO = new CreationDishDTO();
                creationDishDTO.setName(tuple4.getT1());
                creationDishDTO.setCategoryId(tuple4.getT2());
                creationDishDTO.setPrice(Integer.parseInt(tuple4.getT3()));
                creationDishDTO.setDescription(tuple4.getT4());
                return creationDishDTO;
            });
    }

    private Mono<String> uploadImage(Map<String, Part> maps) {
        String key = "image";
        return maps.get(key).content()
            .map(DataBuffer::asInputStream)
            .reduce(SequenceInputStream::new)
            .flatMap(is -> {
                try {
                    return Mono.just(is.readAllBytes());
                } catch (IOException ex) {
                    return Mono.error(ex);
                }
            })
            .zipWith(Mono.just(Objects.requireNonNull(maps.get(key).headers().getContentType())))
            .flatMap(tuple2 ->
                googleStorageService.createBlob(tuple2.getT1(), tuple2.getT2().getType())
            )
            ;
    }

    private Mono<String> getStringValue(Flux<DataBuffer> content) {
        return content.map(DataBuffer::asInputStream)
            .reduce(SequenceInputStream::new)
            .map(inputStream -> new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining("\n")))
            ;
    }

    private Mono<ServerResponse> processSuccessCreateDish(Dish dish) {
        return ServerResponse.ok().body(
            Mono.just(new StatusResponse(StatusResponse.SUCCESS)),
            StatusResponse.class
        );
    }

    private Mono<ServerResponse> processDishException(Throwable throwable) {
        LOG.error("processDishException", throwable);
        if (
            throwable instanceof CreationDishException ||
                throwable instanceof DeleteDishException
        ) {
            return ServerResponse.badRequest().body(
                Mono.just(new StatusWithMsgResponse(StatusResponse.FAILURE, throwable.getMessage())),
                StatusResponse.class
            );
        }
        return Mono.error(throwable);
    }

    public Mono<ServerResponse> removeDish(ServerRequest request) {
        return request.bodyToMono(DeleteDishDTO.class)
            .filter(deleteDishDTO -> Objects.nonNull(deleteDishDTO.getDishID()))
            .map(DeleteDishDTO::getDishID)
            .flatMap(dishService::deleteDish)
            .flatMap(this::processSuccessCreateDish)
            .onErrorResume(this::processDishException)
            .switchIfEmpty(processEmptyDelete());
    }

    private Mono<ServerResponse> processEmptyDelete() {
        return ServerResponse.badRequest().build();
    }
}