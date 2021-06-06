package ru.vsu.ofoodApi.oFoodApi.dto;

public class CreationCategoryDTO {
    private String name;
    private int numberPage;

    public CreationCategoryDTO() {
    }

    public CreationCategoryDTO(String name, int numberPage) {
        this.name = name;
        this.numberPage = numberPage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberPage() {
        return numberPage;
    }

    public void setNumberPage(int numberPage) {
        this.numberPage = numberPage;
    }

    @Override
    public String toString() {
        return "CreationCategoryDTO{" +
            "name='" + name + '\'' +
            ", numberPage=" + numberPage +
            '}';
    }
}