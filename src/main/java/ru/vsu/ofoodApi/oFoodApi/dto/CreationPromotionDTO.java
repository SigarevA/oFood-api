package ru.vsu.ofoodApi.oFoodApi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreationPromotionDTO {
    private String name;
    private String describe;
    private Date start;
    private Date end;
    private int discount;
    private List<String> dishes;
}