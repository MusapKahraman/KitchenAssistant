/*
 * Reference
 * https://stackoverflow.com/questions/13626756/how-can-i-get-onbackpressed-while-searchview-is-activated/22730635#22730635
 */

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
import com.example.kitchen.adapters.BookmarksAdapter;
import com.example.kitchen.adapters.OnRecipeClickListener;
import com.example.kitchen.data.local.entities.Recipe;
import com.example.kitchen.utility.AppConstants;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BookmarksFragment extends Fragment {
    private static final String KEY_LAYOUT_STATE = "state-key";
    private static final String KEY_SEARCH_QUERY = "search-query-key";
    private static final String KEY_SELECTED_RECIPES = "selected-recipes-key";
    @BindView(R.id.rv_recipe_steps) RecyclerView mRecyclerView;
    private Context mContext;
    private OnFragmentScrollListener mScrollListener;
    private OnRecipeClickListener mClickListener;
    private StaggeredGridLayoutManager mLayoutManager;
    private BookmarksAdapter mAdapter;
    private String mQuery;

    public BookmarksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnRecipeClickListener) {
            mClickListener = (OnRecipeClickListener) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement OnRecipeClickListener");
        }
        if (context instanceof OnFragmentScrollListener) {
            mScrollListener = (OnFragmentScrollListener) context;
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
        mAdapter = new BookmarksAdapter(mContext, mClickListener);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dx > 0 || dy > 0) {
                    mScrollListener.onScrollDown();
                } else if (dx < 0 || dy < 0) {
                    mScrollListener.onScrollUp();
                }
            }
        });
        Bundle arguments = getArguments();
        if (arguments != null) {
            List<Recipe> recipes = arguments.getParcelableArrayList(AppConstants.KEY_RECIPES);
            mAdapter.setRecipes(recipes);
        }
        if (savedInstanceState != null) {
            mQuery = savedInstanceState.getString(KEY_SEARCH_QUERY);
            mAdapter.filter(mQuery);
            List<Recipe> selected = savedInstanceState.getParcelableArrayList(KEY_SELECTED_RECIPES);
            mAdapter.setSelectedRecipes(selected);
            mLayoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(KEY_LAYOUT_STATE));
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_LAYOUT_STATE, mLayoutManager.onSaveInstanceState());
        outState.putString(KEY_SEARCH_QUERY, mQuery);
        outState.putParcelableArrayList(KEY_SELECTED_RECIPES, mAdapter.getSelectedRecipes());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mClickListener = null;
        mScrollListener = null;
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