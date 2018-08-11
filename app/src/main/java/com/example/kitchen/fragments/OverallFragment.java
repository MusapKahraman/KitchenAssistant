package com.example.kitchen.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.DialogFragment;
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
import com.example.kitchen.data.firebase.RecipeViewModel;
import com.example.kitchen.data.local.KitchenViewModel;
import com.example.kitchen.data.local.LocalDatabaseInsertListener;
import com.example.kitchen.data.local.entities.Recipe;
import com.example.kitchen.utility.AppConstants;
import com.example.kitchen.utility.BitmapUtils;
import com.example.kitchen.utility.CheckUtils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v4.content.ContextCompat.checkSelfPermission;

public class OverallFragment extends Fragment implements LocalDatabaseInsertListener {
    private static final String LOG_TAG = OverallFragment.class.getSimpleName();
    private static final String KEY_IMAGE_ROTATION = "image-rotation";
    private static final String KEY_REQUEST_PERMISSION = "request-permission";
    private static final String TAG_PICTURE_DIALOG = "picture-dialog";
    @BindView(R.id.iv_recipe_picture)
    ImageView mRecipeImageView;
    @BindView(R.id.btn_rotate)
    ImageButton mRotateButton;
    @BindView(R.id.text_edit_title)
    EditText mTitleView;
    @BindView(R.id.text_edit_servings)
    EditText mServingsView;
    @BindView(R.id.text_edit_prep_time)
    EditText mPrepTimeView;
    @BindView(R.id.text_edit_cook_time)
    EditText mCookTimeView;
    @BindView(R.id.spinner_course)
    Spinner mCourseSpinner;
    @BindView(R.id.spinner_cuisine)
    Spinner mCuisineSpinner;
    @BindView(R.id.spinner_language)
    Spinner mLanguageSpinner;
    @BindView(R.id.btn_publish_recipe)
    ImageButton mPublishButton;
    @BindView(R.id.progress_bar_publish_recipe)
    ProgressBar mProgressBar;
    private KitchenViewModel mKitchenViewModel;
    private RecipeViewModel mRecipeViewModel;
    private FragmentMessageListener mMessageListener;
    private Context mContext;
    private Recipe mRecipe;
    private boolean mDoNotRequestPermission;
    // Deal with write permission to external storage in Glide
    // Reference => https://futurestud.io/tutorials/glide-exceptions-debugging-and-error-handling
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
            // everything worked out, so probably nothing to do
            return false;
        }
    };

    public OverallFragment() {
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
        mKitchenViewModel = ViewModelProviders.of(OverallFragment.this).get(KitchenViewModel.class);
        mRecipeViewModel = ViewModelProviders.of(OverallFragment.this).get(RecipeViewModel.class);
        // Take a picture and load the thumbnail to the image view.
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

        if (savedInstanceState != null) {
            mRecipeImageView.setRotation(savedInstanceState.getFloat(KEY_IMAGE_ROTATION));
            mDoNotRequestPermission = savedInstanceState.getBoolean(KEY_REQUEST_PERMISSION);
            mRecipe = savedInstanceState.getParcelable(AppConstants.KEY_RECIPE);
        } else {
            Bundle arguments = getArguments();
            if (arguments != null) {
                mRecipe = arguments.getParcelable(AppConstants.KEY_RECIPE);
            }
        }

        if (mRecipe != null && !TextUtils.isEmpty(mRecipe.imagePath)) {
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
        mRotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Float rotation = mRecipeImageView.getRotation() + 90f;
                mRecipeImageView.setRotation(rotation);
            }
        });
        if (mRecipeImageView == null)
            mRotateButton.setVisibility(View.GONE);
        else
            mRotateButton.setVisibility(View.VISIBLE);
        mPublishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishRecipe();
            }
        });
        mTitleView.setText(mRecipe.title);
        mServingsView.setText(String.valueOf(mRecipe.servings));
        mPrepTimeView.setText(String.valueOf(mRecipe.prepTime));
        mCookTimeView.setText(String.valueOf(mRecipe.cookTime));

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

        return rootView;
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
        outState.putParcelable(AppConstants.KEY_RECIPE, mRecipe);
    }

    private boolean saveRecipe() {
        // Do not save if no title is provided for the recipe.
        if (CheckUtils.isEmptyTextField(mContext, mTitleView))
            return false;
        String title = mTitleView.getText().toString();
        mRecipe.title = CheckUtils.validateTitle(title);
        mTitleView.setText(mRecipe.title);
        // Take the image from the image view.
        BitmapDrawable drawable = (BitmapDrawable) mRecipeImageView.getDrawable();
        // Create a new file with the image and change value of mImageFilePath to this file. Then delete the older one.
        if (drawable != null) {
            File oldFile = new File(mRecipe.imagePath);
            mRecipe.imagePath = BitmapUtils.writeJpegPrivate(mContext, drawable.getBitmap(), title);
            if (!oldFile.getName().equals(title + ".jpg") && oldFile.getAbsolutePath().contains(mContext.getPackageName())) {
                Boolean deleted = oldFile.delete();
                if (deleted)
                    Log.v(LOG_TAG, "Temporary image file is deleted.");
            }
        }
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mRecipe.writerName = user.getDisplayName();
            mRecipe.writerUid = user.getUid();
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.KEY_RECIPE, mRecipe);
        mMessageListener.onFragmentMessage(0, bundle);
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
                    mRecipe.publicKey = mRecipeViewModel.postRecipe(mRecipe, task.getResult().toString());
                    mKitchenViewModel.insertRecipe(mRecipe, OverallFragment.this);
                }
                mPublishButton.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                Snackbar.make(mProgressBar, R.string.publish_successful, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case AppConstants.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (mRecipe != null && !TextUtils.isEmpty(mRecipe.imagePath)) {
                            int size = getResources().getInteger(R.integer.image_size_px);
                            RequestOptions options = new RequestOptions()
                                    .centerCrop()
                                    .override(size);
                            Glide.with(mContext)
                                    .load(mRecipe.imagePath)
                                    .apply(options)
                                    .into(mRecipeImageView);
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
    public void onDetach() {
        super.onDetach();
        mMessageListener = null;
    }

    @Override
    public void onDataInsert(long id) {
        mRecipe.id = (int) id;
    }
}