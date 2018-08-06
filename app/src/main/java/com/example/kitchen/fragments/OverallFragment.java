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
import com.example.kitchen.data.local.entities.Recipe;
import com.example.kitchen.utility.AppConstants;
import com.example.kitchen.utility.BitmapUtils;
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

import static android.support.v4.content.ContextCompat.checkSelfPermission;

public class OverallFragment extends Fragment {
    private static final String LOG_TAG = OverallFragment.class.getSimpleName();
    private static final String KEY_IMAGE_ROTATION = "image-rotation";
    private static final String KEY_REQUEST_PERMISSION = "request-permission";
    private static final String TAG_PICTURE_DIALOG = "picture-dialog";
    private FragmentMessageListener mMessageListener;
    private Context mContext;
    private View mRootView;
    private ImageView mImageView;
    private EditText mTitleView;
    private EditText servingsView;
    private EditText prepTimeView;
    private EditText cookTimeView;
    private Spinner courseSpinner;
    private Spinner cuisineSpinner;
    private Spinner languageSpinner;
    private ImageButton mPublishButton;
    private ProgressBar mProgressBar;
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
        mRootView = inflater.inflate(R.layout.fragment_overall, container, false);

        // Take a picture and load the thumbnail to the image view.
        mImageView = mRootView.findViewById(R.id.iv_recipe_picture);
        mImageView.setOnClickListener(new View.OnClickListener() {
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
            mImageView.setRotation(savedInstanceState.getFloat(KEY_IMAGE_ROTATION));
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
                    .into(mImageView);
        }

        ImageButton rotate = mRootView.findViewById(R.id.btn_rotate);
        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Float rotation = mImageView.getRotation() + 90f;
                mImageView.setRotation(rotation);
            }
        });

        if (mImageView == null)
            rotate.setVisibility(View.GONE);
        else
            rotate.setVisibility(View.VISIBLE);

        mPublishButton = mRootView.findViewById(R.id.btn_publish_recipe);
        mPublishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishRecipe();
            }
        });

        mProgressBar = mRootView.findViewById(R.id.progress_bar_publish_recipe);
        mTitleView = mRootView.findViewById(R.id.text_edit_title);
        mTitleView.setText(mRecipe.title);

        servingsView = mRootView.findViewById(R.id.text_edit_servings);
        servingsView.setText(String.valueOf(mRecipe.servings));

        prepTimeView = mRootView.findViewById(R.id.text_edit_prep_time);
        prepTimeView.setText(String.valueOf(mRecipe.prepTime));

        cookTimeView = mRootView.findViewById(R.id.text_edit_cook_time);
        cookTimeView.setText(String.valueOf(mRecipe.cookTime));

        courseSpinner = mRootView.findViewById(R.id.spinner_course);
        ArrayAdapter<CharSequence> courseAdapter = ArrayAdapter.createFromResource(mContext,
                R.array.course_array, android.R.layout.simple_spinner_item);
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(courseAdapter);
        courseSpinner.setSelection(courseAdapter.getPosition(mRecipe.course));

        cuisineSpinner = mRootView.findViewById(R.id.spinner_cuisine);
        ArrayAdapter<CharSequence> cuisineAdapter = ArrayAdapter.createFromResource(mContext,
                R.array.cuisine_array, android.R.layout.simple_spinner_item);
        cuisineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cuisineSpinner.setAdapter(cuisineAdapter);
        cuisineSpinner.setSelection(cuisineAdapter.getPosition(mRecipe.cuisine));

        languageSpinner = mRootView.findViewById(R.id.spinner_language);
        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(mContext,
                R.array.language_array, android.R.layout.simple_spinner_item);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(languageAdapter);
        languageSpinner.setSelection(languageAdapter.getPosition(mRecipe.language));

        return mRootView;
    }

    @Override
    public void onPause() {
        saveRecipe();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloat(KEY_IMAGE_ROTATION, mImageView.getRotation());
        outState.putBoolean(KEY_REQUEST_PERMISSION, mDoNotRequestPermission);
        outState.putParcelable(AppConstants.KEY_RECIPE, mRecipe);
    }

    private String validateRecipeTitle(String title) {
        // Separate each character of the input title.
        char[] chars = title.toCharArray();
        // Empty outcome string.
        title = "";
        // Reserve a little box for preventing adjacent spaces.
        char previous = '.';
        // For each character...
        for (char aChar : chars) {
            // Allow letters and spaces; prevent adjacent spaces.
            if (Character.isLetter(aChar) || (aChar == ' ' && previous != ' ')) {
                title = title.concat(String.valueOf(aChar));
                previous = aChar;
            }
        }
        // Delete surrounding spaces and make all characters lower case.
        title = title.trim().toLowerCase();
        // Separate each word.
        String[] words = title.split(" ");
        // Empty outcome string.
        title = "";
        // For each word...
        for (int i = 0; i < words.length; i++) {
            // Separate each character.
            chars = words[i].toCharArray();
            // For each character...
            for (int j = 0; j < chars.length; j++) {
                // Make first letters of each word a capital letter.
                // There is not any one-letter word to be a capital letter in the middle of a sentence.
                if ((i == 0 && j == 0) || (i != 0 && j == 0 && chars.length > 1)) {
                    chars[j] = Character.toUpperCase(chars[j]);
                }
            }
            // Combine filtered characters.
            title = title.concat(String.valueOf(chars));
            if (i != words.length - 1) {
                title = title.concat(" ");
            }
        }
        return title;
    }

    private boolean saveRecipe() {
        String title = mTitleView.getText().toString();
        // Do not save if no title is provided for the recipe.
        if (TextUtils.isEmpty(title)) {
            mTitleView.requestFocus();
            mTitleView.setError(getString(R.string.recipe_title_required));
            return false;
        }
        mRecipe.title = validateRecipeTitle(title);
        mTitleView.setText(mRecipe.title);
        // Take the image from the image view.
        BitmapDrawable drawable = (BitmapDrawable) mImageView.getDrawable();
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
        mRecipe.cookTime = Integer.valueOf(cookTimeView.getText().toString());
        mRecipe.prepTime = Integer.valueOf(prepTimeView.getText().toString());
        mRecipe.servings = Integer.valueOf(servingsView.getText().toString());
        mRecipe.course = courseSpinner.getSelectedItem().toString();
        mRecipe.cuisine = cuisineSpinner.getSelectedItem().toString();
        mRecipe.language = languageSpinner.getSelectedItem().toString();
        mRecipe.timeStamp = new Date().getTime();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mRecipe.writerName = user.getDisplayName();
            mRecipe.writerUid = user.getUid();
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.KEY_RECIPE, mRecipe);
        mMessageListener.onFragmentMessage(0, bundle);
        KitchenViewModel viewModel = ViewModelProviders.of(this).get(KitchenViewModel.class);
        viewModel.insertRecipes(mRecipe);
        return true;
    }

    private void publishRecipe() {
        if (!saveRecipe()) {
            return;
        }
        final String path = mRecipe.imagePath;
        if (path == null || TextUtils.isEmpty(path)) {
            Log.e(LOG_TAG, getString(R.string.missing_picture));
            Snackbar.make(mProgressBar, R.string.missing_picture, Snackbar.LENGTH_SHORT).show();
            return;
        }
        mPublishButton.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        Snackbar.make(mProgressBar, R.string.publishing, Snackbar.LENGTH_SHORT).show();
        Uri file = Uri.fromFile(new File(path));
        final StorageReference ref = FirebaseStorage.getInstance().getReference("images/" + mRecipe.title + ".jpg");
        ref.putFile(file).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    //noinspection ConstantConditions
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    RecipeViewModel viewModel = ViewModelProviders.of(OverallFragment.this).get(RecipeViewModel.class);
                    mRecipe.publicKey = viewModel.writeNewRecipe(mRecipe.title, task.getResult().toString(),
                            mRecipe.servings, mRecipe.prepTime, mRecipe.cookTime, mRecipe.language,
                            mRecipe.cuisine, mRecipe.course, mRecipe.writerUid, mRecipe.writerName);
                    KitchenViewModel kitchenViewModel = ViewModelProviders.of(OverallFragment.this).get(KitchenViewModel.class);
                    kitchenViewModel.insertRecipes(mRecipe);
                }
                mPublishButton.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                Snackbar.make(mProgressBar, R.string.publish_succesful, Snackbar.LENGTH_SHORT).show();
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
                                    .into(mImageView);
                        }
                    } else {
                        mDoNotRequestPermission = true;
                        Snackbar.make(mRootView, R.string.allow_write_external, Snackbar.LENGTH_LONG).show();
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
}