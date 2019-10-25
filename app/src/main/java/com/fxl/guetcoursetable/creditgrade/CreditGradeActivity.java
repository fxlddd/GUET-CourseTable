package com.fxl.guetcoursetable.creditgrade;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fxl.guetcoursetable.R;
import com.fxl.guetcoursetable.Utils.AES;
import com.fxl.guetcoursetable.Utils.OkHttpUtil;
import com.fxl.guetcoursetable.login.LoginActivity;
import com.fxl.guetcoursetable.login.LoginBaseActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.Formatter;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by FXL-PC on 2017/2/14.
 */

public class CreditGradeActivity extends LoginBaseActivity implements View.OnClickListener{
    Toolbar toolbar;
    TextView textViewCreditGrade;
    Button btnDaYi, btnDaEr, btnDaSan,btnDaSi,btnAll;
    SharedPreferences preferences;
    ProgressDialog progressDialog;
    int grade;
    int queryTimes = 0;

    private static Map<String, String> creditGradeRequestBodyMap;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_grade);

        btnDaYi = (Button) findViewById(R.id.btn_credit_grade_query_dayi);
        btnDaEr = (Button) findViewById(R.id.btn_credit_grade_query_daer);
        btnDaSan = (Button) findViewById(R.id.btn_credit_grade_query_dasan);
        btnDaSi = (Button) findViewById(R.id.btn_credit_grade_query_dasi);
        btnAll   = (Button) findViewById(R.id.btn_credit_grade_query_all);

        textViewCreditGrade = (TextView) findViewById(R.id.textview_crditgrade);

        toolbar = (Toolbar) findViewById(R.id.toolbar_credit_grade);
        toolbar.setTitle("学分绩");
        initProgressDialog();
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        preferences = getSharedPreferences("student_infos", MODE_PRIVATE);
        login(preferences.getString("username", ""), AES.decode(preferences.getString("passwd","")));
        grade = Integer.parseInt(preferences.getString("grade", "").substring(3));
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(CreditGradeActivity.this);
        progressDialog.setMessage("网络初始化中，请稍候...");
        progressDialog.show();
    }

    @Override
    public void confirmNum(int confirmInfo) {
        if (confirmInfo==1) {
            progressDialog.dismiss();
            setOnclickListener();
        } else {
            finishActivityToLogin("登录失败，请登录后使用成绩查询。");
        }
    }

    private void setOnclickListener() {
        btnAll.setOnClickListener(this);
        btnDaYi.setOnClickListener(this);
        btnDaEr.setOnClickListener(this);
        btnDaSan.setOnClickListener(this);
        btnDaSi.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_credit_grade_query_dayi:
                getCreditGrade(1);
                break;

            case R.id.btn_credit_grade_query_daer:
                getCreditGrade(2);
                break;

            case R.id.btn_credit_grade_query_dasan:
                getCreditGrade(3);
                break;

            case R.id.btn_credit_grade_query_dasi:
                getCreditGrade(4);
                break;

            case R.id.btn_credit_grade_query_all:
                getCreditGrade(0);
                break;
        }
        toolbar.setTitle("学分绩"+"(查询中...)");
    }

    private void getCreditGrade(final int i) {
        queryTimes++;
        initCreditGradeRequestBodyMap(i);
        Response response;
        final String[] creditGrade = new String[1];
        OkHttpUtil.postAsync("http://"+getServer()+"/student/xuefenji.asp",
                new OkHttpUtil.ResultCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        toolbar.setTitle("学分绩(获取失败)");
                    }

                    @Override
                    public void onResponse(final byte[] response) {
                        String result = null;
                        try {
                            result = new String(response, "gb2312");
                            Log.d("result", result);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        Document document = Jsoup.parse(result);
                        Elements iterm = document.select("B").select("font");
                        creditGrade[0] = iterm.text();
                        handleData(creditGrade[0], i);
                    }
                },creditGradeRequestBodyMap, LoginBaseActivity.requestHeadersMap);
    }


    float j = 100;
    private void handleData(String creditGrade, int i) {

        if (!creditGrade.equals("")) {
            queryTimes = 0;
            textViewCreditGrade.setTextSize(48);
//            textViewCreditGrade.setText(creditGrade);
            showCredit(Float.valueOf(creditGrade));
            j = Float.valueOf(creditGrade);
            setButtonText(i,creditGrade);
        }else{
            if (queryTimes < 2) {
                getCreditGrade(i);
            }else{
                showCredit((float) 100);
                j=100;
            }

        }
    }

    private void showCredit(Float creditGrade) {
        toolbar.setTitle("学分绩");
        ValueAnimator valueAnimator = numChangeAnimate(
                textViewCreditGrade, j,creditGrade
        );
        valueAnimator.setDuration(200);
        valueAnimator.start();

    }

    private ValueAnimator numChangeAnimate(final View view,float oldNum, float newNum) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(oldNum, newNum);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ((TextView)view).setText(new Formatter().format("%.2f", valueAnimator.getAnimatedValue()).toString());
            }
        });
        return valueAnimator;
    }

    private void setButtonText(int i,String s) {
        switch (i) {
            case 0:
                btnAll.setText("入学至今("+s+")");
                break;
            case 1:
                btnDaYi.setText("大一("+s+")");
                break;
            case 2:
                btnDaEr.setText("大二("+s+")");
                break;
            case 3:
                btnDaSan.setText("大三("+s+")");
                break;
            case 4:
                btnDaSi.setText("大四("+s+")");
                break;

        }
    }


    private void initCreditGradeRequestBodyMap(int i) {
        creditGradeRequestBodyMap = new LinkedHashMap<>();
        switch (i) {
            case 0:
                creditGradeRequestBodyMap.put("xn","");
                break;
            case 1:
                creditGradeRequestBodyMap.put("xn",grade+"-"+(grade+1));
                Log.d("msg", grade + "-" + (grade + 1));
                break;
            case 2:
                creditGradeRequestBodyMap.put("xn",(grade+1)+"-"+(grade+2));
                Log.d("msg", "0"+(grade+1)+"-"+(grade+2)+"0");
                break;
            case 3:
                creditGradeRequestBodyMap.put("xn",(grade+2)+"-"+(grade+3));
                break;
            case 4:
                creditGradeRequestBodyMap.put("xn",(grade+3)+"-"+(grade+4));
                break;

        }
        creditGradeRequestBodyMap.put("lwPageSize","1000");
        creditGradeRequestBodyMap.put("lwBtnquery","%B2%E9%D1%AF");
    }

    public void finishActivityToLogin(String errorInfo) {
        String[] errorInfos = {errorInfo, "3"};
        Intent intent = new Intent(CreditGradeActivity.this, LoginActivity.class);
        intent.putExtra("loginTips", errorInfos );
        startActivity(intent);
        finish();
    }
}

