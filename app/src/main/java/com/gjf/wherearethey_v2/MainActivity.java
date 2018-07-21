package com.gjf.wherearethey_v2;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gjf.wherearethey_v2.util.StatusBarUtil;

/**
 * 加载页面类，实现出事页面的显示
 * @author gjf
 * @version 2.0
 */
public class MainActivity extends AppCompatActivity {

    private Handler mHandler = new Handler();
    private Runnable jumpTask = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtil.fullScreen(this);
        mHandler.postDelayed(jumpTask,2000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
