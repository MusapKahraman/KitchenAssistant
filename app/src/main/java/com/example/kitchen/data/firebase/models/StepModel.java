package com.example.kitchen.data.firebase.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class StepModel {
    public String recipeKey;
    public String instruction;
    public int stepNumber;

    public StepModel() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public StepModel(String recipeKey, String instruction, int stepNumber) {
        this.recipeKey = recipeKey;
        this.instruction = instruction;
        this.stepNumber = stepNumber;
    }
}