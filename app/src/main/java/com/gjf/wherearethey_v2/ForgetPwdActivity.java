package com.gjf.wherearethey_v2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gjf.wherearethey_v2.bean.User;
import com.gjf.wherearethey_v2.task.GetInfoByIdTask;
import com.gjf.wherearethey_v2.util.AlertDialogUtil;

/**
 * 忘记密码页面类
 * @author gjf
 * @version 1.0
 */
public class ForgetPwdActivity extends AppCompatActivity
        implements View.OnClickListener,GetInfoByIdTask.OnGetInfoResultListener{

    private EditText et_pwdProtectA;
    private TextView tv_pwdProtectQ;
    private User user;
    private String[] questions = {"你喜欢的人是谁？","你的家乡是哪里？","你的父母结婚纪念日？",
            "你的小学班主任的名字？","你妈妈的名字？","你爸爸的名字？"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);
        user = new User();
        Intent intent = getIntent();//获取传递的消息
        user.setUserId(intent.getStringExtra("userId"));
        TextView tv_youId = findViewById(R.id.tv_yourId);
        String userIdDisp = "ID:"+user.getUserId();
        tv_youId.setText(userIdDisp);
        et_pwdProtectA = findViewById(R.id.et_pwdProtectA);
        tv_pwdProtectQ = findViewById(R.id.tv_pwdProtectQ);
        Button btn_textA = findViewById(R.id.btn_textA);
        btn_textA.setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.tb_forgetPwd);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 通过userId获取密保问题的编号，设置tv_pwdProtectQ
        GetInfoByIdTask getInfoByIdTask = new GetInfoByIdTask();
        getInfoByIdTask.setOnGetInfoResultListener(this);
        getInfoByIdTask.execute(user.getUserId());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_textA:
                if(user.getUserId().equals("")){
                    Toast.makeText(this, user.getUserType(), Toast.LENGTH_SHORT).show();
                }else{
                    if(!et_pwdProtectA.getText().toString().equals("")){
                        //验证密保答案的真确性
                        if(et_pwdProtectA.getText().toString().equals(user.getPwdProtectA())){
                            String desc = "答案正确。\n你的密码是："+user.getPassword();
                            AlertDialogUtil.show(this,desc);
                        }else{
                            AlertDialogUtil.show(this,"答案错误。");
                        }
                    }else{
                        AlertDialogUtil.show(ForgetPwdActivity.this,"答案不能为空");
                    }
                }
                break;
        }
    }

    @Override
    public void onGetIBITResult(User user) {
        if(!user.getUserId().equals("")){
            //数据解密
            user.decrypt();
            this.user = new User(user);
            tv_pwdProtectQ.setText(questions[user.getPwdProtectId()-1]);
        }else{
            this.user = new User(user);
            Toast.makeText(this, user.getUserType(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK &&
                event.getAction() == KeyEvent.ACTION_DOWN){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
