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
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.kitchen.R;
import com.example.kitchen.adapters.OnShoppingListClickListener;
import com.example.kitchen.adapters.ShoppingAdapter;
import com.example.kitchen.data.local.KitchenViewModel;
import com.example.kitchen.data.local.entities.Ware;
import com.example.kitchen.utility.MeasurementUtils;
import com.example.kitchen.widget.ShoppingListWidget;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShoppingFragment extends Fragment {
    private static final String KEY_LAYOUT_STATE = "state-key";
    private static final String KEY_SEARCH_QUERY = "search-query-key";
    private static final String KEY_SHOPPING_LIST = "shopping-list-key";
    private static final String KEY_SELECTED_WARES = "selected-wares-key";
    @BindView(R.id.rv_shopping_list) RecyclerView mRecyclerView;
    private OnFragmentScrollListener fragmentScrollListener;
    private Context mContext;
    private OnShoppingListClickListener mClickListener;
    private LinearLayoutManager mLayoutManager;
    private ShoppingAdapter mAdapter;
    private String mQuery;
    private ArrayList<Ware> mShoppingList;

    public ShoppingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnShoppingListClickListener) {
            mClickListener = (OnShoppingListClickListener) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement OnShoppingListClickListener");
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
        View rootView = inflater.inflate(R.layout.fragment_shopping, container, false);
        ButterKnife.bind(this, rootView);
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new ShoppingAdapter(mContext, mClickListener);
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
            mShoppingList = savedInstanceState.getParcelableArrayList(KEY_SHOPPING_LIST);
            mAdapter.setWares(mShoppingList);
            mAdapter.filter(mQuery);
            List<Ware> selected = savedInstanceState.getParcelableArrayList(KEY_SELECTED_WARES);
            mAdapter.setSelectedWares(selected);
            mLayoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(KEY_LAYOUT_STATE));
        } else {
            KitchenViewModel kitchenViewModel = ViewModelProviders.of(this).get(KitchenViewModel.class);
            kitchenViewModel.getShoppingList().observe(this, new Observer<List<Ware>>() {
                @Override
                public void onChanged(@Nullable List<Ware> wares) {
                    if (wares != null) {
                        mShoppingList = (ArrayList<Ware>) wares;
                        mAdapter.setWares(mShoppingList);
                        ShoppingListWidget.fillShoppingListWidget(mContext, wares);
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
        outState.putParcelableArrayList(KEY_SHOPPING_LIST, mShoppingList);
        outState.putParcelableArrayList(KEY_SELECTED_WARES, mAdapter.getSelectedWares());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Clear all current menu items
        menu.clear();
        // Add new menu items
        inflater.inflate(R.menu.menu_shopping_list, menu);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_item_share:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, generateShareString());
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String generateShareString() {
        StringBuilder result = new StringBuilder(getString(R.string.shopping_list));
        result.append("\n");
        for (Ware item : mShoppingList) {
            String text = item.amount +
                    " " + MeasurementUtils.getAbbreviation(mContext, item.amountType) +
                    " " + item.name;
            result.append("\n").append(text);
        }
        return result.toString();
    }
}