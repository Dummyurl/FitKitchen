package com.explicate.fitkitchen.model;

/**
 * Created by Mahesh Nikam on 01/02/2017.
 */

public class MealSubItemModel {

    private String categoryId;
    private String subMealCategaory;
    private String submealId;
    private String submealName;
    private String submealPrice;
    private String submealDescription;
    private String subimageUrl;

    public MealSubItemModel() {
    }

    public MealSubItemModel(String categoryId, String subMealCategaory, String submealId, String submealName, String submealPrice, String submealDescription, String subimageUrl) {
        this.categoryId = categoryId;
        this.subMealCategaory = subMealCategaory;
        this.submealId = submealId;
        this.submealName = submealName;
        this.submealPrice = submealPrice;
        this.submealDescription = submealDescription;
        this.subimageUrl = subimageUrl;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getSubMealCategaory() {
        return subMealCategaory;
    }

    public void setSubMealCategaory(String subMealCategaory) {
        this.subMealCategaory = subMealCategaory;
    }

    public String getSubmealId() {
        return submealId;
    }

    public void setSubmealId(String submealId) {
        this.submealId = submealId;
    }

    public String getSubmealName() {
        return submealName;
    }

    public void setSubmealName(String submealName) {
        this.submealName = submealName;
    }

    public String getSubmealPrice() {
        return submealPrice;
    }

    public void setSubmealPrice(String submealPrice) {
        this.submealPrice = submealPrice;
    }

    public String getSubmealDescription() {
        return submealDescription;
    }

    public void setSubmealDescription(String submealDescription) {
        this.submealDescription = submealDescription;
    }

    public String getSubimageUrl() {
        return subimageUrl;
    }

    public void setSubimageUrl(String subimageUrl) {
        this.subimageUrl = subimageUrl;
    }
}
