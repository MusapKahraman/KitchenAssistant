package com.example.kitchen.data.local;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.kitchen.data.local.entities.Ingredient;
import com.example.kitchen.data.local.entities.Recipe;
import com.example.kitchen.data.local.entities.Step;

import java.util.List;

// The ViewModel's role is to provide data to the UI and survive configuration changes.
// A ViewModel acts as a communication center between the RecipesRepository and the UI.
public class KitchenViewModel extends AndroidViewModel {
    private final KitchenRepository mRepository;

    // Warning: Never pass context into ViewModel instances.
    // Do not store Activity, Fragment, or View instances or their Context in the ViewModel.
    // If you need the application context, use AndroidViewModel.
    public KitchenViewModel(Application application) {
        super(application);
        mRepository = new KitchenRepository(application);
    }

    // In the ViewModel, use LiveData for changeable data that the UI will use or display.


    public LiveData<Recipe> getRecipe(int id) {
        return mRepository.getRecipe(id);
    }

    public LiveData<List<Recipe>> getAllRecipes() {
        return mRepository.getAllRecipes();
    }

    public LiveData<List<Ingredient>> getIngredientsByRecipe(int recipeId) {
        return mRepository.getIngredientsByRecipe(recipeId);
    }

    public LiveData<List<Ingredient>> getIngredientsByFood(int foodId) {
        return mRepository.getIngredientsByFood(foodId);
    }

    public LiveData<List<Step>> getStepsByRecipe(int recipeId) {
        return mRepository.getStepsByRecipe(recipeId);
    }

    public void insertRecipes(Recipe... recipes) {
        mRepository.insertRecipes(recipes);
    }

    public void deleteRecipes(Recipe... recipes) {
        mRepository.deleteRecipes(recipes);
    }

    public void insertIngredients(Ingredient... ingredients) {
        mRepository.insertIngredients(ingredients);
    }

    public void deleteIngredients(Ingredient... ingredients) {
        mRepository.deleteIngredients(ingredients);
    }

    public void insertSteps(Step... steps) {
        mRepository.insertSteps(steps);
    }

    public void deleteSteps(Step... steps) {
        mRepository.deleteSteps(steps);
    }
}
