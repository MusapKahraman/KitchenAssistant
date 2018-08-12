/*
 * Reference
 * https://stackoverflow.com/questions/5767570/how-to-update-a-menu-item-shown-in-the-actionbar/5767673#5767673
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
import com.example.kitchen.data.firebase.RecipeViewModel;
import com.example.kitchen.data.local.KitchenViewModel;
import com.example.kitchen.data.local.LocalDatabaseInsertListener;
import com.example.kitchen.data.local.entities.Ingredient;
import com.example.kitchen.data.local.entities.Recipe;
import com.example.kitchen.data.local.entities.Step;
import com.example.kitchen.utility.AppConstants;
import com.example.kitchen.utility.MeasurementUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailActivity extends AppCompatActivity implements RecipeViewModel.RatingPostListener, LocalDatabaseInsertListener {
    private static final String KEY_SERVINGS = "servings-key";
    private static final String KEY_RATING_TRANSACTION = "rating-transaction-status-key";
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
    private RecipeViewModel mRecipeViewModel;
    private KitchenViewModel mKitchenViewModel;
    private SharedPreferences mSharedPreferences;
    private Recipe mRecipe;
    private int mServings;
    private boolean mIsRatingProcessing;
    private boolean mIsBookable;
    private boolean mIsInsertedForEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        ButterKnife.bind(this);
        mSharedPreferences = getPreferences(Context.MODE_PRIVATE);
        mRecipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);
        mKitchenViewModel = ViewModelProviders.of(this).get(KitchenViewModel.class);
        boolean isEditable;
        if (getIntent() != null) {
            mRecipe = getIntent().getParcelableExtra(AppConstants.EXTRA_RECIPE);
            isEditable = getIntent().getBooleanExtra(AppConstants.EXTRA_EDITABLE, false);
            mIsBookable = getIntent().getBooleanExtra(AppConstants.EXTRA_BOOKABLE, false);
        } else {
            return;
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
                mKitchenViewModel.insertRecipe(mRecipe, RecipeDetailActivity.this);
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
        if (!isEditable) {
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
        if (savedInstanceState == null) {
            mServings = mRecipe.servings;
        } else {
            mServings = savedInstanceState.getInt(KEY_SERVINGS);
            mIsRatingProcessing = savedInstanceState.getBoolean(KEY_RATING_TRANSACTION);
        }
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

        final LayoutInflater inflater = LayoutInflater.from(this);
        mKitchenViewModel.getIngredientsByRecipe(mRecipe.id).observe(this, new Observer<List<Ingredient>>() {
            @Override
            public void onChanged(@Nullable List<Ingredient> ingredients) {
                if (ingredients == null)
                    return;
                for (Ingredient ingredient : ingredients) {
                    View ingredientView = inflater.inflate(R.layout.item_ingredient, mIngredientsContainer, false);
                    TextView ingredientAmountTextView = ingredientView.findViewById(R.id.tv_ingredient_amount);
                    TextView ingredientTextView = ingredientView.findViewById(R.id.tv_ingredient);
                    String text = ingredient.amount +
                            " " + MeasurementUtils.getAbbreviation(RecipeDetailActivity.this, ingredient.amountType);
                    ingredientAmountTextView.setText(text);
                    ingredientTextView.setText(ingredient.food);
                    mIngredientsContainer.addView(ingredientView);
                }
            }
        });

        mKitchenViewModel.getStepsByRecipe(mRecipe.id).observe(this, new Observer<List<Step>>() {
            @Override
            public void onChanged(@Nullable List<Step> steps) {
                if (steps == null)
                    return;
                for (Step step : steps) {
                    View stepView = inflater.inflate(R.layout.item_step, mStepsContainer, false);
                    TextView stepNumberView = stepView.findViewById(R.id.tv_step_number);
                    stepNumberView.setText(String.valueOf(step.stepNumber));
                    TextView instructionTextView = stepView.findViewById(R.id.tv_instruction);
                    instructionTextView.setText(step.instruction);
                    mStepsContainer.addView(stepView);
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SERVINGS, mServings);
        outState.putBoolean(KEY_RATING_TRANSACTION, mIsRatingProcessing);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (mIsBookable) {
            inflater.inflate(R.menu.menu_recipe_detail_bookable, menu);
        } else {
            inflater.inflate(R.menu.menu_recipe_detail, menu);
        }
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
                mKitchenViewModel.insertRecipe(mRecipe, this);
                Snackbar.make(mAppBarLayout, R.string.recipe_bookmarked, Snackbar.LENGTH_SHORT).show();
                // Delete bookmark icon from action bar menu.
                mIsBookable = false;
                invalidateOptionsMenu();
                return true;
            case R.id.app_bar_share:
                Snackbar.make(mAppBarLayout, "Sharing...", Snackbar.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRatingTransactionSuccessful(int rating) {
        mIsRatingProcessing = false;
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(mRecipe.publicKey, rating);
        editor.apply();
    }

    @Override
    public void onDataInsert(long id) {
        mRecipe.id = (int) id;
        if (mIsInsertedForEdit) {
            Intent intent = new Intent(RecipeDetailActivity.this, RecipeEditActivity.class);
            intent.putExtra(AppConstants.EXTRA_RECIPE, mRecipe);
            startActivity(intent);
            finish();
        }
    }
}
