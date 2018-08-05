package com.example.kitchen.data.firebase.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class RecipeModel {
    public String title;
    public String photoUrl;
    public int servings;
    public int prepTime;
    public int cookTime;
    public String language;
    public String cuisine;
    public String course;
    public String writer;
    public int totalRating;
    public int ratingCount;

    public RecipeModel() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public RecipeModel(String title, String photoUrl, int servings, int prepTime, int cookTime, String language,
                       String cuisine, String course, String writer) {
        this.title = title;
        this.photoUrl = photoUrl;
        this.servings = servings;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
        this.language = language;
        this.cuisine = cuisine;
        this.course = course;
        this.writer = writer;
        this.totalRating = 0;
        this.ratingCount = 0;
    }
}