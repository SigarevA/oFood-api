package ru.vsu.ofoodApi.oFoodApi.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InfoDishInOrder {
    private int count;
    private double price;
}
