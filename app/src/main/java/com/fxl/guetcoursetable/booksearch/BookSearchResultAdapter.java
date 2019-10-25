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

public class BookSearchResultAdapter extends RecyclerView.Adapter<BookSearchResultAdapter.ViewHolder> implements View.OnClickListener{
    private LinkedList<BookInfo> bookInfos;
    private int pageShowed;


    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    //define interface
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, String data);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
//            注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v,(String) v.getTag());
        }
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView bookName;
        TextView bookInfo;
        public ViewHolder(View itemView) {
            super(itemView);
            bookName = (TextView) itemView.findViewById(R.id.book_item_name);
            bookInfo = (TextView) itemView.findViewById(R.id.book_item_info);
        }
    }


    public BookSearchResultAdapter(LinkedList<BookInfo> bookInfos,int pageShowed) {
        this.bookInfos = bookInfos;
        this.pageShowed = pageShowed;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.book_item, parent, false);

        //        给item注册点击事件
        view.setOnClickListener(this);

        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BookInfo bookInfo = bookInfos.get(position);
        //        将数据保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(bookInfo.getDetailUrl());

        holder.bookName.setText(((pageShowed-1)*10+position+1)+"."+bookInfo.getName());
        holder.bookInfo.setText(new StringBuilder(bookInfo.getAuthor())
                .append("\n"+bookInfo.getPublisher())
                .append("\n"+bookInfo.getPublishTime())
                .append("\n"+bookInfo.getIsbn())
//                .append("\n"+bookInfo.getCallNumber())
        );
    }





    @Override
    public int getItemCount() {
        return bookInfos.size();
    }

}
