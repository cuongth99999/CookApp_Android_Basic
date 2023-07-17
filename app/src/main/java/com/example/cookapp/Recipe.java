package com.example.cookapp;

public class Recipe {
    private String image, email, nameRecipe, cookingRecipe;
    private int id;
    private int day, month, year;

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int mouth) {
        this.month = mouth;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNameRecipe() {
        return nameRecipe;
    }

    public void setNameRecipe(String nameRecipe) {
        this.nameRecipe = nameRecipe;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getCookingRecipe() {
        return cookingRecipe;
    }

    public void setCookingRecipe(String cookingRecipe) {
        this.cookingRecipe = cookingRecipe;
    }
}
