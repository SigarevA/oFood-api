package ru.vsu.ofoodApi.oFoodApi.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coordinate {
    private double latitude;
    private double longitude;
}