package com.example.kitchen.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.kitchen.R;
import com.example.kitchen.adapters.OnStorageClickListener;
import com.example.kitchen.adapters.StorageAdapter;
import com.example.kitchen.data.local.KitchenViewModel;
import com.example.kitchen.data.local.entities.Food;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class StorageFragment extends Fragment {
    private static final String KEY_LAYOUT_STATE = "state-key";
    private static final String KEY_SEARCH_QUERY = "search-query-key";
    private static final String KEY_FOOD_LIST = "food-list-key";
    private static final String KEY_SELECTED_FOOD = "selected-food-key";
    @BindView(R.id.rv_storage) RecyclerView mRecyclerView;
    private OnFragmentScrollListener fragmentScrollListener;
    private Context mContext;
    private OnStorageClickListener mClickListener;
    private LinearLayoutManager mLayoutManager;
    private StorageAdapter mAdapter;
    private String mQuery;
    private ArrayList<Food> mFoodList;

    public StorageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnStorageClickListener) {
            mClickListener = (OnStorageClickListener) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement OnStorageClickListener");
        }
        if (context instanceof OnFragmentScrollListener) {
            fragmentScrollListener = (OnFragmentScrollListener) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement OnFragmentScrollListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_storage, container, false);
        ButterKnife.bind(this, rootView);
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new StorageAdapter(mContext, mClickListener);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dx > 0 || dy > 0) {
                    fragmentScrollListener.onScrollDown();
                } else if (dx < 0 || dy < 0) {
                    fragmentScrollListener.onScrollUp();
                }
            }
        });
        if (savedInstanceState != null) {
            mQuery = savedInstanceState.getString(KEY_SEARCH_QUERY);
            mFoodList = savedInstanceState.getParcelableArrayList(KEY_FOOD_LIST);
            mAdapter.setFoods(mFoodList);
            mAdapter.filter(mQuery);
            List<Food> selected = savedInstanceState.getParcelableArrayList(KEY_SELECTED_FOOD);
            mAdapter.setSelectedFood(selected);
            mLayoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(KEY_LAYOUT_STATE));
        } else {
            KitchenViewModel kitchenViewModel = ViewModelProviders.of(this).get(KitchenViewModel.class);
            kitchenViewModel.getStorage().observe(this, new Observer<List<Food>>() {
                @Override
                public void onChanged(@Nullable List<Food> foods) {
                    if (foods != null) {
                        mFoodList = (ArrayList<Food>) foods;
                        mAdapter.setFoods(mFoodList);
                    }
                }
            });
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_SEARCH_QUERY, mQuery);
        outState.putParcelable(KEY_LAYOUT_STATE, mLayoutManager.onSaveInstanceState());
        outState.putParcelableArrayList(KEY_FOOD_LIST, mFoodList);
        outState.putParcelableArrayList(KEY_SELECTED_FOOD, mAdapter.getSelectedFood());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Clear all current menu items
        menu.clear();
        // Add new menu items
        inflater.inflate(R.menu.menu_recipes, menu);
        // Associate searchable configuration with the SearchView
        MenuItem searchMenuItem = menu.findItem(R.id.app_bar_search);
        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Return true to make it expand.
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Delete query when collapsed.
                mQuery = "";
                // Return true to make it collapse.
                return true;
            }
        });
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();
        if (!TextUtils.isEmpty(mQuery)) {
            searchMenuItem.expandActionView();
            searchView.setQuery(mQuery, true);
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.filter(query);
                mQuery = query;
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.filter(newText);
                mQuery = newText;
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
}