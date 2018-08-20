package com.example.kitchen.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.kitchen.R;
import com.example.kitchen.activities.FoodDefinitionActivity;
import com.example.kitchen.adapters.IngredientsAdapter;
import com.example.kitchen.adapters.RecyclerViewItemTouchHelper;
import com.example.kitchen.data.firebase.FoodViewModel;
import com.example.kitchen.data.local.KitchenViewModel;
import com.example.kitchen.data.local.entities.Ingredient;
import com.example.kitchen.data.local.entities.Recipe;
import com.example.kitchen.utility.AppConstants;
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

public class IngredientsFragment extends Fragment
        implements RecyclerViewItemTouchHelper.RecyclerItemTouchHelperListener {
    private static final String LOG_TAG = IngredientsFragment.class.getSimpleName();
    @BindView(R.id.rv_ingredients) RecyclerView mRecyclerView;
    @BindView(R.id.btn_link_define_food) Button mDefineFoodLink;
    @BindView(R.id.spinner_food) SearchableSpinner mFoodSpinner;
    @BindView(R.id.edit_text_amount) TextInputEditText mAmountEditText;
    @BindView(R.id.spinner_measure) Spinner mMeasurementSpinner;
    @BindView(R.id.btn_add_ingredient) Button mAddButton;
    private KitchenViewModel mKitchenViewModel;
    private Context mContext;
    private IngredientsAdapter mAdapter;
    private ArrayAdapter<CharSequence> mMeasurementAdapter;
    private Map<String, String> mFoodMap;
    private ArrayList<Ingredient> mIngredients;
    private ArrayList<String> mFoods;
    private Recipe mRecipe;

    public IngredientsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mKitchenViewModel = ViewModelProviders.of(this).get(KitchenViewModel.class);
        if (savedInstanceState != null) {
            mRecipe = savedInstanceState.getParcelable(AppConstants.KEY_RECIPE);
            mIngredients = savedInstanceState.getParcelableArrayList(AppConstants.KEY_INGREDIENTS);
        } else {
            Bundle arguments = getArguments();
            if (arguments != null) {
                mRecipe = arguments.getParcelable(AppConstants.KEY_RECIPE);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ingredients, container, false);
        ButterKnife.bind(this, rootView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerViewItemTouchHelper(
                0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new IngredientsAdapter(mContext);
        mRecyclerView.setAdapter(mAdapter);
        mFoodSpinner.setTitle(getString(R.string.select_food));
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DeviceUtils.hideKeyboardFrom(mContext, mAmountEditText);
        if (mRecipe.id != 0)
            mKitchenViewModel.getIngredientsByRecipe(mRecipe.id)
                    .observe(this, getIngredientsByRecipeObserver());
        // Fill in the food spinner with data from firebase server.
        FoodViewModel foodViewModel = ViewModelProviders.of(this).get(FoodViewModel.class);
        foodViewModel.getDataSnapshotLiveData()
                .observe(this, getFoodViewModelDataSnapshotObserver());
        mFoodSpinner.setOnItemSelectedListener(getFoodSpinnerOnItemSelectedListener());
        mDefineFoodLink.setOnClickListener(getDefineFoodLinkOnClickListener());
        mAddButton.setOnClickListener(getAddButtonOnClickListener());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(AppConstants.KEY_RECIPE, mRecipe);
        outState.putParcelableArrayList(AppConstants.KEY_INGREDIENTS, mIngredients);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof IngredientsAdapter.IngredientViewHolder) {
            // get the removed ingredient name to display it in snack bar
            String food = mIngredients.get(viewHolder.getAdapterPosition()).food;
            // backup of removed item for undo purpose
            final Ingredient deletedIngredient = mIngredients.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();
            // remove the item from recycler view
            mAdapter.removeItem(viewHolder.getAdapterPosition());
            mKitchenViewModel.deleteIngredient(deletedIngredient);
            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar.make(mRecyclerView,
                    String.format(getString(R.string.removed_ingredient), food), Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.undo, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // undo is selected, restore the deleted item
                    mAdapter.restoreItem(deletedIngredient, deletedIndex);
                    mKitchenViewModel.insertIngredient(deletedIngredient);
                }
            });
            snackbar.show();
        }
    }

    private View.OnClickListener getDefineFoodLinkOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FoodDefinitionActivity.class);
                startActivity(intent);
            }
        };
    }

    private View.OnClickListener getAddButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecipe.id == 0) {
                    Snackbar.make(mAddButton,
                            R.string.can_not_add_ingredient, Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (mFoodSpinner.getSelectedItem() == null) return;
                String name = mFoodSpinner.getSelectedItem().toString();
                String amountType = mMeasurementSpinner.getSelectedItem().toString();
                if (CheckUtils.isEmptyTextField(mContext, mAmountEditText)) return;
                int amount = Integer.valueOf(mAmountEditText.getText().toString());
                // If there is already an item in the list with the same name then just increase its amount.
                int shownId = 0;
                int shownAmount = 0;
                if (mIngredients != null) {
                    for (Ingredient shown : mIngredients) {
                        if (name.equals(shown.food) && amountType.equals(shown.amountType)) {
                            shownId = shown.id;
                            shownAmount = shown.amount;
                        }
                    }
                }
                if (shownId == 0) {
                    mKitchenViewModel.insertIngredient(
                            new Ingredient(mRecipe.id, name, amount, amountType, ""));
                } else {
                    mKitchenViewModel.insertIngredient(
                            new Ingredient(shownId, mRecipe.id, name, amount + shownAmount, amountType, ""));
                }
            }
        };
    }

    private AdapterView.OnItemSelectedListener getFoodSpinnerOnItemSelectedListener() {
        return new AdapterView.OnItemSelectedListener() {
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
        };
    }

    private Observer<DataSnapshot> getFoodViewModelDataSnapshotObserver() {
        return new Observer<DataSnapshot>() {
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
                    ArrayAdapter<String> foodAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, mFoods);
                    foodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mFoodSpinner.setAdapter(foodAdapter);
                }
            }
        };
    }

    private Observer<List<Ingredient>> getIngredientsByRecipeObserver() {
        return new Observer<List<Ingredient>>() {
            @Override
            public void onChanged(@Nullable List<Ingredient> ingredients) {
                mAdapter.setIngredients(ingredients);
                mIngredients = (ArrayList<Ingredient>) ingredients;
            }
        };
    }
}
