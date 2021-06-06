package ru.vsu.ofoodApi.oFoodApi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import ru.vsu.ofoodApi.oFoodApi.handlers.*;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterFunctionConfig {

    @Bean
    public RouterFunction promotionRouterFunction(PromotionHandler promotionHandler) {
        return route()
            .GET("/promotion/all", promotionHandler::getPromotions)
            .GET("/detail-promotion/{id}", promotionHandler::getDetailPromotion)
            .POST("/promotion/create", promotionHandler::createPromotion)
            .build();
    }

    @Bean
    public RouterFunction restaurantRouterFunction(RestaurantHandler restaurantHandler) {
        return route()
            .GET("/restaurant/info/{id}", restaurantHandler::getInfoRestaurantById)
            .build()
            ;
    }

    @Bean
    public RouterFunction categoryRouterFunction(CategoryHandler categoryHandler) {
        return route()
            .GET("/category/all", categoryHandler::getAllCategories)
            .POST("/create-category", categoryHandler::createCategory)
            .POST("/delete-category", categoryHandler::deleteCategory)
            .build();
    }

    @Bean
    public RouterFunction orderRouterFunction(OrderHandler orderHandler) {
        return route()
            .POST("/order/create", orderHandler::processOrderCreation)
            .GET("/order-by-status/{status}", orderHandler::getOrdersWithStatus)
            .GET("/order-by-id/{id}", orderHandler::getOrderById)
            .POST("/order/manage", orderHandler::manageOrder)
            .build();
    }

    @Bean
    public RouterFunction notificationRouter(NotificationHandler notificationHandler) {
        return route()
            .POST("/register-client", notificationHandler::registerClient)
            .GET("/test", notificationHandler::testNotifications)
            .build()
            ;
    }

    @Bean
    public RouterFunction dishRouterFunction(DishHandler dishHandler) {
        return route()
            .GET("/all-dishes-by-category/{id}", dishHandler::getDishesByCategory)
            .GET("/all-current-dishes", dishHandler::getCurrentAllDishes)
            .POST(RequestPredicates.POST("/dish/create").and(accept(MediaType.MULTIPART_FORM_DATA)), dishHandler::createDish)
            .POST("/dish/remove", dishHandler::removeDish)
            .build();
    }
}