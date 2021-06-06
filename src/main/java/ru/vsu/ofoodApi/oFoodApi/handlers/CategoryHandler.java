package ru.vsu.ofoodApi.oFoodApi.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.vsu.ofoodApi.oFoodApi.dto.CreationCategoryDTO;
import ru.vsu.ofoodApi.oFoodApi.dto.DeletionCategoryDTO;
import ru.vsu.ofoodApi.oFoodApi.entities.Category;
import ru.vsu.ofoodApi.oFoodApi.errors.CreationCategoryException;
import ru.vsu.ofoodApi.oFoodApi.errors.DeletionCategoryException;
import ru.vsu.ofoodApi.oFoodApi.responses.StatusResponse;
import ru.vsu.ofoodApi.oFoodApi.responses.StatusWithMsgResponse;
import ru.vsu.ofoodApi.oFoodApi.services.CategoryService;

@Component
public class CategoryHandler {

    private CategoryService categoryService;
    private final Logger LOG = LoggerFactory.getLogger(CategoryHandler.class);

    @Autowired
    public CategoryHandler(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public Mono<ServerResponse> getAllCategories(ServerRequest request) {
        return ServerResponse.ok().body(categoryService.getCategories(), Category.class);
    }

    public Mono<ServerResponse> createCategory(ServerRequest request) {
        return request.bodyToMono(CreationCategoryDTO.class)
            .map(this::convertCreationCategoryDtoTOCategory)
            .flatMap(categoryService::createCategory)
            .flatMap(this::processSuccess)
            .onErrorResume(this::processError);
    }

    public Mono<ServerResponse> deleteCategory(ServerRequest request) {
        return request.bodyToMono(DeletionCategoryDTO.class)
            .map(DeletionCategoryDTO::getCategoryId)
            .flatMap(categoryService::deleteCategoryByID)
            .flatMap(this::processSuccess)
            .onErrorResume(this::processError)
            ;
    }

    private Mono<ServerResponse> processSuccess(Category entity) {
        return ServerResponse.ok().body(
            Mono.just(new StatusResponse(StatusResponse.SUCCESS)),
            StatusResponse.class
        );
    }

    private Mono<ServerResponse> processError(Throwable throwable) {
        if (
            throwable instanceof DeletionCategoryException ||
                throwable instanceof CreationCategoryException
        ) {
            return ServerResponse.badRequest().body(
                Mono.just(new StatusWithMsgResponse(StatusResponse.FAILURE, throwable.getMessage())),
                StatusResponse.class
            );
        }
        return Mono.error(throwable);
    }

    private Category convertCreationCategoryDtoTOCategory(CreationCategoryDTO categoryDTO) {
        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setNumberPage(categoryDTO.getNumberPage());
        return category;
    }
}