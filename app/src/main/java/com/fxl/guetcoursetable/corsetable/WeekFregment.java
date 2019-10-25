package com.fxl.guetcoursetable.corsetable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fxl.guetcoursetable.ImportDataFromLocal.ImportLocalData;
import com.fxl.guetcoursetable.MainActivity;
import com.fxl.guetcoursetable.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Created by FXL-PC on 2017/1/22.
 */

public class WeekFregment extends Fragment implements View.OnClickListener{
    private ArrayList<Course> courseList;
    LinkedList<TextView> corseInfoList;
    LinkedHashMap<String,Integer> corseColor;
    LocalBroadcastManager broadcastManager;
    BroadcastReceiver upDateCorse;
    ImportLocalData imld;
    /** 第一个当前周格子 */
    protected TextView month;
    /** 星期一的格子 */
    protected TextView monColum;
    /** 星期二的格子 */
    protected TextView tueColum;
    /** 星期三的格子 */
    protected TextView wedColum;
    /** 星期四的格子 */
    protected TextView thrusColum;
    /** 星期五的格子 */
    protected TextView friColum;
    /** 星期六的格子 */
    protected TextView satColum;
    /** 星期日的格子 */
    protected TextView sunColum;
    /** 课程表body部分布局 */
    protected RelativeLayout course_table_layout;
    /** 屏幕宽度 **/
    protected int screenWidth;
    /** 课程格子平均宽度 **/
    protected int gridWidth;
    /*格子高度*/
    private int gridHeight;


    int weekNumber;

    int showWeek;

    private int[] background = {
            R.drawable.ic_course_bg_hui,
            R.drawable.ic_course_bg_chocolate,
            R.drawable.ic_course_bg_pink,
            R.drawable.ic_course_bg_cheng,
            R.drawable.ic_course_bg_bohelv,
            R.drawable.ic_course_bg_lan,
            R.drawable.ic_course_bg_lv,
            R.drawable.ic_course_bg_qing,
            R.drawable.ic_course_bg_tao,
            R.drawable.ic_course_bg_zi,
            R.drawable.ic_course_bg_little_blue,
            R.drawable.ic_course_bg_marron,
            R.drawable.ic_course_bg_fen
    };

    private int[] backgroundOverlay = {
            R.drawable.ic_course_bg_hui_multi,
            R.drawable.ic_course_bg_chocolate_multi,
            R.drawable.ic_course_bg_pink_multi,
            R.drawable.ic_course_bg_cheng_multi,
            R.drawable.ic_course_bg_bohelv_multi,
            R.drawable.ic_course_bg_lan_multi,
            R.drawable.ic_course_bg_lv_multi,
            R.drawable.ic_course_bg_qing_multi,
            R.drawable.ic_course_bg_tao_multi,
            R.drawable.ic_course_bg_zi_multi,
            R.drawable.ic_course_bg_little_bule_multi,
            R.drawable.ic_course_bg_marron_multi,
            R.drawable.ic_course_bg_fen_multi
    };


    View view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//      注册接受更改显示周数的广播
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilterupDateCorse = new IntentFilter();
        intentFilterupDateCorse.addAction("android.intent.action.upDateCorseShow");
        upDateCorse = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent){
                showWeek = intent.getIntExtra("week", weekNumber);
                showCourse(showWeek);
            }
        };
        broadcastManager.registerReceiver(upDateCorse, intentFilterupDateCorse);
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;
//        run() runs after all views were rendered...
        view.post(new Runnable() {
            @Override
            public void run() {
                initCourseTable();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        切记反注册广播
        broadcastManager.unregisterReceiver(upDateCorse);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        imld  = new ImportLocalData();
        // Inflate the layout for this fragment
        parseCourse();
        weekNumber = imld.getWeekNum();
        return inflater.inflate(R.layout.fragment_classtable_week, container, false);
    }

    private void initCourseTable() {
        corseInfoList = new LinkedList<>();
        courseList = new ArrayList<>();
        getActivity().findViewById(R.id.week_corse_table).setBackgroundResource(R.color.colorText);
        month = (TextView) view.findViewById(R.id.month);
        monColum = (TextView) view.findViewById(R.id.monday_course);
        tueColum = (TextView) view.findViewById(R.id.tuesday_course);
        wedColum = (TextView) view.findViewById(R.id.wednesday_course);
        thrusColum = (TextView) view.findViewById(R.id.thursday_course);
        friColum = (TextView) view.findViewById(R.id.friday_course);
        satColum  = (TextView) view.findViewById(R.id.saturday_course);
        sunColum = (TextView) view.findViewById(R.id.sunday_course);
        course_table_layout = (RelativeLayout) view.findViewById(R.id.course_rl);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        //屏幕宽度
        int width = dm.widthPixels;
        //平均宽度
        gridWidth = width * 3 / 22 ;
        month.setWidth(gridWidth * 2 / 5);
        monColum.setWidth(gridWidth);
        tueColum.setWidth(gridWidth);
        wedColum.setWidth(gridWidth);
        thrusColum.setWidth(gridWidth);
        friColum.setWidth(gridWidth);
        satColum.setWidth(gridWidth);
        sunColum.setWidth(gridWidth);
        setWeekBackground();

        this.screenWidth = width;
        int height = dm.heightPixels;
        gridHeight = (int) (height / 6);
        //设置课表界面
        //生成左边的6节课框框
        for (int i = 1; i <= 6; i++) {

            TextView tx = new TextView(view.getContext());
            tx.setId(100 + i);
            //除了最后一列，都使用course_text_view_bg背景（最后一列没有右边框）
            tx.setBackgroundResource(R.drawable.course_text_view_bg);
            //相对布局参数
            RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(
                    gridWidth,
                    gridHeight);
            //文字对齐方式
            tx.setGravity(Gravity.CENTER);
            //字体样式

            if (Build.VERSION.SDK_INT < 23) {
                tx.setTextAppearance(view.getContext(), R.style.courseTableText);
            } else {
                tx.setTextAppearance(R.style.courseTableText);
            }


            //设置第一列

            tx.setText(String.valueOf(i));
            rp.width = gridWidth * 2 / 5;
            //设置他们的相对位置
            if (i == 1) {
                rp.addRule(RelativeLayout.BELOW, month.getId());
                Log.d("msg", String.valueOf(month.getId()));
            } else {
                rp.addRule(RelativeLayout.BELOW, i + 99);
            }
//            tx.setAlpha((float) 0.5);
            tx.setLayoutParams(rp);
            course_table_layout.addView(tx);
        }
//          从json文件中导入课表
        showCourse(weekNumber);
    }

    private void setWeekBackground() {

        switch (imld.getDayOfWeek()) {
            case 1:{
                sunColum.setBackgroundResource(R.drawable.course_text_view_bg_at_week);
                break;
            }
            case 2:{
                monColum.setBackgroundResource(R.drawable.course_text_view_bg_at_week);
                break;
            }
            case 3:{
                tueColum.setBackgroundResource(R.drawable.course_text_view_bg_at_week);
                break;
            }
            case 4:{
                wedColum.setBackgroundResource(R.drawable.course_text_view_bg_at_week);
                break;
            }
            case 5:{
                thrusColum.setBackgroundResource(R.drawable.course_text_view_bg_at_week);
                break;
            }
            case 6:{
                friColum.setBackgroundResource(R.drawable.course_text_view_bg_at_week);
                break;
            }
            case 7:{
                satColum.setBackgroundResource(R.drawable.course_text_view_bg_at_week);
                break;
            }
        }
    }

    public  ArrayList<Course> getCoursesBySection(int week, int section) {
        ArrayList<Course> sectionCourses = new ArrayList<>();

        if (section == 6) {
            //返回给周课表只有6（1-6），避免重复返回
            for (Course course: courseList) {
                if ((course.getCourseSection() == 0 || course.getCourseSection() == 6) && course.getCourseWeek() == week ) {
                    sectionCourses.add(course);
                }
            }
        }else{
            for (Course course: courseList) {
                if (course.getCourseSection() == section && course.getCourseWeek() == week) {
                    sectionCourses.add(course);
                }
            }
        }
        return sectionCourses;
    }
    public void showCourse(int week) {
        showWeek = week;
        if (courseList.isEmpty()) {
            parseCourse();
            Log.d("msg","courseList　is empty");
        }

//        清除已有的textview
        for (TextView textView : corseInfoList) {
            course_table_layout.removeView(textView);
            textView = null;
        }

//         遍历每节课每周
        ArrayList<Course> sectionCourse;
        for (int weekNum = 1;weekNum<=7; weekNum++) {
            for (int section = 1;section <= 6;section++) {
                sectionCourse = getCoursesBySection(weekNum, section);
//                判断当前节次有几节课
//                如果当前节次只有一节课
                if (sectionCourse.size() == 1) {
                    Course course = sectionCourse.get(0);
//                    判断不重叠的课程是否过时
                    if (course.getStartWeekNum() <= week && course.getEndWeekNum() >= week){
                        initCourse(corseColor.get(course.getCorseName()), sectionCourse.get(0),false);
                    }else {
                        initCourse(0, sectionCourse.get(0),false);
                    }

//                       如果当前节次有多节课
                } else if (sectionCourse.size() > 1) {
//                    判断是否有课要上
                    boolean desperate = true;
                    for (Course course : sectionCourse) {
                        if (course.getStartWeekNum() <= week && course.getEndWeekNum() >= week){
//                            有课要上，退出循环，显示要上的当前课程，并设置背景为“重复”
                            initCourse(corseColor.get(course.getCorseName()), course,true);
//                            标记当前有课要上状态
                            desperate = false;
                            break;
                        }
                    }
//                    经过遍历如果没有课程要上，则显示list中的第一项课程
                    if (desperate) {
                        initCourse(0, sectionCourse.get(0),true);
                    }

                }
                sectionCourse.clear();
                sectionCourse = null;
            }
        }
        updateDate(week);
    }


    public void parseCourse() {
        corseColor = new LinkedHashMap<>();
        courseList = imld.importCourses();
        int color = 2;
//        匹配corse的颜色，通过map保证同一个课号同一种颜色

        for (Course course : courseList) {
            if (!corseColor.containsKey(course.getCorseName())) {
                if (course.getCorseName().contains("实验")) {
                    corseColor.put(course.getCorseName(),1);
                }else{
                    corseColor.put(course.getCorseName(), color);
                    color++;
                }

            }
            if (color == background.length) {
                color = 2;
            }
        }

    }

    private void initCourse(int color, Course course, boolean overlay) {
        TextView courseInfo = new TextView(view.getContext());
        courseInfo.setTag(course.getCorseNum());
        courseInfo.setOnClickListener(this);
        corseInfoList.add(courseInfo);
        courseInfo.setText( course.getCorseName() + "@\n" + course.getClassRoom());
        //该textview的高度根据其节数的跨度来设置
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                gridWidth-4,
                gridHeight-4);
        //textview的位置由课程开始节数和上课的时间（day of week）确定
        if (course.getCourseSection() >= 1 && course.getCourseSection() <= 5) {
            rlp.topMargin = gridHeight * (course.getCourseSection() - 1) + 2;
        } else {
            rlp.topMargin = gridHeight * 5 + 2;
        }

        // 偏移由这节课是星期几决定
        rlp.addRule(RelativeLayout.RIGHT_OF, 101);
        rlp.leftMargin = gridWidth * (course.getCourseWeek() - 1)+2;
        //字体剧中
        courseInfo.setGravity(Gravity.CENTER);
        courseInfo.setPadding(2,5,2,5);
        // 设置一种背景
        if (overlay) {
            courseInfo.setBackgroundResource(backgroundOverlay[color]);
        } else {
            courseInfo.setBackgroundResource(background[color]);
        }
        courseInfo.setTextSize(12);
        courseInfo.setLayoutParams(rlp);
        if (color == 0) {
            courseInfo.setTextColor(Color.LTGRAY);
        }else{
            courseInfo.setTextColor(Color.WHITE);
        }
        //设置不透明度
//        courseInfo.getBackground().setAlpha(200);
        course_table_layout.addView(courseInfo);
    }

    @Override
    public void onClick(View v) {

        String[] week = {"","一","二","三","四","五","六","日",};
        Course course = courseList.get((Integer) v.getTag());

        Intent intent = new Intent(getContext(), CourseDetailActivity.class);
        int[] courseWeekAndSection = {course.getCourseWeek(), course.getCourseSection(),showWeek};
        intent.putExtra("courseWeekAndSection", courseWeekAndSection);
        startActivity(intent);

        Log.d("clicked", String.valueOf(v.getTag()));
    }


    public void updateDate(int week) {
        Calendar calendar = Calendar.getInstance();
//        此处week为显示的周数，weekNumber为当前（学期）的周数
        calendar.setFirstDayOfWeek(calendar.MONDAY);
        calendar.add(Calendar.WEEK_OF_YEAR,week-weekNumber);
//        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
//            calendar.add(Calendar.WEEK_OF_YEAR, -1);
//        }
        Log.d("week", String.valueOf(week));
        Log.d("weekNUmber", String.valueOf(weekNumber));
        calendar.set(Calendar.DAY_OF_WEEK, 2);
        month.setText((calendar.get(Calendar.MONTH)+calendar.MONTH-1)+"\n月");

        String date = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        for (int i=0; i<7; i++) {
            switch (i) {
                case 0:
                    monColum.setText("周一\n"+date+"日");
                    break;
                case 1:
                    tueColum.setText("周二\n"+date);
                    break;
                case 2:
                    wedColum.setText("周三\n"+date);
                    break;
                case 3:
                    thrusColum.setText("周四\n"+date);
                    break;
                case 4:
                    friColum.setText("周五\n"+date);
                    break;
                case 5:
                    satColum.setText("周六\n"+date);
                    break;
                case 6:
                    sunColum.setText("周日\n"+date);
                    break;

            }
            calendar.add(Calendar.DATE,1);
            if (calendar.get(Calendar.DAY_OF_MONTH)==1) {
                date = ((calendar.get(Calendar.MONTH)+calendar.MONTH-1)+"月");
            }else{
                date = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)+"日");
            }


        }



    }


}