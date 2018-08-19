package com.example.kitchen.data.local;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.kitchen.data.local.entities.Food;
import com.example.kitchen.data.local.entities.Ingredient;
import com.example.kitchen.data.local.entities.Recipe;
import com.example.kitchen.data.local.entities.Step;
import com.example.kitchen.data.local.entities.Ware;

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

    public LiveData<List<Ingredient>> getIngredientsByRecipe(int recipeId) {
        return mRepository.getIngredientsByRecipe(recipeId);
    }

    public LiveData<List<Recipe>> getRecipes() {
        return mRepository.getRecipes();
    }

    public LiveData<Recipe> getRecipeByPublicKey(String publicKey) {
        return mRepository.getRecipeByPublicKey(publicKey);
    }

    public LiveData<List<Step>> getStepsByRecipe(int recipeId) {
        return mRepository.getStepsByRecipe(recipeId);
    }

    public LiveData<List<Food>> getStorage() {
        return mRepository.getStorage();
    }

    public LiveData<List<Ware>> getShoppingList() {
        return mRepository.getShoppingList();
    }

    public void deleteFood(Food... foods) {
        mRepository.deleteFood(foods);
    }

    public void deleteIngredient(Ingredient... ingredients) {
        mRepository.deleteIngredient(ingredients);
    }

    public void deleteRecipe(Recipe... recipes) {
        mRepository.deleteRecipe(recipes);
    }

    public void deleteStep(Step... steps) {
        mRepository.deleteStep(steps);
    }

    public void deleteWare(Ware... wares) {
        mRepository.deleteWare(wares);
    }

    public void insertFood(Food... foods) {
        mRepository.insertFood(foods);
    }

    public void insertIngredient(Ingredient... ingredients) {
        mRepository.insertIngredient(ingredients);
    }

    public void insertRecipe(Recipe recipe, OnRecipeInsertListener listener) {
        mRepository.insertRecipe(recipe, listener);
    }

    public void insertStep(Step... steps) {
        mRepository.insertStep(steps);
    }

    public void insertWare(Ware... wares) {
        mRepository.insertWare(wares);
    }
}
