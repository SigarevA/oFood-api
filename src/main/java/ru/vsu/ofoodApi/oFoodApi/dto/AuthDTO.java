package ru.vsu.ofoodApi.oFoodApi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthDTO {
    private String login;
    private String password;
}