package com.example.kitchen.adapters;

import com.example.kitchen.data.local.entities.Recipe;

public interface OnRecipeClickListener {
    void onRecipeClick(Recipe recipe, boolean isEditable);
}
