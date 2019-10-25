package com.fxl.guetcoursetable.login;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.fxl.guetcoursetable.Utils.OkHttpUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import okhttp3.Call;

/**
 * Created by FXL-PC on 2017/2/16.
 */

public abstract class LoginBaseActivity extends AppCompatActivity {
    public static Map<String, String> loginRequestBodyMap ;
    public static Map<String, String> requestHeadersMap;

    String[] info;
    public String server;

    public String getServer(){
        SharedPreferences preferences = getSharedPreferences("student_infos", MODE_PRIVATE);
        return  preferences.getString("server","bkjw2.guet.edu.cn");
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
                        showWarnDialog();
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


    public abstract void confirmNum(int i);

    public void initRequestHeadersMap() {
        requestHeadersMap.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko");
    }

    public void initLoginRequestBodyMap(String userName,String password) {
        loginRequestBodyMap.put("username", userName);
        loginRequestBodyMap.put("passwd", password);
        loginRequestBodyMap.put("login","%B5%C7%A1%A1%C2%BC");
    }

    public void showWarnDialog() {
        AlertDialog.Builder warnDialog = new AlertDialog.Builder(this);
        warnDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        warnDialog.setTitle("网络说明");
        warnDialog.setMessage("请确认网络是否正常，尝试在设置中自定义正确的域名/IP~"
        );
        warnDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        warnDialog.show();
    }
}
