package com.example.kitchen.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TabHost;

import com.example.kitchen.R;
import com.example.kitchen.activities.MainActivity;
import com.example.kitchen.adapters.OnRecipeClickListener;
import com.example.kitchen.adapters.RecipesAdapter;
import com.example.kitchen.data.Recipe;
import com.example.kitchen.utility.KeyUtils;

import java.util.List;

public class MealBoardFragment extends Fragment {

    private static final String TAG = MealBoardFragment.class.getSimpleName();
    private static final String RV_STATE = "rv-state";
    private static final String TAB_STATE = "tab-state";
    private LinearLayoutManager mLayoutManager;
    private OnRecipeClickListener mClickListener;
    private TabHost mTabHost;
    private HorizontalScrollView mHorizontalScrollView;

    public MealBoardFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_meal_board, container, false);

        // Set tabs as week days.
        mHorizontalScrollView = rootView.findViewById(R.id.horizontalScrollView);
        mTabHost = rootView.findViewById(R.id.tab_host);
        mTabHost.setup();
        mTabHost.addTab(mTabHost.newTabSpec("mon")
                .setIndicator(getString(R.string.monday))
                .setContent(R.id.rv_tab_content));

        mTabHost.addTab(mTabHost.newTabSpec("tue")
                .setIndicator(getString(R.string.tuesday))
                .setContent(R.id.rv_tab_content));

        mTabHost.addTab(mTabHost.newTabSpec("wed")
                .setIndicator(getString(R.string.wednesday))
                .setContent(R.id.rv_tab_content));

        mTabHost.addTab(mTabHost.newTabSpec("thu")
                .setIndicator(getString(R.string.thursday))
                .setContent(R.id.rv_tab_content));

        mTabHost.addTab(mTabHost.newTabSpec("fri")
                .setIndicator(getString(R.string.friday))
                .setContent(R.id.rv_tab_content));

        mTabHost.addTab(mTabHost.newTabSpec("sat")
                .setIndicator(getString(R.string.saturday))
                .setContent(R.id.rv_tab_content));

        mTabHost.addTab(mTabHost.newTabSpec("sun")
                .setIndicator(getString(R.string.sunday))
                .setContent(R.id.rv_tab_content));

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                scrollToSelectedTab();
            }
        });

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mHorizontalScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    Log.v(TAG, "oldScrollX: " + oldScrollX + "newScrollX : " + scrollX);
                }
            });
        }

        // Refresh layout by switching days back and forth. Without doing this, first tab shows empty.
        mTabHost.setCurrentTab(1);
        mTabHost.setCurrentTab(0);

        // Dummy view to fill tabs.
        RecyclerView recyclerView = rootView.findViewById(R.id.rv_tab_content);
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        RecipesAdapter adapter = new RecipesAdapter(mClickListener);
        recyclerView.setAdapter(adapter);
        Bundle arguments = getArguments();
        if (arguments != null) {
            List<Recipe> recipes = arguments.getParcelableArrayList(KeyUtils.KEY_RECIPES);
            adapter.setRecipes(recipes);
            savedInstanceState = arguments.getBundle(KeyUtils.KEY_SAVED_STATE);
            if (savedInstanceState != null) {
                int tabIndex = savedInstanceState.getInt(TAB_STATE);
                mTabHost.setCurrentTab(tabIndex);
                mHorizontalScrollView.setScrollX(getScrollX(tabIndex));
                mLayoutManager.scrollToPosition(savedInstanceState.getInt(RV_STATE));
            }
        }
        return rootView;
    }

    /**
     * Scroll tab widget horizontally to center selected tab header.
     */
    private void scrollToSelectedTab() {
        mHorizontalScrollView.smoothScrollTo(getScrollX(mTabHost.getCurrentTab()), 0);
    }

    private int getScrollX(int tabIndex) {
        DisplayMetrics metrics = new DisplayMetrics();
        if (getActivity() != null)
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        View scrollTarget = mTabHost.getTabWidget().getChildAt(tabIndex);
        int headerFixedWidth = scrollTarget.getWidth();
        int targetX = (int) scrollTarget.getX();
        int currentScrollX = mHorizontalScrollView.getScrollX();
        int deltaX = (int) (targetX - currentScrollX + headerFixedWidth * 0.5 - screenWidth * 0.5);
        return currentScrollX + deltaX;
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
        outState.putInt(RV_STATE, mLayoutManager.findFirstVisibleItemPosition());
        outState.putInt(TAB_STATE, mTabHost.getCurrentTab());

        MainActivity activity = null;
        try {
            activity = (MainActivity) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, e.getMessage());
        }

        if (activity != null) {
            activity.fromMealBoardFragment(outState);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Clear all current menu items
        menu.clear();
        // Add new menu items
        inflater.inflate(R.menu.menu_meal_board, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}