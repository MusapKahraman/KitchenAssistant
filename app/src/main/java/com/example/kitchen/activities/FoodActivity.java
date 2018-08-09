package com.example.kitchen.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kitchen.R;
import com.example.kitchen.data.firebase.FoodViewModel;
import com.example.kitchen.utility.CheckUtils;
import com.example.kitchen.utility.MeasurementUtils;

public class FoodActivity extends AppCompatActivity {
    private FoodViewModel mFoodViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        mFoodViewModel = ViewModelProviders.of(FoodActivity.this).get(FoodViewModel.class);

        final EditText foodNameView = findViewById(R.id.text_edit_food_name);
        final EditText volumeAmountView = findViewById(R.id.text_edit_volume_amount);
        final EditText weightAmountView = findViewById(R.id.text_edit_weight_amount);

        final Spinner volumeSpinner = findViewById(R.id.spinner_volume);
        ArrayAdapter<CharSequence> volumeAdapter = ArrayAdapter.createFromResource(this,
                R.array.volume_array, android.R.layout.simple_spinner_item);
        volumeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        volumeSpinner.setAdapter(volumeAdapter);

        final Spinner weightSpinner = findViewById(R.id.spinner_weight);
        ArrayAdapter<CharSequence> weightAdapter = ArrayAdapter.createFromResource(this,
                R.array.weight_array, android.R.layout.simple_spinner_item);
        weightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weightSpinner.setAdapter(weightAdapter);

        final TextView equalsLabel = findViewById(R.id.tv_equals);

        final CheckBox countableCheckBox = findViewById(R.id.check_countable);
        countableCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    volumeAmountView.setVisibility(View.GONE);
                    weightAmountView.setVisibility(View.GONE);
                    volumeSpinner.setVisibility(View.GONE);
                    weightSpinner.setVisibility(View.GONE);
                    equalsLabel.setVisibility(View.GONE);
                } else {
                    volumeAmountView.setVisibility(View.VISIBLE);
                    weightAmountView.setVisibility(View.VISIBLE);
                    volumeSpinner.setVisibility(View.VISIBLE);
                    weightSpinner.setVisibility(View.VISIBLE);
                    equalsLabel.setVisibility(View.VISIBLE);
                }
            }
        });

        Button addFoodButton = findViewById(R.id.btn_add_food);
        addFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckUtils.isEmptyTextField(FoodActivity.this, foodNameView))
                    return;
                String foodName = CheckUtils.validateTitle(foodNameView.getText().toString());
                foodNameView.setText(foodName);
                float conversionMultiplier = 0;
                if (!countableCheckBox.isChecked()) {
                    int volumeAmount = CheckUtils.getValidNumberFromField(FoodActivity.this, volumeAmountView);
                    if (volumeAmount == 0)
                        return;
                    int weightAmount = CheckUtils.getValidNumberFromField(FoodActivity.this, weightAmountView);
                    if (weightAmount == 0)
                        return;
                    int volumeType = volumeSpinner.getSelectedItemPosition();
                    int weightType = weightSpinner.getSelectedItemPosition();
                    conversionMultiplier = MeasurementUtils.getConversionMultiplier(
                            FoodActivity.this, volumeAmount, volumeType, weightAmount, weightType);
                }
                mFoodViewModel.addFood(foodName, conversionMultiplier);
                Snackbar.make(foodNameView,
                        String.format(getString(R.string.food_added), foodName), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

}