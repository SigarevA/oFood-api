package ru.vsu.ofoodApi.oFoodApi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreationDishDTO {
    private double price;
    private String name;
    private String description;
    private String photoPath;
    private String categoryId;
}
