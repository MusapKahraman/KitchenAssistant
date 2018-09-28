/*
 * Reference
 * https://developer.android.com/training/camera/photobasics
 * https://junjunguo.com/blog/android-take-photo-show-in-list-view-b/
 */

package com.example.kitchen.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.example.kitchen.R;
import com.example.kitchen.adapters.RecipeEditAdapter;
import com.example.kitchen.data.local.entities.Recipe;
import com.example.kitchen.fragments.OnPictureDialogListener;
import com.example.kitchen.fragments.OnSaveRecipeListener;
import com.example.kitchen.utility.AppConstants;
import com.example.kitchen.utility.BitmapUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeEditActivity extends AppCompatActivity
        implements OnPictureDialogListener, OnSaveRecipeListener {
    private static final String LOG_TAG = RecipeEditActivity.class.getSimpleName();
    @BindView(R.id.viewPager) ViewPager mViewPager;
    private Recipe mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_edit);
        ButterKnife.bind(this);
        if (savedInstanceState != null) {
            mRecipe = savedInstanceState.getParcelable(AppConstants.KEY_RECIPE);
        } else if (getIntent() != null) {
            mRecipe = getIntent().getParcelableExtra(AppConstants.EXTRA_RECIPE);
        }
        if (mRecipe == null) {
            mRecipe = new Recipe(new Date().getTime());
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(getString(R.string.new_recipe));
            }
        }
        updateFragments();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(AppConstants.KEY_RECIPE, mRecipe);
    }

    @Override
    public void onSaveRecipeOverview(Bundle bundle) {
        mRecipe = bundle.getParcelable(AppConstants.KEY_RECIPE);
    }

    @Override
    public void onSelectCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent.
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = BitmapUtils.createImageFile(this);
            } catch (IOException ex) {
                Log.e(LOG_TAG, "An error occurred while creating the image file.");
            }
            if (photoFile != null) {
                // File is successfully created
                mRecipe.imagePath = photoFile.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.kitchen.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, AppConstants.REQUEST_CAMERA);
            }
        } else
            Snackbar.make(mViewPager, R.string.no_camera_app, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onSelectGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, AppConstants.REQUEST_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AppConstants.REQUEST_CAMERA:
                if (resultCode == RESULT_OK) {
                    updateFragments();
                }
            case AppConstants.REQUEST_GALLERY:
                if (resultCode == RESULT_OK && data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver()
                                .query(selectedImage, filePathColumn, null, null, null);
                        if (cursor != null) {
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            mRecipe.imagePath = cursor.getString(columnIndex);
                            cursor.close();
                            updateFragments();
                        }
                    }
                }
        }
    }

    private void updateFragments() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.KEY_RECIPE, mRecipe);
        FragmentStatePagerAdapter pagerAdapter =
                new RecipeEditAdapter(this, getSupportFragmentManager(), bundle);
        mViewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(mViewPager);
    }
}
