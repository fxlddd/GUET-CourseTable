package com.fxl.guetcoursetable.corsetable;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.fxl.guetcoursetable.ImportDataFromLocal.ImportLocalData;
import com.fxl.guetcoursetable.MainActivity;
import com.fxl.guetcoursetable.R;

import java.util.ArrayList;
import java.util.LinkedList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by FXL-PC on 2017/1/22.
 */

public class DailyCourseFregment extends Fragment {
    private TextView dailyCorseInfo;
    private LinkedList<Course> courseList;
    private ArrayList<Course> dailyCourseList;
    RecyclerView recyclerView;
    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_classtable_daily, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        dailyCourseList = new ArrayList<>();
        courseList = new LinkedList<>();

        dailyCorseInfo = (TextView) view.findViewById(R.id.daily_class_info);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_daily_corse);
//        run() runs after all views were rendered...
        view.post(new Runnable() {
            @Override
            public void run() {
                ImportLocalData imld = new ImportLocalData();
                if (dailyCourseList.isEmpty()) {
//                    从本地导入课表+
                    dailyCourseList = imld.getDailyCourses(imld.getWeekNum(),
                            imld.getDayOfWeek());
//                    updateDailyCorseInfo();
                }
                showDailyCorse(imld.getWeekNum(),
                        imld.getDayOfWeek());
            }
        });
    }

    private void updateDailyCorseInfo() {

        SharedPreferences preferences = getActivity().getSharedPreferences("student_infos", MODE_PRIVATE);
        int weekNumber = preferences.getInt("week_num", 1);
        int dayOfWeek = preferences.getInt("dayofweek", 1);

        getDailyCorse(weekNumber, dayOfWeek);
    }

    private void getDailyCorse(int weekNumber, int dayOfWeek) {
//        获取的dayoweek的第一天是周日，要转换为周一。

        if (dayOfWeek == 1) {
            dayOfWeek = 7;
        }else{
            dayOfWeek--;
        }

        if (!courseList.isEmpty()){
            for (Course course : courseList) {
                if (course.getStartWeekNum() <= weekNumber && course.getEndWeekNum() >= weekNumber
                        && course.getCourseWeek() == dayOfWeek) {
                    dailyCourseList.add(course);
                }
            }
        }



    }

    private void showDailyCorse(int weekNumber, int dayOfWeek) {
        //      解析课程时候是按课的节次写数据的，理论上添加进来的课程已经按节次排序好了

        String[] week = {"","日","一","二","三","四","五","六"};

        if (!dailyCourseList.isEmpty()) {
            dailyCorseInfo.setText("第"+weekNumber+"周周"+week[dayOfWeek]+",共"+ dailyCourseList.size()+"节课");
        }else{
            dailyCorseInfo.setText("第"+weekNumber+"周周"+week[dayOfWeek]+",今天没有课哦~");

        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.getContext());
        recyclerView.setLayoutManager(layoutManager);
        DailyCorseAdapter bookAdapter = new DailyCorseAdapter(dailyCourseList);
        recyclerView.setAdapter(bookAdapter);
    }


}
