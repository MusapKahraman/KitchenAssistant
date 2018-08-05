package com.example.kitchen.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kitchen.R;
import com.example.kitchen.data.local.KitchenViewModel;
import com.example.kitchen.data.local.entities.Recipe;
import com.example.kitchen.utility.AppConstants;

public class RecipeDetailActivity extends AppCompatActivity {

    private static final String KEY_SERVINGS = "servings-key";
    private Recipe mRecipe;
    private int mServings;
    private AppBarLayout mAppBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        boolean isEditable;
        if (getIntent() != null) {
            mRecipe = getIntent().getParcelableExtra(AppConstants.EXTRA_RECIPE);
            isEditable = getIntent().getBooleanExtra(AppConstants.EXTRA_EDITABLE, false);
        } else {
            return;
        }

        mAppBarLayout = findViewById(R.id.app_bar);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            // Change ActionBar title as the name of the recipe.
            actionBar.setTitle(mRecipe.title);
        }
        ImageView recipeImageView = findViewById(R.id.iv_recipe_image);
        String url = mRecipe.photoUrl;
        if (url != null && url.length() != 0) {
            RequestOptions options = new RequestOptions();
            Glide.with(this).load(url).apply(options.centerCrop()).into(recipeImageView);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecipeDetailActivity.this, RecipeEditActivity.class);
                intent.putExtra(AppConstants.EXTRA_RECIPE, mRecipe);
                startActivity(intent);
                finish();
            }
        });
        if (!isEditable) {
            fab.setVisibility(View.GONE);
            TextView writerView = findViewById(R.id.tv_recipe_writer);
            writerView.setText(mRecipe.writer);
        }

        TextView courseTextView = findViewById(R.id.tv_course);
        courseTextView.setText(mRecipe.course);
        TextView cuisineTextView = findViewById(R.id.tv_cuisine);
        cuisineTextView.setText(mRecipe.cuisine);
        TextView prepTimeTextView = findViewById(R.id.tv_prep_time);
        prepTimeTextView.setText(String.valueOf(mRecipe.prepTime));
        TextView cookTimeTextView = findViewById(R.id.tv_cook_time);
        cookTimeTextView.setText(String.valueOf(mRecipe.cookTime));
        if (savedInstanceState == null) {
            mServings = mRecipe.servings;
        } else {
            mServings = savedInstanceState.getInt(KEY_SERVINGS);
        }
        final TextView servingsTextView = findViewById(R.id.tv_servings);
        servingsTextView.setText(String.valueOf(mServings));
        Button decrementButton = findViewById(R.id.btn_servings_decrement);
        Button incrementButton = findViewById(R.id.btn_servings_increment);
        decrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mServings > 1) {
                    mServings--;
                    servingsTextView.setText(String.valueOf(mServings));
                }
            }
        });
        incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mServings++;
                servingsTextView.setText(String.valueOf(mServings));
            }
        });

        RatingBar ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Snackbar.make(mAppBarLayout, "Rating is: " + rating, Snackbar.LENGTH_SHORT).show();
            }
        });

        Button finishedButton = findViewById(R.id.btn_finished);
        finishedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(mAppBarLayout, "Finished.", Snackbar.LENGTH_SHORT).show();
            }
        });

        LinearLayout ingredientListLayout = findViewById(R.id.container_ingredients);
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int x = 0; x < 9; x++) {
            View view = inflater.inflate(R.layout.item_ingredient, ingredientListLayout, false);
            TextView ingredientView = view.findViewById(R.id.tv_ingredient);
            ingredientView.setText("Wwwwwwww wwwwwwwwwww  ww");
            ingredientListLayout.addView(view);
        }

        LinearLayout stepListLayout = findViewById(R.id.container_steps);
        for (int x = 0; x < 99; x++) {
            View view = inflater.inflate(R.layout.item_step, stepListLayout, false);
            TextView stepNumberView = view.findViewById(R.id.tv_step_number);
            stepNumberView.setText(String.valueOf(x + 1));
            TextView stepView = view.findViewById(R.id.tv_step);
            stepView.setText("Wwwwwwww wwwwwwwwwww wwwwwwwwwwww wwwwwwwwwwww wwwwwwwwwwww wwwwwwwwwwww wwwwwwwwwwww wwwwww wwwwwwww wwwwwwww ww");
            stepListLayout.addView(view);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SERVINGS, mServings);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_recipe_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.app_bar_board:
                Snackbar.make(mAppBarLayout, "Boarding...", Snackbar.LENGTH_SHORT).show();
                return true;
            case R.id.app_bar_bookmark:
                KitchenViewModel viewModel = ViewModelProviders.of(this).get(KitchenViewModel.class);
                viewModel.insertRecipes(mRecipe);
                Snackbar.make(mAppBarLayout, R.string.recipe_bookmarked, Snackbar.LENGTH_SHORT).show();
                return true;
            case R.id.app_bar_share:
                Snackbar.make(mAppBarLayout, "Sharing...", Snackbar.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
