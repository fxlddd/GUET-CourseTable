package com.fxl.guetcoursetable.testquery;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fxl.guetcoursetable.R;
import com.fxl.guetcoursetable.score.ScoreAdapter;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by FXL-PC on 2017/2/13.
 */

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> implements View.OnClickListener{
    private ArrayList<TestQueryModel> tests;



    private ScoreAdapter.OnRecyclerViewItemClickListener mOnItemClickListener = null;

    //define interface
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , String data);
    }




    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView className;
        TextView testInfoText;
        TextView testWeek;
        public ViewHolder(View itemView) {
            super(itemView);
            className = (TextView) itemView.findViewById(R.id.textveiw_className_test_query);
            testInfoText = (TextView) itemView.findViewById(R.id.textview_test_info);
            testWeek = (TextView) itemView.findViewById(R.id.test_week);
        }
    }


    public TestAdapter(ArrayList<TestQueryModel> tests) {
        this.tests = tests;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.test_quety_item, parent, false);

        //        给item注册点击事件
        view.setOnClickListener(this);

        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TestQueryModel testQuerModel = tests.get(position);
        //        将数据保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(testQuerModel.getClassId());

        holder.className.setText(testQuerModel.getClassName());
        holder.testWeek.setText(testQuerModel.getWeek()+"周");
        holder.testInfoText.setText(new StringBuilder("课号:  "+String.valueOf(testQuerModel.getClassId()))
                .append("\n周次:  "+testQuerModel.getWeek())
                .append("\n星期:  "+testQuerModel.getWeekday())
                .append("\n节次:  "+testQuerModel.getTime())
                .append("\n教室:  "+testQuerModel.getClassRoom()));
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
//            注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v,(String) v.getTag());
        }
    }

    public void setOnItemClickListener(ScoreAdapter.OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return tests.size();
    }

}
