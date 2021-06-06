package ru.vsu.ofoodApi.oFoodApi.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.vsu.ofoodApi.oFoodApi.entities.Client;
import ru.vsu.ofoodApi.oFoodApi.repositories.ClientRepo;

import java.util.Date;

@Service
public class ClientService {

    final private Logger LOG = LoggerFactory.getLogger(ClientService.class);
    private ClientRepo clientRepo;

    @Autowired
    public ClientService(ClientRepo clientRepo) {
        this.clientRepo = clientRepo;
    }

    public Flux<Client> getAllClients() {
        return clientRepo.findAll();
    }

    public Flux<Client> getClients(Date flag) {
        return clientRepo.findAllByRegistrationDateBefore(flag);
    }

    public Mono<Void> deleteClientByRegistrationId(Client client) {
        return clientRepo.delete(client);
    }
}