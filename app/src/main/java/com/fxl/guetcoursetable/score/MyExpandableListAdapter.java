package com.fxl.guetcoursetable.score;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.fxl.guetcoursetable.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fxl on 3/23/17.
 */

public class MyExpandableListAdapter extends BaseExpandableListAdapter {
    ArrayList<String> parent = new ArrayList<>();
    LinkedHashMap<String, List> dataset;
    Context mContext;
    private LayoutInflater inflater;
    public MyExpandableListAdapter(Context context, LinkedHashMap<String, List> dataset){
        mContext = context;
        this.dataset = dataset;
        getParent(dataset);
        inflater = LayoutInflater.from(context);
    }

    private void getParent(Map<String, List> dataset) {
        for (String key : dataset.keySet()) {
            this.parent.add(key);
        }
    }

    @Override
    public int getGroupCount() {
        return parent.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return dataset.get(parent.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return parent.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return dataset.get(parent.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.score_parent_item, viewGroup,false);
        }
        TextView textView = (TextView) view.findViewById(R.id.score_parent_item);
        textView.setText(getGroup(i).toString());
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.score_child_item, viewGroup,false);
        }
        TextView className = (TextView) view.findViewById(R.id.score_class_name);
        TextView courseInfo = (TextView) view.findViewById(R.id.score_info);
        ScoreModel score = (ScoreModel) dataset.get(parent.get(i)).get(i1);
        className.setText(score.getClassName());
        courseInfo.setText(score.getGrade()+"|"+score.getCredit().substring(0,3));
        /*if ((i1&1)!=0) {
            view.setBackgroundColor(Color.LTGRAY);
        }else {
            view.setBackgroundColor(Color.WHITE);
        }*/
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
