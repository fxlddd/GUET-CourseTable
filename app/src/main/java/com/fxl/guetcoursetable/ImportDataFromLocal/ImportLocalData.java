package com.fxl.guetcoursetable.ImportDataFromLocal;

import android.content.Context;
import android.content.SharedPreferences;

import com.fxl.guetcoursetable.MainActivity;
import com.fxl.guetcoursetable.corsetable.Course;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.content.Context.MODE_PRIVATE;
import static com.fxl.guetcoursetable.MainActivity.getContext;

/**
 * Created by FXL-PC on 2017/3/3.
 */

public class ImportLocalData {
    private  ArrayList<Course> cors;

    private  ArrayList<Course> dailyCors;
    private  SharedPreferences preferences;


    private  void importCorse(Context context) {
        try {
            InputStreamReader read = new InputStreamReader(
                    new FileInputStream(new File(context.getFilesDir(), "corsetable.json")), "utf-8");
            BufferedReader bufferedReader = new BufferedReader(read);
            StringBuilder tableCorse = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                tableCorse.append(line);
            }
            parseCorse(tableCorse);
            bufferedReader.close();
            read.close();
        } catch (IOException e) {
        }
    }

    private void parseCorse(StringBuilder tableCorse) {
        Gson gson = new Gson();
        cors = gson.fromJson(tableCorse.toString(), new TypeToken<ArrayList<Course>>() {
        }.getType());
    }

//  使用全局context调用
    public  ArrayList<Course> importCourses() {
        cors = new ArrayList<>();
        importCorse(getContext());
        return cors;
    }
//  使用自定义context调用
    public  ArrayList<Course> importCourses(Context context) {
        cors = new ArrayList<>();
        importCorse(context);

        return cors;
    }


    private  void initPreference() {
        preferences = getContext().getSharedPreferences("student_infos", MODE_PRIVATE);
    }

    public  int getDayOfWeek() {
        initPreference();
        return preferences.getInt("dayofweek", 1);
    }

    public  String getPassWd() {
        initPreference();
        return preferences.getString("passwd", "");
    }

    public  String getUserName() {
        initPreference();
        return preferences.getString("username", "");
    }

    public  String getID() {
        initPreference();
        return preferences.getString("id", "123点击登陆").substring(3);
    }

    public String getName() {
        initPreference();
        return preferences.getString("name", "123未登录").substring(3);
    }

    public  boolean isRememberPassWd() {
        initPreference();
        return preferences.getBoolean("remember_passwd", false);
    }

    public  String getGrade() {
        initPreference();
        return preferences.getString("grade", "1232014").substring(3);
    }

    public  int getWeekNum() {
        initPreference();
        if(preferences.getInt("week_num", 1)<=20){
            return preferences.getInt("week_num", 1);
        }
        else {
            return 20;
        }

    }

    public  int getPublicWeekStoraged() {
        initPreference();
        return preferences.getInt("public_week", 1);
    }

    public  String getTerm() {
        initPreference();
        return preferences.getString("term", "");
    }


    //    从本地取得相应周数对应星期几的课表
    private void initDailyCorse(int weekNumber, int dayOfWeek ,Context context) {
        ArrayList<Course> courseList = importCourses(context);
        dailyCors = new ArrayList<>();
        if (dayOfWeek == 1) {
            dayOfWeek = 7;
        } else {
            dayOfWeek--;
        }

        if (!courseList.isEmpty()) {
            for (Course course : courseList) {
                if (course.getStartWeekNum() <= weekNumber && course.getEndWeekNum() >= weekNumber
                        && course.getCourseWeek() == dayOfWeek) {
                    dailyCors.add(course);
                }
            }
        }

//        日课表按上课节次排序
        Collections.sort(dailyCors, new Comparator<Course>(){
            @Override
            public int compare(Course arg0, Course arg1) {

                if(arg0.getCourseSection() < arg1.getCourseSection())
                    return -1;
                else
                    return 1;
            }
        });
    }

//    默认使用全局context调用
    public  ArrayList<Course> getDailyCourses(int weekNumber, int dayOfWeek) {
        initDailyCorse(weekNumber,dayOfWeek, getContext());
        return dailyCors;
    }
//    使用自定义context调用
    public ArrayList<Course> getDailyCourses(int weekNumber, int dayOfWeek, Context context) {
        initDailyCorse(weekNumber,dayOfWeek,context);
        return dailyCors;
    }


    public  ArrayList<Course> getCoursesBySection(int week, int section) {
        ArrayList<Course> courseList = importCourses();
        ArrayList<Course> sectionCourses = new ArrayList<>();

        if (section == 6 || section == 0) {
            //点击事件可能是0或6，返回给周课表只有6（1-6），避免重复返回
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


//      按照开始上课周次排列
        Collections.sort(sectionCourses, new Comparator<Course>(){
            @Override
            public int compare(Course arg0, Course arg1) {

                if(arg0.getStartWeekNum() < arg1.getStartWeekNum())
                    return -1;
                else
                    return 1;
            }
        });
        return sectionCourses;
    }

}
