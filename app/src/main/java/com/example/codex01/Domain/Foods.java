package com.example.codex01.Domain;

import java.io.Serializable;

public class Foods implements Serializable {
    private int CategoryID;
    private String Description;
    private boolean BestFood;
    private int ID;
    private int LocationID;
    private double Price;
    private String ImagePath;
    private int PriceID;
    private double Star;
    private int TimeID;
    private int TimeValue;
    private String Title;
    private int numberInCart;
    private int numberInFavorite;

    public Foods() {

    }

    @Override
    public String toString() {
        return Title;
    }

    public int getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(int categoryID) {
        CategoryID = categoryID;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public boolean isBestFood() {
        return BestFood;
    }

    public void setBestFood(boolean bestFood) {
        BestFood = bestFood;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getLocationID() {
        return LocationID;
    }

    public void setLocationID(int locationID) {
        LocationID = locationID;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }

    public int getPriceID() {
        return PriceID;
    }

    public void setPriceID(int priceID) {
        PriceID = priceID;
    }

    public double getStar() {
        return Star;
    }

    public void setStar(double star) {
        Star = star;
    }

    public int getTimeID() {
        return TimeID;
    }

    public void setTimeID(int timeID) {
        TimeID = timeID;
    }

    public int getTimeValue() {
        return TimeValue;
    }

    public void setTimeValue(int timeValue) {
        TimeValue = timeValue;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public int getNumberInCart() {
        return numberInCart;
    }

    public int getNumberInFavorite() {
        return numberInFavorite;
    }

    public void setNumberInCart(int numberInCart) {
        this.numberInCart = numberInCart;
    }
    public void setNumberInFavorite(int numberInFavorite) {
        this.numberInFavorite = numberInFavorite;
    }
}
