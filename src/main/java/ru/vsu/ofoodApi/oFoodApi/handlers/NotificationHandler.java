package ru.vsu.ofoodApi.oFoodApi.handlers;

import com.google.firebase.ErrorCode;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.vsu.ofoodApi.oFoodApi.dto.RegistrationNotificationDTO;
import ru.vsu.ofoodApi.oFoodApi.entities.Promotion;
import ru.vsu.ofoodApi.oFoodApi.responses.StatusResponse;
import ru.vsu.ofoodApi.oFoodApi.services.ClientService;
import ru.vsu.ofoodApi.oFoodApi.services.PushNotificationService;

import java.util.ArrayList;
import java.util.Date;

@Component
public class NotificationHandler {

    private final Logger LOG = LoggerFactory.getLogger(NotificationHandler.class);
    private PushNotificationService pushNotificationService;
    private ClientService clientService;

    @Autowired
    public NotificationHandler(PushNotificationService pushNotificationService, ClientService clientService) {
        this.pushNotificationService = pushNotificationService;
        this.clientService = clientService;
    }

    public Mono<ServerResponse> registerClient(ServerRequest request) {
        return request.bodyToMono(RegistrationNotificationDTO.class)
            .flatMap(pushNotificationService::saveClient)
            .map(client -> new StatusResponse(StatusResponse.SUCCESS))
            .flatMap(statusResponse ->
                ServerResponse.ok().body(
                    Mono.just(statusResponse),
                    StatusResponse.class
                )
            );
    }

    public Mono<ServerResponse> testNotifications(ServerRequest request) {
        Date flag = new Date();
        ArrayList<String> dishes = new ArrayList<String>();
        dishes.add("607b0c4b04cf13eb9a56aa7d");
        dishes.add("60a530a022b3d3d4af654700");
        final Promotion promotion = new Promotion(
            "dsa",
            "Hay",
            "Tut",
            new Date(),
            new Date(),
            30,
            false,
            dishes
        );
        return ServerResponse.ok().body(
            clientService.getClients(flag)
                .doOnNext(client -> {
                        LOG.debug("{}", client);
                        try {
                            pushNotificationService.notifyClient(promotion, client);
                        } catch (FirebaseMessagingException e) {
                            if (e.getErrorCode() == ErrorCode.NOT_FOUND) {
                                clientService.deleteClientByRegistrationId(client)
                                    .subscribe();
                            }
                            LOG.error("firebase", e);
                            LOG.debug("getErrorCode : {}", e.getErrorCode());
                            LOG.debug("getMessagingErrorCode : {}", e.getMessagingErrorCode());
                            e.printStackTrace();
                        }
                    }
                ).then(),
            Void.class
        );
    }
}