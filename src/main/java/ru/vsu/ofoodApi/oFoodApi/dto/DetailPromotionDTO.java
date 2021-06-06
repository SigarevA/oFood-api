package ru.vsu.ofoodApi.oFoodApi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DetailPromotionDTO {
    private String id;
    private String name;
    private String describe;
    private Date start;
    private Date end;
    private int discount;
    private boolean canceled;
    private List<DishInDetailPromotionDTO> dishes;
}
