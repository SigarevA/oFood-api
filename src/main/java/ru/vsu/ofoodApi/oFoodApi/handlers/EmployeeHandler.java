package ru.vsu.ofoodApi.oFoodApi.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import ru.vsu.ofoodApi.oFoodApi.entities.Employee;

import java.util.Objects;

@Component
public class EmployeeHandler {
    private final Logger LOG = LoggerFactory.getLogger(EmployeeHandler.class);
/*
    public Mono<Tuple2<String, String>> authorize(AuthDTO authDTO) {
        Mono<Employee> userMono = userRepository.findByEmail(authDTO.getEmail());
        return userMono
            .filter(user -> Objects.nonNull(authDTO.getPassword()))
            .filter(user -> bCryptPasswordEncoder.matches(authDTO.getPassword(), user.getPassword()))
            .map(this::generateToken)
            .zipWith(userMono.map(User::getId))
            ;
    }
 */
}