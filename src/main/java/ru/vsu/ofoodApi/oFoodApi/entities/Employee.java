package ru.vsu.ofoodApi.oFoodApi.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    private String id;
    private String role;
    private String FIO;
    private String phone;
    private String login;
    private String password;
}