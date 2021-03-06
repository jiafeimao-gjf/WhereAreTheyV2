package com.gjf.wherearethey_v2;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gjf.wherearethey_v2.bean.MessageIO;
import com.gjf.wherearethey_v2.task.SendMsgTask;
import com.gjf.wherearethey_v2.util.AlertDialogUtil;

/**
 * 设置页面类
 * @author gjf
 * @version 1.0
 */
public class SettingActivity extends AppCompatActivity
        implements SendMsgTask.OnInsertMsgResultListener,View.OnClickListener{
    private MainApplication app;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        app = MainApplication.getInstance();
        findViewById(R.id.btn_locationFre).setOnClickListener(this);
        findViewById(R.id.btn_report).setOnClickListener(this);
        findViewById(R.id.btn_about).setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.tb_setting);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_locationFre:
                AlertDialog.Builder freBuilder = new AlertDialog.Builder(this);
                freBuilder.setTitle("位置分享");
                freBuilder.setMessage("定位置信息发送和接受周期：（5-60，单位秒）");
                final EditText frequency = new EditText(SettingActivity.this);
                frequency.setInputType(InputType.TYPE_CLASS_NUMBER);
                frequency.setText(String.valueOf(app.getLocationFrequency()));
                freBuilder.setView(frequency);
                freBuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!frequency.getText().toString().equals("")){
                            int fre = Integer.valueOf(frequency.getText().toString());
                            if(fre < 5) fre = 5;
                            if(fre > 60) fre = 60;
                            app.setLocationFrequency(fre);
                        }else{
                            Toast.makeText(SettingActivity.this, "输入为空！",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                freBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                });
                freBuilder.show();
                break;
            case R.id.btn_report:
                AlertDialog.Builder reportBuilder = new AlertDialog.Builder(this);
                reportBuilder.setTitle("反馈");
                reportBuilder.setMessage("填入你要反馈的信息：");
                final EditText reportMsg = new EditText(SettingActivity.this);
                reportBuilder.setView(reportMsg);
                reportBuilder.setPositiveButton("发送", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //发送给开发者
                        if(!reportMsg.getText().toString().equals("")){
                            String srcId = app.getUser().getUserId();
                            if(!srcId.equals("admin")){//当前登陆的不是开发者
                                srcId = app.getUser().getUserId();
                                MessageIO messageIO = new MessageIO(srcId, "admin",
                                        reportMsg.getText().toString(),"report");
                                messageIO.encrypt();//加密
                                SendMsgTask sendMsgTask = new SendMsgTask();
                                sendMsgTask.setOnInsertMsgResultListener(SettingActivity.this);
                                sendMsgTask.execute(messageIO);
                            }else{//当前登录的是开发者
                                Toast.makeText(SettingActivity.this,
                                        "开发者不能给自己发消息哦！", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(SettingActivity.this, "消息不能为空",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                reportBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                });
                reportBuilder.show();
                break;
            case R.id.btn_about:
                AlertDialogUtil.show(this,"关于",
                        "描述：专为朋友的位置共享\n"+"开发者：加菲猫"+"\n"+"版本：2.1.2");
                break;
        }
    }

    @Override
    public void onGetSMTResult(String res) {
        Toast.makeText(SettingActivity.this, res, Toast.LENGTH_SHORT).show();
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