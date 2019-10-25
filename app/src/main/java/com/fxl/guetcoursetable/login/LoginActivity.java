package com.fxl.guetcoursetable.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fxl.guetcoursetable.Utils.AES;
import com.fxl.guetcoursetable.Utils.OkHttpUtil;
import com.fxl.guetcoursetable.R;
import com.fxl.guetcoursetable.score.ScoreActivity;
import com.fxl.guetcoursetable.corsetable.ImportCourseTableActivity;
import com.fxl.guetcoursetable.creditgrade.CreditGradeActivity;
import com.fxl.guetcoursetable.testquery.TestQueryActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by FXL-PC on 2017/1/23.
 */

public class LoginActivity extends LoginBaseActivity implements View.OnClickListener{
    Toolbar toolbar;
    TextView loginTipsText;
    EditText userName;
    EditText password;
    CheckBox rememberPasswd;
    String[] loginTips;
    TextView textViewStudentName;
    TextView textViewStudentID;
    ProgressDialog progressDialog;
    public static Map<String, String> requestHeadersMap, loginRequestBodyMap ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initToolBar();
        textViewStudentName = (TextView) findViewById(R.id.textview_student_name);
        textViewStudentID = (TextView) findViewById(R.id.textview_student_id);
        loginTipsText = (TextView) findViewById(R.id.warn_info);
        userName = (EditText) findViewById(R.id.user_name);
        password = (EditText) findViewById(R.id.possword);
        rememberPasswd = (CheckBox) findViewById(R.id.remember_passwd);
        findViewById(R.id.cancel_login).setOnClickListener(this);
        findViewById(R.id.login).setOnClickListener(this);
        initProgressDialog();
        Intent intent = getIntent();
        loginTips = intent.getStringArrayExtra("loginTips");
        setLoginTips(loginTips[0]);

    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_login);
        toolbar.setTitle("登录");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("登陆中请稍候...");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_login:
//                Toast.makeText(this,"你已取消登录",Toast.LENGTH_SHORT).show();
//                发送已经退出登陆的广播
                Intent intent = new Intent("android.intent.action.Logout");
                LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
                finish();
                break;

            case R.id.login:
                login(userName.getText().toString(),password.getText().toString());
                progressDialog.show();
                break;
            default:
                break;
        }
    }

    public void confirmNum(int confirmInfo) {
        if (confirmInfo == 0) {
            setLoginTips("登陆失败，请检查学号及密码。");
            progressDialog.dismiss();
            info = null;
        }
        else{
//          登录成功 获取学生信息
            getInfo();
            info = null;
        }
    }


    public void getInfo() {
        OkHttpUtil.getAsync("http://"+getServer()+"/student/Info.asp", new OkHttpUtil.ResultCallback() {
            @Override
            public void onError(Call call, Exception e) {
                Log.d("msg","获取信息失败");
            }

            @Override
            public void onResponse(byte[] response) {
                String result = null;
                try {
                    result = new String(response, "gb2312");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Document document = Jsoup.parse(result);
                Elements infos = document.select("p");
                Log.d("infos", String.valueOf(infos));
                info = infos.text().split(" ");
//               保存学生信息
                saveStudentInfo(info);
            }
        });
    }

    private void saveStudentInfo(String[] info) {
        SharedPreferences.Editor editor = getSharedPreferences("student_infos",MODE_PRIVATE)
                .edit();
        editor.putBoolean("remember_passwd",rememberPasswd.isChecked());
        editor.putString("username",userName.getText().toString());
        editor.putString("passwd", AES.encode(password.getText().toString()));
        editor.putString("id", info[0]);
        editor.putString("name", info[1]);
        editor.putString("grade", info[3]);
        editor.putString("term", info[4]);
        editor.commit();
//      保存信息后进入下一个Activity并发送已经登陆地广播
        if (rememberPasswd.isChecked()){
            Intent intent = new Intent("android.intent.action.Login");
            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
        }
        progressDialog.dismiss();

        startNextActivity(loginTips[1]);

    }



    private void startNextActivity(String activityNum) {
        switch (activityNum) {
            case "0":
                startActivityFor(ImportCourseTableActivity.class);
                break;
            case "1":
                startActivityFor(ScoreActivity.class);
                break;
            case "2":
                startActivityFor(TestQueryActivity.class);
                break;
            case "3":
                startActivityFor(CreditGradeActivity.class);
                break;
            case "4":
                finish();
                break;
        }
    }

    private void startActivityFor(Class classes) {
        Intent intent = new Intent(LoginActivity.this, classes);
        startActivity(intent);
        finish();
    }

    private void setLoginTips(final String warnInfoText) {
        loginTipsText.setText(warnInfoText);
    }


}
