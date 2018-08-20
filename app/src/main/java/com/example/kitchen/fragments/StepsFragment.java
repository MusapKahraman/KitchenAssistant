package com.example.kitchen.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.kitchen.R;
import com.example.kitchen.adapters.OnStepClickListener;
import com.example.kitchen.adapters.RecyclerViewItemTouchHelper;
import com.example.kitchen.adapters.StepsAdapter;
import com.example.kitchen.data.local.KitchenViewModel;
import com.example.kitchen.data.local.entities.Recipe;
import com.example.kitchen.data.local.entities.Step;
import com.example.kitchen.utility.AppConstants;
import com.example.kitchen.utility.CheckUtils;
import com.example.kitchen.utility.DeviceUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepsFragment extends Fragment
        implements RecyclerViewItemTouchHelper.RecyclerItemTouchHelperListener, OnStepClickListener {
    private static final String KEY_STEP_NUMBER = "step-number-key";
    @BindView(R.id.rv_steps) RecyclerView mRecyclerView;
    @BindView(R.id.text_edit_instruction) TextInputEditText mInstructionText;
    @BindView(R.id.btn_add_instruction) Button addButton;
    private Recipe mRecipe;
    private ArrayList<Step> mSteps;
    private KitchenViewModel mKitchenViewModel;
    private StepsAdapter mAdapter;
    private Context mContext;
    private int mStepNumber;

    public StepsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_steps, container, false);
        ButterKnife.bind(this, rootView);
        mKitchenViewModel = ViewModelProviders.of(this).get(KitchenViewModel.class);
        if (savedInstanceState != null) {
            mRecipe = savedInstanceState.getParcelable(AppConstants.KEY_RECIPE);
            mSteps = savedInstanceState.getParcelableArrayList(AppConstants.KEY_STEPS);
            mStepNumber = savedInstanceState.getInt(KEY_STEP_NUMBER);
        } else {
            Bundle arguments = getArguments();
            if (arguments != null) {
                mRecipe = arguments.getParcelable(AppConstants.KEY_RECIPE);
            }
        }
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerViewItemTouchHelper(
                0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new StepsAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mKitchenViewModel.getStepsByRecipe(mRecipe.id).observe(this, new Observer<List<Step>>() {
            @Override
            public void onChanged(@Nullable List<Step> steps) {
                mAdapter.setSteps(steps);
                mSteps = (ArrayList<Step>) steps;
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckUtils.isEmptyTextField(mContext, mInstructionText))
                    return;
                String instruction = mInstructionText.getText().toString();
                // If there is already an item in the list with the same step number then update that item.
                int shownId = 0;
                for (Step shown : mSteps) {
                    if (mStepNumber == shown.stepNumber) {
                        shownId = shown.id;
                    }
                }
                if (shownId == 0) {
                    mKitchenViewModel.insertStep(new Step(instruction, mSteps.size() + 1, mRecipe.id, ""));
                } else {
                    mKitchenViewModel.insertStep(new Step(shownId, instruction, mStepNumber, mRecipe.id, ""));
                }
                DeviceUtils.hideKeyboardFrom(mContext, mInstructionText);
                mStepNumber = 0;
                mInstructionText.setText("");
            }
        });
        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(AppConstants.KEY_RECIPE, mRecipe);
        outState.putParcelableArrayList(AppConstants.KEY_STEPS, mSteps);
        outState.putInt(KEY_STEP_NUMBER, mStepNumber);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof StepsAdapter.StepViewHolder) {
            // get the removed step number to display it in snack bar
            int stepNumber = mSteps.get(viewHolder.getAdapterPosition()).stepNumber;
            // backup of removed item for undo purpose
            final Step deletedStep = mSteps.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();
            // remove the item from recycler view
            mAdapter.removeItem(viewHolder.getAdapterPosition());
            mKitchenViewModel.deleteStep(deletedStep);
            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar.make(mRecyclerView,
                    String.format(getString(R.string.removed_step), stepNumber), Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.undo, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // undo is selected, restore the deleted item
                    mAdapter.restoreItem(deletedStep, deletedIndex);
                    mKitchenViewModel.insertStep(deletedStep);
                }
            });
            snackbar.show();
        }
    }

    @Override
    public void onStepClick(Step step) {
        mInstructionText.setText(step.instruction);
        mStepNumber = step.stepNumber;
    }
}
