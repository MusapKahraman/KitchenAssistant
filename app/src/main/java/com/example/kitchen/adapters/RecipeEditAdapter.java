package com.example.kitchen.adapters;


import android.content.Context;
import android.os.Bundle;

import com.example.kitchen.R;
import com.example.kitchen.fragments.IngredientsFragment;
import com.example.kitchen.fragments.OverviewFragment;
import com.example.kitchen.fragments.StepsFragment;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class RecipeEditAdapter extends FragmentStatePagerAdapter {
    private final Bundle mBundle;
    private final Context mContext;

    public RecipeEditAdapter(Context context, FragmentManager fm, Bundle bundle) {
        super(fm);
        mContext = context;
        mBundle = bundle;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new OverviewFragment();
                break;
            case 1:
                fragment = new IngredientsFragment();
                break;
            case 2:
                fragment = new StepsFragment();
                break;
        }
        if (fragment != null)
            fragment.setArguments(mBundle);
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.overview);
            case 1:
                return mContext.getString(R.string.ingredients);
            case 2:
                return mContext.getString(R.string.instructions);
            default:
                return null;
        }
    }
}
