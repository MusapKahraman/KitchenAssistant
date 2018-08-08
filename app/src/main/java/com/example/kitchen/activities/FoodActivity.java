package com.example.kitchen.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.kitchen.R;
import com.example.kitchen.data.firebase.FoodViewModel;
import com.example.kitchen.utility.CheckUtils;
import com.example.kitchen.utility.MeasurementUtils;

public class FoodActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

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

        Button addFoodButton = findViewById(R.id.btn_add_food);
        addFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckUtils.isEmptyTextField(FoodActivity.this, foodNameView))
                    return;
                String foodName = CheckUtils.validateTitle(foodNameView.getText().toString());
                foodNameView.setText(foodName);
                if (CheckUtils.isEmptyTextField(FoodActivity.this, volumeAmountView))
                    return;
                int volumeAmount = Integer.valueOf(volumeAmountView.getText().toString());
                if (CheckUtils.isEmptyTextField(FoodActivity.this, weightAmountView))
                    return;
                int weightAmount = Integer.valueOf(weightAmountView.getText().toString());
                int volumeType = volumeSpinner.getSelectedItemPosition();
                int weightType = weightSpinner.getSelectedItemPosition();
                float conversionMultiplier = MeasurementUtils.getConversionMultiplier(
                        FoodActivity.this, volumeAmount, volumeType, weightAmount, weightType);
                FoodViewModel viewModel = ViewModelProviders.of(FoodActivity.this).get(FoodViewModel.class);
                viewModel.addFood(foodName, conversionMultiplier);
                Snackbar.make(foodNameView,
                        String.format(getString(R.string.food_added), foodName), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

}