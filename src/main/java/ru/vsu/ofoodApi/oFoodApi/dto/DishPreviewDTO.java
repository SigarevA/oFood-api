package ru.vsu.ofoodApi.oFoodApi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DishPreviewDTO {
    private String id;
    private double price;
    private String name;
    private String photoPath;
    private String description;
    private Integer discount;
}