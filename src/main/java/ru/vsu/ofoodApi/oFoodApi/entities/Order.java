package ru.vsu.ofoodApi.oFoodApi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "order")
public class Order {
    @Id
    private String id;
    private Date date;
    private String phone;
    private String name;
    private String city;
    private String street;
    private String house;
    private String entrance;
    @JsonIgnore
    private StatusOrder status;
    private String flat;
    @JsonIgnore
    private String registrationId;
    private Map<String, InfoDishInOrder> goods;
    private double surrender;
    @JsonIgnore
    private double totalPrice;
}