package com.fxl.guetcoursetable.settings;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fxl.guetcoursetable.R;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;


/**
 * Created by FXL-PC on 2017/2/26.
 */

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{
    Toolbar toolbar;
    TextView aboutSoftware;
    TextView update;
    TextView customIP;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initToolBar();
        aboutSoftware = (TextView) findViewById(R.id.about_software);
        aboutSoftware.setOnClickListener(this);

        update = (TextView) findViewById(R.id.update);
        update.setOnClickListener(this);

        customIP = (TextView) findViewById(R.id.customIP);
        customIP.setOnClickListener(this);
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        toolbar.setTitle("设置");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.customIP:{
                Intent it = new Intent(this,CustomIPActivity.class);
                startActivity(it);
                break;
            }

            case R.id.update:{
                showUpdateDialog();
                break;
            }

            case R.id.about_software:{
                showAboutDialog();
                break;
            }
        }
    }

    public void showUpdateDialog() {
        AlertDialog.Builder warnDialog = new AlertDialog.Builder(this);
        warnDialog.setTitle("升级说明");
        warnDialog.setMessage("老学长快要毕业了，bug修的差不多了，还有个快速评教一直没时间弄，这应该是最后一版了。如果还有更新，会在小米和华为商店更新。"
        );
        warnDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        warnDialog.show();
    }

    private void showAboutDialog() {
        AlertDialog.Builder aboutDialog = new AlertDialog.Builder(this);
        aboutDialog.setTitle("关于软件");
        aboutDialog.setMessage("本软件由个人所写，与学校官方无关。" +
                "如果您有任何关于本软件的想法或建议，欢迎与我交流。" +
                "\nVersion:0.9.2"+
                "\nMail: fxlddd@foxmail.com"

        );
        aboutDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        aboutDialog.show();
    }
}
