package com.gjf.wherearethey_v3.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gjf.wherearethey_v3.ChangePwdActivity;
import com.gjf.wherearethey_v3.DisplayFriendsActivity;
import com.gjf.wherearethey_v3.LoginActivity;
import com.gjf.wherearethey_v3.MainApplication;
import com.gjf.wherearethey_v3.R;
import com.gjf.wherearethey_v3.SettingActivity;
import com.gjf.wherearethey_v3.bean.Friends;
import com.gjf.wherearethey_v3.bean.MessageIO;
import com.gjf.wherearethey_v3.bean.User;
import com.gjf.wherearethey_v3.task.ChangeNameTask;
import com.gjf.wherearethey_v3.task.CheckIDExistTask;
import com.gjf.wherearethey_v3.task.IsFriendsTask;
import com.gjf.wherearethey_v3.task.SendMsgTask;
import com.gjf.wherearethey_v3.util.AlertDialogUtil;

/** 个人信息页面
 * @author gjf
 * @version 1.0
 */
public class MyInfoFragment extends Fragment implements View.OnClickListener,
        CheckIDExistTask.OnCheckIdResultListener,//获取ID检测结果监听器
        IsFriendsTask.OnCheckIsFriendsResultListener,//获取是否是朋友的监听器
        ChangeNameTask.OnChangeNameResultListener,//获取修改昵称结果监听器
        SendMsgTask.OnInsertMsgResultListener{//获取消息发送结果监听器
    private Context mContext;
    private View mView;
    private MainApplication app;
    private TextView tv_userName;
    private Button btn_login;
    private EditText friendId;
    public MyInfoFragment() { }

    /**
     * 创建页面视图
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return 页面视图
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        app = MainApplication.getInstance();
        mView = inflater.inflate(R.layout.fragment_my_info, container, false);
        tv_userName = mView.findViewById(R.id.tv_userName);
        btn_login = mView.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        mView.findViewById(R.id.btn_changeMyName).setOnClickListener(this);
        mView.findViewById(R.id.btn_changePassword).setOnClickListener(this);
        mView.findViewById(R.id.btn_addFriend).setOnClickListener(this);
        mView.findViewById(R.id.btn_displayFriends).setOnClickListener(this);
        mView.findViewById(R.id.btn_settings).setOnClickListener(this);
        return mView;
    }

    /**
     * 覆盖onResume()方法，根据登陆状态，实现页面登陆状态动态显示
     */
    @Override
    public void onResume() {
        super.onResume();
        if(!app.getUser().getUserId().equals("")){
            tv_userName.setText(app.getUser().getUserName());
            btn_login.setText("去注销");
        }else{
            tv_userName.setText(R.string.userName);
            btn_login.setText("去登录");
        }
    }

    /**\n     * 点击事件响应\n     * @param v 被点击的视图对象\n     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login) {//登陆事件或者注销事件
            Intent LoginIntent = new Intent(mContext, LoginActivity.class);
            startActivity(LoginIntent);
        } else if (v.getId() == R.id.btn_changeMyName) {//修改昵称事件
            if (!app.getUser().getUserId().equals("")) {
                AlertDialog.Builder CMNbuilder = new AlertDialog.Builder(mContext);
                CMNbuilder.setTitle("修改昵称");
                CMNbuilder.setMessage("填入新昵称：");
                final EditText newName = new EditText(mContext);
                newName.setInputType(InputType.TYPE_CLASS_TEXT);
                CMNbuilder.setView(newName);
                CMNbuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!newName.getText().toString().equals("")) {
                            //修改数据库中的昵称
                            app.getUser().setUserName(newName.getText().toString());
                            ChangeNameTask changeNameTask = new ChangeNameTask();
                            changeNameTask.setOnChangePwdResultListener(MyInfoFragment.this);
                            changeNameTask.execute(new User(app.getUser()));
                        } else {
                            Toast.makeText(mContext, "输入不能为空！",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                CMNbuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                CMNbuilder.show();
            } else {
                AlertDialogUtil.show(getActivity(), "您还没有登录");
            }
        } else if (v.getId() == R.id.btn_changePassword) {//修改密码事件
            if (!app.getUser().getUserId().equals("")) {
                Intent changePwd = new Intent(mContext, ChangePwdActivity.class);
                startActivity(changePwd);
            } else {
                AlertDialogUtil.show(getActivity(), "您还没有登录");
            }
        } else if (v.getId() == R.id.btn_displayFriends) {//显示好友事件
            if(!app.getUser().getUserId().equals("")){
                Intent displayFriends = new Intent(mContext, DisplayFriendsActivity.class);
                startActivity(displayFriends);
            }else{
                AlertDialogUtil.show(getActivity(),"您还没有登陆");
            }
        } else if (v.getId() == R.id.btn_addFriend) {//加好友事件
            if (!app.getUser().getUserId().equals("")) {
                AlertDialog.Builder AFbuilder = new AlertDialog.Builder(mContext);
                AFbuilder.setTitle("加朋友");
                AFbuilder.setMessage("填入朋友ID：");
                friendId = new EditText(mContext);
                friendId.setInputType(InputType.TYPE_CLASS_TEXT);
                AFbuilder.setView(friendId);
                AFbuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!friendId.getText().toString().equals("")) {
                            if(!friendId.getText().toString().equals(app.getUser().getUserId())) {
                                // 检查该ID是否已存在
                                CheckIDExistTask checkIDExistTask = new CheckIDExistTask();
                                checkIDExistTask.setOnCheckIdResultListener(MyInfoFragment.this);
                                checkIDExistTask.execute(friendId.getText().toString());
                            }else{
                                Toast.makeText(mContext, "不能加自己为好友哦",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            AlertDialogUtil.show(getActivity(), "输入不能为空");
                        }
                    }
                });
                AFbuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AFbuilder.show();
            }else{
                AlertDialogUtil.show(getActivity(), "您还没有登录");
            }
        } else if (v.getId() == R.id.btn_settings) {//设置事件
            Intent settingIntent = new Intent(mContext, SettingActivity.class);
            startActivity(settingIntent);
        }
    }

    /**
     * 修改昵称结果方法
     * @param res 结果信息
     */
    @Override
    public void onGetCNTResult(String res) {
        Toast.makeText(mContext, res, Toast.LENGTH_SHORT).show();
        tv_userName.setText(app.getUser().getUserName());
    }

    /**
     * 检查ID是否存在结果方法
     * @param res 结果信息
     */
    @Override
    public void onGetCIETResult(String res) {
        if(res.equals("exist")){//用户存在才能加好友
            //检查是否已经是好友
            IsFriendsTask isFriendsTask = new IsFriendsTask();
            isFriendsTask.setOnCheckIsFriendsResultListener(this);
            isFriendsTask.execute(new Friends(friendId.getText().toString(),
                    app.getUser().getUserId()));
        }else{
            Toast.makeText(mContext, res, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 检查是否是朋友结果方法
     * @param res 检查结果
     */
    @Override
    public void onGetIFTResult(String res) {
        if (!res.equals("yes")) {//不是朋友，才能加好友
            //发送加好友的信息到数据库
            MessageIO messageIO = new MessageIO(app.getUser().getUserId(),
                    friendId.getText().toString(), "加好友", "request");
            messageIO.encrypt();//加密
            SendMsgTask sendMsgTask = new SendMsgTask();
            sendMsgTask.setOnInsertMsgResultListener(this);
            sendMsgTask.execute(messageIO);
        }else{
            Toast.makeText(mContext, "你与"+ friendId.getText().toString()+
                    "已经是好友关系了", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 发送加好友请求消息结果方法
     * @param res 请求消息发送结果
     */
    @Override
    public void onGetSMTResult(String res) {
        if(res.equals("success")){
            Toast.makeText(mContext, "已成功发送加好友请求", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(mContext, res, Toast.LENGTH_SHORT).show();
        }
    }
}
