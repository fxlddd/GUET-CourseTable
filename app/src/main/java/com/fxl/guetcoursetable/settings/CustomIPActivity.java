package com.fxl.guetcoursetable.settings;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fxl.guetcoursetable.R;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by FXL-PC on 2017/11/28.
 */

public class CustomIPActivity extends AppCompatActivity {

    Button btn_customIP;
    EditText et_customIP;
    EditText et_customLIBIP;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customip);

        initToolbar();

        btn_customIP = (Button) findViewById(R.id.btn_customip);
        et_customIP = (EditText) findViewById(R.id.et_customip);
        et_customLIBIP = (EditText) findViewById(R.id.et_customlibip);

        SharedPreferences preferences = getSharedPreferences("student_infos", MODE_PRIVATE);
        et_customIP.setText(preferences.getString("server","bkjw2.guet.edu.cn"));
        et_customIP.setSelection(et_customIP.getText().length());

        et_customLIBIP.setText(preferences.getString("libserver","202.193.70.139"));
        et_customLIBIP.setSelection(et_customLIBIP.getText().length());

        btn_customIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = getSharedPreferences("student_infos",MODE_PRIVATE)
                        .edit();
                editor.putString("server", et_customIP.getText().toString());
                editor.putString("libserver", et_customLIBIP.getText().toString());
                editor.apply();
                Toast.makeText(getBaseContext(),"设置成功~",Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_customip);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setTitle("自定义域名/IP");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
