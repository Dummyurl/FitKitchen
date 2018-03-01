package com.explicate.fitkitchen.model;

/**
 * Created by Mahesh Nikam on 06/02/2017.
 */

public class MealDetailModel {

    private String itemId;
    private String itemName;
    private String dishName;
    private String dishDescription;
    private String dishImage;

    public MealDetailModel() {
    }

    public MealDetailModel(String itemId, String itemName, String dishName, String dishDescription, String dishImage) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.dishName = dishName;
        this.dishDescription = dishDescription;
        this.dishImage = dishImage;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getDishDescription() {
        return dishDescription;
    }

    public void setDishDescription(String dishDescription) {
        this.dishDescription = dishDescription;
    }

    public String getDishImage() {
        return dishImage;
    }

    public void setDishImage(String dishImage) {
        this.dishImage = dishImage;
    }
}
