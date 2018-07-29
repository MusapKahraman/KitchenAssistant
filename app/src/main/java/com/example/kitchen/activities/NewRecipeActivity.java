package com.example.kitchen.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.kitchen.R;
import com.example.kitchen.fragments.NewRecipesOverallFragment;
import com.example.kitchen.fragments.PictureDialogFragment;
import com.example.kitchen.utility.KeyUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewRecipeActivity extends AppCompatActivity implements PictureDialogFragment.PictureDialogListener {
    private static final int REQUEST_GALLERY = 1;
    private static final int REQUEST_CAMERA = 2;
    private static final String KEY_NAV_INDEX = "navigator-index-key";
    private static final String KEY_OVERALL = "recipes-fragment-key";
    private String mImageFilePath;
    private BottomNavigationView mNavView;
    private Bundle mRecipeOverallState;
    private int mNavigatorIndex;
    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int oldNavigatorIndex = mNavigatorIndex;
            switch (item.getItemId()) {
                case R.id.navigation_overview:
                    mNavigatorIndex = 0;
                    return true;
                case R.id.navigation_ingredients:
                    mNavigatorIndex = 1;
                    return true;
                case R.id.navigation_directions:
                    mNavigatorIndex = 2;
                    return true;
            }
            if (mNavigatorIndex != oldNavigatorIndex) {
                changeContent();
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);

        mNavView = findViewById(R.id.navigation);
        mNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (savedInstanceState != null) {
            mImageFilePath = savedInstanceState.getString(KeyUtils.KEY_IMAGE_PATH);
            mNavigatorIndex = savedInstanceState.getInt(KEY_NAV_INDEX);
            mRecipeOverallState = savedInstanceState.getBundle(KEY_OVERALL);
        } else if (getIntent() != null) {
            mNavigatorIndex = getIntent().getIntExtra(KeyUtils.EXTRA_NAV_INDEX, 0);
        }
        changeContent();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KeyUtils.KEY_IMAGE_PATH, mImageFilePath);
        outState.putInt(KEY_NAV_INDEX, mNavigatorIndex);
        outState.putBundle(KEY_OVERALL, mRecipeOverallState);
    }

    private void changeContent() {
        switch (mNavigatorIndex) {
            case 0:
                Bundle bundle = new Bundle();
                bundle.putBundle(KeyUtils.KEY_SAVED_STATE, mRecipeOverallState);
                bundle.putString(KeyUtils.KEY_IMAGE_PATH, mImageFilePath);
                NewRecipesOverallFragment fragment = new NewRecipesOverallFragment();
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                break;
            case 1:

                break;
            case 2:

                break;
        }
    }

    public void fromOverallFragment(Bundle fragmentOutState) {
        mRecipeOverallState = fragmentOutState;
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

    /**
     * Reference -> https://developer.android.com/training/camera/photobasics
     */
    @Override
    public void onDialogCameraSelected() {
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
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        } else {
            Snackbar.make(mNavView, R.string.no_camera_app, Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * Reference -> https://developer.android.com/training/camera/photobasics
     */
    @Override
    public void onDialogGallerySelected() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (resultCode == RESULT_OK) {
                    changeContent();
                }
            case REQUEST_GALLERY:
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
                            changeContent();
                        }
                    }
                }
        }
    }
}
