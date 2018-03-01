package com.explicate.fitkitchen.model;

import java.io.Serializable;

/**
 * Created by Mahesh Nikam on 03/02/2017.
 */

public class CartModel implements Serializable {

    private String extraMainId;
    private String extraMainName;
    private String extraDishName;
    private String extraDishPrice;
    private String extraDishImage;

    public CartModel() {
    }

    public CartModel(String extraMainId, String extraMainName, String extraDishName, String extraDishPrice, String extraDishImage) {
        this.extraMainId = extraMainId;
        this.extraMainName = extraMainName;
        this.extraDishName = extraDishName;
        this.extraDishPrice = extraDishPrice;
        this.extraDishImage = extraDishImage;
    }

    public String getExtraMainId() {
        return extraMainId;
    }

    public void setExtraMainId(String extraMainId) {
        this.extraMainId = extraMainId;
    }

    public String getExtraMainName() {
        return extraMainName;
    }

    public void setExtraMainName(String extraMainName) {
        this.extraMainName = extraMainName;
    }

    public String getExtraDishName() {
        return extraDishName;
    }

    public void setExtraDishName(String extraDishName) {
        this.extraDishName = extraDishName;
    }

    public String getExtraDishPrice() {
        return extraDishPrice;
    }

    public void setExtraDishPrice(String extraDishPrice) {
        this.extraDishPrice = extraDishPrice;
    }

    public String getExtraDishImage() {
        return extraDishImage;
    }

    public void setExtraDishImage(String extraDishImage) {
        this.extraDishImage = extraDishImage;
    }
}
