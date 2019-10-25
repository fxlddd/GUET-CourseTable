package com.fxl.guetcoursetable.corsetable;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fxl.guetcoursetable.R;

import java.util.List;

/**
 * Created by FXL-PC on 2017/3/7.
 */

public class CourseDetailAdapter extends ArrayAdapter {
    private int resourceID;
    private int showWeek;
    String[] week = {"","一","二","三","四","五","六","日",};
    public CourseDetailAdapter(Context context, int resource, List<Course> cors, int showWeek) {
        super(context, resource, cors);
        resourceID = resource;
        this.showWeek = showWeek;

    }

    private int[] background = {
            R.drawable.course_info_green,
            R.drawable.course_info_purple,
    };

    private int i = 0;

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Course course = (Course) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceID, parent, false);
        TextView courseName = (TextView) view.findViewById(R.id.course_name_item);
        TextView courseInfo = (TextView) view.findViewById(R.id.course_info_item);
        courseName.setText(course.getCorseName());
        courseInfo.setText(course.getCorseID()
                + "\n教室：" + course.getClassRoom()
                + "\n时间：星期" + week[course.getCourseWeek()] + ",第" + course.getCourseSection() + "大节"
                + "\n周次：" + course.getStartWeekNum() + "-" + course.getEndWeekNum() + "周\n"
//                teacher为老师或者实验课程备注
                + course.getTeacher());

        if (course.getStartWeekNum() <= showWeek
                && course.getEndWeekNum() >= showWeek) {
//            view.findViewById(R.id.course_detail_background).setBackgroundResource(background[i++]);
                view.findViewById(R.id.course_detail_background).setBackgroundResource(R.drawable.course_info_intime);
               // ( view.findViewById(R.id.course_detail_intime)).setVisibility(View.VISIBLE);
        }
        return view;
    }
}
