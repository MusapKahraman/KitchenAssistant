/*
 * Reference
 * https://futurestud.io/tutorials/glide-exceptions-debugging-and-error-handling
 */

package com.example.kitchen.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.DialogFragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
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
import com.example.kitchen.utility.BitmapUtils;
import com.example.kitchen.utility.CheckUtils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v4.content.ContextCompat.checkSelfPermission;

public class OverviewFragment extends Fragment implements RecipeInsertListener {
    private static final String LOG_TAG = OverviewFragment.class.getSimpleName();
    private static final String KEY_IMAGE_ROTATION = "image-rotation";
    private static final String KEY_REQUEST_PERMISSION = "request-permission";
    private static final String KEY_OBSERVABLE_INGREDIENT = "observable-ingredient";
    private static final String KEY_OBSERVABLE_STEP = "observable-step";
    private static final String TAG_PICTURE_DIALOG = "picture-dialog";
    @BindView(R.id.iv_recipe_picture) ImageView mRecipeImageView;
    @BindView(R.id.btn_rotate) ImageButton mRotateButton;
    @BindView(R.id.text_edit_title) EditText mTitleView;
    @BindView(R.id.text_edit_servings) EditText mServingsView;
    @BindView(R.id.text_edit_prep_time) EditText mPrepTimeView;
    @BindView(R.id.text_edit_cook_time) EditText mCookTimeView;
    @BindView(R.id.spinner_course) Spinner mCourseSpinner;
    @BindView(R.id.spinner_cuisine) Spinner mCuisineSpinner;
    @BindView(R.id.spinner_language) Spinner mLanguageSpinner;
    @BindView(R.id.btn_publish_recipe) ImageButton mPublishButton;
    @BindView(R.id.progress_bar_publish_recipe) ProgressBar mProgressBar;
    private KitchenViewModel mKitchenViewModel;
    private RecipeViewModel mRecipeViewModel;
    private IngredientViewModel mIngredientViewModel;
    private StepViewModel mStepViewModel;
    private FragmentMessageListener mMessageListener;
    private Context mContext;
    private Recipe mRecipe;
    private ArrayList<Ingredient> mPublicIngredients;
    private ArrayList<Step> mPublicSteps;
    private boolean mDoNotRequestPermission;
    // Deal with write permission to external storage in Glide
    private final RequestListener<Drawable> requestListener = new RequestListener<Drawable>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            if (mDoNotRequestPermission)
                return false;
            if (checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                try {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            AppConstants.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                } catch (IllegalStateException ex) {
                    ex.fillInStackTrace();
                }
            }
            // important to return false so the error placeholder can be placed
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            // everything worked out
            // There is nothing to rotate if there is no picture.
            if (resource == null) {
                mRotateButton.setVisibility(View.GONE);
            } else {
                mRotateButton.setVisibility(View.VISIBLE);
            }
            return false;
        }
    };
    private boolean mIngredientIsObservable;
    private boolean mStepIsObservable;

    public OverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof FragmentMessageListener) {
            mMessageListener = (FragmentMessageListener) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement FragmentMessageListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        View rootView = inflater.inflate(R.layout.fragment_overall, container, false);
        ButterKnife.bind(this, rootView);
        // ViewModels for local database and firebase.
        mKitchenViewModel = ViewModelProviders.of(this).get(KitchenViewModel.class);
        mRecipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);
        mIngredientViewModel = ViewModelProviders.of(this).get(IngredientViewModel.class);
        mStepViewModel = ViewModelProviders.of(this).get(StepViewModel.class);
        if (savedInstanceState != null) {
            mRecipeImageView.setRotation(savedInstanceState.getFloat(KEY_IMAGE_ROTATION));
            mDoNotRequestPermission = savedInstanceState.getBoolean(KEY_REQUEST_PERMISSION);
            mStepIsObservable = savedInstanceState.getBoolean(KEY_OBSERVABLE_STEP);
            mIngredientIsObservable = savedInstanceState.getBoolean(KEY_OBSERVABLE_INGREDIENT);
            mRecipe = savedInstanceState.getParcelable(AppConstants.KEY_RECIPE);
        } else {
            Bundle arguments = getArguments();
            if (arguments != null) {
                mRecipe = arguments.getParcelable(AppConstants.KEY_RECIPE);
            }
        }
        if (mRecipe == null) {
            return rootView;
        }
        // Fill the edit texts with data from corresponding fields.
        mTitleView.setText(mRecipe.title);
        mServingsView.setText(String.valueOf(mRecipe.servings));
        mPrepTimeView.setText(String.valueOf(mRecipe.prepTime));
        mCookTimeView.setText(String.valueOf(mRecipe.cookTime));
        // Set spinners for courses, cuisines and languages.
        ArrayAdapter<CharSequence> courseAdapter = ArrayAdapter.createFromResource(mContext,
                R.array.course_array, android.R.layout.simple_spinner_item);
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCourseSpinner.setAdapter(courseAdapter);
        mCourseSpinner.setSelection(courseAdapter.getPosition(mRecipe.course));
        ArrayAdapter<CharSequence> cuisineAdapter = ArrayAdapter.createFromResource(mContext,
                R.array.cuisine_array, android.R.layout.simple_spinner_item);
        cuisineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCuisineSpinner.setAdapter(cuisineAdapter);
        mCuisineSpinner.setSelection(cuisineAdapter.getPosition(mRecipe.cuisine));
        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(mContext,
                R.array.language_array, android.R.layout.simple_spinner_item);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLanguageSpinner.setAdapter(languageAdapter);
        mLanguageSpinner.setSelection(languageAdapter.getPosition(mRecipe.language));
        // Fill the recipe image view.
        if (!TextUtils.isEmpty(mRecipe.imagePath)) {
            loadRecipeImage();
        }
        // Listen for clicks on recipe image view to take a picture and show in it.
        mRecipeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a dialog asking for whether to use the camera or gallery.
                DialogFragment dialogFragment = new PictureDialogFragment();
                Activity activity = getActivity();
                if (activity != null)
                    dialogFragment.show(activity.getFragmentManager(), TAG_PICTURE_DIALOG);
                mDoNotRequestPermission = false;
            }
        });
        // Listen for clicks on rotate button to rotate the taken image.
        mRotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Float rotation = mRecipeImageView.getRotation() + 90f;
                mRecipeImageView.setRotation(rotation);
            }
        });
        // Listen for clicks on publish button to publish the recipe on firebase.
        mPublishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishRecipe();
            }
        });
        // Listen for losing focus from title edit text.
        mTitleView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    saveRecipe();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPublishButton.setVisibility(View.GONE);
        mIngredientViewModel.getDataSnapshotLiveData(mRecipe.publicKey)
                .observe(this, new Observer<DataSnapshot>() {
                    @Override
                    public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                        mPublicIngredients = new ArrayList<>();
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
                                    mPublicIngredients.add(new Ingredient(0, ingredient.food,
                                            ingredient.amount, ingredient.amountType,
                                            ingredientSnapshot.getKey()));
                                }
                            }
                        }
                    }
                });
        mStepViewModel.getDataSnapshotLiveData(mRecipe.publicKey)
                .observe(this, new Observer<DataSnapshot>() {
                    @Override
                    public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                        mPublicSteps = new ArrayList<>();
                        if (dataSnapshot != null) {
                            for (DataSnapshot stepSnapshot : dataSnapshot.getChildren()) {
                                StepModel step = null;
                                try {
                                    step = stepSnapshot.getValue(StepModel.class);
                                } catch (DatabaseException e) {
                                    Log.e(LOG_TAG, e.getMessage());
                                }
                                if (step != null) {
                                    mPublicSteps.add(new Step(step.instruction, step.stepNumber, 0,
                                            stepSnapshot.getKey()));
                                }
                            }
                        }
                        // Sort the list in ascending order of step number.
                        Collections.sort(mPublicSteps, new Comparator<Step>() {
                            @Override
                            public int compare(Step o1, Step o2) {
                                return Integer.compare(o1.stepNumber, o2.stepNumber);
                            }
                        });
                        mPublishButton.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void onPause() {
        saveRecipe();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloat(KEY_IMAGE_ROTATION, mRecipeImageView.getRotation());
        outState.putBoolean(KEY_REQUEST_PERMISSION, mDoNotRequestPermission);
        outState.putBoolean(KEY_OBSERVABLE_STEP, mStepIsObservable);
        outState.putBoolean(KEY_OBSERVABLE_INGREDIENT, mIngredientIsObservable);
        outState.putParcelable(AppConstants.KEY_RECIPE, mRecipe);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mMessageListener = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case AppConstants.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (mRecipe != null && !TextUtils.isEmpty(mRecipe.imagePath)) {
                            loadRecipeImage();
                        }
                    } else {
                        mDoNotRequestPermission = true;
                        Snackbar.make(mTitleView, R.string.allow_write_external, Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    @Override
    public void onRecipeInserted(long id) {
        mRecipe.id = (int) id;
        Log.v(LOG_TAG, "Recipe is saved with id: " + mRecipe.id);
    }

    private void loadRecipeImage() {
        int size = getResources().getInteger(R.integer.image_size_px);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .override(size);
        Glide.with(mContext)
                .load(mRecipe.imagePath)
                .listener(requestListener)
                .apply(options)
                .into(mRecipeImageView);
    }

    private boolean saveRecipe() {
        // Do not save if no title is provided for the recipe.
        if (CheckUtils.isEmptyTextField(mContext, mTitleView))
            return false;
        String title = mTitleView.getText().toString();
        mRecipe.title = CheckUtils.validateTitle(title);
        mTitleView.setText(mRecipe.title);
        mRecipe.cookTime = CheckUtils.getPositiveIntegerFromField(mContext, mCookTimeView);
        if (mRecipe.cookTime == -1)
            return false;
        mRecipe.prepTime = CheckUtils.getPositiveIntegerFromField(mContext, mPrepTimeView);
        if (mRecipe.prepTime == -1)
            return false;
        mRecipe.servings = CheckUtils.getPositiveIntegerFromField(mContext, mServingsView);
        if (mRecipe.servings == -1)
            return false;
        mRecipe.course = mCourseSpinner.getSelectedItem().toString();
        mRecipe.cuisine = mCuisineSpinner.getSelectedItem().toString();
        mRecipe.language = mLanguageSpinner.getSelectedItem().toString();
        mRecipe.timeStamp = new Date().getTime();
        // Take the image from the image view.
        BitmapDrawable drawable = (BitmapDrawable) mRecipeImageView.getDrawable();
        // Create a new file with the image and save reference to this file. Then delete the older file.
        if (drawable != null) {
            File oldFile = new File(mRecipe.imagePath);
            mRecipe.imagePath = BitmapUtils.writeJpegPrivate(mContext, drawable.getBitmap(), mRecipe.title);
            if (!oldFile.getName().equals(mRecipe.title + ".jpg") && oldFile.getAbsolutePath().contains(mContext.getPackageName())) {
                Boolean deleted = oldFile.delete();
                if (deleted)
                    Log.v(LOG_TAG, "Temporary image file is deleted.");
            }
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mRecipe.writerName = user.getDisplayName();
            mRecipe.writerUid = user.getUid();
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.KEY_RECIPE, mRecipe);
        mMessageListener.onSaveRecipeOverview(bundle);
        Log.v(LOG_TAG, "Saving...");
        mKitchenViewModel.insertRecipe(mRecipe, this);
        return true;
    }

    private void publishRecipe() {
        if (!saveRecipe()) {
            return;
        }
        final String path = mRecipe.imagePath;
        if (TextUtils.isEmpty(path)) {
            Log.e(LOG_TAG, getString(R.string.missing_picture));
            Snackbar.make(mProgressBar, R.string.missing_picture, Snackbar.LENGTH_SHORT).show();
            return;
        }
        mPublishButton.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        Snackbar.make(mProgressBar, R.string.publishing, Snackbar.LENGTH_SHORT).show();
        mRecipe.publicKey = mRecipeViewModel.postRecipe(mRecipe, "");
        Uri file = Uri.fromFile(new File(path));
        final StorageReference ref = FirebaseStorage.getInstance().getReference("images/" + mRecipe.publicKey + ".jpg");
        ref.putFile(file).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    //noinspection ConstantConditions
                    throw task.getException();
                }
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    uploadRecipeData(task.getResult().toString());
                }
            }
        });
    }

    private void uploadRecipeData(String imageUrl) {
        mRecipe.publicKey = mRecipeViewModel.postRecipe(mRecipe, imageUrl);
        mKitchenViewModel.insertRecipe(mRecipe, OverviewFragment.this);
        mIngredientIsObservable = true;
        mKitchenViewModel.getIngredientsByRecipe(mRecipe.id).observe(this, new Observer<List<Ingredient>>() {
            @Override
            public void onChanged(@Nullable List<Ingredient> ingredients) {
                if (mIngredientIsObservable) {
                    mIngredientIsObservable = false;
                    if (ingredients != null) {
                        ArrayList<String> removalKeys = new ArrayList<>();
                        for (Ingredient publicIngredient : mPublicIngredients) {
                            removalKeys.add(publicIngredient.publicKey);
                        }
                        ArrayList<String> stayList = new ArrayList<>();
                        for (Ingredient ingredient : ingredients) {
                            ingredient.publicKey = mIngredientViewModel.postIngredient(ingredient, mRecipe.publicKey);
                            mKitchenViewModel.insertIngredients(ingredient);
                            for (String removalKey : removalKeys) {
                                if (removalKey.equals(ingredient.publicKey)) {
                                    stayList.add(removalKey);
                                }
                            }

                        }
                        for (String key : stayList) removalKeys.remove(key);
                        for (String removalKey : removalKeys)
                            mIngredientViewModel.removeIngredient(removalKey);
                    }
                }

            }
        });
        mStepIsObservable = true;
        mKitchenViewModel.getStepsByRecipe(mRecipe.id).observe(this, new Observer<List<Step>>() {
            @Override
            public void onChanged(@Nullable List<Step> steps) {
                if (mStepIsObservable) {
                    mStepIsObservable = false;
                    if (steps != null) {
                        ArrayList<String> removalKeys = new ArrayList<>();
                        for (Step publicStep : mPublicSteps) {
                            removalKeys.add(publicStep.publicKey);
                        }
                        ArrayList<String> stayList = new ArrayList<>();
                        for (Step step : steps) {
                            step.publicKey = mStepViewModel.postStep(step, mRecipe.publicKey);
                            mKitchenViewModel.insertSteps(step);
                            for (String removalKey : removalKeys) {
                                if (removalKey.equals(step.publicKey)) {
                                    stayList.add(removalKey);
                                }
                            }
                        }
                        for (String key : stayList) removalKeys.remove(key);
                        for (String removalKey : removalKeys)
                            mStepViewModel.removeStep(removalKey);
                    }
                }
            }
        });
        mPublishButton.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        Snackbar.make(mProgressBar, String.format(getString(R.string.publish_successful), mRecipe.title), Snackbar.LENGTH_SHORT).show();
    }
}