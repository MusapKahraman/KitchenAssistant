package com.example.kitchen.data.firebase.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class IngredientModel {
    public String recipeKey;
    public String food;
    public int amount;
    public String amountType;

    public IngredientModel() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public IngredientModel(String recipeKey, String food, int amount, String amountType) {
        this.recipeKey = recipeKey;
        this.food = food;
        this.amount = amount;
        this.amountType = amountType;
    }
}