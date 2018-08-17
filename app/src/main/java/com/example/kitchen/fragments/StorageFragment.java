package com.example.kitchen.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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

import com.example.kitchen.R;
import com.example.kitchen.adapters.OnFoodClickListener;
import com.example.kitchen.adapters.StorageAdapter;
import com.example.kitchen.data.local.KitchenViewModel;
import com.example.kitchen.data.local.entities.Food;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StorageFragment extends Fragment implements OnFoodClickListener {
    private static final String LOG_TAG = StorageFragment.class.getSimpleName();
    @BindView(R.id.rv_storage) RecyclerView mRecyclerView;
    private Context mContext;
    private LinearLayoutManager mLayoutManager;
    private StorageAdapter mAdapter;

    public StorageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_storage, container, false);
        ButterKnife.bind(this, rootView);
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new StorageAdapter(mContext, this);
        mRecyclerView.setAdapter(mAdapter);
        KitchenViewModel kitchenViewModel = ViewModelProviders.of(this).get(KitchenViewModel.class);
        kitchenViewModel.getStorage().observe(this, new Observer<List<Food>>() {
            @Override
            public void onChanged(@Nullable List<Food> foods) {
                if (foods != null) mAdapter.setFoods(foods);
            }
        });
        return rootView;
    }

    @Override
    public void onFoodClick(Food food) {
        Log.v(LOG_TAG, "Clicked on: " + food.food);
    }
}
