package com.example.kitchen.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.kitchen.R;

import java.util.HashMap;
import java.util.List;

public class MealPlanAdapter extends BaseExpandableListAdapter {

    private final Context mContext;
    private final List<String> mGroupTitles;
    private final HashMap<String, List<String>> mChildren;

    public MealPlanAdapter(Context context, List<String> groupTitles, HashMap<String, List<String>> children) {
        mContext = context;
        mGroupTitles = groupTitles;
        mChildren = children;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String groupTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null)
                convertView = inflater.inflate(R.layout.list_group_meal_board, parent, false);
        }
        if (convertView != null) {
            TextView title = convertView.findViewById(R.id.tv_list_group_title);
            title.setText(groupTitle);
            Button button = convertView.findViewById(R.id.btn_add);
            button.setFocusable(false);
            button.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {

                }
            });
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String childText = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null)
                convertView = inflater.inflate(R.layout.list_child_meal_board, parent, false);
        }
        if (convertView != null) {
            TextView text = convertView.findViewById(R.id.tv_list_child);
            text.setText(childText);
            Button button = convertView.findViewById(R.id.btn_delete);
            button.setFocusable(false);
            button.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {

                }
            });
        }
        return convertView;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroupTitles.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public int getGroupCount() {
        return mGroupTitles.size();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mChildren.get(mGroupTitles.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChildren.get(mGroupTitles.get(groupPosition)).size();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
