package com.example.kitchen.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kitchen.R;
import com.example.kitchen.data.local.entities.Step;

import java.util.List;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepViewHolder> {
    private final Context mContext;
    private final StepClickListener mClickListener;
    private List<Step> mSteps;

    public StepsAdapter(Context context, StepClickListener listener) {
        mContext = context;
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
        final View viewForeground;
        private final TextView mStepNumberTextView;
        private final TextView mInstructionTextView;
        private Step mStep;

        private StepViewHolder(View itemView) {
            super(itemView);
            mStepNumberTextView = itemView.findViewById(R.id.tv_step_number);
            mInstructionTextView = itemView.findViewById(R.id.tv_instruction);
            viewForeground = itemView.findViewById(R.id.view_foreground);
        }

        private void bind(int position) {
            mStep = mSteps.get(position);
            mStepNumberTextView.setText(String.valueOf(mStep.stepNumber));
            mInstructionTextView.setText(mStep.instruction);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onStepClicked(mStep);
                }
            });
        }
    }
}
