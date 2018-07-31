package com.example.kitchen.data;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.kitchen.data.local.entities.Recipe;

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

    public void insertRecipes(Recipe... recipes) {
        mRepository.insertRecipes(recipes);
    }

    public void deleteRecipes(Recipe... recipes) {
        mRepository.deleteRecipes(recipes);
    }

    public LiveData<Recipe> getRecipe(String title) {
        return mRepository.getRecipe(title);
    }

    public LiveData<List<Recipe>> getAllRecipes() {
        return mRepository.getAllRecipes();
    }
}
