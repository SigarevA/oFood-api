package ru.vsu.ofoodApi.oFoodApi.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "dish")
public class Dish {
    @Id
    private String id;
    private double price;
    private String name;
    private String description;
    private String photoPath;
    private String categoryId;
    private Date dateAdded;
    private Date deletionDate;
}