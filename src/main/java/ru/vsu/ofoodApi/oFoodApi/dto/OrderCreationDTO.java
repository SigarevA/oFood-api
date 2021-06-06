package ru.vsu.ofoodApi.oFoodApi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreationDTO {
    private Date date;
    private String phone;
    private String name;
    private String city;
    private String street;
    private String house;
    private String entrance;
    private String flat;
    private double surrender;
    private List<InfoDishInOrderDTO> goods;
    private String comment;
    private String registrationId;
}