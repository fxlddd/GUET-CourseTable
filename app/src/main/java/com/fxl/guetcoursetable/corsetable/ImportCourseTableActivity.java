package com.fxl.guetcoursetable.corsetable;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fxl.guetcoursetable.R;
import com.fxl.guetcoursetable.Utils.AES;
import com.fxl.guetcoursetable.Utils.OkHttpUtil;
import com.fxl.guetcoursetable.Utils.restartapp.RestartAPPTool;
import com.fxl.guetcoursetable.login.LoginActivity;
import com.fxl.guetcoursetable.login.LoginBaseActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by FXL-PC on 2017/2/15.
 */

public class ImportCourseTableActivity extends LoginBaseActivity implements View.OnClickListener{
    TextView studentName;
    TextView studentID;
    Toolbar toolbar;
    Button logoutBtn;
    Button importClassTableBtn;
    SharedPreferences preferences;
    Spinner weekNum;
    WheelView termYear;
    WheelView termPart;
    ProgressDialog progressDialog;
    private static Map<String, String> corseTableRequestBodyMap;
    int[] week = {7,1,2,3,4,5,6};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_class_table);
        toolbar = (Toolbar) findViewById(R.id.toolbar_info);
        preferences = getSharedPreferences("student_infos", MODE_PRIVATE);
        termSelect();
        toolbar.setTitle("导入课表");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        logoutBtn = (Button) findViewById(R.id.logout_btn);
        importClassTableBtn = (Button) findViewById(R.id.import_class_btn);
        weekNum = (Spinner) findViewById(R.id.import_class_weeknum_spinner);
        termYear = (WheelView) findViewById(R.id.term_select_year);
        termPart = (WheelView) findViewById(R.id.term_select_part);


        showStudentInfo();
        logoutBtn.setOnClickListener(this);
        importClassTableBtn.setOnClickListener(this);


    }

    private void showStudentInfo() {
        studentName = (TextView) findViewById(R.id.textview_student_name_importclass);
        studentID = (TextView) findViewById(R.id.textview_student_id_imortclass);
//        SharedPreferences preferences = getSharedPreferences("student_info", MODE_PRIVATE);
        studentName.setText(preferences.getString("name","").substring(3));
        studentID.setText(preferences.getString("id","").substring(3));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logout_btn:{
//                清空文件数据
                preferences.edit().clear().commit();
//                发送已经退出的广播
                Intent intent1 = new Intent("android.intent.action.Logout");
                LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent1);
//                启动登录页面
                Intent intent2 = new Intent(ImportCourseTableActivity.this, LoginActivity.class);
                String[] loginTips = new String[]{"", "0"};
                intent2.putExtra("loginTips",loginTips);
                startActivity(intent2);
                Log.d("msg2",preferences.getString("name","meiwenjian"));
                finish();
                break;
            }

            case R.id.import_class_btn:{
                saveWeekNum();
                initProgressDialog();
                Log.d("week_num", String.valueOf(weekNum.getSelectedItemPosition())+ 1);
                login(preferences.getString("username", ""), AES.decode(preferences.getString("passwd","")));
                break;
            }

        }
    }

    private void saveWeekNum() {
        Calendar calendar =  Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        SharedPreferences.Editor editor = getSharedPreferences("student_infos",MODE_PRIVATE)
                .edit();
        editor.putInt("start_week_num", calendar.get(Calendar.WEEK_OF_YEAR) - weekNum.getSelectedItemPosition());
        editor.putInt("week_num",weekNum.getSelectedItemPosition() + 1);
        editor.apply();

    }




    public void writeJsonStream(OutputStream out, List<Course> course) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
        writer.setIndent("  ");
        writeCorseArray(writer, course);
        writer.close();
    }

    public void writeCorseArray(JsonWriter writer, List<Course> courseList) throws IOException {
        writer.beginArray();
        for (Course course : courseList) {
            writeCorse(writer, course);
            Log.d("course", course.getCorseName());
        }

        writer.endArray();
    }

    public void writeCorse(JsonWriter writer, Course course) throws IOException {
        writer.beginObject();
        writer.name("corseNum").value(course.getCorseNum());
        writer.name("corseName").value(course.getCorseName());
        Log.d("msg", course.getCorseName());
        writer.name("startWeekNum").value(course.getStartWeekNum());
        writer.name("endWeekNum").value(course.getEndWeekNum());
        writer.name("corseWeek").value(course.getCourseWeek());
        writer.name("corseSection").value(course.getCourseSection());
        writer.name("classRoom").value(course.getClassRoom());
        writer.name("corseID").value(course.getCorseID());
        writer.name("teacher").value(course.getTeacher());
        writer.endObject();
    }



    private void getCorse(final String term) {



        initCorseTableRequestBodyMap(term);

        OkHttpUtil.postAsync("http://"+getServer()+"/student/coursetable.asp",
                new OkHttpUtil.ResultCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getBaseContext(), "获取课表失败，请稍后再试。", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(final byte[] response) {
                        String result = null;
                        try {
                            result = new String(response, "gb2312");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        Document document = Jsoup.parse(result);

                        LinkedList<Course> courseList = praseCorses(document);

                        parseExperiment(document, courseList);
                        saveCorseTableToLocal(term, courseList);
                    }
                }, corseTableRequestBodyMap, LoginBaseActivity.requestHeadersMap);
    }

    private void parseExperiment(Document document, LinkedList<Course> courseList) {
        int corseNum = courseList.size();
        String[] weeks = {"一","二","三","四","五","六","日"};
        Elements experiments = document.select("table").get(1).select("tr");

        for (int i = 1; i < experiments.size(); i++) {

            Elements experiment = experiments.get(i).select("td");
            Course course = new Course();
            course.setCorseNum(corseNum);

            course.setCorseName("(实验)" + experiment.get(0).text());
            course.setCorseID("名称：" + experiment.get(1).text()+"("+experiment.get(2).text()+"批次)");
            String experimentInfo = experiment.get(3).text();
            course.setStartWeekNum(Integer.parseInt(experimentInfo.substring(1, experimentInfo.indexOf("周"))));
            course.setEndWeekNum(Integer.parseInt(experimentInfo.substring(1, experimentInfo.indexOf("周"))));
            for (int j=0; j<7; j++) {
                if (experimentInfo.contains(weeks[j])) {
                    course.setCorseWeek(j+1);
                }
            }
            course.setCorseSection(Integer.parseInt(experimentInfo.substring(experimentInfo.lastIndexOf(",")+2,
                    experimentInfo.lastIndexOf(",")+3)));
            course.setClassRoom(experiment.get(4).text());
            course.setTeacher(experiment.get(6).text());
            courseList.add(course);
            corseNum++;
        }
    }

    @NonNull
    private LinkedList<Course> praseCorses(Document document) {
        LinkedList<Course> courseList = new LinkedList<>();
        Elements corseInfos = document.select("table").get(0).select("td");

        int corseNum = 0;

//      网页前35个td是课程
        for (int i = 0;i<35;i++) {
//            将一个课程的信息解析成链表
            List<TextNode> courseInfoList = corseInfos.get(i).textNodes();
//              如果链表大于2则表示该课程有校
            if (courseInfoList.size() > 2) {
                //每三个数据一门课程
                for(int size = 0; size < courseInfoList.size(); size +=3){
                    corseNum = handleCorse( corseNum, i, courseInfoList, courseList,size);
                }
                courseInfoList = null;
            }
        }
        parseTeacher(corseInfos, courseList);
        return courseList;
    }

    private void parseTeacher(Elements iterm, LinkedList<Course> courseList) {
        //        网页第36个td内容是老师
        String[] teachers = iterm.get(35).text().split("；");
        for (String teacher : teachers) {
            for (Course course : courseList) {
                if (teacher.contains(course.getCorseName())) {
                    course.setTeacher("老师："+teacher.substring(teacher.indexOf(":")+1));
                }
            }
        }
    }

    private int handleCorse(int corseNum, int i, List<TextNode> corselist, LinkedList<Course> courseList, int index) {
        Course course = new Course();
        course.setCorseNum(corseNum);
        corseNum++;
        course.setCorseName(corselist.get(index).text());
        course.setStartWeekNum(Integer.valueOf(corselist.get(index+1).text().substring(corselist.get(index+1).text().indexOf("(")+1, corselist.get(index+1).text().indexOf("-"))));
        course.setEndWeekNum(Integer.valueOf(corselist.get(index+1).text().substring(corselist.get(index+1).text().indexOf("-")+1, corselist.get(index+1).text().indexOf(")"))));
        course.setClassRoom(corselist.get(index+1).text().substring(corselist.get(index+1).text().indexOf(")")+1));
        course.setCorseID(corselist.get(index+2).text());
        course.setCorseSection(i / 7 + 1);
        course.setCorseWeek(week[(i + 8) % 7]);
        courseList.add(course);
        return corseNum;
    }

    private void saveCorseTableToLocal(String term, LinkedList<Course> courseList) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(getFilesDir(), "corsetable.json"));
            writeJsonStream(fileOutputStream, courseList);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        RestartAPPTool.restartAPP(getBaseContext(),500);
        Log.d("term", term);
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(ImportCourseTableActivity.this);
        progressDialog.setMessage("获取中，请稍侯...");
        progressDialog.show();
    }

//    private void upDateCorseTable() {
//        Intent intent = new Intent("android.intent.action.upDateCorseShow");
//        intent.putExtra("month", month.getSelectedItemPosition() + 1);
//        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
//    }


    private void termSelect() {
        int grade = Integer.parseInt(preferences.getString("grade","1232014").substring(3));
        String term = preferences.getString("term", "");
        String[] years = {grade+"-"+(grade+1)+"(大一)",(grade+1)+"-"+(grade+2)+"(大二)"
                ,(grade+2)+"-"+(grade+3)+"(大三)",(grade+3)+"-"+(grade+4)+"(大四)",(grade+4)+"-"+(grade+5)+"(大五)"};
        String[] terms = {"第一学期","第二学期"};
        WheelView termSelectYear = (WheelView) findViewById(R.id.term_select_year);
        termSelectYear.setOffset(2);
        termSelectYear.setItems(Arrays.asList(years));

        termSelectYear.setSeletion(Integer.parseInt(term.substring(3,7))-grade);
        Log.d("year", term.substring(3, 7));
        termSelectYear.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                Log.d("msg", "selectedIndex: " + selectedIndex + ", item: " + item);
            }
        });

        WheelView termSelectPart = (WheelView) findViewById(R.id.term_select_part);
        termSelectPart.setOffset(2);
        termSelectPart.setItems(Arrays.asList(terms));

        termSelectPart.setSeletion(Integer.parseInt(term.substring(13))-1);
        Log.d("part", term.substring(13));
        termSelectPart.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                Log.d("msg", "selectedIndex: " + selectedIndex + ", item: " + item);
            }
        });
    }

    @Override
    public void confirmNum(int confirmInfo) {
        if (confirmInfo==1) {
            getCorse(termYear.getSeletedItem().substring(0, 9)+"_"+(termPart.getSeletedIndex()+1));
        } else {
//            finishActivityToLogin("登录失败，请登录后使用成绩查询。");
        }
    }

    private void initCorseTableRequestBodyMap(String term) {
        corseTableRequestBodyMap = new LinkedHashMap<>();
        corseTableRequestBodyMap.put("term",term);
    }
}
