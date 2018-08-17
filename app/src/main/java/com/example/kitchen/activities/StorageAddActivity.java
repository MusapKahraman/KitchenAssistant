package com.example.kitchen.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.kitchen.R;
import com.example.kitchen.data.firebase.FoodViewModel;
import com.example.kitchen.data.local.KitchenViewModel;
import com.example.kitchen.data.local.entities.Food;
import com.example.kitchen.utility.CheckUtils;
import com.example.kitchen.utility.DeviceUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseException;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StorageAddActivity extends AppCompatActivity {
    private static final String LOG_TAG = StorageAddActivity.class.getSimpleName();
    @BindView(R.id.btn_add_food) Button mAddFoodLink;
    @BindView(R.id.spinner_food) SearchableSpinner mFoodSpinner;
    @BindView(R.id.text_edit_amount) EditText mAmountEditText;
    @BindView(R.id.spinner_measure) Spinner mMeasurementSpinner;
    @BindView(R.id.btn_add_to_storage) Button mAddButton;
    private KitchenViewModel mKitchenViewModel;
    private ArrayAdapter<CharSequence> mMeasurementAdapter;
    private ArrayList<String> mFoods;
    private Map<String, String> mFoodMap;
    private ArrayList<Food> mFoodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_add);
        ButterKnife.bind(this);

        mKitchenViewModel = ViewModelProviders.of(this).get(KitchenViewModel.class);

        if (savedInstanceState == null) {
            mKitchenViewModel.getStorage().observe(this, new Observer<List<Food>>() {
                @Override
                public void onChanged(@Nullable List<Food> foodList) {
                    mFoodList = (ArrayList<Food>) foodList;
                }
            });
        }

        FoodViewModel foodViewModel = ViewModelProviders.of(this).get(FoodViewModel.class);
        mFoodSpinner.setTitle(getString(R.string.select_food));
        foodViewModel.getDataSnapshotLiveData().observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    mFoods = new ArrayList<>();
                    mFoodMap = new HashMap<>();
                    for (DataSnapshot foodSnapShot : dataSnapshot.getChildren()) {
                        try {
                            if (foodSnapShot.getValue() != null)
                                mFoodMap.put(foodSnapShot.getKey(), foodSnapShot.getValue().toString());
                            mFoods.add(foodSnapShot.getKey());
                        } catch (DatabaseException e) {
                            Log.e(LOG_TAG, e.getMessage());
                        }
                    }
                    ArrayAdapter<String> foodAdapter = new ArrayAdapter<>(StorageAddActivity.this,
                            android.R.layout.simple_spinner_item, mFoods);
                    foodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mFoodSpinner.setAdapter(foodAdapter);
                }
            }
        });
        mFoodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String food = mFoods.get(position);
                float value = Float.valueOf(mFoodMap.get(food));
                if (value == 0) {
                    mMeasurementAdapter = ArrayAdapter.createFromResource(StorageAddActivity.this,
                            R.array.measurement_array_countable, android.R.layout.simple_spinner_item);
                } else {
                    mMeasurementAdapter = ArrayAdapter.createFromResource(StorageAddActivity.this,
                            R.array.measurement_array_uncountable, android.R.layout.simple_spinner_item);
                }
                mMeasurementAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mMeasurementSpinner.setAdapter(mMeasurementAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFoodSpinner.getSelectedItem() == null) return;
                String name = mFoodSpinner.getSelectedItem().toString();
                String amountType = mMeasurementSpinner.getSelectedItem().toString();
                if (CheckUtils.isEmptyTextField(StorageAddActivity.this, mAmountEditText)) return;
                int amount = Integer.valueOf(mAmountEditText.getText().toString());
                // If there is already an item in the list with the same name then just increase its amount.
                int shownId = 0;
                int shownAmount = 0;
                if (mFoodList != null) {
                    for (Food shown : mFoodList) {
                        if (name.equals(shown.food) && amountType.equals(shown.amountType)) {
                            shownId = shown.id;
                            shownAmount = shown.amount;
                        }
                    }
                }
                if (shownId == 0) {
                    mKitchenViewModel.insertFood(
                            new Food(name, amount, amountType, 0));
                } else {
                    mKitchenViewModel.insertFood(
                            new Food(shownId, name, amount + shownAmount, amountType, 0));
                }
                DeviceUtils.hideKeyboardFrom(StorageAddActivity.this, mAmountEditText);
            }
        });
    }
}
