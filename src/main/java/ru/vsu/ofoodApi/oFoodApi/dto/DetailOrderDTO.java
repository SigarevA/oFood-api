package ru.vsu.ofoodApi.oFoodApi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.vsu.ofoodApi.oFoodApi.entities.StatusOrder;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailOrderDTO {
    private String id;
    private Date date;
    private String phone;
    private String name;
    private String address;
    private StatusOrder status;
    private List<DishInOrderDTO> goods;
    private double surrender;
    private double totalPrice;
}