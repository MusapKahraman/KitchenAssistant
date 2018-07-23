package com.example.kitchen.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.kitchen.R;
import com.example.kitchen.activities.MainActivity;
import com.example.kitchen.adapters.NotebookAdapter;
import com.example.kitchen.adapters.OnRecipeClickListener;
import com.example.kitchen.data.Recipe;
import com.example.kitchen.utility.KeyUtils;

import java.util.List;

public class NotebookFragment extends Fragment {

    private static final String TAG = NotebookFragment.class.getSimpleName();
    private static final String FIRST_ITEM = "state";
    private LinearLayoutManager mLayoutManager;
    private OnRecipeClickListener mClickListener;
    private int mFirstVisibleItemPos;
    private View mRootView;
    private NotebookAdapter mAdapter;

    public NotebookFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRecipeClickListener) {
            mClickListener = (OnRecipeClickListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + "must implement OnRecipeClickListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_recipes, container, false);
        RecyclerView recyclerView = mRootView.findViewById(R.id.rv_recipe_steps);
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        mAdapter = new NotebookAdapter(mClickListener);
        recyclerView.setAdapter(mAdapter);
        Bundle arguments = getArguments();
        if (arguments != null) {
            List<Recipe> recipes = arguments.getParcelableArrayList(KeyUtils.KEY_RECIPES);
            mAdapter.setRecipes(recipes);
            savedInstanceState = arguments.getBundle(KeyUtils.KEY_SAVED_STATE);
            if (savedInstanceState != null) {
                mFirstVisibleItemPos = savedInstanceState.getInt(FIRST_ITEM);
                mLayoutManager.scrollToPosition(mFirstVisibleItemPos);
            }
        }
        return mRootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        sendToActivity(outState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mClickListener = null;
        sendToActivity(new Bundle());
    }

    private void sendToActivity(Bundle outState) {
        mFirstVisibleItemPos = mLayoutManager.findFirstVisibleItemPosition();
        outState.putInt(FIRST_ITEM, mFirstVisibleItemPos);

        MainActivity activity = null;
        try {
            activity = (MainActivity) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, e.getMessage());
        }

        if (activity != null) {
            activity.fromNotebookFragment(outState);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Clear all current menu items
        menu.clear();
        // Add new menu items
        inflater.inflate(R.menu.menu_list_actions, menu);
        // Associate searchable configuration with the SearchView
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.filter(newText);
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.app_bar_filter:
                Snackbar.make(mRootView, "Filtering...", Snackbar.LENGTH_SHORT).show();
                return true;
            case R.id.app_bar_sort:
                Snackbar.make(mRootView, "Sorting...", Snackbar.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}