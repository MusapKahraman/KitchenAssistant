/*
 * Reference
 * http://blog.teamtreehouse.com/contextual-action-bars-removing-items-recyclerview
 */
package com.example.kitchen.adapters;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.kitchen.R;
import com.example.kitchen.data.local.KitchenViewModel;
import com.example.kitchen.data.local.entities.Food;
import com.example.kitchen.utility.MeasurementUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StorageAdapter extends RecyclerView.Adapter<StorageAdapter.FoodViewHolder> {
    private final OnStorageClickListener mOnFoodClickListener;
    private final Context mContext;
    private ArrayList<Food> mSelectedFoods;
    private KitchenViewModel mKitchenViewModel;
    private FragmentActivity mFragmentActivity;
    private List<Food> mFoods;
    private List<Food> mFilteredFoods;
    private boolean mMultiSelect = false;
    private final ActionMode.Callback mActionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mMultiSelect = true;
            mFragmentActivity.getMenuInflater().inflate(R.menu.action_mode, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_mode_delete:
                    for (Food food : mSelectedFoods) {
                        int position = mFoods.indexOf(food);
                        mFoods.remove(food);
                        mKitchenViewModel.deleteFood(food);
                        notifyItemRemoved(position);
                    }
                    break;
            }
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mMultiSelect = false;
            mSelectedFoods.clear();
            notifyDataSetChanged();
        }
    };

    public StorageAdapter(Context context, OnStorageClickListener onFoodClickListener) {
        mSelectedFoods = new ArrayList<>();
        mContext = context;
        mOnFoodClickListener = onFoodClickListener;
        if (context instanceof FragmentActivity) {
            mFragmentActivity = (FragmentActivity) context;
            mKitchenViewModel = ViewModelProviders.of(mFragmentActivity).get(KitchenViewModel.class);
        }
    }

    @Override
    public int getItemCount() {
        if (mFilteredFoods != null) {
            return mFilteredFoods.size();
        } else {
            return 0;
        }
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        if (mFilteredFoods != null) {
            holder.bind(position);
        }
    }

    public void setFoods(List<Food> foods) {
        mFoods = foods;
        mFilteredFoods = foods;
        notifyDataSetChanged();
    }

    public ArrayList<Food> getSelectedFood() {
        return mSelectedFoods;
    }

    public void setSelectedFood(List<Food> foods) {
        if (foods != null && foods.size() > 0) {
            mSelectedFoods = (ArrayList<Food>) foods;
            notifyDataSetChanged();
            ((AppCompatActivity) mContext).startSupportActionMode(mActionModeCallbacks);
        }
    }

    public void filter(CharSequence charSequence) {
        if (charSequence == null || mFoods == null)
            return;
        String charString = charSequence.toString();
        if (!charString.isEmpty()) {
            List<Food> filteredList = new ArrayList<>();
            for (Food row : mFoods) {
                if (row.name.toLowerCase().contains(charString.toLowerCase())) {
                    filteredList.add(row);
                }
            }
            mFilteredFoods = filteredList;
            notifyDataSetChanged();
        } else {
            setFoods(mFoods);
        }
    }

    class FoodViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.food_item_tint) FrameLayout tint;
        @BindView(R.id.tv_ingredient) TextView foodTextView;
        @BindView(R.id.tv_ingredient_amount) TextView amountTextView;
        @BindView(R.id.tv_best_before) TextView bestBeforeTextView;

        private FoodViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bind(int position) {
            final Food current = mFilteredFoods.get(position);
            foodTextView.setText(current.name);
            String text = current.amount + " " + MeasurementUtils.getAbbreviation(mContext, current.amountType);
            amountTextView.setText(text);
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.setTimeInMillis(current.bestBefore);
            String dateFormat = "dd/MM/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
            bestBeforeTextView.setText(sdf.format(calendar.getTime()));
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ((AppCompatActivity) view.getContext()).startSupportActionMode(mActionModeCallbacks);
                    selectFood(current);
                    return true;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mMultiSelect) {
                        selectFood(current);
                    } else {
                        mOnFoodClickListener.onStorageItemClick(current);
                    }
                }
            });

            if (mSelectedFoods.contains(current)) {
                tint.setBackgroundColor(itemView.getResources().getColor(R.color.card_selected_tint));
            } else {
                tint.setBackgroundColor(itemView.getResources().getColor(R.color.card_normal_tint));
            }

        }

        void selectFood(Food food) {
            if (mMultiSelect) {
                if (mSelectedFoods.contains(food)) {
                    mSelectedFoods.remove(food);
                    tint.setBackgroundColor(itemView.getResources().getColor(R.color.card_normal_tint));
                } else {
                    mSelectedFoods.add(food);
                    tint.setBackgroundColor(itemView.getResources().getColor(R.color.card_selected_tint));
                }
            }
        }
    }
}
