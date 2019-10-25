package com.fxl.guetcoursetable.testquery;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import com.fxl.guetcoursetable.MainActivity;
import com.fxl.guetcoursetable.Utils.AES;
import com.fxl.guetcoursetable.Utils.OkHttpUtil;
import com.fxl.guetcoursetable.R;
import com.fxl.guetcoursetable.login.LoginActivity;
import com.fxl.guetcoursetable.login.LoginBaseActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by FXL-PC on 2017/2/16.
 */

public class TestQueryActivity extends LoginBaseActivity {
    Toolbar toolbar;
    String examText;
    ProgressDialog progressDialog;



    private static Map<String, String> testQueryRequestBodyMap;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_query);

        toolbar = (Toolbar) findViewById(R.id.toolbar_test_query);
        toolbar.setTitle("考试安排");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        upDateExam();
    }


    private void upDateExam() {
        initProgressDialog();
        SharedPreferences  preferences = getSharedPreferences("student_infos", MODE_PRIVATE);
        login(preferences.getString("username", ""), AES.decode(preferences.getString("passwd","")));
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(TestQueryActivity.this);
        progressDialog.setMessage("查询中，请稍侯...");
        progressDialog.show();
    }

    public void login(final String userName, final String password) {

        requestHeadersMap = new LinkedHashMap<String, String>();
        loginRequestBodyMap = new LinkedHashMap<>();
        initRequestHeadersMap();
        initLoginRequestBodyMap(userName,password);

        OkHttpUtil.postAsync("http://"+getServer()+"/student/public/login.asp",
                new OkHttpUtil.ResultCallback() {
                    @Override

                    public void onError(Call call, Exception e) {
                        ArrayList<TestQueryModel> testList = importExam(getApplicationContext());
                        showExams(testList);
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.getContext(),"获取失败，已从本地导入。",Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(final byte[] response) {
//                        获取学生信息判断是否登陆成功
                        String result = null;
                        try {
                            result = new String(response, "gb2312");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        Document document = Jsoup.parse(result);
                        String loginTitle = document.title();
                        Log.d("loginInfo","0"+loginTitle+"0");
                        if (loginTitle.contains("教学")) {
                            confirmNum(1);
                        }
                        else if(loginTitle.contains("学生选课")){
                            String[] loginFailed = {"loginFailed"};
                            confirmNum(0);
                            loginFailed = null;

                        }
                        else{
                            showWarnDialog();
                        }
                    }
                },loginRequestBodyMap, requestHeadersMap);
    }

    @Override
    public void confirmNum(int confirmInfo) {
        if (confirmInfo == 0) {
            finishActivityToLogin("登录失败，请登录后使用考试安排查询。");
        } else if(confirmInfo == 1){
            getTestOnline();
        } else if (confirmInfo == 2) {
            ArrayList<TestQueryModel> testList = importExam(getApplicationContext());
            showExams(testList);
            progressDialog.dismiss();
        }
    }



    private void getTestOnline() {
        initTestQueryRequestBodyMap();
        Response response;
        OkHttpUtil.postAsync("http://"+getServer()+"/student/testquery.asp",
                new OkHttpUtil.ResultCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        progressDialog.setMessage("获取失败，请重试。");
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
                        Elements iterm = document.select("td");
                        examText = iterm.text();
                        handleOnlineResult(examText);
                    }
                },testQueryRequestBodyMap, LoginBaseActivity.requestHeadersMap);
    }

    private void handleOnlineResult(String testText) {

        String[] test = testText.split(" ");
        Log.d("msg2", "0"+test[0]+"0");
        if (!test[0].isEmpty()) {
            ArrayList<TestQueryModel> testList = handleTest(test);
            if (testList.size() > 0) {
                progressDialog.dismiss();
                showExams(testList);
            }else{
                Toast.makeText(getBaseContext(), "获取失败", Toast.LENGTH_SHORT).show();
                progressDialog.setMessage("获取失败，请重试。");
            }
        }
        else{
            progressDialog.dismiss();
            toolbar.setTitle("考试安排(0条记录)");
        }
    }

    private void showExams(ArrayList<TestQueryModel> exams) {
        TestAdapter testAdapter = new TestAdapter(exams);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_test_query);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(testAdapter);
        toolbar.setTitle("考试安排("+testAdapter.getItemCount()+"条记录)");
    }

    private ArrayList<TestQueryModel> handleTest(String[] exam) {
        ArrayList<TestQueryModel> testList = new ArrayList<>();
        for (int i = 0; i < exam.length; i = i + 6) {
            TestQueryModel testQueryModel = new TestQueryModel();
            testQueryModel.setClassName(exam[i]);
            testQueryModel.setClassId(exam[i + 1]);
            testQueryModel.setWeek(exam[i + 2]);
            testQueryModel.setWeekday(exam[i + 3]);
            testQueryModel.setTime(exam[i + 4]);
            testQueryModel.setClassRoom(exam[i + 5]);
            testList.add(testQueryModel);
            testQueryModel  = null;
        }
        saveExamsToLocal(testList);
        return testList;
    }


    private void saveExamsToLocal(ArrayList<TestQueryModel> exams) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(getFilesDir(), "exams.json"));
            writeJsonStream(fileOutputStream, exams);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeJsonStream(OutputStream out, ArrayList<TestQueryModel> exams) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
        writer.setIndent("  ");
        writeCorseArray(writer, exams);
        writer.close();
    }

    public void writeCorseArray(JsonWriter writer, ArrayList<TestQueryModel> exams) throws IOException {
        writer.beginArray();
        for (TestQueryModel exam : exams) {
            writeCorse(writer, exam);
        }

        writer.endArray();
    }

    public void writeCorse(JsonWriter writer, TestQueryModel exam) throws IOException {
        writer.beginObject();
        writer.name("className").value(exam.getClassName());
        writer.name("classId").value(exam.getClassId());
        writer.name("week").value(exam.getWeek());
        writer.name("weekday").value(exam.getWeekday());
        writer.name("time").value(exam.getTime());
        writer.name("classRoom").value(exam.getClassRoom());
        writer.endObject();
    }


    private  ArrayList<TestQueryModel> importExam(Context context) {
        ArrayList<TestQueryModel> testList = new ArrayList<>();
        try {
            InputStreamReader read = new InputStreamReader(
                    new FileInputStream(new File(context.getFilesDir(), "exams.json")), "utf-8");
            BufferedReader bufferedReader = new BufferedReader(read);
            StringBuilder exams = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                exams.append(line);
            }
            testList = parseExams(exams);
            bufferedReader.close();
            read.close();
        } catch (IOException e) {
        }
        return  testList;
    }

    private  ArrayList<TestQueryModel> parseExams(StringBuilder exams) {
        Gson gson = new Gson();
        ArrayList<TestQueryModel>  testList = gson.fromJson(exams.toString(), new TypeToken<ArrayList<TestQueryModel>>() {
        }.getType());
        return testList;
    }
/*
    private void setOnItemClickListener(TestAdapter testAdapter) {
        testAdapter.setOnItemClickListener(new ScoreAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, String data) {
                TextView className = (TextView) view.findViewById(R.id.textveiw_className_test_query);
                LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.test_item_division);

//                设置drawlayoutleft的图片为打开，分割线不可见

                if (view.findViewById(R.id.textview_test_info).getVisibility() == View.GONE) {


                   animateOpenForTestInfo(view.findViewById(R.id.textview_test_info));
                    animationOpenForIcon(view.findViewById(R.id.test_query_state));
                    linearLayout.setVisibility(View.GONE);
                } else {

//                   设置drawlayoutleft的图片为关闭，分割线可见
                    animateCloseForTestInfo(view.findViewById(R.id.textview_test_info));
                    animationCloseForIcon(view.findViewById(R.id.test_query_state));
                    linearLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

*/
    private void initTestQueryRequestBodyMap() {
        testQueryRequestBodyMap = new LinkedHashMap<>();
        testQueryRequestBodyMap.put("type","0");
        testQueryRequestBodyMap.put("lwPageSize","1000");
        testQueryRequestBodyMap.put("lwBtnquery","%B2%E9%D1%AF");
    }

    public void finishActivityToLogin(String errorInfo) {
        String[] errorInfos = {errorInfo, "2"};
        Intent intent = new Intent(TestQueryActivity.this, LoginActivity.class);
        intent.putExtra("loginTips", errorInfos );
        startActivity(intent);
        finish();
    }


    private void animationOpenForIcon(View view) {
        RotateAnimation ra = new RotateAnimation(0, 90, Animation.RELATIVE_TO_SELF, 0.5F,
                Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(200);
        ra.setFillAfter(true);
        view.findViewById(R.id.test_query_state).startAnimation(ra);
    }


    private void animationCloseForIcon(View view) {
        RotateAnimation ra = new RotateAnimation(90, 0, Animation.RELATIVE_TO_SELF, 0.5F,
                Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(200);
        ra.setFillAfter(true);
        view.findViewById(R.id.test_query_state).startAnimation(ra);
    }

    private void animateOpenForTestInfo(final View testInfo) {
        testInfo.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int mHeight = testInfo.getMeasuredHeight();
        testInfo.setVisibility(View.VISIBLE);
        ValueAnimator animator = createDropAnimatorForBookInfo(
                testInfo, 0, mHeight
        );
        animator.setDuration(200);
        animator.start();
    }

    private void animateCloseForTestInfo(final View testInfo) {
        int origHeight = testInfo.getHeight();
        ValueAnimator animator = createDropAnimatorForBookInfo(testInfo, origHeight, 0);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                testInfo.setVisibility(View.GONE);
            }
        });
        animator.setDuration(200);
        animator.start();
    }

    private ValueAnimator createDropAnimatorForBookInfo(final View view, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener(){
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        int value = (int) valueAnimator.getAnimatedValue();
                        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                        layoutParams.height = value;
                        view.setLayoutParams(layoutParams);
                    }
                }
        );
        return animator;
    }
}
