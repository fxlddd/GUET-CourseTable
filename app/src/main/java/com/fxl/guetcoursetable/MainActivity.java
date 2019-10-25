package com.fxl.guetcoursetable;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.fxl.guetcoursetable.Utils.restartapp.RestartAPPTool;
import com.fxl.guetcoursetable.corsetable.ImportCourseTableActivity;
import com.fxl.guetcoursetable.corsetable.DailyCourseFregment;
import com.fxl.guetcoursetable.corsetable.WeekFregment;
import com.fxl.guetcoursetable.login.LoginActivity;


import java.util.Calendar;




public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ViewPager pager;
    TabLayout tabLayout;
    TextView textViewStudentName;
    TextView textViewStudentID;
    Spinner weekNumSpinner;
    SharedPreferences preferences;
    int weekNum = 1;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences("student_infos", MODE_PRIVATE);
        context = getApplicationContext();
        getWeek();
        initNavigation();
        initViewPager();
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(pager);
        tabLayout.getTabAt(0).setText("日课表");
        tabLayout.getTabAt(1).setText("周课表");
        tabLayout.getTabAt(2).setText("更多...");
    }



    public static Context getContext() {
        return context;
    }


    private void initNavigation() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        weekNumSpinner = (Spinner) findViewById(R.id.toolbar_week);
        setWeekNum();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar.setNavigationIcon(R.drawable.ic_guet_small);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isRememberPasswd = preferences.getBoolean("remember_passwd", false);
                if (isRememberPasswd){
                    Intent intent =new Intent(MainActivity.this, ImportCourseTableActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    String[] logintips = {"","0"};
                    intent.putExtra("loginTips",logintips);
                    startActivity(intent);
                }
            }
        });

        weekNumSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//                发送通知给weekFragment更新课表显示
                Intent intent = new Intent("android.intent.action.upDateCorseShow");
                intent.putExtra("week", position+1);
                LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void initViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DailyCourseFregment());
        adapter.addFragment(new WeekFregment());
        adapter.addFragment(new EduAdminFregment());
        pager = (ViewPager) this.findViewById(R.id.pager);
        pager.setAdapter(adapter);


        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    //weekNum选中当前周
                    setWeekNum();
                    weekNumSpinner.setVisibility(View.VISIBLE);
                }else{
                    weekNumSpinner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setWeekNum() {
        //      为weeknum设置正确的时间
        weekNumSpinner.setSelection(weekNum-1,false);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.toolbar_main, menu);
//        return true;
//    }
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:         //一定要是Android前缀里面的  否则无效
                mDrawerLayout.openDrawer(GravityCompat.START);
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                break;
        }
        return  true;
    }
*/

/*//    再按一次退出提示
    private long exitTime = 0;
    @Override
    public void onBackPressed() {
//        如果系统时间-exitTime>2000,则exitTime等于此时系统时间
        if((System.currentTimeMillis()-exitTime) > 2000){
            Toast.makeText(getApplicationContext(), "再按一次返回退出", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        }else{
            super.onBackPressed();
        }

    }*/


    private void getWeek() {
        SharedPreferences.Editor editor = getSharedPreferences("student_infos",MODE_PRIVATE)
                .edit();
        Calendar calendar =  Calendar.getInstance();
        int startWeekNumber = preferences.getInt("start_week_num", 0);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        int publicWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (preferences.getInt("start_week_num", 0) != 0) {
            weekNum = publicWeek - startWeekNumber + 1;
            if (weekNum >= 20) {
                weekNum = 20;
            } else if (weekNum < 0) {
                weekNum = preferences.getInt("week_num",0) + publicWeek;
            }
            editor.putInt("week_num", weekNum);
        }
        editor.putInt("dayofweek", dayOfWeek);
        editor.apply();
    }


//  返回前台时查看日期是否一致，防止长时间后台第二天信息更新不及时
//  如果不一致则重启刷新信息
    @Override
    protected void onPostResume() {
        super.onPostResume();
        Calendar calendar =  Calendar.getInstance();
        int dayOfWeekNow = calendar.get(Calendar.DAY_OF_WEEK);
        if (preferences.getInt("dayofweek", 1) != dayOfWeekNow) {
            RestartAPPTool.restartAPP(getContext(),500);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
