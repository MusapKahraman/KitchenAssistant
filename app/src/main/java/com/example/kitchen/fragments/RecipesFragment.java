/*
 * Reference
 * https://stackoverflow.com/questions/13626756/how-can-i-get-onbackpressed-while-searchview-is-activated/22730635#22730635
 */

package com.example.kitchen.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.kitchen.R;
import com.example.kitchen.adapters.OnRecipeClickListener;
import com.example.kitchen.adapters.RecipesAdapter;
import com.example.kitchen.data.local.entities.Recipe;
import com.example.kitchen.utility.AppConstants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipesFragment extends Fragment {
    private static final String LOG_TAG = RecipesFragment.class.getSimpleName();
    private static final String LAYOUT_STATE = "state";
    private static final String SEARCH_QUERY = "search-query";
    @BindView(R.id.rv_recipe_steps) RecyclerView mRecyclerView;
    private OnFragmentScrollListener fragmentScrollListener;
    private OnRecipeClickListener mClickListener;
    private StaggeredGridLayoutManager mLayoutManager;
    private RecipesAdapter mAdapter;
    private String mQuery;

    public RecipesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRecipeClickListener) {
            mClickListener = (OnRecipeClickListener) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement OnRecipeClickListener");
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
        View rootView = inflater.inflate(R.layout.fragment_recipes, container, false);
        ButterKnife.bind(this, rootView);
        if (getResources().getBoolean(R.bool.landscape)) {
            mLayoutManager = new StaggeredGridLayoutManager(1, RecyclerView.HORIZONTAL);
        } else {
            mLayoutManager = new StaggeredGridLayoutManager(1, RecyclerView.VERTICAL);
        }
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new RecipesAdapter(mClickListener);
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
        Bundle arguments = getArguments();
        if (arguments != null) {
            List<Recipe> recipes = arguments.getParcelableArrayList(AppConstants.KEY_RECIPES);
            mAdapter.setRecipes(recipes);
        }
        if (savedInstanceState != null) {
            mQuery = savedInstanceState.getString(SEARCH_QUERY);
            mAdapter.filter(mQuery);
            mLayoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(LAYOUT_STATE));
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SEARCH_QUERY, mQuery);
        outState.putParcelable(LAYOUT_STATE, mLayoutManager.onSaveInstanceState());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mClickListener = null;
        fragmentScrollListener = null;
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