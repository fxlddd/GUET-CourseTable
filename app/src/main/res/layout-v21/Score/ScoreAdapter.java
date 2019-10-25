package com.fxl.okhttptest.Score;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fxl.okhttptest.R;

import java.util.LinkedList;

/**
 * Created by FXL-PC on 2017/2/13.
 */

public class ScoreAdapter extends RecyclerView.Adapter<com.fxl.okhttptest.Score.ScoreAdapter.ViewHolder>{
    static private String[] score;
    private LinkedList<com.fxl.okhttptest.Score.ScoreModel> scores;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView className;
        TextView score;
        TextView scoreInfo;
        public ViewHolder(View itemView) {
            super(itemView);
            className = (TextView) itemView.findViewById(R.id.className);
            score = (TextView) itemView.findViewById(R.id.score);
            scoreInfo = (TextView) itemView.findViewById(R.id.scoreInfo);
        }
    }


    public ScoreAdapter(String scoreTable) {
        String[] score = scoreTable.split(" ");
        this.score = score;
        handleScore();
    }

    private void handleScore() {
        scores = new LinkedList<>();
        for (int i = 0; i < score.length; i = i + 6) {
            com.fxl.okhttptest.Score.ScoreModel scoreModel = new com.fxl.okhttptest.Score.ScoreModel();
            scoreModel.setTerm(score[i]);
            scoreModel.setClassName(score[i + 1]);
            scoreModel.setClassID(score[i + 2]);
            scoreModel.setGrade(score[i + 3]);
            scoreModel.setCredit(score[i + 4]);
            scoreModel.setClassProperty(score[i + 5]);
            scores.add(scoreModel);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.score_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        com.fxl.okhttptest.Score.ScoreModel scoreModel = scores.get(position);
        holder.className.setText(scoreModel.getClassName());
        holder.score.setText(scoreModel.getGrade());
        /*holder.scoreInfo.setText(new StringBuilder(String.valueOf(scoreModel.getClassName()))
                .append("\n分数："+scoreModel.getGrade())
                .append("\n学分："+scoreModel.getCredit())
                .append("\n课程代码："+scoreModel.getClassID())
                .append("\n课程类型"+scoreModel.getClassProperty()));*/
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

}
