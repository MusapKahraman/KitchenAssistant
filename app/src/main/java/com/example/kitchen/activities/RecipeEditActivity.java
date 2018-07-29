package com.example.kitchen.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.kitchen.R;
import com.example.kitchen.adapters.RecipeEditAdapter;
import com.example.kitchen.fragments.FragmentMessageListener;
import com.example.kitchen.fragments.PictureDialogListener;
import com.example.kitchen.utility.KeyUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecipeEditActivity extends AppCompatActivity implements PictureDialogListener, FragmentMessageListener {
    private String mImageFilePath;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_edit);

        mViewPager = findViewById(R.id.viewPager);

        if (savedInstanceState != null) {
            mImageFilePath = savedInstanceState.getString(KeyUtils.KEY_IMAGE_PATH);
        }

        updateFragments();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KeyUtils.KEY_IMAGE_PATH, mImageFilePath);
    }

    private void updateFragments() {
        Bundle bundle = new Bundle();
        bundle.putString(KeyUtils.KEY_IMAGE_PATH, mImageFilePath);
        FragmentStatePagerAdapter pagerAdapter = new RecipeEditAdapter(this, getSupportFragmentManager(), bundle);
        mViewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onFragmentMessage(int fragmentIndex, Bundle bundle) {
        switch (fragmentIndex) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
        }
    }

    /**
     * Reference -> https://developer.android.com/training/camera/photobasics
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "recipe_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    public void onCameraSelect() {
        // Reference -> https://developer.android.com/training/camera/photobasics
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                mImageFilePath = photoFile.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.kitchen.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, KeyUtils.REQUEST_CAMERA);
            }
        } else {
            Snackbar.make(mViewPager, R.string.no_camera_app, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGallerySelect() {
        // Reference -> https://developer.android.com/training/camera/photobasics
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, KeyUtils.REQUEST_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case KeyUtils.REQUEST_CAMERA:
                if (resultCode == RESULT_OK) {
                    // Send mImageFilePath to the fragment.
                    updateFragments();
                }
            case KeyUtils.REQUEST_GALLERY:
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
                            mImageFilePath = cursor.getString(columnIndex);
                            cursor.close();
                            // Send mImageFilePath to the fragment.
                            updateFragments();
                        }
                    }
                }
        }
    }
}
