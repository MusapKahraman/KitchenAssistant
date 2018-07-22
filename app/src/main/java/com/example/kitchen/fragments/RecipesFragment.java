package com.example.kitchen.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.kitchen.R;
import com.example.kitchen.activities.MainActivity;
import com.example.kitchen.adapters.OnRecipeClickListener;
import com.example.kitchen.adapters.RecipesAdapter;
import com.example.kitchen.data.Recipe;
import com.example.kitchen.utility.KeyUtils;

import java.util.List;

public class RecipesFragment extends Fragment {

    private static final String TAG = RecipesFragment.class.getSimpleName();
    private static final String FIRST_ITEM = "state";
    private LinearLayoutManager mLayoutManager;
    private OnRecipeClickListener mClickListener;
    private int mFirstVisibleItemPos;

    public RecipesFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_recipes, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.rv_recipe_steps);
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        final RecipesAdapter adapter = new RecipesAdapter(mClickListener);
        recyclerView.setAdapter(adapter);
        Bundle arguments = getArguments();
        if (arguments != null) {
            List<Recipe> recipes = arguments.getParcelableArrayList(KeyUtils.KEY_RECIPES);
            adapter.setRecipes(recipes);
            savedInstanceState = arguments.getBundle(KeyUtils.KEY_SAVED_STATE);
            if (savedInstanceState != null) {
                mFirstVisibleItemPos = savedInstanceState.getInt(FIRST_ITEM);
                mLayoutManager.scrollToPosition(mFirstVisibleItemPos);
            }
        }

        return rootView;
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
            activity.fromRecipesFragment(outState);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Clear all current menu items
        menu.clear();
        // Add new menu items
        inflater.inflate(R.menu.recipes, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}