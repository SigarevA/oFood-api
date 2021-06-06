package ru.vsu.ofoodApi.oFoodApi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "promotion")
public class Promotion {
    @Id
    private String id;
    private String name;
    @JsonIgnore
    private String describe;
    private Date start;
    private Date end;
    @JsonIgnore
    private int discount;
    @JsonIgnore
    private boolean canceled;
    @JsonIgnore
    private List<String> dishes;
}