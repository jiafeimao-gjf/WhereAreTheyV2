package com.gjf.wherearethey_v3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gjf.wherearethey_v3.bean.Friends;
import com.gjf.wherearethey_v3.bean.MessageIO;
import com.gjf.wherearethey_v3.task.ChangeIsReceiveLocationTask;
import com.gjf.wherearethey_v3.task.DeleteFriendTask;
import com.gjf.wherearethey_v3.task.GetFriendsInfoTask;
import com.gjf.wherearethey_v3.task.SendMsgTask;

import java.util.ArrayList;
import java.util.Date;

/**
 * 朋友详细信息页面类
 * @author gjf
 * @version 1.1
 */
public class FriendsInfoActivity extends AppCompatActivity
    implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener,
        GetFriendsInfoTask.OnGetFriendsInfoResultListener,
        ChangeIsReceiveLocationTask.OnGetChangeIsReceiveResultListener{
    MainApplication app;
    TextView tv_idInfo;
    TextView tv_nameInfo;
    CheckBox cb_isReceiveLocation;
    ArrayList<String> friendInfo;
    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_info);

        Intent intent = getIntent();
        position = intent.getIntExtra("friendPosition",1);
        Friends friends = app.getFriends().get(position);

        tv_idInfo = findViewById(R.id.tv_idInfo);
        tv_nameInfo = findViewById(R.id.tv_nameInfo);
        findViewById(R.id.btn_sendMsg).setOnClickListener(this);
        cb_isReceiveLocation = findViewById(R.id.cb_isReceiveLocation);
        cb_isReceiveLocation.setOnCheckedChangeListener(this);
        findViewById(R.id.btn_deleteFriends).setOnClickListener(this);

        //返回按钮
        Toolbar toolbar = findViewById(R.id.tb_friendsInfo);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        GetFriendsInfoTask getFriendsInfoTask = new GetFriendsInfoTask();
        getFriendsInfoTask.setOnGetFriendsInfoResultListener(this);
        getFriendsInfoTask.execute(friends);
    }

    @Override
    public void onGetFITResult(ArrayList<String> _friendInfo) {
        friendInfo = _friendInfo;
        if(_friendInfo.size()>1){
            tv_idInfo.setText(_friendInfo.get(0));
            tv_nameInfo.setText(_friendInfo.get(1));
            if(_friendInfo.get(2).equals("yes")){
                cb_isReceiveLocation.setChecked(true);
            }else{
                cb_isReceiveLocation.setChecked(false);
            }
        }else{
            Toast.makeText(this, _friendInfo.get(0), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_sendMsg:
                //进入发送聊天室活动页
                AlertDialog.Builder msgDialog = new AlertDialog.Builder(this);
                msgDialog.setMessage("填写发送消息：");
                final EditText et_message = new EditText(this);
                msgDialog.setView(et_message);
                msgDialog.setPositiveButton("发送", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!et_message.getText().toString().equals("")){
                            SendMsgTask sendMsgTask = new SendMsgTask();
                            sendMsgTask.setOnInsertMsgResultListener(new SendMsgTask.OnInsertMsgResultListener() {
                                @Override
                                public void onGetSMTResult(String res) {
                                    if (res.equals("success")) {
                                        Toast.makeText(FriendsInfoActivity.this,
                                                "消息已发送", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(FriendsInfoActivity.this, res,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            MessageIO messageIO = new MessageIO(
                                    app.getUser().getUserId(), friendInfo.get(0),
                                    et_message.getText().toString(), "normal");
                            messageIO.encrypt();//加密
                            sendMsgTask.execute(messageIO);
                        }else{
                            Toast.makeText(FriendsInfoActivity.this,
                                    "不能发送空消息", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                msgDialog.setNegativeButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                });
                msgDialog.show();
                break;
            case R.id.btn_deleteFriends:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("你要删除该好友吗？");
                builder.setMessage(friendInfo.get(1));
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //将该好友从公网数据库中删除
                        DeleteFriendTask deleteFriendTask = new DeleteFriendTask();
                        deleteFriendTask.setOnDeleteFriendResultListener(
                                new DeleteFriendTask.OnDeleteFriendResultListener() {
                                    @Override
                                    public void onGetDFTResult(String res) {
                                        Toast.makeText(FriendsInfoActivity.this, res,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                        deleteFriendTask.execute(new Friends(app.getUser().getUserId(),friendInfo.get(0)));
                        //发送解除好友关系信息给对方
                        SendMsgTask sendMsgTask = new SendMsgTask();
                        sendMsgTask.setOnInsertMsgResultListener(new SendMsgTask.OnInsertMsgResultListener() {
                            @Override
                            public void onGetSMTResult(String res) {
                                if(res.equals("success")){
                                    Toast.makeText(FriendsInfoActivity.this,
                                            "已告知对方，解除好友关系",
                                            Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(FriendsInfoActivity.this, res,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        MessageIO messageIO = new MessageIO();
                        messageIO.setSrcId(app.getUser().getUserId());
                        messageIO.setDestId(friendInfo.get(0));
                        messageIO.setMessage(app.getUser().getUserId()+"和你解除了好友关系");
                        messageIO.setTime(new Date());
                        messageIO.setMsgType("delete");
                        messageIO.encrypt();//加密
                        sendMsgTask.execute(messageIO);
                        //从列表删除
                        app.getFriends().remove(position);
                    }
                });
                builder.setNegativeButton("不删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Friends friends = new Friends();
        friends.setUserId(app.getUser().getUserId());
        friends.setFriendId(friendInfo.get(0));
        if(isChecked){
            //更新云上的状态为 yes
            friends.setIsReceiveLocation("yes");
            ChangeIsReceiveLocationTask changeIsReceiveLocationTask = new ChangeIsReceiveLocationTask();
            changeIsReceiveLocationTask.setOnGetChangeIsReceiveResultListener(this);
            changeIsReceiveLocationTask.execute(friends);
        } else{
            //更新云上的状态为 no
            friends.setIsReceiveLocation("no");
            ChangeIsReceiveLocationTask changeIsReceiveLocationTask = new ChangeIsReceiveLocationTask();
            changeIsReceiveLocationTask.setOnGetChangeIsReceiveResultListener(this);
            changeIsReceiveLocationTask.execute(friends);
        }
    }

    @Override
    public void onGetCIRLTResult(String string) {
        if(string.equals("success")){
            if(cb_isReceiveLocation.isChecked()){
                cb_isReceiveLocation.setText("接收该好友位置");
            }else{
                cb_isReceiveLocation.setText("不接收该好友位置");
            }
        }else{
            Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
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
