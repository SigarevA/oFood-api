package ru.vsu.ofoodApi.oFoodApi.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.vsu.ofoodApi.oFoodApi.dto.CreationDishDTO;
import ru.vsu.ofoodApi.oFoodApi.entities.Dish;
import ru.vsu.ofoodApi.oFoodApi.errors.CreationDishException;
import ru.vsu.ofoodApi.oFoodApi.errors.DeleteDishException;
import ru.vsu.ofoodApi.oFoodApi.repositories.DishRepo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@Service
public class DishService {
    private final Logger LOG = LoggerFactory.getLogger(DishService.class);
    private DishRepo dishRepo;
    private CategoryService categoryService;

    @Autowired
    public DishService(
        DishRepo dishRepo,
        CategoryService categoryService

    ) {
        this.dishRepo = dishRepo;
        this.categoryService = categoryService;
    }

    public Flux<Dish> getDishByCategory(String categoryId) {
        return dishRepo.findByCategoryIdAndDeletionDateIsNull(categoryId);
    }

    public Mono<Dish> findDishById(String dishId) {
        return dishRepo.findById(dishId);
    }

    public Flux<Dish> findCurrentDishes() {
        return dishRepo.findAllByDeletionDateIsNull();
    }

    private Mono<Boolean> checkDishDTO(CreationDishDTO creationDishDTO) {
        if (
            Objects.isNull(creationDishDTO.getDescription()) ||
                Objects.isNull(creationDishDTO.getName()) ||
                Objects.isNull(creationDishDTO.getPhotoPath()) ||
                Objects.isNull(creationDishDTO.getCategoryId())
        )
            return Mono.error(new CreationDishException("Required field is empty"));
        else if (creationDishDTO.getName().trim().length() == 0)
            return Mono.error(new CreationDishException("Name not defined"));
        if (creationDishDTO.getDescription().trim().length() == 0)
            return Mono.error(new CreationDishException("Description not defined"));
        if (creationDishDTO.getPhotoPath().trim().length() == 0)
            return Mono.error(new CreationDishException("PhotoPath not defined"));
        else if (creationDishDTO.getPrice() <= 0)
            return Mono.error(new CreationDishException("Negative price"));
        return Mono.just(true);
    }

    private Mono<Boolean> checkingForCategoryExistence(CreationDishDTO creationDishDTO) {
        return categoryService
            .findByID(creationDishDTO.getCategoryId())
            .map(category -> true)
            .switchIfEmpty(Mono.error(new CreationDishException("Category does not exist")));
    }

    public Mono<Dish> createDish(CreationDishDTO creationDishDTO) {
        return Mono.just(creationDishDTO)
            .filterWhen(this::checkDishDTO)
            .filterWhen(this::checkingForCategoryExistence)
            .map(this::convertCreationDishDTOToDish)
            .flatMap(dishRepo::save)
        ;
    }

    private Dish convertCreationDishDTOToDish(CreationDishDTO creationDishDTO) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        Date creationDate = null;
        try {
            creationDate = format.parse(format.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Dish dish = new Dish();
        dish.setDateAdded(creationDate);
        dish.setName(creationDishDTO.getName());
        dish.setPrice(creationDishDTO.getPrice());
        dish.setDescription(creationDishDTO.getDescription());
        dish.setPhotoPath(creationDishDTO.getPhotoPath());
        dish.setCategoryId(creationDishDTO.getCategoryId());
        return dish;
    }

    public Mono<Dish> deleteDish(String id) {
        return dishRepo.findById(id)
            .switchIfEmpty(Mono.error(new DeleteDishException("dish not found")))
            .map(dish -> {
                    dish.setDeletionDate(new Date());
                    return dish;
                }
            )
            .flatMap(dishRepo::save)
            ;
    }

    public Flux<Dish> getDishesFromIterableId(Iterable<String> ids) {
        return Flux.fromIterable(ids).flatMap(
            dishRepo::findById
        );
    }
}