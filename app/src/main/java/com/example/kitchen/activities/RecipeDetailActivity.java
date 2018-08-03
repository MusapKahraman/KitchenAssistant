package com.example.kitchen.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kitchen.R;
import com.example.kitchen.data.local.entities.Recipe;
import com.example.kitchen.utility.AppConstants;

import java.util.Date;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
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
        }
    }
}
