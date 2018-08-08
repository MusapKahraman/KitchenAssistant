package com.example.kitchen.data.firebase.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class FoodModel {
    public String name;
    public float multiplier;

    public FoodModel() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public FoodModel(String name, float multiplier) {
        this.name = name;
        this.multiplier = multiplier;
    }
}