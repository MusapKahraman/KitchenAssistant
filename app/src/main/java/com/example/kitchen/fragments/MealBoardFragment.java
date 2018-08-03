package com.example.kitchen.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;

import com.example.kitchen.R;
import com.example.kitchen.activities.MainActivity;
import com.example.kitchen.adapters.ExpandableListAdapter;
import com.example.kitchen.data.DataPlaceholders;
import com.example.kitchen.utility.AppConstants;

public class MealBoardFragment extends Fragment {
    private static final String TAG = MealBoardFragment.class.getSimpleName();
    private static final String TAB_LAST = "tab-last_opened";
    private static final String TAB_MONDAY = "tab-monday";
    private static final String TAB_TUESDAY = "tab-tuesday";
    private static final String TAB_WEDNESDAY = "tab-wednesday";
    private static final String TAB_THURSDAY = "tab-thursday";
    private static final String TAB_FRIDAY = "tab-friday";
    private static final String TAB_SATURDAY = "tab-saturday";
    private static final String TAB_SUNDAY = "tab-sunday";
    private View mRootView;
    private HorizontalScrollView mHorizontalScrollView;
    private TabHost mTabHost;
    private ExpandableListView mMonday;
    private ExpandableListView mTuesday;
    private ExpandableListView mWednesday;
    private ExpandableListView mThursday;
    private ExpandableListView mFriday;
    private ExpandableListView mSaturday;
    private ExpandableListView mSunday;

    public MealBoardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_meal_board, container, false);
        // Set tabs as week days.
        mHorizontalScrollView = mRootView.findViewById(R.id.horizontalScrollView);
        mTabHost = mRootView.findViewById(R.id.tab_host);
        mTabHost.setup();
        setWeekDayTabs();
        ExpandableListAdapter adapter = new ExpandableListAdapter(getContext(),
                DataPlaceholders.getGroups(), DataPlaceholders.getChildren());
        mMonday = mRootView.findViewById(R.id.tab_0);
        mMonday.setAdapter(adapter);
        mTuesday = mRootView.findViewById(R.id.tab_1);
        mTuesday.setAdapter(adapter);
        mWednesday = mRootView.findViewById(R.id.tab_2);
        mWednesday.setAdapter(adapter);
        mThursday = mRootView.findViewById(R.id.tab_3);
        mThursday.setAdapter(adapter);
        mFriday = mRootView.findViewById(R.id.tab_4);
        mFriday.setAdapter(adapter);
        mSaturday = mRootView.findViewById(R.id.tab_5);
        mSaturday.setAdapter(adapter);
        mSunday = mRootView.findViewById(R.id.tab_6);
        mSunday.setAdapter(adapter);

        // Set colors of tab titles
        for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
            TextView tv = mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(getResources().getColor(R.color.tab_text));
        }

        Bundle arguments = getArguments();
        if (arguments != null) {
            savedInstanceState = arguments.getBundle(AppConstants.KEY_SAVED_STATE);
            if (savedInstanceState != null) {
                mMonday.onRestoreInstanceState(savedInstanceState.getParcelable(TAB_MONDAY));
                mTuesday.onRestoreInstanceState(savedInstanceState.getParcelable(TAB_TUESDAY));
                mWednesday.onRestoreInstanceState(savedInstanceState.getParcelable(TAB_WEDNESDAY));
                mThursday.onRestoreInstanceState(savedInstanceState.getParcelable(TAB_THURSDAY));
                mFriday.onRestoreInstanceState(savedInstanceState.getParcelable(TAB_FRIDAY));
                mSaturday.onRestoreInstanceState(savedInstanceState.getParcelable(TAB_SATURDAY));
                mSunday.onRestoreInstanceState(savedInstanceState.getParcelable(TAB_SUNDAY));
                mTabHost.setCurrentTab(savedInstanceState.getInt(TAB_LAST));
            }
        }

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                scrollToSelectedTab();
            }
        });

        mMonday.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                Snackbar.make(mRootView, DataPlaceholders.getGroups().get(groupPosition)
                        + " Expanded", Snackbar.LENGTH_SHORT).show();
            }
        });

        mMonday.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                Snackbar.make(mRootView, DataPlaceholders.getGroups().get(groupPosition)
                        + " Collapsed", Snackbar.LENGTH_SHORT).show();
            }
        });

        mMonday.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Snackbar.make(mRootView,
                        DataPlaceholders.getGroups().get(groupPosition) + " : "
                                + DataPlaceholders.getChildren()
                                .get(DataPlaceholders.getGroups().get(groupPosition))
                                .get(childPosition),
                        Snackbar.LENGTH_SHORT).show();
                return false;
            }
        });

        return mRootView;
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

    private void sendToActivity(Bundle outState) {
        outState.putInt(TAB_LAST, mTabHost.getCurrentTab());
        outState.putParcelable(TAB_MONDAY, mMonday.onSaveInstanceState());
        outState.putParcelable(TAB_TUESDAY, mTuesday.onSaveInstanceState());
        outState.putParcelable(TAB_WEDNESDAY, mWednesday.onSaveInstanceState());
        outState.putParcelable(TAB_THURSDAY, mThursday.onSaveInstanceState());
        outState.putParcelable(TAB_FRIDAY, mFriday.onSaveInstanceState());
        outState.putParcelable(TAB_SATURDAY, mSaturday.onSaveInstanceState());
        outState.putParcelable(TAB_SUNDAY, mSunday.onSaveInstanceState());

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
        int id = item.getItemId();
        switch (id) {
            case R.id.app_bar_auto_complete:
                Snackbar.make(mRootView, "Magic happening...", Snackbar.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setWeekDayTabs() {
        mTabHost.addTab(mTabHost.newTabSpec("mon")
                .setIndicator(getString(R.string.monday))
                .setContent(R.id.tab_0));
        mTabHost.addTab(mTabHost.newTabSpec("tue")
                .setIndicator(getString(R.string.tuesday))
                .setContent(R.id.tab_1));
        mTabHost.addTab(mTabHost.newTabSpec("wed")
                .setIndicator(getString(R.string.wednesday))
                .setContent(R.id.tab_2));
        mTabHost.addTab(mTabHost.newTabSpec("thu")
                .setIndicator(getString(R.string.thursday))
                .setContent(R.id.tab_3));
        mTabHost.addTab(mTabHost.newTabSpec("fri")
                .setIndicator(getString(R.string.friday))
                .setContent(R.id.tab_4));
        mTabHost.addTab(mTabHost.newTabSpec("sat")
                .setIndicator(getString(R.string.saturday))
                .setContent(R.id.tab_5));
        mTabHost.addTab(mTabHost.newTabSpec("sun")
                .setIndicator(getString(R.string.sunday))
                .setContent(R.id.tab_6));
    }
}