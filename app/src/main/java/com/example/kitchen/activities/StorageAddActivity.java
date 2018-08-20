/*
 * Reference
 * https://stackoverflow.com/questions/14933330/datepicker-how-to-popup-datepicker-when-click-on-edittext/14933515#14933515
 */

package com.example.kitchen.activities;

import android.app.DatePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StorageAddActivity extends AppCompatActivity implements DeviceUtils.InternetConnectionListener {
    private static final String LOG_TAG = StorageAddActivity.class.getSimpleName();
    private static final String KEY_BEST_BEFORE = "best-before-key";
    @BindView(R.id.btn_link_define_food) Button mAddFoodLink;
    @BindView(R.id.spinner_food) SearchableSpinner mFoodSpinner;
    @BindView(R.id.edit_text_amount) TextInputEditText mAmountEditText;
    @BindView(R.id.spinner_measure) Spinner mMeasurementSpinner;
    @BindView(R.id.text_edit_date) TextInputEditText mBestBeforeDateText;
    @BindView(R.id.btn_add_to_storage) Button mAddButton;
    private KitchenViewModel mKitchenViewModel;
    private Context mContext;
    private ArrayAdapter<CharSequence> mMeasurementAdapter;
    private ArrayList<String> mFoods;
    private Map<String, String> mFoodMap;
    private ArrayList<Food> mFoodList;
    private long mBestBefore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_add);
        ButterKnife.bind(this);
        mContext = this;
        mKitchenViewModel = ViewModelProviders.of(this).get(KitchenViewModel.class);
        if (savedInstanceState == null) {
            DeviceUtils.startConnectionTest(this);
            mKitchenViewModel.getStorage().observe(this, new Observer<List<Food>>() {
                @Override
                public void onChanged(@Nullable List<Food> foodList) {
                    mFoodList = (ArrayList<Food>) foodList;
                }
            });
        } else {
            mBestBefore = savedInstanceState.getLong(KEY_BEST_BEFORE);
        }
        mAddFoodLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FoodDefinitionActivity.class);
                startActivity(intent);
            }
        });
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
                    ArrayAdapter<String> foodAdapter = new ArrayAdapter<>(mContext,
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
                    mMeasurementAdapter = ArrayAdapter.createFromResource(mContext,
                            R.array.measurement_array_countable, android.R.layout.simple_spinner_item);
                } else {
                    mMeasurementAdapter = ArrayAdapter.createFromResource(mContext,
                            R.array.measurement_array_uncountable, android.R.layout.simple_spinner_item);
                }
                mMeasurementAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mMeasurementSpinner.setAdapter(mMeasurementAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mBestBeforeDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(mContext, getOnDateSetListener(calendar), calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFoodSpinner.getSelectedItem() == null) return;
                String name = mFoodSpinner.getSelectedItem().toString();
                String amountType = mMeasurementSpinner.getSelectedItem().toString();
                if (CheckUtils.isEmptyEditText(mContext, mAmountEditText)) return;
                int amount = Integer.valueOf(mAmountEditText.getText().toString());
                // If there is already an item in the list with the same name then just increase its amount.
                int shownId = 0;
                int shownAmount = 0;
                if (mFoodList != null) {
                    for (Food shown : mFoodList) {
                        if (name.equals(shown.name) && amountType.equals(shown.amountType)) {
                            shownId = shown.id;
                            shownAmount = shown.amount;
                        }
                    }
                }
                if (shownId == 0) {
                    mKitchenViewModel.insertFood(
                            new Food(name, amount, amountType, mBestBefore));
                } else {
                    mKitchenViewModel.insertFood(
                            new Food(shownId, name, amount + shownAmount, amountType, mBestBefore));
                }
                DeviceUtils.hideKeyboardFrom(mContext, mAmountEditText);
                Snackbar.make(mAmountEditText,
                        String.format(getString(R.string.added_to_storage), amount, amountType, name),
                        Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_BEST_BEFORE, mBestBefore);
    }

    @Override
    public void onConnectionResult(boolean success) {
        if (!success)
            Snackbar.make(mFoodSpinner, R.string.connect_internet_try_again, Snackbar.LENGTH_LONG).show();
    }

    private DatePickerDialog.OnDateSetListener getOnDateSetListener(final Calendar calendar) {
        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String dateFormat = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
                mBestBeforeDateText.setText(sdf.format(calendar.getTime()));
                mBestBefore = calendar.getTimeInMillis();
            }
        };
    }
}
