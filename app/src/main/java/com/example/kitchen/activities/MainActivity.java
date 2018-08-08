package com.example.kitchen.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kitchen.R;
import com.example.kitchen.adapters.RecipeClickListener;
import com.example.kitchen.data.firebase.RecipeViewModel;
import com.example.kitchen.data.firebase.models.RecipeModel;
import com.example.kitchen.data.local.KitchenViewModel;
import com.example.kitchen.data.local.entities.Recipe;
import com.example.kitchen.fragments.FragmentScrollListener;
import com.example.kitchen.fragments.MealBoardFragment;
import com.example.kitchen.fragments.NotebookFragment;
import com.example.kitchen.fragments.RecipesFragment;
import com.example.kitchen.utility.AppConstants;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseException;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RecipeClickListener, FragmentScrollListener {
    private static final String KEY_NAV_INDEX = "navigator-index-key";
    private static final String KEY_RECIPES_FRAG = "recipes-fragment-key";
    private static final String KEY_NOTEBOOK_FRAG = "notebook-fragment-key";
    private static final String KEY_MEAL_BOARD_FRAG = "meal-board-fragment-key";
    private FloatingActionButton mFab;
    private ProgressBar mProgressBar;
    private Bundle mRecipesFragmentSavedState;
    private Bundle mNotebookFragmentSavedState;
    private Bundle mMealBoardFragmentSavedState;
    private int mNavigatorIndex;
    private KitchenViewModel kitchenViewModel;
    private RecipeViewModel recipeViewModel;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPref = getPreferences(Context.MODE_PRIVATE);
        kitchenViewModel = ViewModelProviders.of(this).get(KitchenViewModel.class);
        recipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);

        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                switch (mNavigatorIndex) {
                    case 0:
                    case 1:
                        intent = new Intent(MainActivity.this, RecipeEditActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });

        mProgressBar = findViewById(R.id.progress_bar_recipes_fragment);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(KEY_NAV_INDEX, mNavigatorIndex);
                editor.apply();
                changeContent();
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // Populate navigation header with user data.
        View navHeader = navigationView.getHeaderView(0);
        ImageView userImage = navHeader.findViewById(R.id.iv_nav_header);
        TextView userName = navHeader.findViewById(R.id.tv_nav_header_title);
        TextView userEmail = navHeader.findViewById(R.id.tv_nav_header_subtitle);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Glide.with(this).load(user.getPhotoUrl()).into(userImage);
            userName.setText(user.getDisplayName());
            userEmail.setText(user.getEmail());
        }

        if (savedInstanceState != null) {
            mRecipesFragmentSavedState = savedInstanceState.getBundle(KEY_RECIPES_FRAG);
            mNotebookFragmentSavedState = savedInstanceState.getBundle(KEY_NOTEBOOK_FRAG);
            mMealBoardFragmentSavedState = savedInstanceState.getBundle(KEY_MEAL_BOARD_FRAG);
        }
        changeContent();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(KEY_RECIPES_FRAG, mRecipesFragmentSavedState);
        outState.putBundle(KEY_NOTEBOOK_FRAG, mNotebookFragmentSavedState);
        outState.putBundle(KEY_MEAL_BOARD_FRAG, mMealBoardFragmentSavedState);
    }

    private void changeContent() {
        mProgressBar.setVisibility(View.GONE);
        mNavigatorIndex = sharedPref.getInt(KEY_NAV_INDEX, 0);
        switch (mNavigatorIndex) {
            case 0:
                mFab.show();
                mProgressBar.setVisibility(View.VISIBLE);
                recipeViewModel.getDataSnapshotLiveData().observe(this, new Observer<DataSnapshot>() {
                    @Override
                    public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                        if (mNavigatorIndex != 0) {
                            // Prevent turning back to online recipes fragment after publishing a recipe.
                            return;
                        }
                        List<Recipe> recipes = new ArrayList<>();
                        if (dataSnapshot != null) {
                            for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                                RecipeModel recipe = null;
                                try {
                                    recipe = recipeSnapshot.getValue(RecipeModel.class);
                                } catch (DatabaseException e) {
                                    Log.e("MainActivity", e.getMessage());
                                }
                                if (recipe != null) {
                                    int total = recipe.totalRating;
                                    int count = recipe.ratingCount;
                                    float rating = 0;
                                    if (count != 0) rating = (float) total / (float) count;
                                    recipes.add(new Recipe(0, recipe.title, recipe.imageUrl, recipe.prepTime, recipe.cookTime,
                                            recipe.language, recipe.cuisine, recipe.course, recipe.writerUid, recipe.writerName,
                                            recipe.servings, 0, recipeSnapshot.getKey(), rating));
                                }
                            }
                        }
                        showRecipes(recipes);
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
                break;
            case 1:
                mFab.show();
                kitchenViewModel.getAllRecipes().observe(this, new Observer<List<Recipe>>() {
                    @Override
                    public void onChanged(@Nullable List<Recipe> recipes) {
                        if (recipes != null)
                            showNotebook(recipes);
                    }
                });
                break;
            case 6:
                kitchenViewModel.getAllRecipes().observe(this, new Observer<List<Recipe>>() {
                    @Override
                    public void onChanged(@Nullable List<Recipe> recipes) {
                        showMealBoard(recipes);
                    }
                });
                break;
        }
    }

    private void showRecipes(List<Recipe> recipes) {
        for (Recipe recipe : recipes)
            Log.v("MainActivity", recipe.toString());
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.recipes);
        Bundle bundle = new Bundle();
        bundle.putBundle(AppConstants.KEY_SAVED_STATE, mRecipesFragmentSavedState);
        bundle.putParcelableArrayList(AppConstants.KEY_RECIPES, (ArrayList<Recipe>) recipes);
        RecipesFragment recipesFragment = new RecipesFragment();
        recipesFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, recipesFragment).commit();
    }

    private void showNotebook(List<Recipe> recipes) {
        for (Recipe recipe : recipes)
            Log.v("MainActivity", recipe.toString());
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.notebook);
        Bundle bundle = new Bundle();
        bundle.putBundle(AppConstants.KEY_SAVED_STATE, mNotebookFragmentSavedState);
        bundle.putParcelableArrayList(AppConstants.KEY_RECIPES, (ArrayList<Recipe>) recipes);
        NotebookFragment notebookFragment = new NotebookFragment();
        notebookFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, notebookFragment).commit();
    }

    private void showMealBoard(List<Recipe> recipes) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.meal_board);
        Bundle bundle = new Bundle();
        bundle.putBundle(AppConstants.KEY_SAVED_STATE, mMealBoardFragmentSavedState);
        bundle.putParcelableArrayList(AppConstants.KEY_RECIPES, (ArrayList<Recipe>) recipes);
        MealBoardFragment mealBoardFragment = new MealBoardFragment();
        mealBoardFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mealBoardFragment).commit();
    }

    public void fromRecipesFragment(Bundle fragmentOutState) {
        mRecipesFragmentSavedState = fragmentOutState;
    }

    public void fromNotebookFragment(Bundle fragmentOutState) {
        mNotebookFragmentSavedState = fragmentOutState;
    }

    public void fromMealBoardFragment(Bundle fragmentOutState) {
        mMealBoardFragmentSavedState = fragmentOutState;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        int oldNavigatorIndex = mNavigatorIndex;
        switch (id) {
            case R.id.nav_recipes:
                mNavigatorIndex = 0;
                break;
            case R.id.nav_notebook:
                mNavigatorIndex = 1;
                break;
            case R.id.nav_suggestions:
                mNavigatorIndex = 2;
                break;
            case R.id.nav_food_storage:
                mNavigatorIndex = 3;
                break;
            case R.id.nav_shopping_list:
                mNavigatorIndex = 4;
                break;
            case R.id.nav_routines:
                mNavigatorIndex = 5;
                break;
            case R.id.nav_meal_board:
                mNavigatorIndex = 6;
                break;
            case R.id.nav_logout:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // User is now signed out.
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });
                break;
        }
        if (mNavigatorIndex != oldNavigatorIndex) {
            mFab.hide();
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onRecipeClick(Recipe recipe, boolean isEditable) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(AppConstants.EXTRA_RECIPE, recipe);
        intent.putExtra(AppConstants.EXTRA_EDITABLE, isEditable);
        intent.putExtra(AppConstants.EXTRA_BOOKABLE, mNavigatorIndex == 0);
        startActivity(intent);
    }

    @Override
    public void onScrollDown() {
        mFab.hide();
    }

    @Override
    public void onScrollUp() {
        mFab.show();
    }
}
