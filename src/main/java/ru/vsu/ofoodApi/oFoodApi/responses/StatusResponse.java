package ru.vsu.ofoodApi.oFoodApi.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatusResponse {
    private String status;

    final public static String SUCCESS = "success";
    final public static String FAILURE = "failure";
}