package com.fxl.guetcoursetable.score;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fxl.guetcoursetable.MainActivity;
import com.fxl.guetcoursetable.Utils.AES;
import com.fxl.guetcoursetable.Utils.OkHttpUtil;
import com.fxl.guetcoursetable.R;
import com.fxl.guetcoursetable.login.LoginActivity;
import com.fxl.guetcoursetable.login.LoginBaseActivity;
import com.fxl.guetcoursetable.testquery.TestQueryModel;
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
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by FXL-PC on 2017/2/14.
 */

public class ScoreActivity extends LoginBaseActivity {
    Toolbar toolbar;
    String scoreText;
    SharedPreferences preferences;
    TextView scoreItem;
    ProgressDialog progressDialog;
    ExpandableListView expandableListView;

    private static Map<String, String> scoreRequestBodyMap;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        initProgressDialog();
        toolbar = (Toolbar) findViewById(R.id.toolbar_score);
        scoreItem = (TextView) findViewById(R.id.score_class_name);
        toolbar.setTitle("成绩");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        expandableListView = (ExpandableListView) findViewById(R.id.explistview_score);
        preferences = getSharedPreferences("student_infos", MODE_PRIVATE);
        login(preferences.getString("username", ""), AES.decode(preferences.getString("passwd","")));
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(ScoreActivity.this);
        progressDialog.setMessage("查询中，请稍侯...");
        progressDialog.show();
    }

    @Override
    public void confirmNum(int confirmInfo) {
        if (confirmInfo==1) {
            try {
                getScore();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            finishActivityToLogin("登录失败，请登录后使用成绩查询。");
        }
    }

    public void getScore() throws IOException {
        initScoreRequestBodyMap();
        Response response;
        OkHttpUtil.postAsync("http://"+getServer()+"/student/Score.asp",
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
                        scoreText = iterm.text();
                        handleScore(scoreText);
                    }
                },scoreRequestBodyMap, LoginBaseActivity.requestHeadersMap);
    }


    private void handleScore(String scoreText) {
        String[] scores = scoreText.split(" ");
        if (scores[0].equals("")) {
            progressDialog.dismiss();
            toolbar.setTitle("成绩(0条记录)");
        }else{
            progressDialog.dismiss();
        }
//        提取出score的信息
        ArrayList<ScoreModel> scoresList = new ArrayList<>();
        for (int i = 0; i < scores.length; i = i + 6) {
            ScoreModel scoreModel = new ScoreModel();
            scoreModel.setNum(i);
            scoreModel.setTerm(scores[i]);
            scoreModel.setClassName(scores[i + 1]);
            scoreModel.setClassID(scores[i + 2]);
            scoreModel.setGrade(scores[i + 3]);
            scoreModel.setCredit(scores[i + 4]);
            scoreModel.setClassProperty(scores[i + 5]);
            scoresList.add(scoreModel);
        }
        if (scoresList.size() > 0) {
            sortScores(scoresList);
            //将成绩保存到本地
            //saveScoreToLocal(scoresList);
        }
        updateToolbarTitle(scoresList.size());
    }

    private void sortScores(ArrayList<ScoreModel> scoresList) {
        //         将score分类
        LinkedHashMap<String, List> dataSet =new LinkedHashMap<>();
        for (ScoreModel score : scoresList) {
            String term = score.getTerm();

            if (dataSet.containsKey(term)) {
                List currentScoreList = dataSet.get(score.getTerm());
                currentScoreList.add(score);
                dataSet.put(term,currentScoreList);
            } else  {
                LinkedList newScoreList = new LinkedList();
                newScoreList.add(score);
                dataSet.put(term, newScoreList);
            }
        }
        showScore(dataSet);
    }

    private void showScore(LinkedHashMap<String, List> dataSet) {
        ExpandableListAdapter adapter = new MyExpandableListAdapter(getBaseContext(), dataSet);
        expandableListView.setAdapter(adapter);
        expandableListView.setGroupIndicator(null);
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
//                屏蔽点击事件
            return true;
            }
        });
    }
/*
    private void saveScoreToLocal(ArrayList<ScoreModel> socores) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(getFilesDir(), "socores.json"));
            writeJsonStream(fileOutputStream, socores);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeJsonStream(OutputStream out, ArrayList<ScoreModel> socores) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
        writer.setIndent("  ");
        writeCorseArray(writer, socores);
        writer.close();
    }

    public void writeCorseArray(JsonWriter writer, ArrayList<ScoreModel> socores) throws IOException {
        writer.beginArray();
        for (ScoreModel score : socores) {
            writeCorse(writer, score);
        }

        writer.endArray();
    }

    public void writeCorse(JsonWriter writer, ScoreModel socore) throws IOException {
        writer.beginObject();
        writer.name("Num").value(socore.getNum());
        writer.name("term").value(socore.getTerm());
        writer.name("className").value(socore.getClassName());
        writer.name("classID").value(socore.getClassID());
        writer.name("grade").value(socore.getGrade());
        writer.name("credit").value(socore.getCredit());
        writer.name("classProperty").value(socore.getClassProperty());
        writer.endObject();
    }


    private  ArrayList<ScoreModel> importScore(Context context) {
        ArrayList<ScoreModel> scoreList = new ArrayList<>();
        try {
            InputStreamReader read = new InputStreamReader(
                    new FileInputStream(new File(context.getFilesDir(), "socores.json")), "utf-8");
            BufferedReader bufferedReader = new BufferedReader(read);
            StringBuilder scores = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                scores.append(line);
            }
            scoreList = parseScores(scores);
            bufferedReader.close();
            read.close();
        } catch (IOException e) {
        }
        return  scoreList;
    }

    private  ArrayList<ScoreModel> parseScores(StringBuilder scores) {
        Gson gson = new Gson();
        ArrayList<ScoreModel>  scoreList = gson.fromJson(scores.toString(), new TypeToken<ArrayList<ScoreModel>>() {
        }.getType());
        return scoreList;
    }
*/
    private void updateToolbarTitle(int scoreNum) {
        int parentNum = expandableListView.getCount();
        for (int i = 0; i<parentNum; i++) {
            expandableListView.expandGroup(i);
        }
        if (scoreNum > 0) {
            toolbar.setTitle("成绩"+"("+scoreNum+"条记录)");
        }else{
            progressDialog.setMessage("未查到成绩");
        }
    }


    private void initScoreRequestBodyMap() {
        scoreRequestBodyMap = new LinkedHashMap<>();
        scoreRequestBodyMap.put("ckind","");
        scoreRequestBodyMap.put("lwPageSize","1000");
        scoreRequestBodyMap.put("lwBtnquery","%B2%E9%D1%AF");
    }


    public void finishActivityToLogin(String errorInfo) {
        String[] errorInfos = {errorInfo, "1"};
        Intent intent = new Intent(ScoreActivity.this, LoginActivity.class);
        intent.putExtra("loginTips", errorInfos );
        startActivity(intent);
        finish();
    }



}

