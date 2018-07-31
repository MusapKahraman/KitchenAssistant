package com.example.kitchen.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.kitchen.R;
import com.example.kitchen.utility.AppConstants;
import com.example.kitchen.utility.BitmapUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static android.support.v4.content.ContextCompat.checkSelfPermission;

public class OverallFragment extends Fragment {
    private static final String KEY_IMAGE_ROTATION = "image-rotation";
    private static final String KEY_REQUEST_PERMISSION = "request-permission";
    private Context mContext;
    private String mImageFilePath;
    private ImageView mImageView;
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
    private boolean mDoNotRequestPermission;
    private EditText mTitleView;
    private View mRootView;
    private FragmentMessageListener mMessageListener;

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
            throw new ClassCastException(context.toString()
                    + "must implement FragmentMessageListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
                    dialogFragment.show(activity.getFragmentManager(), "picture");
                mDoNotRequestPermission = false;
            }
        });

        Bundle arguments = getArguments();
        if (arguments != null) {
            mImageFilePath = arguments.getString(AppConstants.KEY_IMAGE_PATH);
        }

        if (savedInstanceState != null) {
            mImageView.setRotation(savedInstanceState.getFloat(KEY_IMAGE_ROTATION));
            mDoNotRequestPermission = savedInstanceState.getBoolean(KEY_REQUEST_PERMISSION);
            mImageFilePath = savedInstanceState.getString(AppConstants.KEY_IMAGE_PATH);
        }

        if (mImageFilePath != null) {
            int size = getResources().getInteger(R.integer.thumbnail_size);
            Glide.with(mContext)
                    .load(mImageFilePath)
                    .listener(requestListener)
                    .apply(new RequestOptions().override(size).centerCrop())
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

        Button saveRecipeButton = mRootView.findViewById(R.id.btn_save);
        saveRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRecipe();
            }
        });

        mTitleView = mRootView.findViewById(R.id.text_edit_title);

        int layoutItemId = android.R.layout.simple_dropdown_item_1line;
        String[] mealsArray = getResources().getStringArray(R.array.course_array);
        List<String> dogList = Arrays.asList(mealsArray);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, layoutItemId, dogList);
        AutoCompleteTextView autocompleteView = mRootView.findViewById(R.id.text_edit_course);
        autocompleteView.setAdapter(adapter);

        return mRootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloat(KEY_IMAGE_ROTATION, mImageView.getRotation());
        outState.putBoolean(KEY_REQUEST_PERMISSION, mDoNotRequestPermission);
        outState.putString(AppConstants.KEY_IMAGE_PATH, mImageFilePath);
        sendToActivity(outState);
    }

    private void sendToActivity(Bundle outState) {
        mMessageListener.onFragmentMessage(0, outState);
    }

    private void saveRecipe() {
        String title = mTitleView.getText().toString();
        if (TextUtils.isEmpty(title)) {
            mTitleView.requestFocus();
            mTitleView.setError(getString(R.string.recipe_title_required));
            return;
        }
        BitmapDrawable drawable = (BitmapDrawable) mImageView.getDrawable();
        if (drawable != null) {
            File oldFile = new File(mImageFilePath);
            Log.v("OverallFragment", "Title: " + title);
            Log.v("OverallFragment", "Old file: " + oldFile.getName());
            Log.v("OverallFragment", "Old file is at: " + oldFile.getAbsolutePath());
            Log.v("OverallFragment", "Package Name: " + mContext.getPackageName());
            if (!oldFile.getName().equals(title + ".jpg")) {
                mImageFilePath = BitmapUtils.writeJpegPrivate(mContext, drawable.getBitmap(), title);
                Log.v("OverallFragment", "New file created at: " + mImageFilePath);
                if (oldFile.getAbsolutePath().contains(mContext.getPackageName())) {
                    Boolean deleted = oldFile.delete();
                    if (deleted)
                        Log.v("OverallFragment", "Old file is deleted.");
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case AppConstants.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        int size = getResources().getInteger(R.integer.thumbnail_size);
                        Glide.with(mContext)
                                .load(mImageFilePath)
                                .apply(new RequestOptions().override(size).centerCrop())
                                .into(mImageView);
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