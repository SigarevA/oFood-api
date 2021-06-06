package ru.vsu.ofoodApi.oFoodApi.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.vsu.ofoodApi.oFoodApi.dto.CreationPromotionDTO;
import ru.vsu.ofoodApi.oFoodApi.entities.Promotion;
import ru.vsu.ofoodApi.oFoodApi.repositories.PromotionRepo;

import java.util.ArrayList;


import static org.mockito.ArgumentMatchers.any;

public class PromotionServiceTest {

    private final Logger LOG = LoggerFactory.getLogger(PromotionServiceTest.class);

    private PromotionService promotionService;
    private CreationPromotionDTO creationPromotionDTO;
    private PromotionRepo promotionRepo;

    @BeforeEach
    void setUp() {
        LOG.debug("setup test");
        MapperService mapperService = new MapperService();
        promotionRepo = Mockito.mock(PromotionRepo.class);
        Mockito.when(promotionRepo.save(any())).thenReturn(Mono.just(new Promotion()));
        promotionService = new PromotionService(promotionRepo, mapperService);
    }

    @Test
    public void getDiscountForTheProductEmptyPromotionTest() {
        Promotion promotion1 = new Promotion();
        promotion1.setId("12");
        Promotion promotion2 = new Promotion();
        promotion1.setId("23");
        Mockito.when(promotionRepo.findAllByStartAfterAndEndBefore(any(), any()))
            .thenReturn(Flux.just(promotion1, promotion2));
        StepVerifier.create(
            promotionService.getDiscountForTheProduct("45")
        )
            .expectNext(0)
            .verifyComplete()
        ;
    }

    @Test
    public void getDiscountForTheProductOnePromotionTest() {
        Promotion promotion1 = new Promotion();
        promotion1.setId("12");
        Promotion promotion2 = new Promotion();
        promotion1.setId("23");
        ArrayList<String> dishes = new ArrayList<>();
        dishes.add("45");
        dishes.add("28");
        promotion1.setDiscount(45);
        promotion1.setDishes(dishes);
        Mockito.when(promotionRepo.findAllByStartAfterAndEndBefore(any(), any()))
            .thenReturn(Flux.just(promotion1, promotion2));
        StepVerifier.create(
            promotionService.getDiscountForTheProduct("45")
        )
            .expectNext(45)
            .verifyComplete()
        ;
    }

    @Test
    public void getDiscountForTheProductTwoPromotionTest() {
        Promotion promotion1 = new Promotion();
        promotion1.setId("12");
        Promotion promotion2 = new Promotion();
        promotion1.setId("23");
        ArrayList<String> dishes = new ArrayList<>();
        dishes.add("45");
        dishes.add("28");
        promotion1.setDiscount(45);
        promotion1.setDishes(dishes);
        promotion2.setDishes(dishes);
        promotion2.setDiscount(55);
        Mockito.when(promotionRepo.findAllByStartAfterAndEndBefore(any(), any()))
            .thenReturn(Flux.just(promotion1, promotion2));
        StepVerifier.create(
            promotionService.getDiscountForTheProduct("45")
        )
            .expectNext(55)
            .verifyComplete()
        ;
    }

/*
    @Test
    void createPromotion() {
        List<String> dishes = new ArrayList<>();
        dishes.add("new ArrayList<String>()");
        creationPromotionDTO.setDishes(dishes);
        StepVerifier.create(
            promotionService.createPromotion(creationPromotionDTO)
        )
            .expectNext(new Promotion())
            .verifyComplete()
        ;
    }

    @Test
    void startDateAfterEndDate() {
        List<String> dishes = new ArrayList<>();
        dishes.add("new ArrayList<String>()");
        creationPromotionDTO.setDishes(dishes);
        Date current = new Date();
        creationPromotionDTO.setStart(new Date(20000L + current.getTime()));
        creationPromotionDTO.setEnd(new Date(10000L + current.getTime()));
        creationPromotionDTO.setDiscount(45);
        StepVerifier.create(
            promotionService.createPromotion(creationPromotionDTO)
        )
         //   .expectNext(new Promotion())
            .verifyComplete()
        ;
    }
    */
}