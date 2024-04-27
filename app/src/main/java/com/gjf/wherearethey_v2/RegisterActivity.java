package com.gjf.wherearethey_v2;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.gjf.wherearethey_v2.bean.User;
import com.gjf.wherearethey_v2.task.RegisterTask;
import com.gjf.wherearethey_v2.util.AlertDialogUtil;
import com.gjf.wherearethey_v2.util.LogUtil;

/**
 * 注册页面类
 * @author gjf
 * @version 1.0
 */
public class RegisterActivity extends AppCompatActivity
        implements RegisterTask.OnRegisterResultListener{
    private static String TAG = "RegisterActivity";

    private EditText et_registerUserId;
    private EditText et_registerPassword;
    private EditText et_registerUserName;
    private EditText et_pwdProtectA;
    private Spinner sp_pwdProtectC;
    private User user;
    private Button btn_registerConfirm;
    private String[] questions = {"你喜欢的人是谁？","你的家乡是哪里？","你的父母结婚纪念日？",
            "你的小学班主任的名字？","你妈妈的名字？","你爸爸的名字？"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        LogUtil.i(TAG,"[onCreate]");
        user = new User();
        et_registerUserId = findViewById(R.id.et_registerUserId);
        et_registerPassword = findViewById(R.id.et_registerPassword);
        et_registerUserName = findViewById(R.id.et_registerUserName);
        et_pwdProtectA  = findViewById(R.id.et_pwdProtectA);
        sp_pwdProtectC = findViewById(R.id.sp_pwdProtectC);
        //Spinner初始化
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                R.layout.item_select,questions);
        sp_pwdProtectC.setPrompt("请选择问题");
        sp_pwdProtectC.setAdapter(arrayAdapter);
        sp_pwdProtectC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                user.setPwdProtectId(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        sp_pwdProtectC.setSelection(0);
        user.setPwdProtectId(0);

        btn_registerConfirm = findViewById(R.id.btn_registerConfirm);
        btn_registerConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAnyEmpty()){
                    btn_registerConfirm.setEnabled(false);
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle("注册信息如下：");
                    String msg = "";
                    msg += "用户Id:" + et_registerUserId.getText().toString();
                    msg += "\n用户密码：" + et_registerPassword.getText().toString();
                    msg += "\n昵称：" + et_registerUserName.getText().toString();
                    msg += "\n" + (sp_pwdProtectC.getSelectedItemPosition() + 1)+
                            ":"+questions[sp_pwdProtectC.getSelectedItemPosition()] +
                            "：" + et_pwdProtectA.getText().toString()+
                            "\n提示:密保问题的答案用于找回密码，请牢记。";
                    builder.setMessage(msg);
                    builder.setPositiveButton("确认注册", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            User user = new User();
                            user.setUserId(et_registerUserId.getText().toString());
                            user.setPassword(et_registerPassword.getText().toString());
                            user.setUserName(et_registerUserName.getText().toString());
                            user.setPwdProtectId(sp_pwdProtectC.getSelectedItemPosition() + 1);
                            user.setPwdProtectA(et_pwdProtectA.getText().toString());
                            user.setUserType("normal");
                            //数据加密
                            user.encrypt();
                            //调用异步任务,向数据库加入数据
                            RegisterTask registerTask = new RegisterTask();
                            registerTask.setOnRegisterResultListener(RegisterActivity.this);
                            registerTask.execute(user);
                        }
                    });
                    builder.setNegativeButton("再修改一下", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                }else{
                    AlertDialogUtil.show(RegisterActivity.this,"输入信息任何一项不能为空！");
                }

            }
        });
        //返回按钮
        Toolbar toolbar = findViewById(R.id.tb_register);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onGetRTResult(String res) {
        LogUtil.i(TAG,"[onGetRTResult] res");
        AlertDialogUtil.show(this,"注册结果：",res);
        btn_registerConfirm.setEnabled(true);
        finish();
    }

    /**
     * 判断输入内容是否为空
     * @return 空为true,不空为false
     */
    private boolean isAnyEmpty(){
        return (et_registerUserId.getText().toString().equals("")||
                et_registerPassword.getText().toString().equals("")||
                et_registerUserName.getText().toString().equals("")||
                et_pwdProtectA.getText().toString().equals(""));
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

