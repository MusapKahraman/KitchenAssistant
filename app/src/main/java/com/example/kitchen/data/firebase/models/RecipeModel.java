package com.example.kitchen.data.firebase.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class RecipeModel {
    public String title;
    public String imageUrl;
    public int servings;
    public int prepTime;
    public int cookTime;
    public String language;
    public String cuisine;
    public String course;
    public String writerUid;
    public String writerName;
    public int totalRating;
    public int ratingCount;
    public int rating;

    public RecipeModel() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public RecipeModel(String title, String imageUrl, int servings, int prepTime, int cookTime, String language,
                       String cuisine, String course, String writerUid, String writerName) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.servings = servings;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
        this.language = language;
        this.cuisine = cuisine;
        this.course = course;
        this.writerUid = writerUid;
        this.writerName = writerName;
    }
}