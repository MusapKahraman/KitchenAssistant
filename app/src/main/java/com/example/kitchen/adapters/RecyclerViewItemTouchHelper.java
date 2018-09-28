/*
 * Reference
 * https://www.androidhive.info/2017/09/android-recyclerview-swipe-delete-undo-using-itemtouchhelper/
 */

package com.example.kitchen.adapters;

import android.graphics.Canvas;
import android.view.View;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewItemTouchHelper extends ItemTouchHelper.SimpleCallback {
    private final RecyclerItemTouchHelperListener listener;

    public RecyclerViewItemTouchHelper(int dragDirs, int swipeDirs, RecyclerItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            View foregroundView = null;
            if (viewHolder instanceof IngredientsAdapter.IngredientViewHolder) {
                foregroundView = ((IngredientsAdapter.IngredientViewHolder) viewHolder).viewForeground;
            } else if (viewHolder instanceof StepsAdapter.StepViewHolder) {
                foregroundView = ((StepsAdapter.StepViewHolder) viewHolder).mViewForeground;
            }
            if (foregroundView != null)
                getDefaultUIUtil().onSelected(foregroundView);
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View foregroundView = null;
        if (viewHolder instanceof IngredientsAdapter.IngredientViewHolder) {
            foregroundView = ((IngredientsAdapter.IngredientViewHolder) viewHolder).viewForeground;
        } else if (viewHolder instanceof StepsAdapter.StepViewHolder) {
            foregroundView = ((StepsAdapter.StepViewHolder) viewHolder).mViewForeground;
        }
        if (foregroundView != null)
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        View foregroundView = null;
        if (viewHolder instanceof IngredientsAdapter.IngredientViewHolder) {
            foregroundView = ((IngredientsAdapter.IngredientViewHolder) viewHolder).viewForeground;
        } else if (viewHolder instanceof StepsAdapter.StepViewHolder) {
            foregroundView = ((StepsAdapter.StepViewHolder) viewHolder).mViewForeground;
        }
        if (foregroundView != null)
            getDefaultUIUtil().clearView(foregroundView);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View foregroundView = null;
        if (viewHolder instanceof IngredientsAdapter.IngredientViewHolder) {
            foregroundView = ((IngredientsAdapter.IngredientViewHolder) viewHolder).viewForeground;
        } else if (viewHolder instanceof StepsAdapter.StepViewHolder) {
            foregroundView = ((StepsAdapter.StepViewHolder) viewHolder).mViewForeground;
        }
        if (foregroundView != null)
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onSwiped(viewHolder);
    }

    public interface RecyclerItemTouchHelperListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder);
    }
}
