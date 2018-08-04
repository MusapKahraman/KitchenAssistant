package com.example.kitchen.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.kitchen.R;
import com.example.kitchen.data.firebase.FoodViewModel;
import com.google.firebase.database.DataSnapshot;

public class IngredientsFragment extends Fragment {
    private static final String LOG_TAG = IngredientsFragment.class.getSimpleName();
    private FragmentMessageListener mMessageListener;

    public IngredientsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentMessageListener) {
            mMessageListener = (FragmentMessageListener) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement FragmentMessageListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ingredients, container, false);

        final FoodViewModel viewModel = ViewModelProviders.of(this).get(FoodViewModel.class);
        LiveData<DataSnapshot> liveData = viewModel.getDataSnapshotLiveData();
        liveData.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot food : dataSnapshot.getChildren()) {
                        String item = food.getKey();
                        Log.v(LOG_TAG, "item: " + item);
                    }
                }
            }
        });

        final EditText editText = rootView.findViewById(R.id.edit_text);
        final EditText editTextConversion = rootView.findViewById(R.id.edit_text_conversion);
        Button button = rootView.findViewById(R.id.btn_add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.addFood(editText.getText().toString(), Float.valueOf(editTextConversion.getText().toString()));
            }
        });
        return rootView;
    }

    @Override
    public void onDetach() {
        mMessageListener = null;
        super.onDetach();
    }
}
