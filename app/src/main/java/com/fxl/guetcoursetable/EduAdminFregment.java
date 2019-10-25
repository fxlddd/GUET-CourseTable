package com.fxl.guetcoursetable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.fxl.guetcoursetable.booksearch.BookSearchActivity;
import com.fxl.guetcoursetable.corsetable.ImportCourseTableActivity;
import com.fxl.guetcoursetable.creditgrade.CreditGradeActivity;
import com.fxl.guetcoursetable.login.LoginActivity;
import com.fxl.guetcoursetable.settings.SettingsActivity;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by FXL-PC on 2017/1/22.
 */

public class EduAdminFregment extends Fragment implements View.OnClickListener{
    private TextView scoreQuery;
    private TextView testQuery;
    private TextView creditGradeQuery;
    private TextView pingJiao;
    private TextView bookQuery;
    private TextView setting;
    private FloatingSearchView mFlowtingSearchView;
    TextView textViewStudentName;
    TextView textViewStudentID;
    View loginInfo;
    SharedPreferences preferences;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_eduadmin, container, false);
        scoreQuery = (TextView) view.findViewById(R.id.score_query);
        testQuery = (TextView) view.findViewById(R.id.test_query);
        creditGradeQuery = (TextView) view.findViewById(R.id.btn_credit_grade_query);
        pingJiao = (TextView) view.findViewById(R.id.btn_ping_jiao);
        bookQuery = (TextView) view.findViewById(R.id.book_query);
        setting = (TextView) view.findViewById(R.id.setting);
        mFlowtingSearchView = (FloatingSearchView) view.findViewById(R.id.floating_search_view);
//        searchView = (MaterialSearchView) view.findViewById(R.id.search_view);

        loginInfo = view.findViewById(R.id.student_info_show);

        preferences = getActivity().getSharedPreferences("student_infos", MODE_PRIVATE);
        textViewStudentName = (TextView) view.findViewById(R.id.textview_student_name);
        textViewStudentID = (TextView) view.findViewById(R.id.textview_student_id);

        upDateLoginInfo();
        scoreQuery.setOnClickListener(this);
        testQuery.setOnClickListener(this);
        creditGradeQuery.setOnClickListener(this);
        pingJiao.setOnClickListener(this);
        bookQuery.setOnClickListener(this);
        setting.setOnClickListener(this);
        loginInfo.setOnClickListener(this);


        registerBroadcast();

        return view;



    }

    private void registerBroadcast() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilterLogin = new IntentFilter();
        intentFilterLogin.addAction("android.intent.action.Login");
        BroadcastReceiver loginReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent){
                upDateLoginInfo();
            }
        };

        IntentFilter intentFilterLogout = new IntentFilter();
        intentFilterLogout.addAction("android.intent.action.Logout");
        BroadcastReceiver logoutReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent){
                upDateLoginInfo();
            }
        };
        broadcastManager.registerReceiver(loginReceiver, intentFilterLogin);
        broadcastManager.registerReceiver(logoutReceiver, intentFilterLogout);
    }

    private void upDateLoginInfo() {
        textViewStudentName.setText(preferences.getString("name", "123未登录").substring(3));
        textViewStudentID.setText(preferences.getString("id", "123点击登陆").substring(3));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.student_info_show:{
                if (isLogin()) {
                    Intent intent = new Intent(getContext(), ImportCourseTableActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    String[] logintips = {"", "0"};
                    intent.putExtra("loginTips", logintips);
                    startActivity(intent);
                }
                break;
            }

            case R.id.score_query: {
                if (isLogin()) {
                    Intent intent = new Intent(v.getContext(), com.fxl.guetcoursetable.score.ScoreActivity.class);
                    startActivity(intent);
                } else {
                    startLoginActivity("1");
                }
                break;
            }

            case R.id.test_query:{
                if (isLogin()) {
                    Intent intent = new Intent(v.getContext(), com.fxl.guetcoursetable.testquery.TestQueryActivity.class);
                    startActivity(intent);
                } else {
                    startLoginActivity("2");
                }
                break;
            }

            case R.id.btn_credit_grade_query:{
                if (isLogin()) {
                    Intent intent = new Intent(getContext(), CreditGradeActivity.class);
                    startActivity(intent);
                } else {
                    startLoginActivity("3");
                }
                break;
            }

            case R.id.btn_ping_jiao:{
                Toast.makeText(getContext(), "该功能暂未开放",Toast.LENGTH_SHORT).show();
                break;
            }

            case R.id.book_query:{

                setFlowSearchViewFunction();
                break;
            }

            case R.id.setting:{
                startSettingActivity();
                break;
            }

        }
    }

    private void startSettingActivity() {
        Intent intent = new Intent(getContext(), SettingsActivity.class);
        startActivity(intent);
    }


    private void setFlowSearchViewFunction() {
        mFlowtingSearchView.setSearchFocused(true);
        mFlowtingSearchView.setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.tabLayout).setVisibility(View.GONE);

        mFlowtingSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

            }

            @Override
            public void onSearchAction(String currentQuery) {
                if (!currentQuery.isEmpty()) {
                    mFlowtingSearchView.setVisibility(View.GONE);
                    Intent intent = new Intent(getContext(), BookSearchActivity.class);
                    intent.putExtra("searchWord",currentQuery);
                    startActivity(intent);
                    getActivity().findViewById(R.id.tabLayout).setVisibility(View.VISIBLE);
                }
            }
        });
        setCancelBookSearch();
    }

    private void setCancelBookSearch() {
        mFlowtingSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {

            }

            @Override
            public void onFocusCleared() {
                getActivity().findViewById(R.id.tabLayout).setVisibility(View.VISIBLE);
                mFlowtingSearchView.clearQuery();
                mFlowtingSearchView.setVisibility(View.GONE);
            }
        });
    }

    public Boolean isLogin() {
        SharedPreferences preferences = this.getActivity().getSharedPreferences("student_infos", MODE_PRIVATE);
        if (preferences.getBoolean("remember_passwd", false)) {
            return true;
        } else {
            return false;
        }
    }

    public void startLoginActivity(String activityNum) {
        String[] errorInfos = {"还没登录哟~", activityNum};
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.putExtra("loginTips", errorInfos );
        startActivity(intent);
    }

    class FregmentBroadcaseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }



}