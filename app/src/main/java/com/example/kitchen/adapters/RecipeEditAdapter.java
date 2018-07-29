package com.example.kitchen.adapters;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.kitchen.R;
import com.example.kitchen.fragments.IngredientsFragment;
import com.example.kitchen.fragments.OverallFragment;
import com.example.kitchen.fragments.StepsFragment;

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
                fragment = new OverallFragment();
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
                return mContext.getString(R.string.directions);
            default:
                return null;
        }
    }
}
