package com.example.kitchen.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import com.example.kitchen.fragments.BookmarksFragment;
import com.example.kitchen.fragments.FragmentScrollListener;
import com.example.kitchen.fragments.MealPlanFragment;
import com.example.kitchen.fragments.RecipesFragment;
import com.example.kitchen.fragments.StorageFragment;
import com.example.kitchen.utility.AppConstants;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RecipeClickListener,
        FragmentScrollListener {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String KEY_NAV_INDEX = "navigator-index-key";
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view) NavigationView mNavigationView;
    @BindView(R.id.progress_bar_recipes_fragment) ProgressBar mProgressBar;
    private KitchenViewModel mKitchenViewModel;
    private RecipeViewModel mRecipeViewModel;
    private SharedPreferences mSharedPreferences;
    private int mSelectionIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mKitchenViewModel = ViewModelProviders.of(this).get(KitchenViewModel.class);
        mRecipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);
        mSharedPreferences = getPreferences(Context.MODE_PRIVATE);
        if (savedInstanceState == null) {
            mSelectionIndex = mSharedPreferences.getInt(KEY_NAV_INDEX, 0);
            showSelectedContent();
        } else {
            mSelectionIndex = savedInstanceState.getInt(KEY_NAV_INDEX);
        }
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                switch (mSelectionIndex) {
                    case 0:
                    case 1:
                        intent = new Intent(MainActivity.this, RecipeEditActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
        // Set navigation drawer.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Save index in local storage to be able to start from the point where the user leaves the app.
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putInt(KEY_NAV_INDEX, mSelectionIndex);
                editor.apply();
                showSelectedContent();
            }
        };
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
        // Show user image, name and email address in navigation header.
        View navHeader = mNavigationView.getHeaderView(0);
        ImageView userImage = navHeader.findViewById(R.id.iv_nav_header);
        TextView userName = navHeader.findViewById(R.id.tv_nav_header_title);
        TextView userEmail = navHeader.findViewById(R.id.tv_nav_header_subtitle);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Uri uri = user.getPhotoUrl();
            if (uri != null) {
                Glide.with(this).load(user.getPhotoUrl()).into(userImage);
            } else {
                userImage.setVisibility(View.GONE);
            }
            userName.setText(user.getDisplayName());
            userEmail.setText(user.getEmail());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_NAV_INDEX, mSelectionIndex);
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
        int lastIndex = mSelectionIndex;
        switch (id) {
            case R.id.nav_recipes:
                mSelectionIndex = 0;
                break;
            case R.id.nav_bookmarks:
                mSelectionIndex = 1;
                break;
            case R.id.nav_suggestions:
                mSelectionIndex = 2;
                break;
            case R.id.nav_storage:
                mSelectionIndex = 3;
                break;
            case R.id.nav_shopping_list:
                mSelectionIndex = 4;
                break;
            case R.id.nav_meal_plan:
                mSelectionIndex = 5;
                break;
            case R.id.nav_routines:
                mSelectionIndex = 6;
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
        if (mSelectionIndex != lastIndex) {
            mFab.hide();
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onRecipeClick(Recipe recipe, boolean isEditable) {
        if (recipe == null)
            return;
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(AppConstants.EXTRA_RECIPE, recipe);
        intent.putExtra(AppConstants.EXTRA_EDITABLE, isEditable);
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

    private void showSelectedContent() {
        mProgressBar.setVisibility(View.GONE);
        switch (mSelectionIndex) {
            case 0:
                mFab.show();
                mProgressBar.setVisibility(View.VISIBLE);
                fetchRecipesInPages();
                break;
            case 1:
                mFab.show();
                mKitchenViewModel.getAllRecipes().observe(this, new Observer<List<Recipe>>() {
                    @Override
                    public void onChanged(@Nullable List<Recipe> recipes) {
                        if (recipes != null)
                            showBookmarks(recipes);
                    }
                });
                break;
            case 3:
                showStorage();
                break;
            case 5:
                showMealBoard();
                break;
        }
    }

    private void showRecipes(List<Recipe> recipes) {
        mProgressBar.setVisibility(View.GONE);
        for (Recipe recipe : recipes)
            Log.v("MainActivity", recipe.toString());
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.recipes);
        Bundle args = new Bundle();
        args.putParcelableArrayList(AppConstants.KEY_RECIPES, (ArrayList<Recipe>) recipes);
        RecipesFragment recipesFragment = new RecipesFragment();
        recipesFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, recipesFragment).commit();
    }

    private void showBookmarks(List<Recipe> recipes) {
        for (Recipe recipe : recipes)
            Log.v("MainActivity", recipe.toString());
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.bookmarks);
        Bundle args = new Bundle();
        args.putParcelableArrayList(AppConstants.KEY_RECIPES, (ArrayList<Recipe>) recipes);
        BookmarksFragment bookmarksFragment = new BookmarksFragment();
        bookmarksFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, bookmarksFragment).commit();
    }

    private void showStorage() {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.storage);
        StorageFragment storageFragment = new StorageFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, storageFragment).commit();
    }

    private void showMealBoard() {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.meal_plan);
        MealPlanFragment mealBoardFragment = new MealPlanFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mealBoardFragment).commit();
    }

    private void fetchRecipesInPages() {
        // Fetch recipe data from firebase.
        mRecipeViewModel.getPagedDataSnapshotLiveData(10, "rating")
                .observe(this, new Observer<DataSnapshot>() {
                    @Override
                    public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                        // Prevent turning back to online recipes fragment after publishing a recipe.
                        if (mSelectionIndex != 0) return;
                        List<Recipe> recipes = new ArrayList<>();
                        if (dataSnapshot != null) {
                            for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                                RecipeModel recipe = null;
                                try {
                                    recipe = recipeSnapshot.getValue(RecipeModel.class);
                                } catch (DatabaseException e) {
                                    Log.e(LOG_TAG, e.getMessage());
                                }
                                // Translate data from snapshot into local database version.
                                if (recipe != null) {
                                    float total = (float) recipe.totalRating;
                                    float count = (float) recipe.ratingCount;
                                    float rating = count != 0 ? total / count : 0;
                                    recipes.add(new Recipe(0, recipe.title, recipe.imageUrl,
                                            recipe.prepTime, recipe.cookTime, recipe.language,
                                            recipe.cuisine, recipe.course, recipe.writerUid,
                                            recipe.writerName, recipe.servings, 0,
                                            recipeSnapshot.getKey(), rating));
                                }
                            }
                        }
                        // Sort the list in descending order of ratings.
                        Collections.sort(recipes, new Comparator<Recipe>() {
                            @Override
                            public int compare(Recipe o1, Recipe o2) {
                                return (int) (o2.rating - o1.rating);
                            }
                        });
                        showRecipes(recipes);
                    }
                });
    }
}
