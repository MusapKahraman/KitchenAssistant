package com.example.kitchen.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kitchen.R;
import com.example.kitchen.data.local.entities.Ingredient;
import com.example.kitchen.utility.MeasurementUtils;

import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder> {
    private List<Ingredient> mIngredients;
    private final Context mContext;

    public IngredientsAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getItemCount() {
        if (mIngredients != null) {
            return mIngredients.size();
        } else {
            return 0;
        }
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        if (mIngredients != null) {
            holder.bind(position);
        }
    }

    public void setIngredients(List<Ingredient> ingredients) {
        mIngredients = ingredients;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        mIngredients.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Ingredient ingredient, int position) {
        mIngredients.add(position, ingredient);
        notifyItemInserted(position);
    }

    public class IngredientViewHolder extends RecyclerView.ViewHolder {
        private Ingredient mIngredient;
        private final TextView mAmountTextView;
        private final TextView mIngredientTextView;
        final View viewForeground;

        private IngredientViewHolder(View itemView) {
            super(itemView);
            mAmountTextView = itemView.findViewById(R.id.tv_ingredient_amount);
            mIngredientTextView = itemView.findViewById(R.id.tv_ingredient);
            viewForeground = itemView.findViewById(R.id.view_foreground);
        }

        private void bind(int position) {
            mIngredient = mIngredients.get(position);
            String text = mIngredient.amount + " " + MeasurementUtils.getAbbreviation(mContext, mIngredient.amountType);
            mAmountTextView.setText(text);
            mIngredientTextView.setText(mIngredient.food);
        }
    }
}
