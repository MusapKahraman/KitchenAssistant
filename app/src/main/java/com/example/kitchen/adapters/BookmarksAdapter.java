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
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.kitchen.R;
import com.example.kitchen.data.local.KitchenViewModel;
import com.example.kitchen.data.local.entities.Recipe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.RecipeCardViewHolder> {
    private final RecipeClickListener mRecipeClickListener;
    private final ArrayList<Recipe> mSelectedRecipes = new ArrayList<>();
    private KitchenViewModel mKitchenViewModel;
    private FragmentActivity mFragmentActivity;
    private List<Recipe> mRecipes;
    private List<Recipe> mFilteredRecipes;
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
                    for (Recipe recipe : mSelectedRecipes) {
                        int position = mRecipes.indexOf(recipe);
                        mRecipes.remove(recipe);
                        mKitchenViewModel.deleteRecipes(recipe);
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
            mSelectedRecipes.clear();
            notifyDataSetChanged();
        }
    };

    public BookmarksAdapter(Context context, RecipeClickListener recipeClickListener) {
        mRecipeClickListener = recipeClickListener;
        if (context instanceof FragmentActivity) {
            mFragmentActivity = (FragmentActivity) context;
            mKitchenViewModel = ViewModelProviders.of(mFragmentActivity).get(KitchenViewModel.class);
        }
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
                if (row.title.toLowerCase().contains(charString.toLowerCase()) || row.writerName.contains(charSequence)) {
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
        @BindView(R.id.tv_recipe_title) TextView recipeNameTextView;
        @BindView(R.id.tv_card_cook_time) TextView cookTimeTextView;
        @BindView(R.id.iv_card_recipe_image) ImageView recipeImageView;
        @BindView(R.id.ratingBar) RatingBar ratingBar;
        @BindView(R.id.card_recipe_tint) FrameLayout recipeCardTint;

        private RecipeCardViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bind(int position) {
            ratingBar.setVisibility(View.GONE);
            final Recipe current = mFilteredRecipes.get(position);
            recipeNameTextView.setText(current.title);
            int totalTime = current.cookTime + current.prepTime;
            String cookTime = String.format(itemView.getResources().getString(R.string.minutes_abbreviation), totalTime);
            cookTimeTextView.setText(cookTime);
            String url = current.imagePath;
            if (url != null && url.length() != 0) {
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.ingredients)
                        .error(R.drawable.ingredients)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.HIGH);
                Glide.with(itemView)
                        .load(url)
                        .apply(options)
                        .into(recipeImageView);
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
                recipeCardTint.setBackgroundColor(itemView.getResources().getColor(R.color.card_selected_tint));
            } else {
                recipeCardTint.setBackgroundColor(itemView.getResources().getColor(R.color.card_normal_tint));
            }

        }

        void selectRecipe(Recipe recipe) {
            if (mMultiSelect) {
                if (mSelectedRecipes.contains(recipe)) {
                    mSelectedRecipes.remove(recipe);
                    recipeCardTint.setBackgroundColor(itemView.getResources().getColor(R.color.card_normal_tint));
                } else {
                    mSelectedRecipes.add(recipe);
                    recipeCardTint.setBackgroundColor(itemView.getResources().getColor(R.color.card_selected_tint));
                }
            }
        }
    }
}
