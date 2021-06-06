package ru.vsu.ofoodApi.oFoodApi.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import ru.vsu.ofoodApi.oFoodApi.dto.DetailOrderDTO;
import ru.vsu.ofoodApi.oFoodApi.dto.DishInOrderDTO;
import ru.vsu.ofoodApi.oFoodApi.dto.ManageOrderRequestDTO;
import ru.vsu.ofoodApi.oFoodApi.dto.OrderCreationDTO;
import ru.vsu.ofoodApi.oFoodApi.entities.Dish;
import ru.vsu.ofoodApi.oFoodApi.entities.InfoDishInOrder;
import ru.vsu.ofoodApi.oFoodApi.entities.Order;
import ru.vsu.ofoodApi.oFoodApi.entities.StatusOrder;
import ru.vsu.ofoodApi.oFoodApi.errors.CreationOrderError;
import ru.vsu.ofoodApi.oFoodApi.errors.ManageOrderException;
import ru.vsu.ofoodApi.oFoodApi.responses.StatusResponse;
import ru.vsu.ofoodApi.oFoodApi.responses.StatusWithMsgResponse;
import ru.vsu.ofoodApi.oFoodApi.services.DishService;
import ru.vsu.ofoodApi.oFoodApi.services.OrderService;

import java.util.*;

@Component
public class OrderHandler {
    private final Logger LOG = LoggerFactory.getLogger(OrderHandler.class);
    private OrderService orderService;
    private DishService dishService;

    @Autowired
    public OrderHandler(
        OrderService orderService,
        DishService dishService
    ) {
        this.orderService = orderService;
        this.dishService = dishService;
    }

    public Mono<ServerResponse> processOrderCreation(ServerRequest request) {
        return request.bodyToMono(OrderCreationDTO.class)
            .doOnNext(orderCreationDTO -> {
                    LOG.debug("orderCreationDTO : {}", orderCreationDTO);
                }
            )
            .flatMap(orderService::createOrder)
            .flatMap(order ->
                ServerResponse.ok()
                    .body(
                        Mono.just(new StatusResponse(StatusResponse.SUCCESS)),
                        StatusResponse.class
                    )
            )
            .onErrorResume(this::processCreationFailure)
            ;
    }

    private Mono<ServerResponse> processCreationFailure(Throwable ex) {
        LOG.error("error", ex);
        if (ex instanceof CreationOrderError)
            return ServerResponse.badRequest().body(
                Mono.just(new StatusWithMsgResponse(StatusResponse.FAILURE, ex.getMessage())),
                StatusResponse.class
            );
        LOG.error("error", ex);
        return ServerResponse.badRequest().build();
    }

    public Mono<ServerResponse> getOrdersWithStatus(ServerRequest request) {
        try {
            StatusOrder statusOrder = StatusOrder.valueOf(request.pathVariable("status"));
            return ServerResponse.ok().body(
                orderService.getOrders(statusOrder),
                Order.class
            );
        } catch (IllegalArgumentException ex) {
            return getServerResponseForStatusNotDefined();
        }
    }

    public Mono<ServerResponse> manageOrder(ServerRequest request) {
        return request.bodyToMono(ManageOrderRequestDTO.class)
            .doOnNext(dto -> LOG.debug("dto : {}", dto))
            .flatMap(dto ->
                orderService.manageOrder(dto.getOrderID(), dto.getStatusOrder())
            )
            .flatMap(order -> ServerResponse.ok().body(
                Mono.just(new StatusResponse(
                    StatusResponse.SUCCESS)
                ),
                StatusResponse.class))
            .onErrorResume(this::processErrorManageOrder);
    }

    private Mono<ServerResponse> processErrorManageOrder(Throwable ex) {
        LOG.error("error", ex);
        if (ex instanceof IllegalArgumentException) {
            return getServerResponseForStatusNotDefined();
        }
        if (ex instanceof ServerWebInputException) {
            return getServerResponseForStatusNotDefined();
        }
        if (ex instanceof ManageOrderException) {
            return ServerResponse.badRequest().body(
                Mono.just(new StatusWithMsgResponse(StatusResponse.FAILURE, ex.getMessage())),
                StatusResponse.class
            );
        }
        return ServerResponse.badRequest().build();
    }

    private Mono<ServerResponse> getServerResponseForStatusNotDefined() {
        return ServerResponse.badRequest()
            .body(
                Mono.just(new StatusWithMsgResponse(StatusResponse.FAILURE, "status not defined")),
                StatusWithMsgResponse.class
            );
    }

    public Mono<ServerResponse> getOrderById(ServerRequest request) {
        String id = request.pathVariable("id");
        LOG.debug("id : {}", id);
        return orderService.getOrderByID(id)
            .flatMap(
                // TODO remove checking
                order -> dishService.getDishesFromIterableId(
                    Objects.nonNull(order.getGoods()) ?
                        order.getGoods().keySet() :
                        Collections.emptyList()
                )
                    .collectList()
                    .map(dishes -> convertOrderToDetailOrder(order, dishes))
            )
            .flatMap(order ->
                ServerResponse.ok().body(
                    Mono.just(order),
                    DetailOrderDTO.class
                )
            )
            .switchIfEmpty(
                ServerResponse.badRequest()
                    .body(
                        Mono.just(new StatusWithMsgResponse(StatusResponse.FAILURE, "order not found")),
                        StatusWithMsgResponse.class
                    )
            );
    }

    private DetailOrderDTO convertOrderToDetailOrder(Order order, List<Dish> dishes) {
        DetailOrderDTO detailOrderDTO = new DetailOrderDTO();
        detailOrderDTO.setId(order.getId());
        detailOrderDTO.setDate(order.getDate());
        detailOrderDTO.setPhone(order.getPhone());
        detailOrderDTO.setName(order.getName());
        detailOrderDTO.setAddress(getAddress(order));
        detailOrderDTO.setStatus(order.getStatus());
        detailOrderDTO.setGoods(getDishesInOrder(order.getGoods(), dishes));
        detailOrderDTO.setSurrender(order.getSurrender());
        detailOrderDTO.setTotalPrice(order.getTotalPrice());
        return detailOrderDTO;
    }

    private List<DishInOrderDTO> getDishesInOrder(
        Map<String, InfoDishInOrder> goods, List<Dish> dishes) {
        ArrayList<DishInOrderDTO> dishInOrderDTOS = new ArrayList<>();
        for (Dish dish : dishes) {
            InfoDishInOrder infoDishInOrder = goods.get(dish.getId());
            DishInOrderDTO dishInOrderDTO = new DishInOrderDTO();
            dishInOrderDTO.setId(dish.getId());
            dishInOrderDTO.setCount(infoDishInOrder.getCount());
            dishInOrderDTO.setName(dish.getName());
            dishInOrderDTO.setPrice(infoDishInOrder.getPrice());
            dishInOrderDTOS.add(dishInOrderDTO);
        }
        return dishInOrderDTOS;
    }

    private String getAddress(Order order) {
        StringBuilder addressBuilder = new StringBuilder();
        addressBuilder.append("г. ")
            .append(order.getCity())
            .append(", ул. ")
            .append(order.getStreet())
            .append(", д. ")
            .append(order.getHouse());
        if (Objects.nonNull(order.getEntrance())) {
            addressBuilder.append(", подъезд ")
                .append(order.getEntrance());
        }
        if (Objects.nonNull(order.getFlat())) {
            addressBuilder.append(", кв. ")
                .append(order.getFlat());
        }
        return addressBuilder.toString();
    }
}