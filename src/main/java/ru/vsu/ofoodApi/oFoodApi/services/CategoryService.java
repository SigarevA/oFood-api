package ru.vsu.ofoodApi.oFoodApi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.vsu.ofoodApi.oFoodApi.entities.Category;
import ru.vsu.ofoodApi.oFoodApi.errors.CreationCategoryException;
import ru.vsu.ofoodApi.oFoodApi.errors.DeletionCategoryException;
import ru.vsu.ofoodApi.oFoodApi.repositories.CategoryRepo;

import java.util.Date;
import java.util.Objects;

@Service
public class CategoryService {
    private CategoryRepo categoryRepo;

    @Autowired
    public CategoryService(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    public Flux<Category> getCategories() {
        return categoryRepo.findAllByDeletionDateIsNull();
    }

    public Mono<Category> findByID(String id) {
        return categoryRepo.findById(id);
    }

    private Mono<Boolean> checkFreeNumberPage(Category category) {
        return categoryRepo.findByNumberPage(category.getNumberPage())
            .flatMap(cat -> Mono.just(false))
            .switchIfEmpty(Mono.just(true))
            ;
    }

    private Mono<Boolean> checkCategory(Category category) {
        if (Objects.isNull(category.getName()) && category.getName().trim().length() == 0)
            return Mono.error(new CreationCategoryException("Property name has an invalid value"));
        if (category.getNumberPage() < 1)
            return Mono.error(new CreationCategoryException("Property number page has an invalid value"));
        return Mono.just(true);
    }

    public Mono<Category> createCategory(Category category) {
        return Mono.just(category)
            .filterWhen(this::checkCategory)
            .filterWhen(this::checkFreeNumberPage)
            .switchIfEmpty(Mono.error(new CreationCategoryException("Number page is busy")))
            .flatMap(categoryRepo::save)
        ;
    }

    public Mono<Category> deleteCategoryByID(String id) {
        if (Objects.isNull(id))
            return Mono.error(new DeletionCategoryException("id is null"));
        return categoryRepo
            .findById(id)
            .switchIfEmpty(Mono.error(new DeletionCategoryException("Category not found")))
            .map(category -> {
                category.setDeletionDate(new Date());
                return category;
            })
            .flatMap(categoryRepo::save)
            ;
    }
}