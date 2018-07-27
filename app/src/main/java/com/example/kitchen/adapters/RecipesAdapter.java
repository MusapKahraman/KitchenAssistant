package com.example.kitchen.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kitchen.R;
import com.example.kitchen.data.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeCardViewHolder> {
    private final OnRecipeClickListener mRecipeClickListener;
    private List<Recipe> mRecipes;
    private List<Recipe> mFilteredRecipes;

    public RecipesAdapter(OnRecipeClickListener recipeClickListener) {
        mRecipeClickListener = recipeClickListener;
    }

    @Override
    public int getItemCount() {
        if (mFilteredRecipes != null) {
            return mFilteredRecipes.size();
        } else {
            return 0;
        }
    }

    @NonNull
    @Override
    public RecipeCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_recipes_card, parent, false);
        return new RecipeCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeCardViewHolder holder, int position) {
        if (mFilteredRecipes != null) {
            holder.bind(position);
        }
    }

    public void setRecipes(List<Recipe> recipes) {
        mRecipes = recipes;
        mFilteredRecipes = recipes;
        notifyDataSetChanged();
    }

    public void filter(CharSequence charSequence) {
        String charString = charSequence.toString();
        if (!charString.isEmpty()) {
            List<Recipe> filteredList = new ArrayList<>();
            for (Recipe row : mRecipes) {
                if (row.title.toLowerCase().contains(charString.toLowerCase()) || row.writer.contains(charSequence)) {
                    filteredList.add(row);
                }
            }
            mFilteredRecipes = filteredList;
            notifyDataSetChanged();
        } else {
            setRecipes(mRecipes);
        }
    }

    public class RecipeCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView recipeNameTextView;
        private final ImageView recipeImageView;
        private int recipeId;
        private String recipeName;

        private RecipeCardViewHolder(View itemView) {
            super(itemView);
            recipeNameTextView = itemView.findViewById(R.id.tv_recipe_name);
            recipeImageView = itemView.findViewById(R.id.iv_card_recipe_image);
            itemView.setOnClickListener(this);
        }

        private void bind(int position) {
            Recipe current = mFilteredRecipes.get(position);
            recipeId = current.id;
            recipeName = current.title;
            recipeNameTextView.setText(recipeName);
            String url = current.photoUrl;
            if (url != null && url.length() != 0) {
                RequestOptions options = new RequestOptions();
                Glide.with(itemView).load(url).apply(options.centerCrop()).into(recipeImageView);
            }
        }

        @Override
        public void onClick(View v) {
            mRecipeClickListener.onRecipeClick(recipeId, recipeName);
        }
    }
}
