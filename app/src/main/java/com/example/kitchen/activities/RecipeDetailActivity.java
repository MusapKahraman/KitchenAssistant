package com.example.kitchen.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kitchen.R;
import com.example.kitchen.data.local.entities.Recipe;
import com.example.kitchen.utility.AppConstants;

import java.util.Date;

public class RecipeDetailActivity extends AppCompatActivity {

    private static final String KEY_SERVINGS = "servings-key";
    private int mServings;
    private AppBarLayout mAppBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        mAppBarLayout = findViewById(R.id.app_bar);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        boolean isEditable = false;
        Recipe recipe = new Recipe(getString(R.string.new_recipe), new Date().getTime());
        if (getIntent() != null) {
            recipe = getIntent().getParcelableExtra(AppConstants.EXTRA_RECIPE);
            isEditable = getIntent().getBooleanExtra(AppConstants.EXTRA_EDITABLE, false);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            // Change ActionBar title as the name of the recipe.
            actionBar.setTitle(recipe.title);
        }
        ImageView recipeImageView = findViewById(R.id.iv_recipe_image);
        String url = recipe.photoUrl;
        if (url != null && url.length() != 0) {
            RequestOptions options = new RequestOptions();
            Glide.with(this).load(url).apply(options.centerCrop()).into(recipeImageView);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecipeDetailActivity.this, RecipeEditActivity.class);
                startActivity(intent);
                finish();
            }
        });
        if (!isEditable) {
            fab.setVisibility(View.GONE);
            TextView writerView = findViewById(R.id.tv_recipe_writer);
            writerView.setText(recipe.writer);
        }

        TextView courseTextView = findViewById(R.id.tv_course);
        courseTextView.setText(recipe.course);
        TextView cuisineTextView = findViewById(R.id.tv_cuisine);
        cuisineTextView.setText(recipe.cuisine);
        TextView prepTimeTextView = findViewById(R.id.tv_prep_time);
        prepTimeTextView.setText(String.valueOf(recipe.prepTime));
        TextView cookTimeTextView = findViewById(R.id.tv_cook_time);
        cookTimeTextView.setText(String.valueOf(recipe.cookTime));
        if (savedInstanceState == null) {
            mServings = recipe.servings;
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
                Snackbar.make(mAppBarLayout, "Bookmarking...: ", Snackbar.LENGTH_SHORT).show();
                return true;
            case R.id.app_bar_share:
                Snackbar.make(mAppBarLayout, "Sharing...", Snackbar.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
