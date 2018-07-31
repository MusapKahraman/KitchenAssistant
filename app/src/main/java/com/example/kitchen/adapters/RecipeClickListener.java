package com.example.kitchen.adapters;

import com.example.kitchen.data.local.entities.Recipe;

public interface RecipeClickListener {
    void onRecipeClick(Recipe recipe, boolean isEditable);
}
