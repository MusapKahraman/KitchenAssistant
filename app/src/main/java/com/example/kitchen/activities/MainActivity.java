package com.example.kitchen.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.kitchen.R;
import com.example.kitchen.adapters.OnRecipeClickListener;
import com.example.kitchen.data.DataPlaceholders;
import com.example.kitchen.data.Recipe;
import com.example.kitchen.fragments.NotebookFragment;
import com.example.kitchen.fragments.RecipesFragment;
import com.example.kitchen.utility.KeyUtils;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnRecipeClickListener {

    private static final String KEY_NAV_INDEX = "navigator-index-key";
    private static final String KEY_RECIPES_FRAG = "recipes-fragment-key";
    private static final String KEY_NOTEBOOK_FRAG = "notebook-fragment-key";
    private Bundle mRecipesFragmentSavedState;
    private Bundle mNotebookFragmentSavedState;
    private int mNavigatorIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                changeContent();
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Log.v("MainActivity", "Navigator Index: " + mNavigatorIndex);
        if (savedInstanceState != null) {
            mRecipesFragmentSavedState = savedInstanceState.getBundle(KEY_RECIPES_FRAG);
            mNotebookFragmentSavedState = savedInstanceState.getBundle(KEY_NOTEBOOK_FRAG);
            mNavigatorIndex = savedInstanceState.getInt(KEY_NAV_INDEX);
        } else if (getIntent() != null) {
            mNavigatorIndex = getIntent().getIntExtra(KeyUtils.EXTRA_NAV_INDEX, 0);
        }
        changeContent();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(KEY_RECIPES_FRAG, mRecipesFragmentSavedState);
        outState.putBundle(KEY_NOTEBOOK_FRAG, mNotebookFragmentSavedState);
        outState.putInt(KEY_NAV_INDEX, mNavigatorIndex);
    }

    private void changeContent() {
        Log.v("MainActivity", "Changing content with Navigator Index: " + mNavigatorIndex);
        Intent intent;
        switch (mNavigatorIndex) {
            case 0:
                showRecipes(DataPlaceholders.getRecipes());
                break;
            case 1:
                showNotebook(DataPlaceholders.getNotebook());
                break;
            case 5:
                intent = new Intent(this, RoutinesActivity.class);
                startActivity(intent);
                break;
            case 6:
                intent = new Intent(this, MealBoardActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void showRecipes(List<Recipe> recipes) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.recipes);
        Bundle bundle = new Bundle();
        bundle.putBundle(KeyUtils.KEY_SAVED_STATE, mRecipesFragmentSavedState);
        bundle.putParcelableArrayList(KeyUtils.KEY_RECIPES, (ArrayList<Recipe>) recipes);
        RecipesFragment recipesFragment = new RecipesFragment();
        recipesFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, recipesFragment).commit();
    }

    private void showNotebook(List<Recipe> recipes) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.notebook);
        Bundle bundle = new Bundle();
        bundle.putBundle(KeyUtils.KEY_SAVED_STATE, mNotebookFragmentSavedState);
        bundle.putParcelableArrayList(KeyUtils.KEY_RECIPES, (ArrayList<Recipe>) recipes);
        NotebookFragment notebookFragment = new NotebookFragment();
        notebookFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, notebookFragment).commit();
    }

    public void fromRecipesFragment(Bundle fragmentOutState) {
        mRecipesFragmentSavedState = fragmentOutState;
    }

    public void fromNotebookFragment(Bundle fragmentOutState) {
        mNotebookFragmentSavedState = fragmentOutState;
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
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
                                // user is now signed out
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                finish();
                            }
                        });
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRecipeClick(int recipeId, String recipeName) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(KeyUtils.EXTRA_RECIPE_ID, recipeId);
        intent.putExtra(KeyUtils.EXTRA_RECIPE_NAME, recipeName);
        startActivity(intent);
    }
}
