package ru.vsu.ofoodApi.oFoodApi.services;

import org.springframework.stereotype.Service;
import ru.vsu.ofoodApi.oFoodApi.dto.CreationPromotionDTO;
import ru.vsu.ofoodApi.oFoodApi.dto.DishPreviewDTO;
import ru.vsu.ofoodApi.oFoodApi.entities.Dish;
import ru.vsu.ofoodApi.oFoodApi.entities.Promotion;

@Service
public class MapperService {
    public DishPreviewDTO convert(Dish dish) {
        return new DishPreviewDTO(
            dish.getId(),
            dish.getPrice(),
            dish.getName(),
            dish.getPhotoPath(),
            dish.getDescription(),
            0
        );
    }

    public Promotion convertToPromotion(CreationPromotionDTO creationPromotionDTO) {
        Promotion promotion = new Promotion();
        promotion.setDishes(creationPromotionDTO.getDishes());
        promotion.setDiscount(creationPromotionDTO.getDiscount());
        promotion.setName(creationPromotionDTO.getName());
        promotion.setDescribe(creationPromotionDTO.getDescribe());
        promotion.setStart(creationPromotionDTO.getStart());
        promotion.setEnd(creationPromotionDTO.getEnd());
        return promotion;
    }
}