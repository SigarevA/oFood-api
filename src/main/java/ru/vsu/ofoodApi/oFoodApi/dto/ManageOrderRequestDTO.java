package ru.vsu.ofoodApi.oFoodApi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.vsu.ofoodApi.oFoodApi.entities.StatusOrder;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManageOrderRequestDTO {
    private String orderID;
    private StatusOrder statusOrder;
}