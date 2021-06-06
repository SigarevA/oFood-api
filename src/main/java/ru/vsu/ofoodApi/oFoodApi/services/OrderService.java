package ru.vsu.ofoodApi.oFoodApi.services;

import com.google.firebase.messaging.FirebaseMessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import ru.vsu.ofoodApi.oFoodApi.dto.InfoDishInOrderDTO;
import ru.vsu.ofoodApi.oFoodApi.dto.OrderCreationDTO;
import ru.vsu.ofoodApi.oFoodApi.entities.Dish;
import ru.vsu.ofoodApi.oFoodApi.entities.InfoDishInOrder;
import ru.vsu.ofoodApi.oFoodApi.entities.Order;
import ru.vsu.ofoodApi.oFoodApi.entities.StatusOrder;
import ru.vsu.ofoodApi.oFoodApi.errors.CreationOrderError;
import ru.vsu.ofoodApi.oFoodApi.errors.ManageOrderException;
import ru.vsu.ofoodApi.oFoodApi.repositories.OrderRepo;

import java.util.*;

@Service
public class OrderService {
    private final Logger LOG = LoggerFactory.getLogger(OrderService.class);
    private OrderRepo orderRepo;
    private DishService dishService;
    private PromotionService promotionService;
    private PushNotificationService pushNotificationService;

    @Autowired
    public OrderService(
        OrderRepo orderRepo,
        DishService dishService,
        PromotionService promotionService,
        PushNotificationService pushNotificationService
    ) {
        this.orderRepo = orderRepo;
        this.dishService = dishService;
        this.promotionService = promotionService;
        this.pushNotificationService = pushNotificationService;
    }

    private boolean checkEmptyProperty(OrderCreationDTO orderCreationDTO) {
        return Objects.isNull(orderCreationDTO.getName()) ||
            Objects.isNull(orderCreationDTO.getPhone()) ||
            Objects.isNull(orderCreationDTO.getCity()) ||
            Objects.isNull(orderCreationDTO.getStreet()) ||
            Objects.isNull(orderCreationDTO.getHouse()) ||
            Objects.isNull(orderCreationDTO.getEntrance()) ||
            Objects.isNull(orderCreationDTO.getFlat())
            ;
    }

    private Mono<Boolean> checkCount(InfoDishInOrderDTO infoDishInOrderDTO) {
        if (infoDishInOrderDTO.getCount() < 1)
            return Mono.error(new CreationOrderError("Invalid amount"));
        if (Objects.isNull(infoDishInOrderDTO.getDishId()))
            return Mono.error(new CreationOrderError("Empty dishId"));
        else
            return Mono.just(true);
    }

    private Mono<List<Tuple2<Dish, Integer>>> checkingExistenceDishes(OrderCreationDTO orderCreationDTO) {
        return Flux.fromIterable(orderCreationDTO.getGoods())
            .filterWhen(this::checkCount)
            .map(InfoDishInOrderDTO::getDishId)
            .flatMap(dishId ->
                dishService.findDishById(dishId)
                    .switchIfEmpty(Mono.error(new CreationOrderError("product does not exist")))
                    .zipWith(promotionService.getDiscountForTheProduct(dishId))
            )
            .collectList()
            ;
    }

    public Mono<Boolean> validCreationOrder(OrderCreationDTO orderCreationDTO) {
        if (checkEmptyProperty(orderCreationDTO)) {
            return Mono.error(new CreationOrderError("empty required property"));
        } else if (
            Objects.nonNull(orderCreationDTO.getDate()) &&
                orderCreationDTO.getDate().before(new Date())
        ) {
            return Mono.error(new CreationOrderError("retroactive order"));
        } else
            return Mono.just(true);
    }

    public Mono<Order> createOrder(OrderCreationDTO orderCreationDTO) {
        LOG.debug("create order");
        return Mono.just(orderCreationDTO)
            .filterWhen(this::validCreationOrder)
            .flatMap(orderCreationDTO1 -> Mono.just(orderCreationDTO1)
                .zipWith(checkingExistenceDishes(orderCreationDTO1))
                .map(this::convertTuple2ToOrder)
            )
            .flatMap(orderRepo::save)
            ;
    }

    public Flux<Order> getOrders(StatusOrder statusOrder) {
        return orderRepo.findAllByStatus(statusOrder);
    }

    private Order convertTuple2ToOrder(Tuple2<OrderCreationDTO, List<Tuple2<Dish, Integer>>> tuple) {
        Order order = new Order();
        OrderCreationDTO orderCreationDTO = tuple.getT1();
        order.setPhone(orderCreationDTO.getPhone());
        order.setName(orderCreationDTO.getName());
        order.setCity(orderCreationDTO.getCity());
        order.setStreet(orderCreationDTO.getStreet());
        order.setHouse(orderCreationDTO.getHouse());
        order.setEntrance(orderCreationDTO.getEntrance());
        order.setStatus(StatusOrder.INIT);
        order.setFlat(orderCreationDTO.getFlat());
        order.setRegistrationId(orderCreationDTO.getRegistrationId());
        order.setSurrender(orderCreationDTO.getSurrender());
        double totalPrice = 0.0;
        Map<String, InfoDishInOrder> goods = new HashMap<>();
        List<Tuple2<Dish, Integer>> dishesWithDiscount = tuple.getT2();
        for (Tuple2<Dish, Integer> dishWithDiscount : dishesWithDiscount) {
            InfoDishInOrder infoDishInOrder = new InfoDishInOrder();
            infoDishInOrder.setPrice(dishWithDiscount.getT1().getPrice() * ((100 - dishWithDiscount.getT2()) / 100.0));
            for (InfoDishInOrderDTO infoDishInOrderDTO : orderCreationDTO.getGoods()) {
                LOG.debug("infoDTO : {}", infoDishInOrderDTO);
                LOG.debug("dish : {}", dishWithDiscount.getT1());
                LOG.debug("result : {}", infoDishInOrderDTO.getDishId().equals(dishWithDiscount.getT1().getId()));
                if (infoDishInOrderDTO.getDishId().equals(dishWithDiscount.getT1().getId())) {
                    infoDishInOrder.setCount(infoDishInOrderDTO.getCount());
                    break;
                }
            }
            LOG.debug("info : {}", infoDishInOrder);
            goods.put(dishWithDiscount.getT1().getId(), infoDishInOrder);
        }
        order.setGoods(goods);
        return order;
    }

    public Mono<Order> getOrderByID(String id) {
        return orderRepo.findById(id);
    }

    public Mono<Order> manageOrder(String id, StatusOrder status) {
        return orderRepo.findById(id)
            .switchIfEmpty(Mono.error(new ManageOrderException("Order not found")))
            .filter(order -> this.checkStatus(order.getStatus(), status))
            .switchIfEmpty(Mono.error(new ManageOrderException("Invalid status")))
            .map(order -> {
                order.setStatus(status);
                return order;
            })
            .flatMap(orderRepo::save)
            .doOnNext(this::sendPush)
            ;
    }

    private void sendPush(Order order) {
        LOG.debug("send push");
        LOG.debug("order : {}", order);
        int count = 0;
        double price = 0.0;
        for (InfoDishInOrder infoDishInOrder : order.getGoods().values()) {
            price += infoDishInOrder.getCount() * infoDishInOrder.getPrice();
            count += infoDishInOrder.getCount();
        }
        String body = "Общее количество товаров: " + count + ", общая стоимость : " + price;
        String title = "";
        if (order.getStatus() == StatusOrder.ACCEPTED)
            title = "Заказ принят!";
        if (order.getStatus() == StatusOrder.REJECTED)
            title = "Заказ отклонен!";
        try {
            pushNotificationService.sendPushNotification(
                order.getRegistrationId(),
                title,
                body
            );
        } catch (FirebaseMessagingException e) {
            LOG.error("sendPush", e);
        }

    }

    private boolean checkStatus(StatusOrder prevStatus, StatusOrder newStatus) {
        if (StatusOrder.INIT == prevStatus)
            return newStatus == StatusOrder.ACCEPTED || newStatus == StatusOrder.REJECTED;
        else if (StatusOrder.ACCEPTED == prevStatus)
            return newStatus == StatusOrder.REJECTED || newStatus == StatusOrder.EXECUTED;
        else
            return false;
    }
}