package ru.vsu.ofoodApi.oFoodApi.services;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.vsu.ofoodApi.oFoodApi.dto.RegistrationNotificationDTO;
import ru.vsu.ofoodApi.oFoodApi.entities.Client;
import ru.vsu.ofoodApi.oFoodApi.entities.Promotion;
import ru.vsu.ofoodApi.oFoodApi.entities.StatusOrder;
import ru.vsu.ofoodApi.oFoodApi.repositories.ClientRepo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class PushNotificationService {

    private final Logger LOG = LoggerFactory.getLogger(PushNotificationService.class);
    private FirebaseOptions firebaseOptions;
    private ClientRepo clientRepo;
    private PromotionService promotionService;

    @Autowired
    public PushNotificationService(
        FirebaseOptions firebaseOptions,
        ClientRepo clientRepo,
        PromotionService promotionService
    ) {
        this.firebaseOptions = firebaseOptions;
        this.clientRepo = clientRepo;
        this.promotionService = promotionService;
        FirebaseApp.initializeApp(firebaseOptions);
    }

    public Mono<Client> saveClient(RegistrationNotificationDTO registrationNotificationDTO) {
        Client client = new Client();
        client.setRegistrationId(registrationNotificationDTO.getRegistrationId());
        client.setRegistrationDate(new Date());
        return clientRepo.save(client);
    }

    public void notifyClient(Promotion promotion, Client client) throws FirebaseMessagingException {
        LOG.debug("notify client");
        Message message = Message.builder()
            .setToken(client.getRegistrationId())
            .setNotification(
                Notification.builder()
                    .setTitle(promotion.getName())
                    .setBody(promotion.getDescribe())
                    .build()
            )
            .build();
        String response = FirebaseMessaging.getInstance().send(message);
        LOG.debug("response : {}", response);
    }

    public String sendPushNotification(String registrationId, String title, String body) throws FirebaseMessagingException {
        Message message = Message.builder()
            .setToken(registrationId)
            .setNotification(
                Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build()
            )
            .build();
         return FirebaseMessaging.getInstance().send(message);
    }

    public void pushNotification() {
        LOG.debug("push notification");
        Message message = Message.builder()
            .setNotification(
                Notification.builder()
                    .setTitle("Hello")
                    .setBody("50% discount")
                    .build()
            )
            .build();
    }

    @Scheduled(cron = "0 0 18 * * *")
    public void reportCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        Date flag = new Date();
        String date = format.format(flag);
        try {
            LOG.debug("time : {}", format.parse(date).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int mod = 1000 * 60 * 60 * 24;
        LOG.debug("xx {}", format.get2DigitYearStart());
        LOG.debug("current - {}", new Date().getTime());
        LOG.debug("nn - {}", new Date((flag.getTime() / mod) * mod));
        Flux<Promotion> promotionFLux = promotionService.getCurrentPromotions();
        promotionFLux
            .doOnNext(promotion -> {
                LOG.debug("{}", format.format(promotion.getStart()));
                LOG.debug("res : {}", format.format(promotion.getStart()).equals(date));
            })
            .doOnComplete(() -> LOG.debug("ddsad"))
            .subscribe();
        LOG.debug("{}", format.format(flag));
    }
/*
    private void sendPromotion(Promotion promotion) {
        clientRepo.findAllByRegistrationDateBefore(new Date());
    } */
}