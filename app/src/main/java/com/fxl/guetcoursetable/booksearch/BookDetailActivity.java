package com.fxl.guetcoursetable.booksearch;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.webkit.WebSettings;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fxl.guetcoursetable.R;
import com.fxl.guetcoursetable.Utils.OkHttpUtil;
import com.fxl.guetcoursetable.score.ScoreAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.LinkedList;

import okhttp3.Call;

/**
 * Created by FXL-PC on 2017/2/23.
 */

public class BookDetailActivity extends AppCompatActivity {
    TextView tvBookDetailName;
    TextView tvBookDetailInfo;
    Toolbar toolbar;
    private LinkedList<BookDetailInfo> bookDetailInfos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_search_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar_book_detail);
        toolbar.setTitle("图书详情");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bookDetailInfos = new LinkedList<>();
        tvBookDetailName = (TextView) findViewById(R.id.book_detail_name);
        tvBookDetailInfo = (TextView) findViewById(R.id.book_detail_info);

        Intent intent = getIntent();
        getBookDetail(intent.getStringExtra("bookDetailUrl"));

    }


    private void getBookDetail(String url) {
        SharedPreferences preferences = getSharedPreferences("student_infos", MODE_PRIVATE);
        String libserver = preferences.getString("libserver","202.193.70.139");

        OkHttpUtil.getAsync("http://"+libserver+"/"+url, new OkHttpUtil.ResultCallback() {
            @Override
            public void onError(Call call, Exception e) {
                Toast.makeText(getBaseContext(), "获取失败", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onResponse(byte[] response) {
                String result = null;
                result = new String(response);
                Document document = Jsoup.parse(result);
//               清空已存在的bookdetailinfo
                bookDetailInfos.clear();
                handleBookDetailDate(document);
                showBookDetail(document);
            }
        });
    }

    private void showBookDetail(Document document) {

        RecyclerView bookDetailRrecyclerView = (RecyclerView) findViewById(R.id.Recycler_book_detail_item);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext()){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        BookDetailStateAdapter bookDetailAdapter = new BookDetailStateAdapter(bookDetailInfos);
        bookDetailRrecyclerView.setLayoutManager(layoutManager);
        bookDetailRrecyclerView.setAdapter(bookDetailAdapter);
        tvBookDetailName.setText(document.select("h1").select("a").text());
        tvBookDetailInfo.setVisibility(View.VISIBLE);
        tvBookDetailName.setVisibility(View.VISIBLE);

        Elements bookInfo = document.select("li");
        if (!bookInfo.isEmpty()){
            tvBookDetailInfo.setText(new StringBuilder("作者:  "+bookInfo.get(0).select("a").text()+
                    "\n价格:  "+bookInfo.get(1).text().substring(2)+
                    "\n出版社:  "+bookInfo.get(2).select("a").text()+
                    "\n索书号:  "+bookInfo.get(3).select("a").text()+
                    "\nISBN:  "+bookInfo.get(4).select("a").text()+
                    "\n分类号:  "+bookInfo.get(5).select("a").text()+
                    "\n页数:  "+bookInfo.get(6).text().substring(2)+
                    "\n出版日期:  "+bookInfo.get(7).text().substring(4)));
            setBookDetailOnClickedListener(bookDetailAdapter);

        }else{
            Toast.makeText(getBaseContext(), "获取详细信息失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void setBookDetailOnClickedListener(BookDetailStateAdapter bookDetailAdapter) {
        bookDetailAdapter.setOnItemClickListener(new ScoreAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, String data) {
                TextView libeary = (TextView) view.findViewById(R.id.libeary);
                LinearLayout divisionLine = (LinearLayout) view.findViewById(R.id.book_detail_item_info_division);
                if (view.findViewById(R.id.book_state_info).getVisibility() == View.GONE) {
//                    view.findViewById(R.id.book_state_info).setVisibility(View.VISIBLE);
                    animateOpenForBookInfo(view.findViewById(R.id.book_state_info));
//                    Drawable icOpen = ContextCompat.getDrawable(view.getContext(),R.drawable.ic_open);
//                    icOpen.setBounds(0,0,icOpen.getMinimumWidth(),icOpen.getMinimumHeight());
//                    libeary.setCompoundDrawables(icOpen,null,null,null);

                    animationOpenForIcon(view);
//                    divisionLine.setVisibility(View.GONE);
                } else {
//                   设置drawlayoutleft的图片为关闭，分割线可见
                    animateCloseForBookInfo(view.findViewById(R.id.book_state_info));
//                    view.findViewById(R.id.book_state_info).setVisibility(View.GONE);
                    animationCloseForIcon(view);
//                    divisionLine.setVisibility(View.VISIBLE);
                }
            }
        });
    }




    private void handleBookDetailDate(Document document) {
        for (int i = 1; i<document.select("table").select("tr").size(); i++){
            BookDetailInfo bookDetailInfo = new BookDetailInfo();
            Elements bookDetail = document.select("tr").get(i).select("td");
            bookDetailInfo.setCallNumber(bookDetail.get(1).text());
            bookDetailInfo.setBookNum(bookDetail.get(2).text());
            bookDetailInfo.setLibeary(bookDetail.get(4).text());
            bookDetailInfo.setState(bookDetail.get(5).text());
            bookDetailInfo.setBorrowDate(bookDetail.get(6).text());
            bookDetailInfo.setReturnDate(bookDetail.get(7).text());
            bookDetailInfo.setBookClass(bookDetail.get(8).text());
            bookDetailInfos.add(bookDetailInfo);
            Log.d("msg", bookDetailInfo.getBorrowDate());
            bookDetailInfo = null;
            bookDetail = null;

        }
    }


    private void animationOpenForIcon(View view) {
        RotateAnimation ra = new RotateAnimation(0, 90, Animation.RELATIVE_TO_SELF, 0.5F,
                Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(200);
        ra.setFillAfter(true);
        view.findViewById(R.id.book_detail_state).startAnimation(ra);
    }


    private void animationCloseForIcon(View view) {
        RotateAnimation ra = new RotateAnimation(90, 0, Animation.RELATIVE_TO_SELF, 0.5F,
                Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(200);
        ra.setFillAfter(true);
        view.findViewById(R.id.book_detail_state).startAnimation(ra);
    }

    private void animateOpenForBookInfo(final View bookInfo) {
        bookInfo.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int mHeight = bookInfo.getMeasuredHeight();
        bookInfo.setVisibility(View.VISIBLE);
        ValueAnimator animator = createDropAnimatorForBookInfo(
                bookInfo, 0, mHeight
        );
        animator.setDuration(200);
        animator.start();
    }

    private void animateCloseForBookInfo(final View bookInfo) {
        int origHeight = bookInfo.getHeight();
        ValueAnimator animator = createDropAnimatorForBookInfo(bookInfo, origHeight, 0);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                bookInfo.setVisibility(View.GONE);
            }
        });
        animator.setDuration(200);
        animator.start();
    }

    private ValueAnimator createDropAnimatorForBookInfo(final View view, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener(){
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        int value = (int) valueAnimator.getAnimatedValue();
                        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                        layoutParams.height = value;
                        view.setLayoutParams(layoutParams);
                    }
                }
        );
        return animator;
    }

}
