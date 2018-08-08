package com.example.kitchen.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.kitchen.R;
import com.example.kitchen.activities.FoodActivity;
import com.example.kitchen.adapters.IngredientsAdapter;
import com.example.kitchen.data.firebase.FoodViewModel;
import com.example.kitchen.data.local.KitchenViewModel;
import com.example.kitchen.data.local.entities.Ingredient;
import com.example.kitchen.data.local.entities.Recipe;
import com.example.kitchen.utility.AppConstants;
import com.example.kitchen.utility.CheckUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseException;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.List;

public class IngredientsFragment extends Fragment {
    private ArrayList<String> mFoods;
    private FragmentMessageListener mMessageListener;
    private Context mContext;
    private Recipe mRecipe;

    public IngredientsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof FragmentMessageListener) {
            mMessageListener = (FragmentMessageListener) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement FragmentMessageListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ingredients, container, false);

        Button addFoodLink = rootView.findViewById(R.id.tv_add_food_link);
        addFoodLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FoodActivity.class);
                startActivity(intent);
            }
        });

        if (savedInstanceState != null) {
            mRecipe = savedInstanceState.getParcelable(AppConstants.KEY_RECIPE);
        } else {
            Bundle arguments = getArguments();
            if (arguments != null) {
                mRecipe = arguments.getParcelable(AppConstants.KEY_RECIPE);
            }
        }

        RecyclerView recyclerView = rootView.findViewById(R.id.rv_ingredients);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        final IngredientsAdapter adapter = new IngredientsAdapter();
        recyclerView.setAdapter(adapter);

        final KitchenViewModel kitchenViewModel = ViewModelProviders.of(IngredientsFragment.this).get(KitchenViewModel.class);
        kitchenViewModel.getIngredientsByRecipe(mRecipe.id).observe(this, new Observer<List<Ingredient>>() {
            @Override
            public void onChanged(@Nullable List<Ingredient> ingredients) {
                adapter.setIngredients(ingredients);
            }
        });

        final SearchableSpinner foodSpinner = rootView.findViewById(R.id.spinner_food);
        foodSpinner.setTitle(getString(R.string.select_food));

        final FoodViewModel viewModel = ViewModelProviders.of(this).get(FoodViewModel.class);
        viewModel.getDataSnapshotLiveData().observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    mFoods = new ArrayList<>();
                    for (DataSnapshot foodSnapShot : dataSnapshot.getChildren()) {
                        try {
                            mFoods.add(foodSnapShot.getKey());
                            ArrayAdapter<String> foodAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, mFoods);
                            foodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            foodSpinner.setAdapter(foodAdapter);
                        } catch (DatabaseException e) {
                            Log.e("IngredientsFragment", e.getMessage());
                        }
                    }
                }
            }
        });

        final EditText amountEditText = rootView.findViewById(R.id.text_edit_amount);

        final Spinner measurementSpinner = rootView.findViewById(R.id.spinner_measure);
        ArrayAdapter<CharSequence> measurementAdapter = ArrayAdapter.createFromResource(mContext,
                R.array.measurement_array, android.R.layout.simple_spinner_item);
        measurementAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        measurementSpinner.setAdapter(measurementAdapter);

        Button addButton = rootView.findViewById(R.id.btn_add_ingredient);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodSpinner.getSelectedItem() == null)
                    return;
                String name = foodSpinner.getSelectedItem().toString();
                String amountType = measurementSpinner.getSelectedItem().toString();
                if (CheckUtils.isEmptyTextField(mContext, amountEditText))
                    return;
                int amount = Integer.valueOf(amountEditText.getText().toString());
                Ingredient ingredient = new Ingredient(mRecipe.id, name, amount, amountType);
                kitchenViewModel.insertIngredients(ingredient);
            }
        });
        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(AppConstants.KEY_RECIPE, mRecipe);
    }

    @Override
    public void onDetach() {
        mMessageListener = null;
        super.onDetach();
    }
}
