package com.gjf.wherearethey_v2;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.gjf.wherearethey_v2.bean.Friends;
import com.gjf.wherearethey_v2.bean.MessageIO;
import com.gjf.wherearethey_v2.bean.NowLocation;
import com.gjf.wherearethey_v2.bean.User;
import com.gjf.wherearethey_v2.task.LoginTask;
import com.gjf.wherearethey_v2.util.AlertDialogUtil;
import com.gjf.wherearethey_v2.util.PermissionsChecker;
import com.gjf.wherearethey_v2.util.SharedUtil;

import java.util.ArrayList;

/**
 * 登录页面类
 *
 * @author gjf
 * @version 1.0
 */
public class LoginActivity extends AppCompatActivity
        implements View.OnClickListener, LoginTask.OnLoginResultListener,
        CompoundButton.OnCheckedChangeListener {
    private EditText et_userId;
    private EditText et_password;
    boolean isOnline;
    private MainApplication app;
    private Button btn_loginConfirm;
    private SharedUtil mShare;
    private CheckBox cb_isRememberPwd;

    /**
     * 权限数组要与Manifest.xml中权限一致
     */
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_PHONE_STATE
    };
    /**
     * 权限相关变量
     */
    private static final String EXTRA_PERMISSIONS =
            "com.java.gjf.wherearethey_v2.permission.extra_permission";
    private static final int PERMISSION_REQUEST_CODE = 0;//系统权限管理页面的参数
    private static final String PACKAGE_URL_SCHEME = "package";//方案scheme
    private PermissionsChecker permissionsChecker;
    private boolean isRequireCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() == null || !getIntent().hasExtra(EXTRA_PERMISSIONS)) {
            isRequireCheck = true;
        }
        setContentView(R.layout.activity_login);
        permissionsChecker = new PermissionsChecker(this);
        app = MainApplication.getInstance();
        mShare = SharedUtil.getInstance( "share_loginInfo");
        et_userId = findViewById(R.id.et_userId);
        et_password = findViewById(R.id.et_password);
        cb_isRememberPwd = findViewById(R.id.cb_isRememberPwd);
        cb_isRememberPwd.setOnCheckedChangeListener(this);
        if (!app.getUser().getUserId().equals("")) {
            et_userId.setText(app.getUser().getUserId());
            et_password.setText(app.getUser().getPassword());
            isOnline = true;
        } else {
            et_userId.setText(mShare.readStringShared("ID", ""));
            if (mShare.readStringShared("isRemember", "").equals("yes")) {
                et_password.setText(mShare.readStringShared("password", ""));
                cb_isRememberPwd.setChecked(true);
            } else {
                cb_isRememberPwd.setChecked(false);
            }
            isOnline = false;
        }
        btn_loginConfirm = findViewById(R.id.btn_loginConfirm);
        btn_loginConfirm.setOnClickListener(this);
        findViewById(R.id.btn_register).setOnClickListener(this);
        findViewById(R.id.btn_forgetPassword).setOnClickListener(this);
        if (!app.getUser().getUserId().equals("")) {
            et_userId.setText(app.getUser().getUserId());
            et_password.setText(app.getUser().getPassword());
            btn_loginConfirm.setText("注销");
        } else {
            btn_loginConfirm.setText("登录");
        }
    }

    /**
     * 记住密码选择状态改变响应方法
     *
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mShare.writeStringShared("isRemember", "yes");
            mShare.writeStringShared("password", et_password.getText().toString());
        } else {
            mShare.writeStringShared("isRemember", "no");
            mShare.writeStringShared("password", "");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isRequireCheck) {
            if (permissionsChecker.lacksPermissions(PERMISSIONS)) {
                ActivityCompat.requestPermissions(this,
                        PERMISSIONS, PERMISSION_REQUEST_CODE);
            }
        } else {
            isRequireCheck = true;
        }
        if (mShare.readStringShared("isRemember", "").equals("yes")) {
            et_password.setText(mShare.readStringShared("password", ""));
            cb_isRememberPwd.setChecked(true);
        } else {
            cb_isRememberPwd.setChecked(false);
        }
    }

    /**
     * 请求权限申请结果
     *
     * @param requestCode  请求代码
     * @param permissions  权限集合
     * @param grantResults 获取结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
            isRequireCheck = true;
        } else {
            isRequireCheck = false;
            showMissingPermissionDialog();
        }
    }

    /**
     * @param grantResults 权限回调数组，-1表示未获取
     * @return 是否已获取
     */
    private boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 未在Manifest.xml声明权限提示对话框
     */
    private void showMissingPermissionDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(R.string.help);
        builder.setMessage(R.string.string_help_text);
        //拒绝退出应用
        builder.setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        //同意则进行设置
        builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts(PACKAGE_URL_SCHEME, getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_loginConfirm:
                if (!isAnyEmpty()) {
                    if (!isOnline) {
                        btn_loginConfirm.setEnabled(false);
                        User user = new User();
                        user.setUserId(et_userId.getText().toString());
                        user.setPassword(et_password.getText().toString());
                        LoginTask logintask = new LoginTask();
                        logintask.setOnLoginResultListener(this);
                        logintask.execute(user);
                    } else {
                        app.setUser(new User());
                        app.setFriends(new ArrayList<Friends>());
                        app.setMessages(new ArrayList<MessageIO>());
                        app.setNowLocations(new ArrayList<NowLocation>());
                        Toast.makeText(LoginActivity.this, "注销成功",
                                Toast.LENGTH_SHORT).show();
                        app.getTga().finish();
                        if (!mShare.readStringShared("isRemember", "").equals("yes")) {
                            et_password.setText("");
                        }
                        btn_loginConfirm.setText("登录");
                        isOnline = false;
                    }
                } else {
                    AlertDialogUtil.show(LoginActivity.this,
                            "用户名和密码任何一项不能为空！");
                }
                //成功就finish，失败弹出提示
                break;
            case R.id.btn_register://注册事件
                if (app.getUser().getUserId().equals("")) {
                    Intent registerIntent = new Intent(this, RegisterActivity.class);
                    startActivity(registerIntent);
                } else {
                    Toast.makeText(this, "您已登录，请先注销，再注册！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_forgetPassword://忘记密码事件
                if (!et_userId.getText().toString().equals("")) {
                    Intent forgetPwdIntent = new Intent(this, ForgetPwdActivity.class);
                    forgetPwdIntent.putExtra("userId", et_userId.getText().toString());
                    startActivity(forgetPwdIntent);
                } else {
                    AlertDialogUtil.show(LoginActivity.this,
                            "要输入用户名才能去重置密码");
                }
                break;
        }
    }

    /**
     * 判断输入内容是否为空
     */
    private boolean isAnyEmpty() {
        return (et_userId.getText().toString().equals("") ||
                et_password.getText().toString().equals(""));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK &&
                event.getAction() == KeyEvent.ACTION_DOWN) {
            if (!app.getUser().getUserId().equals("")) {//已登录
                finish();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("退出");
                builder.setMessage("您确定退出吗？");
                builder.setPositiveButton("是的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.setNegativeButton("再看看", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onGetLTResult(User user) {
        if (!user.getUserId().equals("")) {
            //数据解密
            user.decrypt();
            app.setUser(user);
            Toast.makeText(this, "登陆成功", Toast.LENGTH_SHORT).show();
            btn_loginConfirm.setText("注销");
            isOnline = true;
            mShare.writeStringShared("ID", user.getUserId());
            if (cb_isRememberPwd.isChecked()) {
                mShare.writeStringShared("password", et_password.getText().toString());
            }
            Intent intent = new Intent(this, TabGroupActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, user.getUserType(), Toast.LENGTH_SHORT).show();
        }
        btn_loginConfirm.setEnabled(true);
    }

}
