package com.example.kitchen.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.HorizontalScrollView;
import android.widget.TabHost;
import android.widget.Toast;

import com.example.kitchen.R;
import com.example.kitchen.activities.MainActivity;
import com.example.kitchen.adapters.ExpandableListAdapter;
import com.example.kitchen.utility.KeyUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MealBoardFragment extends Fragment {

    private static final String TAG = MealBoardFragment.class.getSimpleName();
    private static final String TAB_STATE = "tab-state";
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private TabHost mTabHost;
    private HorizontalScrollView mHorizontalScrollView;

    public MealBoardFragment() {
        // Required empty public constructor
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*
        if (context instanceof OnRecipeClickListener) {
        } else {
            throw new ClassCastException(context.toString()
                    + "must implement OnRecipeClickListener");
        }
        */
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
                .setContent(R.id.elv_tab_content));

        mTabHost.addTab(mTabHost.newTabSpec("tue")
                .setIndicator(getString(R.string.tuesday))
                .setContent(R.id.elv_tab_content));

        mTabHost.addTab(mTabHost.newTabSpec("wed")
                .setIndicator(getString(R.string.wednesday))
                .setContent(R.id.elv_tab_content));

        mTabHost.addTab(mTabHost.newTabSpec("thu")
                .setIndicator(getString(R.string.thursday))
                .setContent(R.id.elv_tab_content));

        mTabHost.addTab(mTabHost.newTabSpec("fri")
                .setIndicator(getString(R.string.friday))
                .setContent(R.id.elv_tab_content));

        mTabHost.addTab(mTabHost.newTabSpec("sat")
                .setIndicator(getString(R.string.saturday))
                .setContent(R.id.elv_tab_content));

        mTabHost.addTab(mTabHost.newTabSpec("sun")
                .setIndicator(getString(R.string.sunday))
                .setContent(R.id.elv_tab_content));

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

        // get the list view
        ExpandableListView expListView = rootView.findViewById(R.id.elv_tab_content);
        // prepare list data
        prepareListData();
        // set list adapter
        ExpandableListAdapter listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });

        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getContext(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });

        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getContext(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();

            }
        });

        Bundle arguments = getArguments();
        if (arguments != null) {
            savedInstanceState = arguments.getBundle(KeyUtils.KEY_SAVED_STATE);
            if (savedInstanceState != null) {
                int tabIndex = savedInstanceState.getInt(TAB_STATE);
                mTabHost.setCurrentTab(tabIndex);
                mHorizontalScrollView.setScrollX(getScrollX(tabIndex));
            }
        }
        return rootView;
    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        // Adding child data
        listDataHeader.add("Top 250");
        listDataHeader.add("Now Showing");
        listDataHeader.add("Coming Soon..");

        // Adding child data
        List<String> top250 = new ArrayList<>();
        top250.add("The Shawshank Redemption");
        top250.add("The Godfather");
        top250.add("The Godfather: Part II");
        top250.add("Pulp Fiction");
        top250.add("The Good, the Bad and the Ugly");
        top250.add("The Dark Knight");
        top250.add("12 Angry Men");

        List<String> nowShowing = new ArrayList<>();
        nowShowing.add("The Conjuring");
        nowShowing.add("Despicable Me 2");
        nowShowing.add("Turbo");
        nowShowing.add("Grown Ups 2");
        nowShowing.add("Red 2");
        nowShowing.add("The Wolverine");

        List<String> comingSoon = new ArrayList<>();
        comingSoon.add("2 Guns");
        comingSoon.add("The Smurfs 2");
        comingSoon.add("The Spectacular Now");
        comingSoon.add("The Canyons");
        comingSoon.add("Europa Report");

        listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
        listDataChild.put(listDataHeader.get(1), nowShowing);
        listDataChild.put(listDataHeader.get(2), comingSoon);
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
        sendToActivity(new Bundle());
    }

    private void sendToActivity(Bundle outState) {
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

    @SuppressWarnings("EmptyMethod")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}