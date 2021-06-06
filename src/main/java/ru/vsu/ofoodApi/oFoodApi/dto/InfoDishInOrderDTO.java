package ru.vsu.ofoodApi.oFoodApi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InfoDishInOrderDTO {
    private String dishId;
    private int count;
}