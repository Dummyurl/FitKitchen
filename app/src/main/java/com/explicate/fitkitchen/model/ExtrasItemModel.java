package com.explicate.fitkitchen.model;

/**
 * Created by Mahesh Nikam on 01/02/2017.
 */

public class ExtrasItemModel {

    private String extrasItemId;
    private String extrasItemPrice;
    private String extrasItemName;
    private String extrasItemDescription;
    private String extrasItemImageUrl;

    public ExtrasItemModel() {
    }

    public ExtrasItemModel(String extrasItemId, String extrasItemPrice, String extrasItemName, String extrasItemDescription, String extrasItemImageUrl) {
        this.extrasItemId = extrasItemId;
        this.extrasItemPrice = extrasItemPrice;
        this.extrasItemName = extrasItemName;
        this.extrasItemDescription = extrasItemDescription;
        this.extrasItemImageUrl = extrasItemImageUrl;
    }

    public String getExtrasItemId() {
        return extrasItemId;
    }

    public void setExtrasItemId(String extrasItemId) {
        this.extrasItemId = extrasItemId;
    }

    public String getExtrasItemPrice() {
        return extrasItemPrice;
    }

    public void setExtrasItemPrice(String extrasItemPrice) {
        this.extrasItemPrice = extrasItemPrice;
    }

    public String getExtrasItemName() {
        return extrasItemName;
    }

    public void setExtrasItemName(String extrasItemName) {
        this.extrasItemName = extrasItemName;
    }

    public String getExtrasItemDescription() {
        return extrasItemDescription;
    }

    public void setExtrasItemDescription(String extrasItemDescription) {
        this.extrasItemDescription = extrasItemDescription;
    }

    public String getExtrasItemImageUrl() {
        return extrasItemImageUrl;
    }

    public void setExtrasItemImageUrl(String extrasItemImageUrl) {
        this.extrasItemImageUrl = extrasItemImageUrl;
    }
}
