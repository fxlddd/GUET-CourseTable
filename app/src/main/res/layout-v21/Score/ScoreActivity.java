package com.fxl.okhttptest.Score;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.fxl.okhttptest.R;

/**
 * Created by FXL-PC on 2017/2/14.
 */

public class ScoreActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        String score = intent.getStringExtra("score");
        com.fxl.okhttptest.Score.ScoreAdapter scoreAdapter = new com.fxl.okhttptest.Score.ScoreAdapter(score);
        recyclerView.setAdapter(scoreAdapter);
        TextView scoreNum = (TextView) findViewById(R.id.scoreNum);
        scoreNum.setText("共查到"+scoreAdapter.getItemCount()+"条记录");

    }
}

