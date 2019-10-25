package com.fxl.guetcoursetable.score;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fxl.guetcoursetable.R;

import java.util.LinkedList;

/**
 * Created by FXL-PC on 2017/2/13.
 */

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder>implements View.OnClickListener{
    static private String[] score;
    private LinkedList<ScoreModel> scores;
    View view;


    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    //define interface
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , String data);
    }



    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView className;
        TextView score;
        TextView scoreInfo;
        public ViewHolder(View itemView) {
            super(itemView);
            className = (TextView) itemView.findViewById(R.id.score_class_name);
            score = (TextView) itemView.findViewById(R.id.score_info);

        }
    }


    public ScoreAdapter(String[] score) {
        this.score = score;
        handleScore();
    }

    private void handleScore() {
        scores = new LinkedList<>();
            for (int i = 0; i < score.length; i = i + 6) {
                ScoreModel scoreModel = new ScoreModel();
                scoreModel.setNum(i);
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
                R.layout.score_child_item, parent, false);
//        给item注册点击事件
        this.view= view;

        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ScoreModel scoreModel = scores.get(position);

//        将数据保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(position);

        holder.className.setText(scoreModel.getClassName());
        holder.score.setText(scoreModel.getGrade()+"|"+scoreModel.getCredit().substring(0,3));

        if (((int)holder.itemView.getTag() & 1) != 0) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(),
                    R.color.background));
        }else{
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(),
                    R.color.colorText));
        }
    }



    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
//            注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v,(String) v.getTag());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

}
