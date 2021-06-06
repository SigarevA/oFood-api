package ru.vsu.ofoodApi.oFoodApi.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "restaurant")
public class Restaurant {
    @Id
    private String id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private Coordinate coordinate;
    private int startOfWorkDay;
    private int endOfTheWorkingDay;
}