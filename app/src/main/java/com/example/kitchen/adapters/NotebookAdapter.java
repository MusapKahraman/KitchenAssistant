package com.example.kitchen.adapters;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kitchen.R;
import com.example.kitchen.data.local.entities.Recipe;

import java.util.ArrayList;
import java.util.List;

public class NotebookAdapter extends RecyclerView.Adapter<NotebookAdapter.RecipeCardViewHolder> {
    private final RecipeClickListener mRecipeClickListener;
    private final ArrayList<Recipe> mSelectedRecipes = new ArrayList<>();
    private List<Recipe> mRecipes;
    private List<Recipe> mFilteredRecipes;
    private boolean mMultiSelect = false;
    private final ActionMode.Callback mActionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mMultiSelect = true;
            menu.add(R.string.delete);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == 0) {
                for (Recipe recipe : mSelectedRecipes) {
                    mRecipes.remove(recipe);
                }
            }
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mMultiSelect = false;
            mSelectedRecipes.clear();
            notifyDataSetChanged();
        }
    };

    public NotebookAdapter(RecipeClickListener recipeClickListener) {
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
        View view = inflater.inflate(R.layout.item_recipe_card, parent, false);
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
        if (charSequence == null)
            return;
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

    class RecipeCardViewHolder extends RecyclerView.ViewHolder {
        private final TextView recipeNameTextView;
        private final TextView cookTimeTextView;
        private final ImageView recipeImageView;
        private final RatingBar ratingBar;
        private final CardView recipeCard;

        private RecipeCardViewHolder(View itemView) {
            super(itemView);
            recipeNameTextView = itemView.findViewById(R.id.tv_recipe_title);
            cookTimeTextView = itemView.findViewById(R.id.tv_card_cook_time);
            recipeImageView = itemView.findViewById(R.id.iv_card_recipe_image);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            recipeCard = itemView.findViewById(R.id.card_recipe);
        }

        private void bind(int position) {
            ratingBar.setVisibility(View.GONE);
            final Recipe current = mFilteredRecipes.get(position);
            recipeNameTextView.setText(current.title);
            int totalTime = current.cookTime + current.prepTime;
            String cookTime = String.format(itemView.getResources().getString(R.string.minutes_abbreviation), totalTime);
            cookTimeTextView.setText(cookTime);
            String url = current.photoUrl;
            if (url != null && url.length() != 0) {
                RequestOptions options = new RequestOptions();
                Glide.with(itemView).load(url).apply(options.centerCrop()).into(recipeImageView);
            }

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ((AppCompatActivity) view.getContext()).startSupportActionMode(mActionModeCallbacks);
                    selectRecipe(current);
                    return true;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mMultiSelect) {
                        selectRecipe(current);
                    } else {
                        mRecipeClickListener.onRecipeClick(current, true);
                    }
                }
            });

            if (mSelectedRecipes.contains(current)) {
                recipeCard.setBackgroundColor(itemView.getResources().getColor(R.color.selected_card_back));
            } else {
                recipeCard.setBackgroundColor(itemView.getResources().getColor(R.color.card_back));
            }

        }

        void selectRecipe(Recipe recipe) {
            if (mMultiSelect) {
                if (mSelectedRecipes.contains(recipe)) {
                    mSelectedRecipes.remove(recipe);
                    recipeCard.setBackgroundColor(itemView.getResources().getColor(R.color.card_back));
                } else {
                    mSelectedRecipes.add(recipe);
                    recipeCard.setBackgroundColor(itemView.getResources().getColor(R.color.selected_card_back));
                }
            }
        }
    }
}
