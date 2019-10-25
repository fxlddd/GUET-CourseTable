package com.fxl.guetcoursetable.booksearch;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fxl.guetcoursetable.R;
import com.fxl.guetcoursetable.score.ScoreAdapter;

import java.util.LinkedList;

/**
 * Created by FXL-PC on 2017/2/13.
 */

public class BookDetailStateAdapter extends RecyclerView.Adapter<BookDetailStateAdapter.ViewHolder> implements View.OnClickListener{
    private LinkedList<BookDetailInfo> bookDetailInfos;



    private ScoreAdapter.OnRecyclerViewItemClickListener mOnItemClickListener = null;

    //define interface
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, String data);
    }




    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView libeary;
        TextView bookState;
        TextView bookStateInfo;
        public ViewHolder(View itemView) {
            super(itemView);
            libeary = (TextView) itemView.findViewById(R.id.libeary);
            bookState = (TextView) itemView.findViewById(R.id.state);
            bookStateInfo = (TextView) itemView.findViewById(R.id.book_state_info);
        }
    }


    public BookDetailStateAdapter(LinkedList<BookDetailInfo> bookDetailInfos) {
        this.bookDetailInfos = bookDetailInfos;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.book_detail_item, parent, false);

        //        给item注册点击事件
        view.setOnClickListener(this);

        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BookDetailInfo bookDetailInfo = bookDetailInfos.get(position);
        //        将数据保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(bookDetailInfo.getBookNum());

        holder.libeary.setText(bookDetailInfo.getLibeary());
        holder.bookState.setText(bookDetailInfo.getState());
        holder.bookStateInfo.setText(new StringBuilder("索书号:  "+bookDetailInfo.getCallNumber())
                .append("\n条码号:  "+bookDetailInfo.getBookNum())
                .append("\n馆藏地点:  "+bookDetailInfo.getLibeary())
                .append("\n馆藏状态:  "+bookDetailInfo.getState())
                .append("\n借出时间:  "+bookDetailInfo.getBorrowDate())
                .append("\n归还时间:  "+bookDetailInfo.getReturnDate())
                .append("\n流通类型:  "+bookDetailInfo.getBookClass()));
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
        return bookDetailInfos.size();
    }

}
