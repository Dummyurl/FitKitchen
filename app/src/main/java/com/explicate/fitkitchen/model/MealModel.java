package com.explicate.fitkitchen.model;

/**
 * Created by Mahesh  on 18/01/2017.
 */

public class MealModel {

    private String mealId;
    private String mealPrice;
    private String mealShortName;
    private String mealDescription;
    private String imageUrl;

    public MealModel() {
    }

    public MealModel(String mealId,String mealPrice, String mealShortName, String mealDescription, String imageUrl) {
        this.mealId = mealId;
        this.mealPrice = mealPrice;
        this.mealShortName = mealShortName;
        this.mealDescription = mealDescription;
        this.imageUrl = imageUrl;
    }

    public String getMealId() {
        return mealId;
    }

    public void setMealId(String mealId) {
        this.mealId = mealId;
    }

    public String getMealPrice() {
        return mealPrice;
    }

    public void setMealPrice(String mealPrice) {
        this.mealPrice = mealPrice;
    }

    public String getMealShortName() {
        return mealShortName;
    }

    public void setMealShortName(String mealShortName) {
        this.mealShortName = mealShortName;
    }

    public String getMealDescription() {
        return mealDescription;
    }

    public void setMealDescription(String mealDescription) {
        this.mealDescription = mealDescription;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
