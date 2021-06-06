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
@Document(collection = "category")
public class Category {
    @Id
    private String id;
    private String name;
    private int numberPage;
    private Date dateAdded;
    private Date deletionDate;
}