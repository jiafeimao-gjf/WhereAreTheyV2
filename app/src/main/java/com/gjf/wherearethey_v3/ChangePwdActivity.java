package com.gjf.wherearethey_v3;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gjf.wherearethey_v3.bean.User;
import com.gjf.wherearethey_v3.task.ChangePwdTask;
import com.gjf.wherearethey_v3.util.AlertDialogUtil;

/**
 * 修改密码页面类
 * @author gjf
 * @version 1.0
 */
public class ChangePwdActivity extends AppCompatActivity
        implements View.OnClickListener,ChangePwdTask.OnChangePwdResultListener{
    private TextView tv_cPwdUserId;
    private EditText et_oldPassword;
    private EditText et_newPassword;
    private EditText et_cfNewPassword;
    private Button btn_pwdChangeConfirm;
    private MainApplication app;

    /**
     * 覆盖页面视图初始化方法
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        app = MainApplication.getInstance();
        tv_cPwdUserId = findViewById(R.id.tv_cPwdUserId);
        String idInfo = "ID:" + app.getUser().getUserId();
        tv_cPwdUserId.setText(idInfo);
        et_oldPassword = findViewById(R.id.et_oldPassword);
        et_oldPassword.setText(app.getUser().getPassword());
        et_oldPassword.setEnabled(false);//使原密码无法编辑
        et_newPassword = findViewById(R.id.et_newPassword);
        et_cfNewPassword = findViewById(R.id.et_cfNewPassword);
        btn_pwdChangeConfirm = findViewById(R.id.btn_pwdChangeConfirm);
        btn_pwdChangeConfirm.setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.tb_changePwd);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 点击事件响应方法
     * @param v 点击的视图对象
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_pwdChangeConfirm) {
            if (!isAnyEmpty()) {
                if (et_newPassword.getText().toString().
                        equals(et_cfNewPassword.getText().toString())) {
                    if (!et_newPassword.getText().toString().
                            equals(app.getUser().getPassword())) {
                        //修改数据库中的密码
                        app.getUser().setPassword(et_cfNewPassword.getText().toString());
                        ChangePwdTask changePwdTask = new ChangePwdTask();
                        changePwdTask.setOnChangePwdResultListener(this);
                        User user = new User(app.getUser());
                        user.encrypt();//加密
                        changePwdTask.execute(user);
                    } else {
                        AlertDialogUtil.show(this, "新密码和旧密码不能一样哦！");
                    }
                } else {
                    AlertDialogUtil.show(this, "密码和验证密码不一致");
                }
            } else {
                AlertDialogUtil.show(this, "密码和验证密码任何一项不能为空");
            }
        }
    }

    /**
     * 修改密码结果方法
     * @param res 修改结果
     */
    @Override
    public void onGetCPTResult(String res) {
        if(res.equals("修改成功")){
            app.setUser(new User());
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示'")
                    .setMessage("密码已重置，请重新登录");
            builder.setPositiveButton("去登录", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(ChangePwdActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                    app.getTga().finish();
                }
            });
            builder.show();
        }else{
            AlertDialogUtil.show(this,res);
        }
    }

    /**
     * 判断输入内容是否为空
     * @return 空为false，不空为true
     */
    private boolean isAnyEmpty(){
        return et_newPassword.getText().toString().equals("")||
                et_cfNewPassword.getText().toString().equals("");
    }

    /**
     * 按下返回键
     * @param keyCode
     * @param event
     * @return 结果
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK &&
                event.getAction() == KeyEvent.ACTION_DOWN){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}