package com.fxl.guetcoursetable.corsetable;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.fxl.guetcoursetable.R;


import java.util.ArrayList;


/**
 * Created by FXL-PC on 2017/2/13.
 */

public class DailyCorseAdapter extends RecyclerView.Adapter<DailyCorseAdapter.ViewHolder>{
    private ArrayList<Course> courseInfos;


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView corseName;
        TextView corseInfo;
        public ViewHolder(View itemView) {
            super(itemView);
            corseName = (TextView) itemView.findViewById(R.id.daily_class_item_name);
            corseInfo = (TextView) itemView.findViewById(R.id.daily_class_item_info);
        }
    }


    public DailyCorseAdapter(ArrayList<Course> courseInfos) {
        this.courseInfos = courseInfos;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.daily_class_item, parent, false);

        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Course course = courseInfos.get(position);

        holder.corseName.setText(course.getCorseName());
        holder.corseInfo.setText(new StringBuilder("时间: 第"+ course.getCourseSection()+"大节")
                .append("\n教室: "+ course.getClassRoom()));
    }



    @Override
    public int getItemCount() {
        return courseInfos.size();
    }



}
