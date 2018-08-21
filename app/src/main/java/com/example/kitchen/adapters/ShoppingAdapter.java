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
import com.example.kitchen.data.local.entities.Ware;
import com.example.kitchen.utility.MeasurementUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.WareViewHolder> {
    private final OnShoppingListClickListener mOnWareClickListener;
    private final Context mContext;
    private ArrayList<Ware> mSelectedWares;
    private KitchenViewModel mKitchenViewModel;
    private FragmentActivity mFragmentActivity;
    private List<Ware> mWares;
    private List<Ware> mFilteredWares;
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
                    for (Ware ware : mSelectedWares) {
                        int position = mWares.indexOf(ware);
                        mWares.remove(ware);
                        mKitchenViewModel.deleteWare(ware);
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
            mSelectedWares.clear();
            notifyDataSetChanged();
        }
    };

    public ShoppingAdapter(Context context, OnShoppingListClickListener onWareClickListener) {
        mSelectedWares = new ArrayList<>();
        mContext = context;
        mOnWareClickListener = onWareClickListener;
        if (context instanceof FragmentActivity) {
            mFragmentActivity = (FragmentActivity) context;
            mKitchenViewModel = ViewModelProviders.of(mFragmentActivity).get(KitchenViewModel.class);
        }
    }

    @Override
    public int getItemCount() {
        if (mFilteredWares != null) {
            return mFilteredWares.size();
        } else {
            return 0;
        }
    }

    @NonNull
    @Override
    public WareViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_food, parent, false);
        return new WareViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WareViewHolder holder, int position) {
        if (mFilteredWares != null) {
            holder.bind(position);
        }
    }

    public void setWares(List<Ware> wares) {
        mWares = wares;
        mFilteredWares = wares;
        notifyDataSetChanged();
    }

    public ArrayList<Ware> getSelectedWares() {
        return mSelectedWares;
    }

    public void setSelectedWares(List<Ware> wares) {
        if (wares != null && wares.size() > 0) {
            mSelectedWares = (ArrayList<Ware>) wares;
            notifyDataSetChanged();
            ((AppCompatActivity) mContext).startSupportActionMode(mActionModeCallbacks);
        }
    }

    public void filter(CharSequence charSequence) {
        if (charSequence == null || mWares == null)
            return;
        String charString = charSequence.toString();
        if (!charString.isEmpty()) {
            List<Ware> filteredList = new ArrayList<>();
            for (Ware row : mWares) {
                if (row.name.toLowerCase().contains(charString.toLowerCase())) {
                    filteredList.add(row);
                }
            }
            mFilteredWares = filteredList;
            notifyDataSetChanged();
        } else {
            setWares(mWares);
        }
    }

    class WareViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.food_item_tint) FrameLayout tint;
        @BindView(R.id.tv_ingredient) TextView foodTextView;
        @BindView(R.id.tv_ingredient_amount) TextView amountTextView;

        private WareViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bind(int position) {
            final Ware current = mFilteredWares.get(position);
            foodTextView.setText(current.name);
            String text = current.amount + " " + MeasurementUtils.getAbbreviation(mContext, current.amountType);
            amountTextView.setText(text);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ((AppCompatActivity) view.getContext()).startSupportActionMode(mActionModeCallbacks);
                    selectWare(current);
                    return true;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mMultiSelect) {
                        selectWare(current);
                    } else {
                        mOnWareClickListener.onShoppingListItemClick(current);
                    }
                }
            });

            if (mSelectedWares.contains(current)) {
                tint.setBackgroundColor(itemView.getResources().getColor(R.color.card_selected_tint));
            } else {
                tint.setBackgroundColor(itemView.getResources().getColor(R.color.card_normal_tint));
            }

        }

        void selectWare(Ware ware) {
            if (mMultiSelect) {
                if (mSelectedWares.contains(ware)) {
                    mSelectedWares.remove(ware);
                    tint.setBackgroundColor(itemView.getResources().getColor(R.color.card_normal_tint));
                } else {
                    mSelectedWares.add(ware);
                    tint.setBackgroundColor(itemView.getResources().getColor(R.color.card_selected_tint));
                }
            }
        }
    }
}
