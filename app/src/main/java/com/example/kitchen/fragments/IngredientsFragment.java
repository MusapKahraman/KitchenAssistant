package com.example.kitchen.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.kitchen.R;
import com.example.kitchen.activities.FoodActivity;
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
import java.util.List;

public class IngredientsFragment extends Fragment implements RecyclerViewItemTouchHelper.RecyclerItemTouchHelperListener {
    private ArrayList<String> mFoods;
    private FragmentMessageListener mMessageListener;
    private Context mContext;
    private Recipe mRecipe;
    private KitchenViewModel mViewModel;
    private RecyclerView mRecyclerView;
    private IngredientsAdapter mAdapter;
    private ArrayList<Ingredient> mIngredients;

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
            mIngredients = savedInstanceState.getParcelableArrayList(AppConstants.KEY_INGREDIENTS);
        } else {
            Bundle arguments = getArguments();
            if (arguments != null) {
                mRecipe = arguments.getParcelable(AppConstants.KEY_RECIPE);
            }
        }

        mRecyclerView = rootView.findViewById(R.id.rv_ingredients);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerViewItemTouchHelper(
                0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new IngredientsAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mViewModel = ViewModelProviders.of(IngredientsFragment.this).get(KitchenViewModel.class);
        mViewModel.getIngredientsByRecipe(mRecipe.id).observe(this, new Observer<List<Ingredient>>() {
            @Override
            public void onChanged(@Nullable List<Ingredient> ingredients) {
                mAdapter.setIngredients(ingredients);
                mIngredients = (ArrayList<Ingredient>) ingredients;
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
                IngredientsFragment.this.mViewModel.insertIngredients(ingredient);
                DeviceUtils.hideKeyboardFrom(mContext, amountEditText);
            }
        });
        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(AppConstants.KEY_RECIPE, mRecipe);
        outState.putParcelableArrayList(AppConstants.KEY_INGREDIENTS, mIngredients);
    }

    @Override
    public void onDetach() {
        mMessageListener = null;
        super.onDetach();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof IngredientsAdapter.IngredientViewHolder) {
            // get the removed item id to display it in snack bar
            String food = mIngredients.get(viewHolder.getAdapterPosition()).food;

            // backup of removed item for undo purpose
            final Ingredient deletedIngredient = mIngredients.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            mAdapter.removeItem(viewHolder.getAdapterPosition());
            mViewModel.deleteIngredients(deletedIngredient);

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar.make(mRecyclerView, String.format(getString(R.string.removed_ingredient), food), Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.undo, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // undo is selected, restore the deleted item
                    mAdapter.restoreItem(deletedIngredient, deletedIndex);
                    mViewModel.insertIngredients(deletedIngredient);
                }
            });
            //snackbar.setActionTextColor(getResources().getColor(R.color.accent));
            snackbar.show();
        }
    }
}
