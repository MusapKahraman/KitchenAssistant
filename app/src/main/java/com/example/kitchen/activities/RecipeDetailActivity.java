/*
 * Reference
 * https://stackoverflow.com/questions/5767570/how-to-update-a-menu-item-shown-in-the-actionbar/5767673#5767673
 * https://developer.android.com/training/sharing/shareaction
 */

package com.example.kitchen.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.kitchen.R;
import com.example.kitchen.data.firebase.IngredientViewModel;
import com.example.kitchen.data.firebase.RecipeViewModel;
import com.example.kitchen.data.firebase.StepViewModel;
import com.example.kitchen.data.firebase.models.IngredientModel;
import com.example.kitchen.data.firebase.models.StepModel;
import com.example.kitchen.data.local.KitchenViewModel;
import com.example.kitchen.data.local.RecipeInsertListener;
import com.example.kitchen.data.local.entities.Ingredient;
import com.example.kitchen.data.local.entities.Recipe;
import com.example.kitchen.data.local.entities.Step;
import com.example.kitchen.utility.AppConstants;
import com.example.kitchen.utility.MeasurementUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailActivity extends AppCompatActivity implements RecipeViewModel.RatingPostListener, RecipeInsertListener {
    private static final String LOG_TAG = RecipeDetailActivity.class.getSimpleName();
    private static final String KEY_SERVINGS = "servings-key";
    private static final String KEY_RATING_TRANSACTION = "rating-transaction-status-key";
    private static final String KEY_EDITABLE = "editable-key";
    private static final String KEY_BOOKABLE = "bookable-key";
    private static final String KEY_INSERTED_FOR_EDIT = "key-inserted-for-edit";
    @BindView(R.id.app_bar) AppBarLayout mAppBarLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.iv_recipe_image) ImageView mRecipeImageView;
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.divider_rating) View mRatingDivider;
    @BindView(R.id.label_rating) TextView mRatingLabel;
    @BindView(R.id.ratingBar) RatingBar mRatingBar;
    @BindView(R.id.tv_recipe_writer) TextView mWriterTextView;
    @BindView(R.id.tv_course) TextView mCourseTextView;
    @BindView(R.id.tv_cuisine) TextView mCuisineTextView;
    @BindView(R.id.tv_prep_time) TextView mPrepTimeTextView;
    @BindView(R.id.tv_cook_time) TextView mCookTimeTextView;
    @BindView(R.id.tv_servings) TextView mServingsTextView;
    @BindView(R.id.btn_servings_decrement) ImageButton mDecrementButton;
    @BindView(R.id.btn_servings_increment) ImageButton mIncrementButton;
    @BindView(R.id.btn_finished) Button mFinishedButton;
    @BindView(R.id.container_ingredients) LinearLayout mIngredientsContainer;
    @BindView(R.id.container_steps) LinearLayout mStepsContainer;
    private LayoutInflater mLayoutInflater;
    private KitchenViewModel mKitchenViewModel;
    private RecipeViewModel mRecipeViewModel;
    private SharedPreferences mSharedPreferences;
    private Recipe mRecipe;
    private List<Ingredient> mIngredients;
    private List<Step> mSteps;
    private int mServings;
    private boolean mIsRatingProcessing;
    private boolean mIsInsertedForEdit;
    private boolean mIsBookable;
    private boolean mIsEditable;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        ButterKnife.bind(this);
        mSharedPreferences = getPreferences(Context.MODE_PRIVATE);
        mKitchenViewModel = ViewModelProviders.of(this).get(KitchenViewModel.class);
        mRecipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);
        IngredientViewModel ingredientViewModel = ViewModelProviders.of(this).get(IngredientViewModel.class);
        StepViewModel stepViewModel = ViewModelProviders.of(this).get(StepViewModel.class);
        if (getIntent() != null) {
            mRecipe = getIntent().getParcelableExtra(AppConstants.EXTRA_RECIPE);
            mIsBookable = getIntent().getBooleanExtra(AppConstants.EXTRA_BOOKABLE, false);
            mIsEditable = getIntent().getBooleanExtra(AppConstants.EXTRA_EDITABLE, false);
        }
        if (savedInstanceState == null) {
            mServings = mRecipe.servings;
        } else {
            mServings = savedInstanceState.getInt(KEY_SERVINGS);
            mIsRatingProcessing = savedInstanceState.getBoolean(KEY_RATING_TRANSACTION);
            mIsInsertedForEdit = savedInstanceState.getBoolean(KEY_INSERTED_FOR_EDIT);
            mIsBookable = savedInstanceState.getBoolean(KEY_BOOKABLE);
            mIsEditable = savedInstanceState.getBoolean(KEY_EDITABLE);
        }
        // Change ActionBar title as the name of the recipe.
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mRecipe.title);
        }

        String url = mRecipe.imagePath;
        if (url != null && url.length() != 0) {
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.ingredients)
                    .error(R.drawable.ingredients)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH);
            Glide.with(this)
                    .load(url)
                    .apply(options)
                    .into(mRecipeImageView);
        }

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mRecipe.publicKey)) {
                    mKitchenViewModel.insertRecipe(mRecipe, RecipeDetailActivity.this);
                } else {
                    mKitchenViewModel.getRecipeByPublicKey(mRecipe.publicKey).observe(RecipeDetailActivity.this, new Observer<Recipe>() {
                        @Override
                        public void onChanged(@Nullable Recipe recipe) {
                            if (recipe != null) {
                                startEditingActivity(recipe);
                            }
                        }
                    });
                }
                mIsInsertedForEdit = true;
            }
        });

        int rating = mSharedPreferences.getInt(mRecipe.publicKey, 0);
        mRatingBar.setRating(rating);
        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (!mIsRatingProcessing) {
                    mIsRatingProcessing = true;
                    int currentRating = (int) rating;
                    int lastRating = mSharedPreferences.getInt(mRecipe.publicKey, 0);
                    mRecipeViewModel.postRating(mRecipe.publicKey, currentRating, lastRating, RecipeDetailActivity.this);
                }
            }
        });
        if (!mIsEditable) {
            mFab.setVisibility(View.GONE);
            mWriterTextView.setText(mRecipe.writerName);
        } else {
            mRatingBar.setVisibility(View.GONE);
            mRatingLabel.setVisibility(View.GONE);
            mRatingDivider.setVisibility(View.GONE);
        }
        mCourseTextView.setText(mRecipe.course);
        mCuisineTextView.setText(mRecipe.cuisine);
        mPrepTimeTextView.setText(String.valueOf(mRecipe.prepTime));
        mCookTimeTextView.setText(String.valueOf(mRecipe.cookTime));
        mServingsTextView.setText(String.valueOf(mServings));
        mDecrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mServings > 1) {
                    mServings--;
                    mServingsTextView.setText(String.valueOf(mServings));
                }
            }
        });
        mIncrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mServings++;
                mServingsTextView.setText(String.valueOf(mServings));
            }
        });

        mFinishedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(mAppBarLayout, "Finished.", Snackbar.LENGTH_SHORT).show();
            }
        });

        mLayoutInflater = LayoutInflater.from(this);

        if (mRecipe.id != 0) {
            mKitchenViewModel.getIngredientsByRecipe(mRecipe.id).observe(this, new Observer<List<Ingredient>>() {
                @Override
                public void onChanged(@Nullable List<Ingredient> ingredients) {
                    mIngredients = ingredients;
                    showIngredients();
                }
            });

            mKitchenViewModel.getStepsByRecipe(mRecipe.id).observe(this, new Observer<List<Step>>() {
                @Override
                public void onChanged(@Nullable List<Step> steps) {
                    mSteps = steps;
                    showSteps();
                }
            });
        } else {
            ingredientViewModel.getDataSnapshotLiveData(mRecipe.publicKey)
                    .observe(this, new Observer<DataSnapshot>() {
                        @Override
                        public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                            mIngredients = new ArrayList<>();
                            if (dataSnapshot != null) {
                                for (DataSnapshot ingredientSnapshot : dataSnapshot.getChildren()) {
                                    IngredientModel ingredient = null;
                                    try {
                                        ingredient = ingredientSnapshot.getValue(IngredientModel.class);
                                    } catch (DatabaseException e) {
                                        Log.e(LOG_TAG, e.getMessage());
                                    }
                                    // Translate data from snapshot into local database version.
                                    if (ingredient != null) {
                                        mIngredients.add(new Ingredient(0, ingredient.food,
                                                ingredient.amount, ingredient.amountType,
                                                ingredientSnapshot.getKey()));
                                    }
                                }
                            }
                            showIngredients();
                        }
                    });
            stepViewModel.getDataSnapshotLiveData(mRecipe.publicKey)
                    .observe(this, new Observer<DataSnapshot>() {
                        @Override
                        public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                            mSteps = new ArrayList<>();
                            if (dataSnapshot != null) {
                                for (DataSnapshot stepSnapshot : dataSnapshot.getChildren()) {
                                    StepModel step = null;
                                    try {
                                        step = stepSnapshot.getValue(StepModel.class);
                                    } catch (DatabaseException e) {
                                        Log.e(LOG_TAG, e.getMessage());
                                    }
                                    if (step != null) {
                                        mSteps.add(new Step(step.instruction, step.stepNumber, 0,
                                                stepSnapshot.getKey()));
                                    }
                                }
                            }
                            // Sort the list in ascending order of step number.
                            Collections.sort(mSteps, new Comparator<Step>() {
                                @Override
                                public int compare(Step o1, Step o2) {
                                    return Integer.compare(o1.stepNumber, o2.stepNumber);
                                }
                            });
                            showSteps();
                        }
                    });
        }
    }

    private void showIngredients() {
        if (mIngredients == null)
            return;
        for (Ingredient ingredient : mIngredients) {
            View ingredientView = mLayoutInflater.inflate(R.layout.item_ingredient, mIngredientsContainer, false);
            TextView ingredientAmountTextView = ingredientView.findViewById(R.id.tv_ingredient_amount);
            TextView ingredientTextView = ingredientView.findViewById(R.id.tv_ingredient);
            String text = ingredient.amount +
                    " " + MeasurementUtils.getAbbreviation(RecipeDetailActivity.this, ingredient.amountType);
            ingredientAmountTextView.setText(text);
            ingredientTextView.setText(ingredient.food);
            mIngredientsContainer.addView(ingredientView);
        }
    }

    private void showSteps() {
        if (mSteps == null)
            return;
        for (Step step : mSteps) {
            View stepView = mLayoutInflater.inflate(R.layout.item_step, mStepsContainer, false);
            TextView stepNumberView = stepView.findViewById(R.id.tv_step_number);
            stepNumberView.setText(String.valueOf(step.stepNumber));
            TextView instructionTextView = stepView.findViewById(R.id.tv_instruction);
            instructionTextView.setText(step.instruction);
            mStepsContainer.addView(stepView);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SERVINGS, mServings);
        outState.putBoolean(KEY_RATING_TRANSACTION, mIsRatingProcessing);
        outState.putBoolean(KEY_INSERTED_FOR_EDIT, mIsInsertedForEdit);
        outState.putBoolean(KEY_BOOKABLE, mIsBookable);
        outState.putBoolean(KEY_EDITABLE, mIsEditable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (mIsBookable) {
            inflater.inflate(R.menu.menu_recipe_detail_bookable, menu);
            mFab.setVisibility(View.GONE);
        } else {
            inflater.inflate(R.menu.menu_recipe_detail, menu);
            if (mIsEditable) {
                mFab.setVisibility(View.VISIBLE);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_item_bookmark:
                mKitchenViewModel.insertRecipe(mRecipe, this);
                Snackbar.make(mAppBarLayout, R.string.recipe_bookmarked, Snackbar.LENGTH_SHORT).show();
                // Do not show bookmark icon from action bar menu anymore.
                mIsBookable = false;
                invalidateOptionsMenu();
                return true;
            case R.id.menu_item_share:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, generateShareString());
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String generateShareString() {
        // TODO: Make servings, cuisine and course translatable.
        StringBuilder result = new StringBuilder(mRecipe.title +
                "\n\n" + getString(R.string.servings) + ": " + mRecipe.servings +
                "\n" + getString(R.string.cuisine) + ": " + mRecipe.cuisine +
                "\n" + getString(R.string.course) + ": " + mRecipe.course +
                "\n\n" + getString(R.string.ingredients));
        for (Ingredient ingredient : mIngredients) {
            String text = ingredient.amount +
                    " " + MeasurementUtils.getAbbreviation(RecipeDetailActivity.this, ingredient.amountType) +
                    " " + ingredient.food;
            result.append("\n").append(text);
        }
        result.append("\n\n").append(getString(R.string.instructions));
        for (Step step : mSteps) {
            String text = step.stepNumber +
                    ". " + step.instruction;
            result.append("\n").append(text);
        }
        return result.toString();
    }

    @Override
    public void onRatingTransactionSuccessful(int rating) {
        mIsRatingProcessing = false;
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(mRecipe.publicKey, rating);
        editor.apply();
    }

    @Override
    public void onRecipeInserted(long id) {
        mRecipe.id = (int) id;
        if (mIngredients != null) {
            for (Ingredient i : mIngredients) {
                i.recipeId = mRecipe.id;
                mKitchenViewModel.insertIngredients(i);
            }
        }
        if (mSteps != null) {
            for (Step s : mSteps) {
                s.recipeId = mRecipe.id;
                mKitchenViewModel.insertSteps(s);
            }
        }
        if (mIsInsertedForEdit) {
            startEditingActivity(mRecipe);
        }
    }

    private void startEditingActivity(Recipe recipe) {
        Intent intent = new Intent(RecipeDetailActivity.this, RecipeEditActivity.class);
        intent.putExtra(AppConstants.EXTRA_RECIPE, recipe);
        startActivity(intent);
        finish();
    }
}
