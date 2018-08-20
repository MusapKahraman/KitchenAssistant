package com.example.kitchen.activities;

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
import android.widget.Spinner;

import com.example.kitchen.R;
import com.example.kitchen.data.firebase.FoodViewModel;
import com.example.kitchen.data.local.KitchenViewModel;
import com.example.kitchen.data.local.entities.Ware;
import com.example.kitchen.utility.CheckUtils;
import com.example.kitchen.utility.DeviceUtils;
import com.example.kitchen.widget.ShoppingListWidget;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseException;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShoppingAddActivity extends AppCompatActivity implements DeviceUtils.InternetConnectionListener {
    private static final String LOG_TAG = ShoppingAddActivity.class.getSimpleName();
    @BindView(R.id.btn_link_define_food) Button mAddFoodLink;
    @BindView(R.id.spinner_food) SearchableSpinner mFoodSpinner;
    @BindView(R.id.edit_text_amount) TextInputEditText mAmountEditText;
    @BindView(R.id.spinner_measure) Spinner mMeasurementSpinner;
    @BindView(R.id.btn_add_to_storage) Button mAddButton;
    private KitchenViewModel mKitchenViewModel;
    private Context mContext;
    private ArrayAdapter<CharSequence> mMeasurementAdapter;
    private ArrayList<String> mWares;
    private Map<String, String> mWareMap;
    private ArrayList<Ware> mWareList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_add);
        ButterKnife.bind(this);
        mContext = this;
        mKitchenViewModel = ViewModelProviders.of(this).get(KitchenViewModel.class);
        if (savedInstanceState == null) {
            DeviceUtils.startConnectionTest(this);
            mKitchenViewModel.getShoppingList().observe(this, new Observer<List<Ware>>() {
                @Override
                public void onChanged(@Nullable List<Ware> wares) {
                    mWareList = (ArrayList<Ware>) wares;
                    ShoppingListWidget.fillShoppingListWidget(mContext, wares);
                }
            });
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
                    mWares = new ArrayList<>();
                    mWareMap = new HashMap<>();
                    for (DataSnapshot foodSnapShot : dataSnapshot.getChildren()) {
                        try {
                            if (foodSnapShot.getValue() != null)
                                mWareMap.put(foodSnapShot.getKey(), foodSnapShot.getValue().toString());
                            mWares.add(foodSnapShot.getKey());
                        } catch (DatabaseException e) {
                            Log.e(LOG_TAG, e.getMessage());
                        }
                    }
                    ArrayAdapter<String> foodAdapter = new ArrayAdapter<>(mContext,
                            android.R.layout.simple_spinner_item, mWares);
                    foodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mFoodSpinner.setAdapter(foodAdapter);
                }
            }
        });
        mFoodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String food = mWares.get(position);
                float value = Float.valueOf(mWareMap.get(food));
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
                if (mWareList != null) {
                    for (Ware shown : mWareList) {
                        if (name.equals(shown.name) && amountType.equals(shown.amountType)) {
                            shownId = shown.id;
                            shownAmount = shown.amount;
                        }
                    }
                }
                if (shownId == 0) {
                    mKitchenViewModel.insertWare(
                            new Ware(name, amount, amountType));
                } else {
                    mKitchenViewModel.insertWare(
                            new Ware(shownId, name, amount + shownAmount, amountType));
                }
                DeviceUtils.hideKeyboardFrom(mContext, mAmountEditText);
                Snackbar.make(mAmountEditText,
                        String.format(getString(R.string.added_to_shopping_list), amount, amountType, name),
                        Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnectionResult(boolean success) {
        if (!success)
            Snackbar.make(mFoodSpinner, R.string.connect_internet_try_again, Snackbar.LENGTH_LONG).show();
    }
}
