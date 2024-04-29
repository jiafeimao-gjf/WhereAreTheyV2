package com.gjf.wherearethey_v3.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.gjf.wherearethey_v3.MainApplication;
import com.gjf.wherearethey_v3.R;
import com.gjf.wherearethey_v3.adapter.MessagesListAdapter;
import com.gjf.wherearethey_v3.bean.MessageIO;
import com.gjf.wherearethey_v3.task.GetMyMsgTask;

import java.util.ArrayList;

/** 消息显示页面，消息的获取和显示
 * @author gjf
 * @version 2.0
 */
public class MessageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        GetMyMsgTask.OnGetMyMsgResultListener{
    private Context mContext;
    private View mView = null;
    private SwipeRefreshLayout srl_msgRefresh;//刷新小视图
    private ListView lv_messages;
    private MainApplication app;
    private MessagesListAdapter messagesAdapter;

    /**
     * 初始化页面视图
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
        mView = inflater.inflate(R.layout.fragment_message, container, false);
        srl_msgRefresh = mView.findViewById(R.id.srl_msgRresh);
        srl_msgRefresh.setOnRefreshListener(this);
        srl_msgRefresh.setColorSchemeResources(R.color.red,R.color.orange,
                R.color.green,R.color.blue);
        lv_messages = mView.findViewById(R.id.lv_messages);
        //从本地获取保存的消息，初始化消息适配器
        if(!app.getUser().getUserId().equals("")){
            if(app.getMessages().isEmpty()){
                GetMyMsgTask getMyMsgTask = new GetMyMsgTask();
                getMyMsgTask.setOnGetMyMsgListener(this);
                getMyMsgTask.execute(app.getUser().getUserId());
            }else {
                messagesAdapter = new MessagesListAdapter(getActivity(),
                        R.layout.item_message, app.getMessages());
                lv_messages.setAdapter(messagesAdapter);
                lv_messages.setOnItemClickListener(messagesAdapter);
                lv_messages.setOnItemLongClickListener(messagesAdapter);
            }
            Toast.makeText(mContext, "下拉刷新信息列表", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(mContext, "请登陆后获取好友消息", Toast.LENGTH_SHORT).show();
        }

        return mView;
    }

    /**
     * 刷新响应方法
     */
    @Override
    public void onRefresh() {
        if(!app.getUser().getUserId().equals("")){
            GetMyMsgTask getMyMsgTask = new GetMyMsgTask();
            getMyMsgTask.setOnGetMyMsgListener(this);
            getMyMsgTask.execute(app.getUser().getUserId());
        }else {
            Toast.makeText(mContext, "你没有登陆哦", Toast.LENGTH_SHORT).show();
            srl_msgRefresh.setRefreshing(false);
        }
    }

    /**
     * 获取消息结果响应方法
     * @param msgList 获取的消息数组
     */
    @Override
    public void onGetMMTResult(ArrayList<MessageIO> msgList) {
        srl_msgRefresh.setRefreshing(false);
        //将数据存储到本地
        if(!msgList.get(0).getSrcId().equals("")){//数据不为空
            for(int i = 0;i < msgList.size();i++){
                msgList.get(i).decrypt();//解密
            }
            //显示刷新的信息
            messagesAdapter = new MessagesListAdapter(getActivity(), R.layout.item_message,msgList);
            lv_messages.setAdapter(messagesAdapter);
            lv_messages.setOnItemClickListener(messagesAdapter);
            lv_messages.setOnItemLongClickListener(messagesAdapter);
        }else{
            Toast.makeText(mContext, msgList.get(0).getMessage(), Toast.LENGTH_SHORT).show();
            msgList.clear();
            messagesAdapter = new MessagesListAdapter(getActivity(),
                    R.layout.item_message,msgList);
            lv_messages.setAdapter(messagesAdapter);
            lv_messages.setOnItemClickListener(messagesAdapter);
            lv_messages.setOnItemLongClickListener(messagesAdapter);
        }
        app.setMessages(msgList);//更新全局内存数据
    }
}

