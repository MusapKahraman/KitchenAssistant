package com.example.kitchen.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kitchen.R;
import com.example.kitchen.data.local.entities.Step;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepViewHolder> {
    private final OnStepClickListener mClickListener;
    private List<Step> mSteps;

    public StepsAdapter(OnStepClickListener listener) {
        mClickListener = listener;
    }

    @Override
    public int getItemCount() {
        if (mSteps != null) {
            return mSteps.size();
        } else {
            return 0;
        }
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_step, parent, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        if (mSteps != null) {
            holder.bind(position);
        }
    }

    public void setSteps(List<Step> steps) {
        mSteps = steps;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        mSteps.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Step step, int position) {
        mSteps.add(position, step);
        notifyItemInserted(position);
    }

    public class StepViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.view_foreground) View mViewForeground;
        @BindView(R.id.tv_step_number) TextView mStepNumberTextView;
        @BindView(R.id.tv_instruction) TextView mInstructionTextView;
        private Step mStep;

        private StepViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bind(int position) {
            mStep = mSteps.get(position);
            mStepNumberTextView.setText(String.valueOf(mStep.stepNumber));
            mInstructionTextView.setText(mStep.instruction);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onStepClick(mStep);
                }
            });
        }
    }
}
