package ru.vsu.ofoodApi.oFoodApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OFoodApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(OFoodApiApplication.class, args);
	}

}