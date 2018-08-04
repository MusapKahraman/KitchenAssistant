package com.example.kitchen.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.kitchen.R;
import com.example.kitchen.adapters.RecipeEditAdapter;
import com.example.kitchen.data.local.entities.Recipe;
import com.example.kitchen.fragments.FragmentMessageListener;
import com.example.kitchen.fragments.PictureDialogListener;
import com.example.kitchen.utility.AppConstants;
import com.example.kitchen.utility.BitmapUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class RecipeEditActivity extends AppCompatActivity implements PictureDialogListener, FragmentMessageListener {
    private ViewPager mViewPager;
    private Recipe mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_edit);
        mViewPager = findViewById(R.id.viewPager);

        if (savedInstanceState != null) {
            mRecipe = savedInstanceState.getParcelable(AppConstants.KEY_RECIPE);
        } else if (getIntent() != null) {
            mRecipe = getIntent().getParcelableExtra(AppConstants.EXTRA_RECIPE);
        }
        if (mRecipe == null) {
            mRecipe = new Recipe(getString(R.string.new_recipe), new Date().getTime());
        }
        updateFragments();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(AppConstants.KEY_RECIPE, mRecipe);
    }

    private void updateFragments() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.KEY_RECIPE, mRecipe);
        FragmentStatePagerAdapter pagerAdapter = new RecipeEditAdapter(this, getSupportFragmentManager(), bundle);
        mViewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    public void onFragmentMessage(int fragmentIndex, Bundle bundle) {
        switch (fragmentIndex) {
            case 0:
                mRecipe = bundle.getParcelable(AppConstants.KEY_RECIPE);
                break;
            case 1:
                break;
            case 2:
                break;
        }
    }

    @Override
    public void onCameraSelected() {
        // Reference -> https://developer.android.com/training/camera/photobasics
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = BitmapUtils.createImageFile(this);
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                // File is successfully created
                mRecipe.photoUrl = photoFile.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.kitchen.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, AppConstants.REQUEST_CAMERA);
            }
        } else {
            Snackbar.make(mViewPager, R.string.no_camera_app, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGallerySelected() {
        // Reference -> https://developer.android.com/training/camera/photobasics
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, AppConstants.REQUEST_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AppConstants.REQUEST_CAMERA:
                if (resultCode == RESULT_OK) {
                    // Send mImageFilePath to the fragment.
                    updateFragments();
                }
            case AppConstants.REQUEST_GALLERY:
                // Reference -> https://junjunguo.com/blog/android-take-photo-show-in-list-view-b/
                if (resultCode == RESULT_OK && data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver()
                                .query(selectedImage, filePathColumn, null, null,
                                        null);
                        if (cursor != null) {
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            mRecipe.photoUrl = cursor.getString(columnIndex);
                            cursor.close();
                            // Send mImageFilePath to the fragment.
                            updateFragments();
                        }
                    }
                }
        }
    }
}
