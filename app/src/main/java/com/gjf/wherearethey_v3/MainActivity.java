package com.gjf.wherearethey_v3;

import static com.gjf.wherearethey_v3.util.SharedUtil.STATIC_PRIVACY_SP;
import static com.gjf.wherearethey_v3.util.SharedUtil.STATIC_PRIVACY_SP_KEY;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.gjf.wherearethey_v3.util.AlertDialogUtil;
import com.gjf.wherearethey_v3.util.LogUtil;
import com.gjf.wherearethey_v3.util.SharedUtil;
import com.gjf.wherearethey_v3.util.StatusBarUtil;

/**
 * 加载页面类，实现出事页面的显示
 *
 * @author gjf
 * @version 2.0
 */
public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";
    private Handler mHandler = new Handler();
    private Runnable jumpTask = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtil.fullScreen(this);
        LogUtil.i(TAG, "[onCreate]");
        if (!SharedUtil.getInstance(STATIC_PRIVACY_SP).readBooleanShared(STATIC_PRIVACY_SP_KEY, false)) {
            privacyDialogShow();
        } else {
            mHandler.postDelayed(jumpTask, 1000);
        }
    }

    private void privacyDialogShow() {
        LogUtil.i(TAG, "[privacyDialogShow]");
        AlertDialogUtil.show(this, "隐私协议", "我们收集存储的信息：\n1、您的账号和密码 \n2、您的位置信息 \n3、您与好友的聊天信息 \n\n 以上信息都会加密存储。", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedUtil.getInstance(STATIC_PRIVACY_SP).writeBooleanShared(STATIC_PRIVACY_SP_KEY, true, new SharedUtil.FinishCallback() {
                    @Override
                    public void finishSpWrite() {
                        MainApplication.getInstance().intBaiduMap();
                        mHandler.postDelayed(jumpTask, 1000);
                    }
                });
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "即将退出！！！", Toast.LENGTH_LONG).show();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.this.finish();
                    }
                }, 2000);
                dialog.dismiss();

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
