package com.example.kitchen.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.kitchen.R;
import com.example.kitchen.activities.NewRecipeActivity;
import com.example.kitchen.utility.KeyUtils;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;

public class NewRecipesOverallFragment extends Fragment {
    private static final String TAG = NewRecipesOverallFragment.class.getSimpleName();
    private static final String KEY_REQUEST_PERMISSION = "request-permission";
    private static final String KEY_IMAGE_ROTATION = "image-rotation";
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private Context mContext;
    private View mRootView;
    private String mImageFilePath;
    private ImageView mImageView;
    private boolean mDoNotRequestPermission;

    // Deal with write permission to external storage in Glide
    // Reference => https://futurestud.io/tutorials/glide-exceptions-debugging-and-error-handling
    private final RequestListener<Drawable> requestListener = new RequestListener<Drawable>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            if (mDoNotRequestPermission)
                return false;
            if (checkSelfPermission(mContext,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
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

    public NewRecipesOverallFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        mRootView = inflater.inflate(R.layout.fragment_new_recipe_overall, container, false);
        // Take a picture and load the thumbnail to the image view.
        mImageView = mRootView.findViewById(R.id.iv_recipe_image);
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
        // Get savedInstanceState from the activity.
        Bundle arguments = getArguments();
        if (arguments != null) {
            mImageFilePath = arguments.getString(KeyUtils.KEY_IMAGE_PATH);
            savedInstanceState = arguments.getBundle(KeyUtils.KEY_SAVED_STATE);
            if (savedInstanceState != null) {
                mDoNotRequestPermission = savedInstanceState.getBoolean(KEY_REQUEST_PERMISSION);
                mImageView.setRotation(savedInstanceState.getFloat(KEY_IMAGE_ROTATION));
            }
        }
        if (mImageFilePath != null) {
            int size = getResources().getInteger(R.integer.thumbnail_size);
            Glide.with(mContext)
                    .load(mImageFilePath)
                    .listener(requestListener)
                    .apply(new RequestOptions().override(size).centerCrop())
                    .into(mImageView);
        }
        Button rotate = mRootView.findViewById(R.id.btn_rotate);
        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Float rotation = mImageView.getRotation() + 90f;
                mImageView.setRotation(rotation);
            }
        });
        return mRootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        sendToActivity(outState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        sendToActivity(new Bundle());
    }

    private void sendToActivity(Bundle outState) {
        outState.putBoolean(KEY_REQUEST_PERMISSION, mDoNotRequestPermission);
        outState.putFloat(KEY_IMAGE_ROTATION, mImageView.getRotation());

        NewRecipeActivity activity = null;
        try {
            activity = (NewRecipeActivity) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, e.getMessage());
        }

        if (activity != null) {
            activity.fromOverallFragment(outState);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
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
}