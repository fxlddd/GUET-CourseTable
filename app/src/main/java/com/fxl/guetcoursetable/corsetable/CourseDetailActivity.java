package com.fxl.guetcoursetable.corsetable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.fxl.guetcoursetable.ImportDataFromLocal.ImportLocalData;
import com.fxl.guetcoursetable.R;


import java.util.ArrayList;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by FXL-PC on 2017/3/7.
 */

public class CourseDetailActivity extends AppCompatActivity {
    ArrayList<Course> sectionCors;
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);


        Intent intent = getIntent();
        int[] courseWeekAndSection = intent.getIntArrayExtra("courseWeekAndSection");
        ImportLocalData im = new ImportLocalData();
        sectionCors = im.getCoursesBySection(courseWeekAndSection[0],
                courseWeekAndSection[1]);

        CourseDetailAdapter courseDetailAdapter = new CourseDetailAdapter(getBaseContext(),
                R.layout.course_detail_item, sectionCors,courseWeekAndSection[2]);
        ListView listView = (ListView) findViewById(R.id.course_detail_listview);

        listView.setAdapter(courseDetailAdapter);
        initToolBar(courseWeekAndSection);
    }

    private void initToolBar(int[] courseWeekAndSection) {
        String[] week = {"","一","二","三","四","五","六","日"};
        toolbar = (Toolbar) findViewById(R.id.toolbar_course_detail);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setTitle("第"+ courseWeekAndSection[2]+"周周"+week[courseWeekAndSection[0]]+
                ",第"+courseWeekAndSection[1]+"大节");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorText));
    }
}
