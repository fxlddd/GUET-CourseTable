package com.fxl.guetcoursetable.booksearch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.fxl.guetcoursetable.R;
import com.fxl.guetcoursetable.Utils.OkHttpUtil;
import com.fxl.guetcoursetable.score.ScoreAdapter;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;

import okhttp3.Call;

/**
 * Created by FXL-PC on 2017/2/18.
 */

public class BookSearchActivity extends AppCompatActivity {
    private FloatingSearchView mFlowtingSearchView;
    private LinkedList<BookInfo> bookInfos;
    RecyclerView recycler_bookinfo;
    Toolbar toolbar;
    View bookDetailInfo;
    TextView pageNum;
    ActionBar actionBar;
    Button priviousPage;
    Button nextPage;
    String searchWord;
    ProgressDialog progressDialog;
    TextView bookCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_search);
        Intent intent = getIntent();
        final String searchWord = intent.getStringExtra("searchWord");

        bookInfos = new LinkedList<>();


        toolbar = (Toolbar) findViewById(R.id.toolbar_book_search);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pageNum = (TextView) findViewById(R.id.page_num);
        priviousPage = (Button) findViewById(R.id.privious_page);
        nextPage = (Button) findViewById(R.id.next_page);
        bookCount = (TextView) findViewById(R.id.book_count);

        mFlowtingSearchView = (FloatingSearchView) findViewById(R.id.floating_search_in_book_search_activiey);
//        mFlowtingSearchView.setSearchText(searchWord);
        recycler_bookinfo = (RecyclerView) findViewById(R.id.recycler_bookinfo);
        bookDetailInfo = findViewById(R.id.book_detail);
        searchBook(searchWord);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_searchview, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showSearchView();
                return false;
            }
        });
        return true;
    }

    private void showSearchView() {
        mFlowtingSearchView.setVisibility(View.VISIBLE);
        recycler_bookinfo.setVisibility(View.GONE);
        bookCount.setVisibility(View.GONE);
        mFlowtingSearchView.setSearchFocused(true);
        initFLowSearchView();
    }

    private void initFLowSearchView() {
        mFlowtingSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
                mFlowtingSearchView.setSearchFocused(true);
            }

            @Override
            public void onFocusCleared() {
                recycler_bookinfo.setVisibility(View.VISIBLE);
                mFlowtingSearchView.clearQuery();
                mFlowtingSearchView.setVisibility(View.GONE);
            }
        });
        mFlowtingSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

            }

            @Override
            public void onSearchAction(String currentQuery) {
                if (currentQuery.isEmpty()) {
                    mFlowtingSearchView.clearFocus();
                }else{
                    searchBook(currentQuery);
                }
                bookCount.setVisibility(View.VISIBLE);
            }
        });
    }





    private void searchBook(String searchWord) {
        searchBook(searchWord, 1);
    }

    private void searchBook(final String searchWord, int page) {
        progressDialogStart();
        bookCount.setText("查询中,请稍候.");
        this.searchWord = searchWord;
        bookInfos.clear();
        actionBar.setTitle("馆藏查询："+searchWord);
        try {
            SharedPreferences preferences = getSharedPreferences("student_infos", MODE_PRIVATE);
            String libserver = preferences.getString("libserver","202.193.70.139");
            OkHttpUtil.getAsync("http://"+libserver+"/NTRdrBookRetr.aspx?page="+page+"&strKeyValue=" + URLEncoder.encode(searchWord, "UTF-8") + "&strType=text&tabletype=*&strpageNum=10&strSort=asc", new OkHttpUtil.ResultCallback() {
                @Override
                public void onError(Call call, Exception e) {
                    bookCount.setText("查询失败,请重试.");
                    progressDialogStop();
                }

                @Override
                public void onResponse(byte[] response) {
                    String result = null;
                    result = new String(response);
                    handleSearchResult(result);
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void progressDialogStart() {
        progressDialog = new ProgressDialog(BookSearchActivity.this);
        progressDialog.setMessage("查询中请稍候...");
        progressDialog.show();
    }

    private void progressDialogStop() {
        progressDialog.dismiss();
    }



    private void handleSearchResult(String result) {
        Document document = Jsoup.parse(result);

        Elements iterm = document.select("div[class=\"titbar\"]");
        Elements name = document.select("h3").select("a");
        final int pageShowed = Integer.parseInt(document.select("span[id=\"CuurtPage\"]").text());
        final int pageSum = Integer.parseInt(document.select("span[id=\"ConutPage\"]").text());

        setPageTools(pageShowed,pageSum);
        int resultCount = Integer.parseInt(document.select("span[id=\"labAllCount\"]").text());
        int showCount;

        if ((resultCount - pageShowed * 10) >= 0) {
            showCount = 10;
        } else {
            showCount = resultCount - (pageShowed-1) * 10;
        }
        bookCount.setText("共"+resultCount+"条搜索结果");
        for (int i = 0; i<showCount; i++) {
            BookInfo bookInfo = new BookInfo();
            bookInfo.setId(name.get(i).attr("href"));
            bookInfo.setName(name.get(i).text());
            bookInfo.setAuthor(iterm.get(i).select("span").get(0).text());
            bookInfo.setPublisher(iterm.get(i).select("span").get(1).text());
            bookInfo.setPublishTime(iterm.get(i).select("span").get(2).text());
            bookInfo.setIsbn(iterm.get(i).select("span").get(3).text());
            bookInfo.setCallNumber(iterm.get(i).select("span").get(4).text());
            bookInfos.add(bookInfo);
            bookInfo = null;
        }

        progressDialogStop();
        showSearchResult(bookInfos,pageShowed);
    }

    private void setPageTools(final int pageShowed, final int pageSum) {

        pageNum.setText(pageShowed+"/"+pageSum);

        priviousPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pageShowed != 1) {
                    searchBook(searchWord, pageShowed - 1);
                }else {
                    Snackbar.make(v, "已经是第一页了~", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pageSum > pageShowed) {
                    searchBook(searchWord, pageShowed + 1);
                }else {
                    Snackbar.make(v, "已经是最后一页了~", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showSearchResult(LinkedList<BookInfo> bookInfos,int pageShowed) {
        findViewById(R.id.book_serarh_scrollview).scrollTo(0,0 );
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext()){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recycler_bookinfo.setLayoutManager(layoutManager);
        BookSearchResultAdapter bookAdapter = new BookSearchResultAdapter(bookInfos,pageShowed);
        recycler_bookinfo.setAdapter(bookAdapter);
        bookAdapter.setOnItemClickListener(new BookSearchResultAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, String data) {
                Intent intent = new Intent(BookSearchActivity.this, BookDetailActivity.class);
                intent.putExtra("bookDetailUrl", data);
                startActivity(intent);
            }
        });
    }

}
